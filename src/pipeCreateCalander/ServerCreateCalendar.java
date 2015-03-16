package pipeCreateCalander;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import pipeCreateGroup.ServerCreateGroup;
import dataStructures.Calendar;
import serverReturnTypes.ServerAvailabilityResult;
import serverReturnTypes.ServerCalendarResult;
import superClasses.ServerManager;

public class ServerCreateCalendar extends ServerManager{
	
	public final String SQL_CREATE_PRIVATE_CALENDAR = "Insert into PrivateCalendar (privateCalendarName, userName) values (?,?)";
	public final String SQL_CREATE_GROUP_CALENDAR = "Insert into GroupCalendar (groupCalendarName, groupName) values (?,?)";
	public final String SQL_CHECK_IF_PRIVATENAME_IS_AVAILABLE = "Select privateCalendarName from PrivateCalendar where privateCalendarName = ?";
	public final String SQL_CHECK_IF_GROUPNAME_IS_AVAILABLE = "Select groupCalendarName from GroupCalendar where groupCalendarName = ?";
	public final String SQL_CHECK_IF_GROUP_EXIST = "Select groupName from `Group` where groupName = ?";
	
	public ServerAvailabilityResult checkIfPrivateCalendarNameIsAvailable(String name){
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CHECK_IF_PRIVATENAME_IS_AVAILABLE, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			)
			{
			statement.setString(1, name);
			result = statement.executeQuery();

			boolean gotResult = false;

			while (result.next()){
				gotResult = true;
				theResult.isAvailable = false;
				theResult.didSucceed = true;
			}

			if (gotResult == false){
				theResult.isAvailable = true;
				theResult.didSucceed = true;
			}

				}catch (SQLException e){
					System.out.println("Got exception in checkIfGroupNameIsAvailable");
					ServerCreateGroup.processSQLException(e);
					theResult.isAvailable = false;
					theResult.didSucceed = false;
					theResult.errorMessage = e.getMessage();
				}
		return theResult;
	}
	
	public ServerAvailabilityResult checkIfGroupCalendarNameIsAvailable(String name){
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CHECK_IF_GROUPNAME_IS_AVAILABLE, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, name);
			result = statement.executeQuery();
			
			boolean gotResult = false;
			
			while (result.next()){
				gotResult = true;
				theResult.isAvailable = false;
				theResult.didSucceed = true;
			}
			
			if (gotResult == false){
				theResult.isAvailable = true;
				theResult.didSucceed = true;
			}
			
				}catch (SQLException e){
					System.out.println("Got exception in checkIfGroupNameIsAvailable");
					ServerCreateGroup.processSQLException(e);
					theResult.isAvailable = false;
					theResult.didSucceed = false;
					theResult.errorMessage = e.getMessage();
				}
		return theResult;
	}
	
	public ServerAvailabilityResult checkIfGroupExist(String groupToCheck){
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CHECK_IF_GROUP_EXIST, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, groupToCheck);
			result = statement.executeQuery();

			boolean gotResult = false;

			while (result.next()){
				gotResult = true;
				theResult.isAvailable = true;
				theResult.didSucceed = true;
			}

			if (gotResult == false){
				theResult.isAvailable = false;
				theResult.didSucceed = true;
			}

				}catch (SQLException e){
					System.out.println("Got exception in checkIfGroupNameIsAvailable");
					ServerCreateGroup.processSQLException(e);
					theResult.isAvailable = false;
					theResult.didSucceed = false;
					theResult.errorMessage = e.getMessage();
				}
		return theResult;
	}
	
	public ServerCalendarResult createPrivateCalendar(Calendar calendar){
		ServerCalendarResult theResult = new ServerCalendarResult();
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_PRIVATE_CALENDAR, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			) 
		{
			statement.setString(1, calendar.name);
			statement.setString(2, calendar.username);
			int affected = statement.executeUpdate();
			
			if (affected == 1) {
				theResult.didSucceed = true;
			} else {
				theResult.didSucceed = false;
				theResult.errorMessage = "Unknown error while creating private calendar";
			}
		} catch (SQLException e) {
			ServerCreateGroup.processSQLException(e);
			System.out.println("Got exception in createPrivateCalendar()");
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		
		return theResult;			
	}
	
	public ServerCalendarResult createGroupCalendar(Calendar calendar){
		ServerCalendarResult theResult = new ServerCalendarResult();
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_GROUP_CALENDAR, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				) 
				{
			statement.setString(1, calendar.name);
			statement.setString(2, calendar.groupname);
			int affected = statement.executeUpdate();
			
			if (affected == 1) {
				theResult.didSucceed = true;
			} else {
				theResult.didSucceed = false;
				theResult.errorMessage = "Unknown error while creating group calendar";
			}
				} catch (SQLException e) {
					ServerCreateGroup.processSQLException(e);
					System.out.println("Got exception in createGroupCalendar()");
					theResult.didSucceed = false;
					theResult.errorMessage = e.getMessage();
				}
		
		return theResult;			
	}

}
