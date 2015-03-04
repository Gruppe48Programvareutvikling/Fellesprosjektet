package pipeCreateEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dataStructures.Event;
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
	private final String SQL_FIND_GROUP_CALENDAR = "Select userName from GroupUsers where groupName=? and userName !=?";
	private final String SQL_FIND_ROOM = "SELECT r1.roomNumber FROM Room r1, Event e1 WHERE r1.numberOfSeats > ? AND r1.roomNumber NOT IN ( SELECT r2.roomNumber FROM Event e2, Room r2 WHERE r2.roomNumber = r1.roomNumber AND (? <= e2.startDate AND e? >= e2.startDate) OR (? <= e2.endDate AND  ? >= e2.startDate) )";
	private final String SQL_CREATE_EVENT = "INSERT INTO Event(name, groupName, descriptions, startDate, endDate, privateCalendarName, groupCalendarName,location,userName,roomNumber) VALUES (?,?,?,?,?,?,?,?,?,?)";
	
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
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_GROUP_CALENDAR, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
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
	
	public ServerRoomResult findRoomResult(String numberOfSeats){
		ServerRoomResult theResult = new ServerRoomResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_ROOM, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
			){
				statement.setString(1,numberOfSeats);
				statement.setString(2, Event.this.startDate.g);
					
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
			statement.setDate(4, eventToCreate.startDate);
			statement.setDate(5, eventToCreate.endDate);
			//statement.setString(6, eventToCreate.privateCalendarName);
			//statment.setString(7, eventToCreate.groupCalendarName);
			statement.setString(8, eventToCreate.location);
			statement.setString(9, User.currentUser().username);
			statement.setInt(10, eventToCreate.room.roomNumber);
			
		}catch (SQLException e) {
			// TODO: handle exception
		}
			return null;
	}
}
