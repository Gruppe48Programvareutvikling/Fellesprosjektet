package pipeSeeInvitations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import serverReturnTypes.ServerInvitationsResult;
import serverReturnTypes.ServerNotificationsResult;
import superClasses.ServerManager;
import superClasses.ServerResult;

public class ServerSeeInvitations extends ServerManager {
	private final String SQL_GET_INVITATIONS = "select * from Event join InvitesToEvent on Event.eventId = InvitesToEvent.eventId where InvitesToEvent.userName = ?";
	private final String SQL_RESPONDTO_INVITATIONS = "update InvitesToEvent set status = ? where username = ? and eventId = ?";
	private final String SQL_SEND_NOTIFICATION = "insert into Notification(date, message, userName) values (NOW(), ?, ?)";		
	private ServerInvitationsResult invitations;
	
	public ServerInvitationsResult checkForInvitations(String username) {
		ServerInvitationsResult theResult = new ServerInvitationsResult(); 
		
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_INVITATIONS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, username);
			result = statement.executeQuery();
			
			
			boolean gotResult = false;
			
			while (result.next()){
				gotResult = true;
		
				theResult.eventids.add(result.getString("eventId"));
				theResult.names.add(result.getString("name"));
				theResult.descriptions.add(result.getString("description"));
				theResult.startdates.add(result.getString("startDate"));
				theResult.enddates.add(result.getString("endDate"));
				theResult.locations.add( result.getString("location"));
				theResult.usernames.add(result.getString("username"));
				theResult.statuses.add(result.getString("status"));
			}
			invitations = theResult;
			if (gotResult == false){
				theResult.eventids = null;
			}
				}catch (SQLException e){
					System.out.println("Got exception");
					ServerManager.processSQLException(e);
					theResult.eventids = null;
					theResult.errorMessage = e.getMessage();
		
				}
		return theResult;
	}
	
	
	public ServerResult respontoInvitation(int eventId, String response, String username){
		ServerInvitationsResult theResult = new ServerInvitationsResult();
		
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_RESPONDTO_INVITATIONS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, response);
			statement.setString(2, username);
			statement.setInt(3, eventId);
			int affected = statement.executeUpdate();
			
			if (affected == 1){
				theResult.didSucceed = true;
			} else {
				theResult.didSucceed = false;
				theResult.errorMessage = "Unknown error while updating invitation";
			}
				}catch (SQLException e){
					System.out.println("Got exception in respondToInvitations");
					ServerManager.processSQLException(e);
					theResult.didSucceed = false;
					theResult.errorMessage = e.getMessage();
				}
		ServerNotificationsResult notifictionsResult = sendNotification(eventId, response, username);
		
		if (!theResult.didSucceed){
			return theResult;			
		}
		else{
			return notifictionsResult;
		}
	}
	
	public ServerNotificationsResult sendNotification(int eventId, String response, String username){
		ServerNotificationsResult theResult = new ServerNotificationsResult();
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_SEND_NOTIFICATION, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			int c = 0;
			for (int i = 0; i < invitations.eventids.size(); i++) {
				if (Integer.parseInt(invitations.eventids.get(i)) == eventId){
					c = i;
				}
			}
			String creator = invitations.usernames.get(c);
			String input = username + " status is " + response + " to your invitation to event id " + eventId;
			statement.setString(1, input);
			statement.setString(2, creator);
			int affected = statement.executeUpdate();
			
			if (affected == 1){
				theResult.didSucceed = true;
			} else {
				theResult.didSucceed = false;
				theResult.errorMessage = "Unknown error while updating notification";
			}
				}catch (SQLException e){
					System.out.println("Got exception in sendNotification");
					ServerManager.processSQLException(e);
					theResult.didSucceed = false;
					theResult.errorMessage = e.getMessage();
				}
		if (theResult.didSucceed){
			
		}
		return theResult;
	}
}

