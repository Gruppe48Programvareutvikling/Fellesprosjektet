package pipeCheckRSVPStatusForEvents;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerEvents;
import superClasses.ServerManager;

public class ServerCheckRSVPStatusForEvents extends ServerEvents {
	
	private final String SQL_CHECK_IF_EVENT_EXIST = "SELECT e.name, i.userName, status FROM InvitesToEvent i, Event e WHERE e.eventId = i.eventId AND i.eventId = ?";
	
	public ServerAvailabilityResult checkIfEventExist(String eventId){
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();
		ResultSet result = null;

		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CHECK_IF_EVENT_EXIST, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, eventId);
			result = statement.executeQuery();

			boolean gotResult = false;

			while (result.next()){
				gotResult = true;
				theResult.isAvailable = true;
				theResult.didSucceed = true;
				System.out.print(result.getString("name"));
				System.out.print("  ");
				System.out.print(result.getString("userName"));
				System.out.print("  ");
				System.out.println(result.getString("status"));
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
