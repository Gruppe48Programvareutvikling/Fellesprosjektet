package pipeCheckAvailability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerManager;

public class ServerCheckAvailability extends ServerManager {

	private final String SQL_CHECK_IF_SELF_IS_AVAILABLE = 
			  "select name, startDate, endDate "
			  +"from InvitesToEvent,Event "
			  + "where InvitesToEvent.userName=Event.userName "
			  + "and InvitesToEvent.userName=? "
			  + "and ((Event.startDate <= ? "
			  + "and Event.endDate >= ?) "
			  + "OR (Event.startDate <= ? "
			  + "and Event.endDate >= ?))";
	
	public ServerAvailabilityResult checkIfAvailable(String startdate, String enddate, String username){
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();

		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CHECK_IF_SELF_IS_AVAILABLE, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, username);
			statement.setString(2, startdate);
			statement.setString(3, startdate);
			statement.setString(4, enddate);
			statement.setString(5, enddate);
			result = statement.executeQuery();

			boolean gotResult = false;

			while (result.next()){
				gotResult = true;
				theResult.isAvailable = false;
				theResult.didSucceed = true;
				theResult.eventname = result.getString("name");
				theResult.startdate = result.getString("startDate");
				theResult.enddate = result.getString("endDate");
			}

			if (gotResult == false){
				theResult.isAvailable = true;
				theResult.didSucceed = true;
			}

				}catch (SQLException e){
					System.out.println("Got exception");
					ServerManager.processSQLException(e);
					theResult.isAvailable = false;
					theResult.didSucceed = false;
					theResult.errorMessage = e.getMessage();
				}
		return theResult;
	}
	
}
