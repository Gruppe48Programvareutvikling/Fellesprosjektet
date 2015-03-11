package pipeEditEvent;


import dataStructures.Event;
import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerGetCalendarsResult;
import superClasses.SuperUser;

public class UserEditEvent extends SuperUser {
	private enum State{ENTER_OPTION, ENTER_NAME, ENTER_DESCRIPTION, ENTER_STARTDATE, ENTER_ENDDATE, ENTER_SCLOCK, ENTER_ECLOCK, ENTER_GROUP_NAME,
		ENTER_PRIVATE_CALENDAR_NAME, ENTER_GROUP_CALENDAR_NAME, ENTER_LOCATION, ENTER_Participants, ADD_MORE, ENTER_NUMBER_OF_SEATS, 
		ENTER_ROOM_NUMBER, ENTER_MORE}
	State state = State.ENTER_OPTION;
	public Event eventConstructor = new Event();
	public ServerEditEvent server = new ServerEditEvent();
	public UserEditEvent(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserEditEvent class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserEditEvent is done");
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("What do you want to edit?");
	}
	
	public void sendNextInput(String nextInput) {
		switch (this.state){
		case ENTER_OPTION:
			//vise alle events
			
			switch (nextInput){
			case "name":
				this.state = State.ENTER_NAME;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new name");
				break;
			case "description":
				this.state = State.ENTER_DESCRIPTION;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new description");
				break;
			case "startdate":
				this.state = State.ENTER_STARTDATE;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new startdate dd/mm/yyyy");
				break;
			case "enddate":
				this.state = State.ENTER_ENDDATE;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new enddate dd/mm/yyyy");
				break;
			case "startclock":
				this.state = State.ENTER_SCLOCK;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new start time hh/mm");
				break;
			case "endclock":
				this.state = State.ENTER_ENDDATE;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new end time hh/mm");
				break;
			case "participant":
				this.state = State.ENTER_Participants;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter the username of the additional participant"); //være med?
			case "group":
				this.state = State.ENTER_GROUP_NAME;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new groupname");
			case "location":
				this.state = State.ENTER_LOCATION;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new location");
			case "room":
				this.state = State.ENTER_NUMBER_OF_SEATS;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter desired number of seats");
			}
		case ENTER_NAME:
			if (nextInput.length() <= 45){ 
				this.eventConstructor.name = nextInput;
				
				this.state = State.ENTER_MORE;
				
				
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write a description of you event (maximum of 200 characters)");
		
		}
	}
	
	public void userAsksForHelp() {
		
	}
}
