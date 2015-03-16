package adminPipeDeleteUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerManager;

public class ServerDeleteUser extends ServerManager {

	private final String SQL_DELETE_USER = "DELETE FROM User WHERE userName = ?";
	
	// The isAvailable attribute of the return value indicates whether the user was found and deleted or not
	public ServerAvailabilityResult deleteUser(String usernameToDelete) {
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();

		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_DELETE_USER, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			) 
		{
			statement.setString(1, usernameToDelete);
			int rowsAffected = statement.executeUpdate();
			
			theResult.didSucceed = true;
			theResult.errorMessage = null;
			
			if (rowsAffected == 1) {
				theResult.isAvailable = true;
			} else {
				theResult.isAvailable = false;
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
}
