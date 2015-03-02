package pipeCreateEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dataStructures.Event;
import serverReturnTypes.ServerFindUserResult;
import serverReturnTypes.ServerGetCalendarsResult;
import serverReturnTypes.ServerRoomResult;
import superClasses.ServerManager;
import superClasses.ServerResult;

public class ServerCreateEvent extends ServerManager {
	private final String SQL_FIND_USER = "Select userName from User where userName =?";
	private final String SQL_FIND_GROUPS_FROM_USERNAME = "Select groupName from ";
	public ServerResult createEvent(Event event){
		return null;
		
	}
	public ServerGetCalendarsResult getListOfGroupsTheUserIsPartOf(String userName){
		return null;
		
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
				theResult.userExists = false;
				theResult.didSucceed = true;
			} 
			
			if (gotResult == false) {
				theResult.userExists = true;
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
	public ServerRoomResult findRoomResult(){
		return null;
	}
}
