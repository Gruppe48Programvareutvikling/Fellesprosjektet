package pipeCreateEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;



import com.mysql.jdbc.Statement;

import pipeEditEvent.ServerEditEvent;
import dataStructures.Event;
import dataStructures.Invitation;
import dataStructures.Notification;
import dataStructures.User;
import serverReturnTypes.ServerCreateEventResult;
import serverReturnTypes.ServerEventsResult;
import serverReturnTypes.ServerFindGroupResult;
import serverReturnTypes.ServerFindUserResult;
import serverReturnTypes.ServerGetCalendarsResult;
import serverReturnTypes.ServerNotificationsResult;
import serverReturnTypes.ServerRoomResult;
import superClasses.ServerManager;
import superClasses.ServerResult;

public class ServerCreateEvent extends ServerManager {
	private final String SQL_FIND_USER = "Select userName from User where userName =?";
	private final String SQL_FIND_PRIVATE_CALENDAR_NAME = "Select privateCalendarName FROM PrivateCalendar WHERE userName =?";
	private final String SQL_FIND_GROUPS_FROM_USERNAME = "Select groupName from GroupUsers where userName =?";
	private final String SQL_FIND_GROUP_USERS_FROM_GROUP = "Select userName from GroupUsers where groupName=? and userName !=?";
	private final String SQL_FIND_ROOM = "SELECT r1.roomNumber, r1.numberOfSeats FROM Room r1, Event e1 WHERE r1.numberOfSeats >= ? AND r1.roomNumber NOT IN ( SELECT r2.roomNumber FROM Event e2, Room r2 WHERE r2.roomNumber = e2.roomNumber AND ((? <= e2.startDate AND ? >= e2.startDate) OR (? <= e2.endDate AND  ? >= e2.startDate)) )";
	private final String SQL_CREATE_EVENT = "INSERT INTO Event(name, description, startDate, endDate, groupCalendarName,location,userName,roomNumber) VALUES (?,?,?,?,?,?,?,?)";
	private final String SQL_FIND_GROUP_CALENDAR_NAME = "SELECT groupCalendarName FROM GroupCalendar WHERE groupName =?";
	private final String SQL_CREATE_INVITATION = "INSERT INTO InvitesToEvent(userName,eventId,status) VALUES (?,?,?)";
	private final String SQL_CREATE_NOTIFICATION = "INSERT INTO Notification(date,message,userName) VALUES (?,?,?)";
	private final String SQL_FIND_NOTIFICATIONID = "Select invitationId from Invitation where userName =?";
	private final String SQL_FIND_EVENTID = "Select eventId from Event WHERE name=? AND description =? AND startDate =? AND endDate =? AND privateCalendarName=? AND groupCalendarName =? AND location =? AND userName=? AND roomNumber=?";
	private final String SQL_FIND_EVENTID_WITHOUT_ROOM = "Select eventId from Event WHERE name=? AND description =? AND startDate =? AND endDate =? AND privateCalendarName=? AND groupCalendarName =? AND location =? AND userName=? AND roomNumber IS NULL";
	private final String SQL_CREATE_PRIVATE_CALENDAR_EVENT = "INSERT INTO PrivateCalendarEvent(privateCalendarName, eventId) VALUES (?,?)";
	
	public ServerFindGroupResult getListOfGroupsTheUserIsPartOf(String userName){
		ServerFindGroupResult theResult = new ServerFindGroupResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_GROUPS_FROM_USERNAME, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				){
			statement.setString(1, userName);
			result = statement.executeQuery();
			
			boolean gotResult = false;
			while (result.next()){
				gotResult = true;
				theResult.addGroupName(result.getString(1));
				
				theResult.didSucceed = true;
				
				
			}
			if (gotResult == false){
				theResult.didSucceed = true;
				System.out.println("The user is not a member of a group");
			}
					
		}catch (SQLException e) {
			theResult.didSucceed = false;
			
			// TODO: handle exception
		}
		
		
		return theResult;
		
	}
	public ServerFindGroupResult getGroupUsers(String groupName){
		ServerFindGroupResult theResult = new ServerFindGroupResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_GROUP_USERS_FROM_GROUP, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				){
					
					statement.setString(1, groupName);
					statement.setString(2, User.currentUser().username);
					result = statement.executeQuery();
					
					while (result.next()){
						
						theResult.groupUsers.add(result.getString(1));
						theResult.didSucceed = true;
					}
					
					
				}catch(SQLException e){
					theResult.didSucceed = false;
					System.out.println("Couldnt find CalendarName");
				}
		return theResult;
	}
	public ServerFindUserResult findUser(String userName){
		ServerFindUserResult theResult = new ServerFindUserResult();
		ResultSet result = null;
		try(
			Connection connection = this.getDataBaseConnection();
			PreparedStatement statement = connection.prepareStatement(SQL_FIND_USER, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		){
			statement.setString(1, userName);
			result = statement.executeQuery();
			
			boolean gotResult = false;
			while (result.next()) {
				gotResult = true;
				theResult.userExists = true;
				theResult.didSucceed = true;
					
				
			
			} 
			
			if (gotResult == false) {
				theResult.userExists = false;
				theResult.didSucceed = true;
			}
		} catch (SQLException e) {
			System.out.println("Got exception");
			ServerManager.processSQLException(e);
			
			theResult.userExists = false;
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		
	
		
		return theResult;
	}
	public ServerGetCalendarsResult findPrivateCalendar(String userName){
		ServerGetCalendarsResult theResult = new ServerGetCalendarsResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_PRIVATE_CALENDAR_NAME, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				){
					statement.setString(1, userName);
					result = statement.executeQuery();
					boolean gotResult = false;
					while(result.next()){
						gotResult = true;
						theResult.privateCalendarName = result.getString(1);
						theResult.didSucceed = true;
						
					}
					if (gotResult == false){
						theResult.didSucceed = true;
						
					}	
				}catch (SQLException e) {
					theResult.didSucceed = false;
					
					
				}
				
		return theResult;
	}
	
	public ServerRoomResult findRoomResult(String numberOfSeats, Date startDate, Date endDate){
		ServerRoomResult theResult = new ServerRoomResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_ROOM, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
			){
				statement.setString(1,numberOfSeats);
				statement.setString(2, dateToString(startDate));
				statement.setString(3, dateToString(endDate));
				statement.setString(4, dateToString(startDate));
				statement.setString(5, dateToString(endDate));
				result = statement.executeQuery();
				
				boolean gotResult = false;
				
				while(result.next() && !(theResult.roomNumber.contains(result.getInt(1)))){
					gotResult = true;
					theResult.roomNumber.add(result.getInt(1));
					theResult.numberOfSeats.add(result.getInt(2));
					theResult.roomIsAvailable = true;
					theResult.didSucceed = true;
				}
				if (gotResult == false){
					theResult.roomIsAvailable = false;
					theResult.didSucceed = true;
				}
			}catch (SQLException e) {
				
				theResult.roomIsAvailable = false;
				theResult.didSucceed = false;
				theResult.errorMessage = e.getMessage();
			}
		return theResult;
	}
	public ServerGetCalendarsResult getGroupCalendarName(String groupName){
		ServerGetCalendarsResult theResult = new ServerGetCalendarsResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_GROUP_CALENDAR_NAME, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
			){
			statement.setString(1, groupName);
			result = statement.executeQuery();
			boolean gotResult = false;
			while(result.next()){
				gotResult = true;
				theResult.groupCalendarName = result.getString(1);
				theResult.didSucceed = true;
			}
			if(gotResult == false){
				theResult.didSucceed = true;
				
				System.out.println("Group has no group calendar");
			}
		}catch (SQLException e) {
			ServerCreateEvent.processSQLException(e);
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
			
		}
			
		return theResult;
	}
	public ServerCreateEventResult createEvent(Event eventToCreate){
		ServerCreateEventResult result = new ServerCreateEventResult();
		ResultSet theResult = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_EVENT, Statement.RETURN_GENERATED_KEYS)
			){
			//for (int i = 0; i < eventToCreate.participants.size(); i++) {
				
			
				statement.setString(1, eventToCreate.name );
				
				statement.setString(2, eventToCreate.description);
				statement.setString(3, dateToString(eventToCreate.startDate));
				statement.setString(4, dateToString(eventToCreate.endDate));
				statement.setString(5, eventToCreate.privateCalendarName); //maa finne privat navn
				statement.setString(6, eventToCreate.groupCalendarName);
				statement.setString(7, eventToCreate.location);
				statement.setString(8, User.currentUser().username);

				
				statement.setString(5, eventToCreate.groupCalendarName);
				statement.setString(6, eventToCreate.location);
				statement.setString(7, User.currentUser().username);

				if(eventToCreate.roomNumber != 0){
					statement.setInt(8, eventToCreate.roomNumber);
				}else{
					statement.setNull(8, java.sql.Types.INTEGER);
				}
				int affected = statement.executeUpdate();
				
				if (affected == 1){
					result.didSucceed = true;
					theResult = statement.getGeneratedKeys();
					ResultSetMetaData rsmd = theResult.getMetaData();
					
					while(theResult.next()){
						result.eventId = theResult.getInt(1);
						
					}
						
				}
				else{
					result.didSucceed = false;
					result.errorMessage = "Unknown";
				}
			//}
//			for (int i = 0; i < eventToCreate.groupUsers.size(); i++) {
//				
//				
//
//				statement.setString(5, null); //maa finne privat navn
//				statement.setString(6, eventToCreate.groupCalendarName);
//				
//				int affected = statement.executeUpdate();
//				
//				if (affected == 1){
//					result.didSucceed = true;
//						
//				}
//				else{
//					result.didSucceed = false;
//					result.errorMessage = "Unknown";
//				}
			//}
			
		}catch (SQLException e) {

			ServerCreateEvent.processSQLException(e);
			result.didSucceed = false;
			result.errorMessage = e.getMessage();
		}
			return result;
	}

	public ServerResult createNotification(Notification notification){
		ServerResult result = new ServerResult();
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_NOTIFICATION, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
				){
				statement.setString(1, dateToString(notification.date));
				statement.setString(2, notification.message);
				statement.setString(3, notification.username);
				int affected = statement.executeUpdate();
				if (affected == 1){
					result.didSucceed = true;
				}else{
					result.didSucceed = false;
					result.errorMessage = "Unknown error while creating notification";
					
				}
		}catch (SQLException e) {
			// TODO: handle exception
		ServerCreateEvent.processSQLException(e);
		result.didSucceed = false;
		result.errorMessage = e.getMessage();
		}
		
		return result;
	}
	public ServerResult createPrivateCalendarEvent(String userName, int eventId){
		ServerResult result = new ServerResult();
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_PRIVATE_CALENDAR_EVENT, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				){
				statement.setString(1, userName + "'s Calendar");
				statement.setInt(2, eventId);
				int affected = statement.executeUpdate();
				if (affected == 1){
					result.didSucceed = true;
				}else{
					result.didSucceed = false;
					result.errorMessage = "Unknow error while creating privateCalendarEvent";
				}
		}catch (SQLException e) {
			// TODO: handle exception
			ServerCreateEvent.processSQLException(e);
			result.didSucceed = false;
			result.errorMessage = e.getMessage();
		}
		
		
		return result;
	}
	
	public ServerNotificationsResult createInvitation(Invitation invitationToCreate, String status){
		ServerNotificationsResult result = new ServerNotificationsResult();
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_INVITATION, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				){
				statement.setString(1, invitationToCreate.invitert.username);
				statement.setInt(2, invitationToCreate.id);
				statement.setString(3, status);
				
				int affected = statement.executeUpdate();
				
				if (affected == 1){
					result.didSucceed = true;
				}else{
					result.didSucceed = false;
					result.errorMessage = "unknown";
				}
			
			
			
		}catch (SQLException e) {
			// server: handle exception
			ServerCreateEvent.processSQLException(e);
			result.errorMessage = e.getMessage();
		}
		
		return result;
	}
	
	public String dateToString(Date date){
		String string = Integer.toString(date.getYear()+1900) + "-"	+ Integer.toString(date.getMonth()+1) + "-" + Integer.toString(date.getDate()) + " " + 
				Integer.toString(date.getHours()) + ":" + Integer.toString(date.getMinutes());
		
		
		return string;
	}
}//
//maa gi calendarName
//lage notifications og invitations
