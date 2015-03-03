package pipeEventsForTheDay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerManager;

public class ServerEventsForTheDay extends ServerManager {
	
	private final String SQL_GET_EVENT = ("select name, description, startDate, endDate, location, roomNumber "+
										  "from InvitesToEvent,Event "+
										  "where InvitesToEvent.userName=Event.userName and InvitesToEvent.userName=? and (select distinct(date(startDate)) from Event where date(startDate)=CURDATE())");

	public ServerAvailabilityResult getEvent(String username){
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();
		ResultSet result = null;

		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_EVENT, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, username);
			result = statement.executeQuery();

			boolean gotResult = false;

			while (result.next()){
				gotResult = true;
				theResult.isAvailable = true;
				theResult.didSucceed = true;
				System.out.println("Name:\t\t"+result.getString("name"));
				System.out.println("Description:\t"+result.getString("description"));
				System.out.println("Start date:\t"+result.getDate("startDate")+" "+result.getTime("startDate"));
				System.out.println("End date:\t"+result.getDate("endDate")+" "+result.getTime("endDate"));
				System.out.println("Location:\t"+result.getString("location"));
				System.out.println("Room:\t\t"+result.getString("roomNumber"));
				System.out.println("-----------------------------------------------\n");

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
