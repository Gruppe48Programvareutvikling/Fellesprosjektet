package pipeCheckRSVPStatusForEvents;

import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerRSVPStatusesResult;
import superClasses.SuperUser;

public class UserCheckRSVPStatusForEvents extends SuperUser {

	private enum State {SET_EVENT_TO_CHECK}
	
	private State state = State.SET_EVENT_TO_CHECK;
	private ServerCheckRSVPStatusForEvents server = new ServerCheckRSVPStatusForEvents();

	
	public UserCheckRSVPStatusForEvents(ControllerInterface delegator) {
		this.delegator = delegator;
		
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("Which event you want to see RSVP status for?");
	}
	
	public void sendNextInput(String nextInput) {
		switch(this.state){
		case SET_EVENT_TO_CHECK:
			ServerRSVPStatusesResult availability = this.server.checkIfEventExist(nextInput);
			if (!availability.didSucceed){
				this.delegator.delegateIsReadyForNextInputWithPrompt("There was an error with the message \"" + availability.errorMessage + "\"\nTry again");
			}
			if (availability.isAvailable){
				this.delegator.delegateIsDone(availability.toString());
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The eventId doesent exist. Try a another eventId");
			}
			break;
		}
	}
	
	public void userAsksForHelp() {
		switch (this.state) {
		case SET_EVENT_TO_CHECK:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type in an eventId you want to check the RSVP status for (can't be \"help\")");
			break;
		}
	}
}
