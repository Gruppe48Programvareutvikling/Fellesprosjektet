package pipeCreateEvent;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.hamcrest.core.SubstringMatcher;

import javafx.scene.web.PromptData;
import dataStructures.Event;
import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerFindUserResult;
import serverReturnTypes.ServerGetCalendarsResult;
import serverReturnTypes.ServerRoomResult;
import superClasses.ServerResult;
import superClasses.SuperUser;

public class UserCreateEvent extends SuperUser {
	private enum State{ENTER_NAME, ENTER_DESCRIPTION, ENTER_STARTDATE, ENTER_DAY, ENTER_MONTH, ENTER_YEAR, ENTER_CLOCK, ENTER_CALENDAR,
		ENTER_PRIVATE_CALENDAR_NAME, ENTER_GROUP_CALENDAR_NAME, ENTER_LOCATION, ENTER_Participants, ADD_MORE, ENTER_ROOM_NUMBER}
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
			
		case ENTER_STARTDATE: //kommer inn som dd/mm/yyyy
			int counter = 0;
			for( int i=0; i<nextInput.length(); i++ ) {
			    if( nextInput.charAt(i) == '/' ) {
			        counter++;
			    } 
			}
			if (counter !=2){
				this.delegator.delegateIsReadyForNextInputWithPrompt("Wrong format. Please try again. dd/mm/yyyy");
			}
			String[] date = nextInput.split("/");
			String sDay = date[0];
			String sMonth = date[1];
			String sYear = date[2];
		
			
			try{
				int day = Integer.parseInt(sDay);
				int month = Integer.parseInt(sMonth);
				int year = Integer.parseInt(sYear);
				if (year>0){
					this.eventConstructor.startDate.setYear(year-1900);
				}
				if (month >=1 && month <=12){
					this.eventConstructor.startDate.setMonth(month);
				
					Calendar cal = new GregorianCalendar(this.eventConstructor.startDate.getYear(), month, 1);
					daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					if (day <= daysInMonth && day >0 ){
						this.eventConstructor.startDate.setDate(day);
						this.state = State.ENTER_CLOCK;
						this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the time of the day");
			
						break;
					}
				}
				else{this.delegator.delegateIsReadyForNextInputWithPrompt("Date does not exist");
				
				}
				}catch (NumberFormatException e){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please use numbers");
			}
			
		case ENTER_CLOCK:
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
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the username of the participant");
			}else {
				this.delegator.delegateIsReadyForNextInputWithPrompt("The name of your location was too long, please try again");
			}
			break;
		case ENTER_Participants:
			if (nextInput.length() <= 45){
					//skjekk med server
					ServerFindUserResult result = this.server.findUser(nextInput);
					if (result.userExists == true){
						this.eventConstructor.participants.add(new User(nextInput));
						this.state = State.ADD_MORE;
						this.delegator.delegateIsReadyForNextInputWithPrompt("Do you want to add additional participants Y/N?");
					
					}else{
						this.state = State.ADD_MORE;
						this.delegator.delegateIsReadyForNextInputWithPrompt("This user does not exist, want to try again Y/N?");
				
					}
			}else{
				this.state = State.ADD_MORE;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Username was too long, want to try again Y/N?");
		
			}
			break;
		case ADD_MORE:
			if (nextInput == "Y" || nextInput == "y"){
				this.state = State.ENTER_Participants;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the username of the participant");
			}
			if (nextInput == "N" || nextInput == "n"){
				this.state = State.ENTER_ROOM_NUMBER;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the number of desired seats");
			}else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write Y/N");
			}
			break;
		case ENTER_ROOM_NUMBER:
			if (nextInput.length() <= 11)
				try{
					int numberOfSeats = Integer.parseInt(nextInput);
					ServerRoomResult result = this.server.				//TIME + Duration
				}catch (NumberFormatException e){
					
				}
			
		}
		
	}
	
	public void userAsksForHelp() {
		
	}
}
