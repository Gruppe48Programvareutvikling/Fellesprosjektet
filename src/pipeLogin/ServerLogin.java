package pipeLogin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerManager;
import superClasses.ServerResult;

public class ServerLogin extends ServerManager {
	private final String SQL_FIND_USERNAME_AND_PASSWORD = "SELECT userName, password from User WHERE userName =? AND password =?";
	
	public ServerLogin(){
		
	}
	public ServerAvailabilityResult LoginWith(String userName, String password){
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();
		ResultSet result = null;
		try( 
			Connection connection = this.getDataBaseConnection();
			PreparedStatement statement = connection.prepareStatement(SQL_FIND_USERNAME_AND_PASSWORD, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
		){
			statement.setString(1, userName);
			statement.setString(2, password);
			result = statement.executeQuery();
			boolean gotResult = false;
			while (result.next()) {
				gotResult = true;
				theResult.isAvailable = true;
				theResult.didSucceed = true;
			}
			
			if (gotResult == false) {
					theResult.isAvailable = false;
					theResult.didSucceed = true;
			} 
		}catch(SQLException e){
			System.out.println("Got exception");
			ServerManager.processSQLException(e);
			
			theResult.isAvailable = false;
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
			
		}
		
		return theResult;
		
	}
}
