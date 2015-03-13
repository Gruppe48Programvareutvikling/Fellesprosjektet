package pipeEditEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import pipeCreateEvent.ServerCreateEvent;
import dataStructures.Event;
import dataStructures.Notification;
import dataStructures.User;
import serverReturnTypes.ServerEventsResult;
import serverReturnTypes.ServerGetCalendarsResult;
import serverReturnTypes.ServerRoomResult;
import superClasses.ServerEvents;
import superClasses.ServerResult;

public class ServerEditEvent extends ServerEvents {
	private final String SQL_UPDATE_NAME = "UPDATE Event SET name =? WHERE eventId =?";
	private final String SQL_UPDATE_DESCRIPTION = "UPDATE Event SET description =? WHERE eventId =?";
	private final String SQL_GET_LIST_OF_EVENTS = "Select * FROM Event WHERE userName =? AND privateCalendarName =?";
	private Event eventConstructor = new Event ();
	public ServerEventsResult getListOfEvents(String userName){
		ServerEventsResult theResult = new ServerEventsResult();
		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_LIST_OF_EVENTS, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
				){
				statement.setString(1, userName);
				statement.setString(2, userName + "'s Calendar");
				result = statement.executeQuery();
				boolean gotResult = false;
				this.eventConstructor.creator = userName;
				while(result.next()){
					gotResult = true;
					theResult.didSucceed = true;
					this.eventConstructor.eventId = result.getInt(1);
					this.eventConstructor.name = result.getString(2);
					this.eventConstructor.description = result.getString(3);
					this.eventConstructor.startDate = result.getTimestamp(4);
					this.eventConstructor.startDate.setYear(this.eventConstructor.startDate.getYear()+1900);
					this.eventConstructor.startDate.setMonth((this.eventConstructor.startDate.getMonth()+1));
					this.eventConstructor.endDate = result.getTimestamp(5);
					this.eventConstructor.endDate.setYear(this.eventConstructor.endDate.getYear()+1900);
					this.eventConstructor.endDate.setMonth((this.eventConstructor.endDate.getMonth()+1));
					this.eventConstructor.privateCalendarName = result.getString(6);
					this.eventConstructor.groupCalendarName = result.getString(7);
					this.eventConstructor.location = result.getString(8);
					this.eventConstructor.roomNumber = result.getInt(10);
					theResult.events.add(eventConstructor);
					
					
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
	
	public ServerResult editName(String name, int eventId){
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
					result.errorMessage = "Unknown error while changing new name";
					
				}
		}catch (SQLException e) {
			// TODO: handle exception
		ServerCreateEvent.processSQLException(e);
		result.didSucceed = false;
		result.errorMessage = e.getMessage();
		}
		
		return result;
	}
	public ServerResult editDescription(String description, int eventId){
		ServerResult result = new ServerResult();
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_DESCRIPTION, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
				){
				statement.setString(1, description);
				statement.setInt(2,eventId);
				int affected = statement.executeUpdate();
				if (affected == 1){
					result.didSucceed = true;
				}else{
					result.didSucceed = false;
					result.errorMessage = "Unknown error while changing new description";
					
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
