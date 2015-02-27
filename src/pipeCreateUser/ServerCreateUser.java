package pipeCreateUser;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dataStructures.User;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerManager;
import superClasses.ServerResult;

public class ServerCreateUser extends ServerManager {
	
	private final String SQL_CHECK_IF_USER_IS_AVAILABLE = "SELECT userName FROM User WHERE userName=?";
	private final String SQL_CREATE_USER				= "INSERT INTO User(userName, password, mail, phone) VALUES (?,?,?,?)";
	private final String SQL_CREATE_PrivateCalendar		= "INSERT INTO PrivateCalendar(privateCalendarName, userName) VALUES (?,?)";
	
	public ServerAvailabilityResult checkIfUsernameIsAvailable(String usernameToCheck) {
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();
		
		ResultSet result = null;
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CHECK_IF_USER_IS_AVAILABLE, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			) 
		{
			statement.setString(1, usernameToCheck);
			result = statement.executeQuery();
			
			boolean gotResult = false;
			
			while (result.next()) {
				gotResult = true;
				theResult.isAvailable = false;
				theResult.didSucceed = true;
			} 
			
			if (gotResult == false) {
				theResult.isAvailable = true;
				theResult.didSucceed = true;
			}
		} catch (SQLException e) {
			System.out.println("Got exception");
			ServerManager.processSQLException(e);
			
			theResult.isAvailable = false;
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		
		return theResult;
	}
	
	public ServerResult createUser(User userToCreate) {
		ServerResult theResult = new ServerResult();
		
		ResultSet result = null;
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_USER, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			) 
		{
			statement.setString(1, userToCreate.username);
			statement.setString(2, userToCreate.password);
			statement.setString(3, userToCreate.mail);
			statement.setString(4, userToCreate.phoneNumber);
			int affected = statement.executeUpdate();
			
			if (affected == 1) {
				theResult.didSucceed = true;
				createPrivateCalendar(userToCreate.username);
			} else {
				theResult.didSucceed = false;
				theResult.errorMessage = "Unknown error while creating user";
			}
		} catch (SQLException e) {
			ServerCreateUser.processSQLException(e);
			
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		
		return theResult;
	}
	private void createPrivateCalendar(String username){
		ServerResult theResult = new ServerResult();
		try(Connection connection = this.getDataBaseConnection();
			PreparedStatement statement = connection.prepareStatement(SQL_CREATE_PrivateCalendar, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			) 
		{
			statement.setString(1, username + "'s Calendar");
			statement.setString(2, username);
			int affected = statement.executeUpdate();
			if (affected == 1){
				theResult.didSucceed = true;
			}
			else{
				theResult.didSucceed = false;
				theResult.errorMessage = "Unknow error while creating private calendar";
			}
			
		} catch (SQLException e) {
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
			
		}
		
	}
}
