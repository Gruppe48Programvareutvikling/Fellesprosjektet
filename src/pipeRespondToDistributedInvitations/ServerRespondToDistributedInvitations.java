package pipeRespondToDistributedInvitations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import serverReturnTypes.ServerInvitationsResult;
import superClasses.ServerManager;

public class ServerRespondToDistributedInvitations extends ServerManager {
	
	private final String SQL_GET_INVITATIONS = "select * from Event join InvitesToEvent on Event.eventId = InvitesToEvent.eventId where InvitesToEvent.userName = ? and Event.userName = ?";
	private final String SQL_RESPONDTO_INVITATIONS = "update InvitesToEvent set status = ? where username = ? and eventId = ?";
			

	public ServerInvitationsResult checkForInvitations(String username, String selectedUsername) {
		ServerInvitationsResult theResult = new ServerInvitationsResult(); 
		
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_INVITATIONS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, selectedUsername);
			statement.setString(2, username);
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
	
	public ServerInvitationsResult respontoInvitation(int eventId, String response, String selectedUsername){
		ServerInvitationsResult theResult = new ServerInvitationsResult();
		
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_RESPONDTO_INVITATIONS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, response);
			statement.setString(2, selectedUsername);
			statement.setInt(3, eventId);
			int affected = statement.executeUpdate();
			
			if (affected == 1){
				theResult.didSucceed = true;
			} else {
				theResult.didSucceed = false;
				theResult.errorMessage = "Unknown error while updating invitation";
			}
				}catch (SQLException e){
					System.out.println("Got exception");
					ServerManager.processSQLException(e);
					theResult.didSucceed = false;
					theResult.errorMessage = e.getMessage();
				}
		
		return theResult;
	}
}
