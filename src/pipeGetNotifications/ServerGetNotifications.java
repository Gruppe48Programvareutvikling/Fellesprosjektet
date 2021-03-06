package pipeGetNotifications;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import serverReturnTypes.ServerNotificationsResult;
import superClasses.ServerManager;

public class ServerGetNotifications extends ServerManager {
	
	private final String SQL_GET_NOTIFICATIONS = "SELECT date, message FROM Notification WHERE userName=? order by notificationId desc Limit 10";

	
	public ServerNotificationsResult getNotifications(String username){
		ServerNotificationsResult theResult = new ServerNotificationsResult();
		ResultSet result = null;

		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_NOTIFICATIONS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, username);
			result = statement.executeQuery();

			boolean gotResult = false;

			while (result.next()){
				gotResult = true;
				theResult.isAvailable = true;
				theResult.didSucceed = true;
				theResult.date.add(result.getDate("date").toString());
				theResult.time.add(result.getTime("date").toString());
				theResult.message.add(result.getString("message"));
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
