package pipeCreateEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import dataStructures.Event;
import dataStructures.Invitation;
import dataStructures.User;
import serverReturnTypes.ServerFindGroupResult;
import serverReturnTypes.ServerFindUserResult;
import serverReturnTypes.ServerGetCalendarsResult;
import serverReturnTypes.ServerRoomResult;
import superClasses.ServerManager;
import superClasses.ServerResult;

public class ServerCreateEvent extends ServerManager {
	private final String SQL_FIND_USER = "Select userName from User where userName =?";
	private final String SQL_FIND_GROUPS_FROM_USERNAME = "Select groupName from GroupUsers where userName =?";
	private final String SQL_FIND_GROUP_USERS_FROM_GROUP = "Select userName from GroupUsers where groupName=? and userName !=?";
	private final String SQL_FIND_ROOM = "SELECT r1.roomNumber FROM Room r1, Event e1 WHERE r1.numberOfSeats > ? AND r1.roomNumber NOT IN ( SELECT r2.roomNumber FROM Event e2, Room r2 WHERE r2.roomNumber = e2.roomNumber AND (? <= e2.startDate AND ? >= e2.startDate) OR (? <= e2.endDate AND  ? >= e2.startDate) )";
	private final String SQL_CREATE_EVENT = "INSERT INTO Event(name, groupName, descriptions, startDate, endDate, privateCalendarName, groupCalendarName,location,userName,roomNumber) VALUES (?,?,?,?,?,?,?,?,?,?)";
	private final String SQL_FIND_GROUP_CALENDAR_NAME = "SELECT groupCalendarName FROM GroupCalendar WHERE groupName =?";
	
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
				
				while(result.next()){
					gotResult = true;
					theResult.roomNumber.add(result.getInt(1));
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
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_EVENT, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
			){
			statement.setString(1, groupName);
			result = statement.executeQuery();
			boolean gotResult = false;
			while(result.next()){
				gotResult = true;
				theResult.calendarnames = result.getString(1);
				theResult.didSucceed = true;
			}
			if(gotResult == false){
				theResult.didSucceed = true;
				
				System.out.println("Group has no group calendar");
			}
		}catch (SQLException e) {
			theResult.didSucceed = false;
			System.out.println("sql error");
		}
			
		return null;
	}
	public ServerResult createEvent(Event eventToCreate){
		ServerResult result = new ServerResult();
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_EVENT, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
			){
			statement.setString(1, eventToCreate.name );
			//statement.setString(2, eventToCreate.groupName);
			statement.setString(3, eventToCreate.description);
			statement.setString(4, dateToString(eventToCreate.startDate));
			statement.setString(5, dateToString(eventToCreate.endDate));
			//statement.setString(6, eventToCreate.privateCalendarName);
			//statment.setString(7, eventToCreate.groupCalendarName);
			statement.setString(8, eventToCreate.location);
			statement.setString(9, User.currentUser().username);
			statement.setInt(10, eventToCreate.room.roomNumber);
			int affected = statement.executeUpdate();
			
			if (affected == 1){
				result.didSucceed = true;
					
			}
			else{
				result.didSucceed = false;
				result.errorMessage = "Uknow Error while creating event";
			}
			
		}catch (SQLException e) {
			result.didSucceed = false;
			result.errorMessage = e.getMessage();
		}
			return result;
	}
	public ServerResult createInvitations(Invitation invitationToCreate){
		
		return null;
	}
	
	public String dateToString(Date date){
		String string = Integer.toString(date.getYear()) + "-"	+ Integer.toString(date.getMonth()) + "-" + Integer.toString(date.getDate()) + " " + 
				Integer.toString(date.getHours()) + ":" + Integer.toString(date.getMinutes());
		
		
		return string;
	}
}
