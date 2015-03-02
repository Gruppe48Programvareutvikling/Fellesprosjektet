package pipeEventsForTheDay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerManager;

public class ServerEventsForTheDay extends ServerManager {
	
	private final String SQL_GET_EVENT = "SELECT name, description, startDate, endDate, location, roomNumber FROM Event WHERE eventId=? order by eventId";

	public ServerAvailabilityResult getEvent(String username){
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();
		ResultSet result = null;

		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_EVENT, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, "1");
			result = statement.executeQuery();

			boolean gotResult = false;

			while (result.next()){
				gotResult = true;
				theResult.isAvailable = true;
				theResult.didSucceed = true;
				System.out.print(result.getDate("date"));
				System.out.print("  ");
				System.out.println(result.getString("message"));
			}

			if (gotResult == false){
				theResult.isAvailable = false;
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
