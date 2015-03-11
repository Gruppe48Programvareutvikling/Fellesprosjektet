package pipeEditEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import pipeCreateEvent.ServerCreateEvent;
import dataStructures.Event;
import dataStructures.Notification;
import serverReturnTypes.ServerEventsResult;
import serverReturnTypes.ServerGetCalendarsResult;
import serverReturnTypes.ServerRoomResult;
import superClasses.ServerEvents;
import superClasses.ServerResult;

public class ServerEditEvent extends ServerEvents {
	private final String SQL_UPDATE_NAME = "UPDATE Event SET name =? WHERE eventId =?";
	private final String SQL_GET_LIST_OF_EVENTS = "Select eventId, name, description, startDate, endDate, location, roomNumber FROM Event WHERE userName =?";
	
	public ServerEventsResult getListOfEvents(String userName){
		ServerEventsResult theResult = new ServerEventsResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_NAME, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
				){
				statement.setString(1, userName);
				
				result = statement.executeQuery();
				boolean gotResult = false;
				while(result.next()){
					gotResult = true;
					theResult.didSucceed = true;
					theResult.eventId = result.getInt(1);
					theResult.name = result.getString(2);
					theResult.description = result.getString(3)
					theResult.startDate = result.getDate(4);
					theResult.endDate = result.getDate(5);
					theResult.location = result.getString(6);
					theResult.roomNumber = result.getInt(7);
					
					
				}
				if (gotResult == false){
					theResult.didSucceed = true;
					theResult.errorMessage = "You have not created an event yet";
				}
		}catch (SQLException e) {
			// TODO: handle exception
		ServerCreateEvent.processSQLException(e);
		theResult.didSucceed = false;
		theResult.errorMessage = e.getMessage();
		}
		
		return theResult;
	}
	
	public ServerResult createNotification(String name, int eventId){
		ServerResult result = new ServerResult();
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_NAME, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
				){
				statement.setString(1, name);
				statement.setInt(2,eventId);
				int affected = statement.executeUpdate();
				if (affected == 1){
					result.didSucceed = true;
				}else{
					result.didSucceed = false;
					result.errorMessage = "Unknown error while creating new name";
					
				}
		}catch (SQLException e) {
			// TODO: handle exception
		ServerCreateEvent.processSQLException(e);
		result.didSucceed = false;
		result.errorMessage = e.getMessage();
		}
		
		return result;
	}
	
	public ServerRoomResult findRoomResult(String numberOfSeats, Date startDate, Date endDate){
		ServerRoomResult theResult = new ServerRoomResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_FIND_ROOM, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
			){
				statement.setString(1,numberOfSeats);
				statement.setString(2, dateToString(startDate));
				statement.setString(3, dateToString(endDate));
				statement.setString(4, dateToString(startDate));
				statement.setString(5, dateToString(endDate));
				result = statement.executeQuery();
				
				boolean gotResult = false;
				
				while(result.next() && !(theResult.roomNumber.contains(result.getInt(1)))){
					gotResult = true;
					theResult.roomNumber.add(result.getInt(1));
					theResult.numberOfSeats.add(result.getInt(2));
					theResult.roomIsAvailable = true;
					theResult.didSucceed = true;
				}
				if (gotResult == false){
					theResult.roomIsAvailable = false;
					theResult.didSucceed = true;
				}
			}catch (SQLException e) {
				
				theResult.roomIsAvailable = false;
				theResult.didSucceed = false;
				theResult.errorMessage = e.getMessage();
			}
		return theResult;
	}
	public String dateToString(Date date){
		String string = Integer.toString(date.getYear()) + "-"	+ Integer.toString(date.getMonth()) + "-" + Integer.toString(date.getDate()) + " " + 
				Integer.toString(date.getHours()) + ":" + Integer.toString(date.getMinutes());
		
		
		return string;
	}
}
