package pipeCheckRSVPStatusForEvents;

import java.util.ArrayList;
import java.util.List;

import dataStructures.Event;
import dataStructures.EventRSVPStatus;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerEventsResult;
import serverReturnTypes.ServerRSVPStatusesResult;
import superClasses.SuperUser;

public class UserCheckRSVPStatusForEvents extends SuperUser {

	public static final String PROMPT_FETCHING_INVITATIONS = "Fetching invitations...";
	public static final String PROMPT_PICK_EVENT 		   = "Which event you want to see the RSVP status for";
	public static final String DONE_NO_EVENTS 			   = "You currently have to created any events that will happen in the future.";
	public static final String ERROR_INVALID_NUMBER		   = "That is not a valid ID. Try again!\n" + UserCheckRSVPStatusForEvents.PROMPT_PICK_EVENT;
	public static final String ERROR_ID_NOT_FOUND          = "That is not one of the IDs from the list. Try again!\n" + UserCheckRSVPStatusForEvents.PROMPT_PICK_EVENT;
	
	private enum State {GET_LIST_OF_EVENTS, GET_LIST_OF_RSVPS}
	
	private State state = State.GET_LIST_OF_EVENTS;
	private ServerCheckRSVPStatusForEvents server = new ServerCheckRSVPStatusForEvents();
	private List<Event> listOfEventsFound = new ArrayList<Event>();
	
	public UserCheckRSVPStatusForEvents(ControllerInterface delegator) {
		this.delegator = delegator;
	}
	
	public void startRunning() {
		this.delegator.delegateIsWaitingForServerWithMessage(UserCheckRSVPStatusForEvents.PROMPT_FETCHING_INVITATIONS);
		
		ServerEventsResult eventsResult = this.server.getListOfEventsForCurrentUser();
		
		if (eventsResult.events.size() > 0) {
			String eventsString = String.format("\n%-5s %-20s %-25s %-25s\n", "ID", "NAME", "START", "END");
		
			for (Event event: eventsResult.events) {
				eventsString += String.format("%-5s %-20s %-25s %-25s\n", event.eventId, event.name, event.startDate, event.endDate);
				this.listOfEventsFound.add(event);
			}
		
			eventsString += "\nEnter the ID of the event you want to see RSVP statuses for";
		
			this.state = State.GET_LIST_OF_RSVPS;
			
			this.delegator.delegateIsReadyForNextInputWithPrompt(eventsString);
		} else {
			this.delegator.delegateIsDone(UserCheckRSVPStatusForEvents.DONE_NO_EVENTS);
		}
	}
	
	public void sendNextInput(String nextInput) {
		
		switch (this.state) {
		case GET_LIST_OF_EVENTS: break;
		case GET_LIST_OF_RSVPS:
			try {
				Integer eventId = Integer.parseInt(nextInput);
				
				ServerRSVPStatusesResult rsvpResult = null;
				
				for (Event event: this.listOfEventsFound) {
					if (event.eventId == eventId) {
						rsvpResult = this.server.getRSVPsForEvent(event);
						break;
					}
				}
				
				if (rsvpResult != null) {
					String rsvpString = String.format("\n%-20s %-15s\n", "USER", "RESPONSE");
					
					for (EventRSVPStatus rsvp: rsvpResult.rsvpStatuses) {
						rsvpString += String.format("%-20s %-15s\n", rsvp.userName, EventRSVPStatus.stringForStatus(rsvp.status));
					}
					
					this.delegator.delegateIsDone(rsvpString);
				} else {
					this.delegator.delegateIsReadyForNextInputWithPrompt(UserCheckRSVPStatusForEvents.ERROR_ID_NOT_FOUND);
				}
				
			} catch (NumberFormatException e) {
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCheckRSVPStatusForEvents.ERROR_INVALID_NUMBER);
			}
			break;
		}

	}
	
	public void userAsksForHelp() {
		switch (this.state) {
		case GET_LIST_OF_RSVPS:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type in an eventId you want to check the RSVP status for (can't be \"help\")");
			break;
		default:
			this.delegator.delegateIsReadyForNextInputWithPrompt("Sorry, I can't help you with this");
			break;
		}
	}
}
