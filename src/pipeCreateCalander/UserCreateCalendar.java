package pipeCreateCalander;

import dataStructures.Calendar;
import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerResult;
import superClasses.SuperUser;

public class UserCreateCalendar extends SuperUser{

	private enum State {SET_CALENDAR_TYPE, SET_GROUP_FOR_CALENDAR, SET_NAME_FOR_CALENDAR}
	
	private State state = State.SET_CALENDAR_TYPE;
	private Calendar constructor = new Calendar();
	private ServerCreateCalendar server = new ServerCreateCalendar();
	
	public UserCreateCalendar(ControllerInterface delegator){
		this.delegator = delegator;
	}
	
	@Override
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("Do you want to create a private calander or group " 
				+ "calander? Type in \"group\" or \"private\"");
	}
	
	@Override
	public void sendNextInput(String nextInput) {
		switch (this.state){
		case SET_CALENDAR_TYPE:
			if (nextInput.equals("group")){
				this.state = State.SET_NAME_FOR_CALENDAR;
				this.constructor.isPrivate = false;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Type in the name for your group calendar");
			}
			else if (nextInput.equals("private")){
				this.constructor.isPrivate = true;
				this.constructor.username = User.currentUser().username;
				this.state = State.SET_NAME_FOR_CALENDAR;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Type in the name for your private calendar");
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write \"group\" or \"private\", you bastard");
			}
			break;
		case SET_NAME_FOR_CALENDAR:
			if (constructor.isPrivate){
				ServerAvailabilityResult availability = this.server.checkIfPrivateCalendarNameIsAvailable(nextInput);
				if (availability.isAvailable){
					this.constructor.name = nextInput;
					ServerResult result = this.server.createPrivateCalendar(constructor);
					if (result.didSucceed){
						this.delegator.delegateIsDone("You have successfully created a private calendar");
					}
					else{
						this.delegator.delegateIsDone("There was an error creating the private calendar with the message " + result.errorMessage);
					}
				}
				else{
					if (availability.didSucceed) {
						this.delegator.delegateIsReadyForNextInputWithPrompt("The calendar name is already taken, try a new one");
					} else {
						this.delegator.delegateIsReadyForNextInputWithPrompt("There was an error with the message \"" + availability.errorMessage + "\"\nTry again");
					}
				}
			}
			else{
				ServerAvailabilityResult availability = this.server.checkIfGroupCalendarNameIsAvailable(nextInput);
				this.state = State.SET_GROUP_FOR_CALENDAR;
				if (availability.isAvailable){
					this.constructor.name = nextInput;
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please type in the name of the group you want to connect the calendar with");
				}
				else{
					if (availability.didSucceed) {
						this.delegator.delegateIsReadyForNextInputWithPrompt("The calendar name is already taken, try a new one");
					} else {
						this.delegator.delegateIsReadyForNextInputWithPrompt("There was an error with the message \"" + availability.errorMessage + "\"\nTry again");
					}
				}
			}
			break;
		case SET_GROUP_FOR_CALENDAR:
			ServerAvailabilityResult groupexist = this.server.checkIfGroupExist(nextInput);
			if (groupexist.isAvailable){
				this.constructor.groupname = nextInput;
				ServerResult result = this.server.createGroupCalendar(constructor);
				if (result.didSucceed){
					this.delegator.delegateIsDone("You have successfully created a group calendar");
				}
				else{
					this.delegator.delegateIsDone("There was an error creating the group calendar with the message " + result.errorMessage);
				}
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The groupname doesent exist, try a new one");
			}
			break;
		}
	}

	@Override
	public void userAsksForHelp() {
		switch(this.state){
		case SET_CALENDAR_TYPE:
			this.delegator.delegateIsReadyForNextInputWithPrompt("Please write \"group\" or \"private\", you bastard");
		break;
		case SET_GROUP_FOR_CALENDAR:
			this.delegator.delegateIsReadyForNextInputWithPrompt("Please type in the name of the group " 
					+ "that you want your group calendar to be connected with");
		break;
		case SET_NAME_FOR_CALENDAR:
			this.delegator.delegateIsReadyForNextInputWithPrompt("Type in the name for your calendar");
		break;
		}
	}

}
