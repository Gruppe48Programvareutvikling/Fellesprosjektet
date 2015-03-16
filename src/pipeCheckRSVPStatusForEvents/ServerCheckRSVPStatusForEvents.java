package pipeCheckRSVPStatusForEvents;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;

import dataStructures.EventRSVPStatus;
import dataStructures.EventRSVPStatus.Status;
import dataStructures.User;
import dataStructures.Event;

import serverReturnTypes.ServerEventsResult;
import serverReturnTypes.ServerRSVPStatusesResult;

import superClasses.ServerEvents;
import superClasses.ServerManager;

public class ServerCheckRSVPStatusForEvents extends ServerEvents {
	
	private final String SQL_GET_LIST_OF_EVENTS = "SELECT * FROM Event e WHERE e.startDate > ? AND e.userName = ?";
	private final String SQL_GET_RSVP_STATUSES  = "SELECT * FROM InvitesToEvent WHERE eventId = ?";
	
	public ServerEventsResult getListOfEventsForCurrentUser() {
		ServerEventsResult theResult = new ServerEventsResult();
		ResultSet result = null;
		
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_LIST_OF_EVENTS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			)
		{
			Date date = new Date();
			Timestamp timeStamp = new java.sql.Timestamp(date.getTime());
			statement.setTimestamp(1, timeStamp);
			statement.setString(2, User.currentUser().username);
			
			result = statement.executeQuery();

			ArrayList<Event> events = new ArrayList<Event>();
			
			while (result.next()) {
				Event someEvent = new Event();
				
				someEvent.eventId = result.getInt("eventId");
				someEvent.name = result.getString("name");
				someEvent.description = result.getString("description");
				someEvent.startDate = result.getTimestamp("startDate");
				someEvent.endDate = result.getTimestamp("endDate");
				//someEvent.privateCalendarName = result.getString("privateCalendarName");
				someEvent.groupCalendarName = result.getString("groupCalendarName");
				someEvent.location = result.getString("location");
				someEvent.creator = result.getString("userName");
				someEvent.roomNumber = result.getInt("roomNumber");
				
				events.add(someEvent);
			}
			
			// If there were no events, theResult.events will be an empty array.
			theResult.events = events;
			theResult.didSucceed = true;
			theResult.errorMessage = null;
			
		} catch (SQLException e) {
			System.out.println("Got exception");
			ServerManager.processSQLException(e);
			theResult.events = null;
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		
		return theResult;
	}
	
	public ServerRSVPStatusesResult getRSVPsForEvent(Event event) {
		ServerRSVPStatusesResult theResult = new ServerRSVPStatusesResult();
		ResultSet result = null;

		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_RSVP_STATUSES, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			)
		{
			statement.setInt(1, event.eventId);
			result = statement.executeQuery();

			ArrayList<EventRSVPStatus> rsvps = new ArrayList<EventRSVPStatus>();
			
			while (result.next()) {
				EventRSVPStatus rsvp = new EventRSVPStatus();
				
				rsvp.event = event;
				rsvp.userName = result.getString("userName");
				String statusString = result.getString("status");
				if (statusString != null) {
					rsvp.status = Status.valueOf(statusString);
				} else {
					rsvp.status = Status.NOT_YET_RESPONDED;
				}
				
				rsvps.add(rsvp);
			}
			
			theResult.rsvpStatuses = rsvps;
			theResult.didSucceed = true;
			theResult.errorMessage = null;

		} catch (SQLException e) {
			System.out.println("Got exception");
			ServerManager.processSQLException(e);
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		
		return theResult;
	}

}
