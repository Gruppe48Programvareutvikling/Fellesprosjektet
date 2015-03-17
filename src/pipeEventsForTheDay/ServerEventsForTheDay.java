package pipeEventsForTheDay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerManager;

public class ServerEventsForTheDay extends ServerManager {
	
	private final String SQL_GET_EVENT = ("select distinct(name), description, startDate, endDate, location, roomNumber "+
										  "from InvitesToEvent,Event "+
										  "where InvitesToEvent.userName=Event.userName and InvitesToEvent.userName=? and (date(Event.startDate)<=CURDATE()) and (date(Event.endDate)>=CURDATE())");
	
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
				
				theResult.name.add(result.getString("name"));
				theResult.description.add(result.getString("description"));
				theResult.location.add(result.getString("location"));
				theResult.roomNumber.add(result.getString("roomNumber"));
				theResult.startDate.add(result.getDate("startDate"));
				theResult.endDate.add(result.getDate("endDate"));
				theResult.startTime.add(result.getTime("startDate"));
				theResult.endTime.add(result.getTime("endDate"));

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
