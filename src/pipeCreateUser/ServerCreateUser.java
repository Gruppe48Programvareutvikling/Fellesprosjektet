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
	
	private final String SQL_CHECK_IF_USER_IS_AVAILABLE = "SQL QUERY GOES HERE";
	private final String SQL_CREATE_USER				= "SQL QUERY GOES HERE";
	
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
			
			while (result.next()) {
//				lostItems.put(result.getString("phone"), result.getString("lostItems"));
				
				// Set the availability result
			}			
		} catch (SQLException e) {
			ServerCreateUser.processSQLException(e);
			
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
			result = statement.executeQuery();
			
			while (result.next()) {
//				lostItems.put(result.getString("phone"), result.getString("lostItems"));
				
				// Set the availability result
			}			
		} catch (SQLException e) {
			ServerCreateUser.processSQLException(e);
			
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		
		return theResult;
	}
}
