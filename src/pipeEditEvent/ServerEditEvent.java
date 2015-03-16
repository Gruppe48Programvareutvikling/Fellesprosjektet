package pipeEditEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import pipeCreateEvent.ServerCreateEvent;
import dataStructures.Event;
import dataStructures.Invitation;
import dataStructures.Notification;
import dataStructures.User;
import serverReturnTypes.ServerEventsResult;
import serverReturnTypes.ServerFindUserResult;
import serverReturnTypes.ServerGetCalendarsResult;
import serverReturnTypes.ServerNotificationsResult;
import serverReturnTypes.ServerRoomResult;
import superClasses.ServerEvents;
import superClasses.ServerManager;
import superClasses.ServerResult;

public class ServerEditEvent extends ServerEvents {
	
	private final String SQL_GET_LIST_OF_EVENTS = "Select * FROM Event WHERE userName =? AND privateCalendarName =?";
	
	private final String SQL_UPDATE_EVENT = "UPDATE Event SET name =?, description =?, startDate =?, endDate =?, privateCalendarName =?,"
			+ "groupCalendarName =?, location =?, userName=?, roomnumber =? WHERE eventId =?";
	private final String SQL_FIND_PRIVATE_CALENDAR_NAME = "SELECT privateCalendarName FROM PrivateCalendar WHERE userName =?";
	private final String SQL_FIND_ROOM = "SELECT r1.roomNumber, r1.numberOfSeats FROM Room r1, Event e1 WHERE r1.numberOfSeats >= ? AND r1.roomNumber NOT IN ( SELECT r2.roomNumber FROM Event e2, Room r2 WHERE r2.roomNumber = e2.roomNumber AND ((? <= e2.startDate AND ? >= e2.startDate) OR (? <= e2.endDate AND  ? >= e2.startDate)) )";
	private final String SQL_FIND_USER = "Select userName from User where userName =?";
	private final String SQL_ISINVITED = "Select userName From PrivateCalendar Where privateCalendarname IN(Select privateCalendarName From Event WHERE name =? AND description =? AND startDate =? AND endDate =? AND location =? AND userName=? AND roomNumber=?)";
	private final String SQL_LIST_OF_PARTICIPANTS = "Select userName From PrivateCalendar Where privateCalendarname IN(Select privateCalendarName From Event WHERE name =? AND description =? AND startDate =? AND endDate =? AND location =? AND userName=? AND roomNumber=?)";
	private final String SQL_FIND_EVENTIDLIST = "Select eventId from Event WHERE name=? AND description =? AND startDate =? AND endDate =? AND location =? AND userName=? AND roomNumber=?";
	private final String SQL_CREATE_EVENT = "INSERT INTO Event(name, description, startDate, endDate, privateCalendarName, groupCalendarName,location,userName,roomNumber) VALUES (?,?,?,?,?,?,?,?,?)";
	private final String SQL_CREATE_NOTIFICATION = "INSERT INTO Notification(date,message,userName) VALUES (?,?,?)";
	private final String SQL_FIND_EVENTID = "Select eventId from Event WHERE name=? AND description =? AND startDate =? AND endDate =? AND privateCalendarName=? AND groupCalendarName =? AND location =? AND userName=? AND roomNumber=?";
	private final String SQL_FIND_EVENTID_WITHOUT_ROOM = "Select eventId from Event WHERE name=? AND description =? AND startDate =? AND endDate =? AND privateCalendarName=? AND groupCalendarName =? AND location =? AND userName=? AND roomNumber IS NULL";
	private final String SQL_CREATE_INVITATION = "INSERT INTO InvitesToEvent(userName,eventId,status) VALUES (?,?,?)";
	public ServerEventsResult getListOfEvents(String userName){
		ServerEventsResult theResult = new ServerEventsResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_LIST_OF_EVENTS, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
				){
				statement.setString(1, userName);
				statement.setString(2, userName + "'s Calendar");
				result = statement.executeQuery();
				boolean gotResult = false;
				
				while(result.next()){
					Event eventConstructor = new Event ();
					eventConstructor.creator = userName;
					gotResult = true;
					theResult.didSucceed = true;
					eventConstructor.eventId = result.getInt(1);
					eventConstructor.name = result.getString(2);
					eventConstructor.description = result.getString(3);
					eventConstructor.startDate = result.getTimestamp(4);
					eventConstructor.startDate.setYear(eventConstructor.startDate.getYear());
					eventConstructor.startDate.setMonth((eventConstructor.startDate.getMonth()));
					eventConstructor.endDate = result.getTimestamp(5);
					eventConstructor.endDate.setYear(eventConstructor.endDate.getYear());
					eventConstructor.endDate.setMonth((eventConstructor.endDate.getMonth()));
					eventConstructor.privateCalendarName = result.getString(6);
					eventConstructor.groupCalendarName = result.getString(7);
					eventConstructor.location = result.getString(8);
					eventConstructor.roomNumber = result.getInt(10);
					theResult.events.add(eventConstructor);
					
					
				}
				if (gotResult == false){
					theResult.didSucceed = true;
					theResult.errorMessage = "You have not created an event yet";
				}
		}catch (SQLException e) {
			// TODO: handle exception
		ServerCreateEvent.processSQLException(e);
		theResult.didSucceed = false;
		theResult.errorMessage = e.getMessage();
		}
		
		return theResult;
	}
	
	public ServerResult editEvent(Event eventToCreate){
		ServerResult result = new ServerResult();
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_EVENT, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
				){
				statement.setString(1, eventToCreate.name);
				statement.setString(2, eventToCreate.description);
				statement.setString(3, dateToString(eventToCreate.startDate));
				statement.setString(4, dateToString(eventToCreate.endDate));
				statement.setString(5, eventToCreate.privateCalendarName);
				statement.setString(6, eventToCreate.groupCalendarName);
				statement.setString(7, eventToCreate.location);
				statement.setString(8, eventToCreate.creator);
				if (eventToCreate.roomNumber == 0){
					statement.setNull(9, java.sql.Types.INTEGER);
				}else{
					statement.setInt(9, eventToCreate.roomNumber);
				}
				
				statement.setInt(10,eventToCreate.eventId);
				int affected = statement.executeUpdate();
				if (affected == 1){
					result.didSucceed = true;
				}else{
					result.didSucceed = false;
					result.errorMessage = "Unknown error while editing event";
					
				}
		}catch (SQLException e) {
			// TODO: handle exception
		ServerCreateEvent.processSQLException(e);
		result.didSucceed = false;
		result.errorMessage = e.getMessage();
		}
		
		return result;
	}
	public ServerEventsResult getEventIdListWithRoom(Event event){
		ServerEventsResult theResult = new ServerEventsResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_EVENTIDLIST, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				){ //name, description, startDate, endDate, privateCalendarName, groupCalendarName,location,userName,roomNumber
			statement.setString(1, event.name);
			statement.setString(2, event.description);
			statement.setString(3, dateToString(event.startDate));
			statement.setString(4, dateToString(event.endDate));
			
			
			statement.setString(5, event.location);
			statement.setString(6, event.creator);
			statement.setInt(7, event.roomNumber);
			
		
			result = statement.executeQuery();
			boolean gotResult = false;
			while(result.next()){
				gotResult = true;
				theResult.eventIds.add(result.getInt(1));
				theResult.didSucceed = true;
				
			}
			if (gotResult ==false){
				theResult.didSucceed = true;
				theResult.errorMessage = "No eventId was found";
				
			}
			
		}catch (SQLException e) {
			// TODO: handle exception
			ServerCreateEvent.processSQLException(e);
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
						theResult.privateCalendarNames.add(result.getString(1));
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
	} //Select userName From PrivateCalendar Where privateCalendarname IN(Select privateCalendarName From Event WHERE name =? AND description =? AND startDate =? AND endDate =? AND location =? AND userName=? AND roomNumber=?)
	public ServerFindUserResult isUserInvited(Event event){
		ServerFindUserResult theResult = new ServerFindUserResult();
		ResultSet result = null;
		try(
			Connection connection = this.getDataBaseConnection();
			PreparedStatement statement = connection.prepareStatement(SQL_ISINVITED, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		){
			statement.setString(1, event.name);
			statement.setString(2, event.description);
			statement.setString(3, dateToString(event.startDate));
			statement.setString(4, dateToString(event.endDate));
			statement.setString(5, event.location);
			statement.setString(6, event.creator);
			statement.setInt(7, event.roomNumber);
			result = statement.executeQuery();
			
			boolean gotResult = false;
			while (result.next()) {
				gotResult = true;
				theResult.userName = result.getString(1);
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
	public ServerFindUserResult Participants(Event event){
		ServerFindUserResult theResult = new ServerFindUserResult();
		ResultSet result = null;
		try(
			Connection connection = this.getDataBaseConnection();
			PreparedStatement statement = connection.prepareStatement(SQL_LIST_OF_PARTICIPANTS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		){
			statement.setString(1, event.name);
			statement.setString(2, event.description);
			statement.setString(3, dateToString(event.startDate));
			statement.setString(4, dateToString(event.endDate));
			statement.setString(6, event.location);
			statement.setString(7, event.creator);
			statement.setInt(8, event.roomNumber);
			result = statement.executeQuery();
			
			boolean gotResult = false;
			while (result.next()) {
				gotResult = true;
				theResult.userName = result.getString(1);
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
	
	public ServerResult createEvent(Event eventToCreate){
		ServerResult result = new ServerResult();
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_EVENT, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
			){
			//for (int i = 0; i < eventToCreate.participants.size(); i++) {
				
			
				statement.setString(1, eventToCreate.name );
				
				statement.setString(2, eventToCreate.description);
				statement.setString(3, dateToString(eventToCreate.startDate));
				statement.setString(4, dateToString(eventToCreate.endDate));
				statement.setString(5, eventToCreate.privateCalendarName); //må finne privat navn
				statement.setString(6, eventToCreate.groupCalendarName);
				statement.setString(7, eventToCreate.location);
				statement.setString(8, User.currentUser().username);
				if(eventToCreate.roomNumber != 0){
					statement.setInt(9, eventToCreate.roomNumber);
				}else{
					statement.setNull(9, java.sql.Types.INTEGER);
				}
				int affected = statement.executeUpdate();
				
				if (affected == 1){
					result.didSucceed = true;
						
				}
				else{
					result.didSucceed = false;
					result.errorMessage = "Unknown";
				}
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
	public ServerEventsResult getEventIdWithRoom(Event event){
		ServerEventsResult theResult = new ServerEventsResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_EVENTID, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				){ //name, description, startDate, endDate, privateCalendarName, groupCalendarName,location,userName,roomNumber
			statement.setString(1, event.name);
			statement.setString(2, event.description);
			statement.setString(3, dateToString(event.startDate));
			statement.setString(4, dateToString(event.endDate));
			statement.setString(5, event.privateCalendarName);
			statement.setString(6, event.groupCalendarName);
			statement.setString(7, event.location);
			statement.setString(8, event.creator);
			statement.setInt(9, event.roomNumber);
			
		
			result = statement.executeQuery();
			boolean gotResult = false;
			while(result.next()){
				gotResult = true;
				theResult.eventId = result.getInt(1);
				theResult.didSucceed = true;
				
			}
			if (gotResult ==false){
				theResult.didSucceed = true;
				theResult.errorMessage = "No eventId was found";
				
			}
			
		}catch (SQLException e) {
			// TODO: handle exception
			ServerCreateEvent.processSQLException(e);
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		return theResult;
	}
	public ServerEventsResult getEventIdWithoutRoom(Event event){
		ServerEventsResult theResult = new ServerEventsResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_EVENTID_WITHOUT_ROOM, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				){ //name, description, startDate, endDate, privateCalendarName, groupCalendarName,location,userName,roomNumber
			statement.setString(1, event.name);
			statement.setString(2, event.description);
			statement.setString(3, dateToString(event.startDate));
			statement.setString(4, dateToString(event.endDate));
			statement.setString(5, event.privateCalendarName);
			statement.setString(6, event.groupCalendarName);
			statement.setString(7, event.location);
			statement.setString(8, event.creator);
			
			
		
			result = statement.executeQuery();
			boolean gotResult = false;
			while(result.next()){
				gotResult = true;
				theResult.eventId = result.getInt(1);
				theResult.didSucceed = true;
				
			}
			if (gotResult ==false){
				theResult.didSucceed = true;
				theResult.errorMessage = "No eventId was found";
				
			}
			
		}catch (SQLException e) {
			// TODO: handle exception
			ServerCreateEvent.processSQLException(e);
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		return theResult;
	}
	public ServerNotificationsResult createInvitation(Invitation invitationToCreate){
		ServerNotificationsResult result = new ServerNotificationsResult();
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_INVITATION, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				){
				statement.setString(1, invitationToCreate.invitert.username);
				statement.setInt(2, invitationToCreate.id);
				statement.setString(3, "MAYBE");
				
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
				Integer.toString(date.getHours()) + ":" + Integer.toString(date.getMinutes()) + ":00";
		
		
		return string;
	}
	
}
