package pipeCreateEvent;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javafx.scene.web.PromptData;
import dataStructures.Event;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerGetCalendarsResult;
import superClasses.ServerResult;
import superClasses.SuperUser;

public class UserCreateEvent extends SuperUser {
	private enum State{ENTER_NAME, ENTER_DESCRIPTION, ENTER_DAY, ENTER_MONTH, ENTER_YEAR, ENTER_DURATION, ENTER_CALENDAR,
		ENTER_PRIVATE_CALENDAR_NAME, ENTER_GROUP_CALENDAR_NAME, ENTER_LOCATION, ENTER_Participants, ENTER_ROOM_NUMBER}
	State state = State.ENTER_NAME;
	private Event eventConstructor = new Event();
	private ServerCreateEvent server = new ServerCreateEvent();
	private int daysInMonth = 0;
	public UserCreateEvent(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserCreateEvent class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserCreateEvent is done");
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("What is the name of your event?");
	}
	
	public void sendNextInput(String nextInput) {
		switch (this.state){
		case ENTER_NAME:
			if (nextInput.length() <= 45){ 
				this.eventConstructor.name = nextInput;
				this.state = State.ENTER_DESCRIPTION;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write a description of you event (maximum of 200 characters)");
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The name of your event was too long. Please write a shorter name");
			}
			break;
		case ENTER_DESCRIPTION:
			if (nextInput.length() <=200){
				this.eventConstructor.description = nextInput;
				this.state = State.ENTER_YEAR;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the year for your event");
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The description was too long. Please write a shorter name");
			}
			break;
		case ENTER_YEAR:
			try{int year = Integer.parseInt(nextInput);
			
			
				if (year>0){
					this.eventConstructor.date.setYear(year-1900);
					this.state = State.ENTER_MONTH;
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the month  (1-12)");
			
				}
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write a plausible year");
				}catch(NumberFormatException e){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the year with digits");
				}
			break;
		case ENTER_MONTH:
			try{
				int month = Integer.parseInt(nextInput);
				if (month >=1 && month <=12){
					this.eventConstructor.date.setMonth(month);
					this.state = State.ENTER_DAY;
					Calendar cal = new GregorianCalendar(this.eventConstructor.date.getYear(), month, 1);
					daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the day of your event (1-" + daysInMonth + ")");
				
				}else{
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the correct month (mm)");
				}
			}catch(NumberFormatException e){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the month with 2 digits");
			}
			break;
		case ENTER_DAY:
			try{
				int day = Integer.parseInt(nextInput);
				
				if (day>=1 && day <= daysInMonth){
					this.eventConstructor.date.setDate(day);
					this.state = State.ENTER_DURATION;
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the duration of the event in min");
				}else{
					this.delegator.delegateIsReadyForNextInputWithPrompt("This date does not exist. Please try again");
				}
			}catch(NumberFormatException e){
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the day as a number");
			}
			break;
			
		case ENTER_DURATION:
			if (nextInput.length()<= 11 ){
				try{
					double duration = Double.parseDouble(nextInput);
					this.eventConstructor.duration = duration;
					this.state = State.ENTER_CALENDAR;
					
					ServerGetCalendarsResult result = server.getListOfGroupsTheUserIsPartOf();
					this.delegator.delegateIsReadyForNextInputWithPrompt("Choose the calendar you want to put the event in");
				}catch(NumberFormatException e){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the duration as a number"); //Må man skrive kommatall?
				}
				
				
			}else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The duration is too long. Please write a shorter duration");
			}
			break;
		case ENTER_CALENDAR:
			if (nextInput.length()<=45){
				//Har valgt kalender
				this.state = State.ENTER_LOCATION;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Write the location of you event");
			}else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The name of the calendar was too long, try again");
			}
			
			break;
		case ENTER_LOCATION:
			if (nextInput.length()<=45){
				this.eventConstructor.location = nextInput;
				this.state = State.ENTER_Participants;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the usernames of the participants");
			}else {
				this.delegator.delegateIsReadyForNextInputWithPrompt("The name of your location was too long, please try again");
			}
			break;
		case ENTER_Participants:
			if (nextInput.length() <= 45){
				if (nextInput == "y" || nextInput == "Y"){
					this.eventConstructor.participants.add(nextInput);
				
				}
			}
		}
		
	}
	
	public void userAsksForHelp() {
		
	}
}
