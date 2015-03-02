package pipeCreateEvent;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
	private enum State{ENTER_NAME, ENTER_DESCRIPTION, ENTER_STARTDATE, ENTER_ENDDATE, ENTER_SCLOCK, ENTER_ECLOCK, ENTER_CALENDAR,
		ENTER_PRIVATE_CALENDAR_NAME, ENTER_GROUP_CALENDAR_NAME, ENTER_LOCATION, ENTER_Participants, ADD_MORE, ENTER_ROOM_NUMBER}
	State state = State.ENTER_NAME;
	private Event eventConstructor = new Event();
	private ServerCreateEvent server = new ServerCreateEvent();
	private int daysInMonth = 0;
	public UserCreateEvent(ControllerInterface delegator) {
		this.delegator = delegator;
		
//		
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
				this.state = State.ENTER_STARTDATE;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write date for your event dd/mm/yyyy");
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
					this.eventConstructor.startDate.setYear(year);
				}
				if (month >=1 && month <=12){
					this.eventConstructor.startDate.setMonth(month);
				
					Calendar cal = new GregorianCalendar(this.eventConstructor.startDate.getYear(), month, 1);
					daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					if (day <= daysInMonth && day >0 ){
						this.eventConstructor.startDate.setDate(day);
						this.state = State.ENTER_SCLOCK;
						this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the time of the day hh.mm");
			
						
					}
				}
				else{this.delegator.delegateIsReadyForNextInputWithPrompt("Date does not exist");
				
				}
				}catch (NumberFormatException e){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please use numbers");
			}
			break;
			
		case ENTER_SCLOCK:
			String[] sTime = nextInput.split("\\.");
			
				
					try{
						int	hour = Integer.parseInt(sTime[0]);
						int min = Integer.parseInt(sTime[1]);
						if(hour >= 0 && hour <=23 && min >= 0 && min <=59){
							
							this.eventConstructor.startDate.setHours(hour);
							this.eventConstructor.startDate.setMinutes(min);
							this.state = State.ENTER_ENDDATE;
							this.delegator.delegateIsReadyForNextInputWithPrompt("Write the enddate of the event");
					
							
						}
					}catch(NumberFormatException e){
						this.delegator.delegateIsReadyForNextInputWithPrompt("Please write it as hh.mm");
					}	
			break;
		case ENTER_ENDDATE: //kommer inn som dd/mm/yyyy
			counter = 0;
			for( int i=0; i<nextInput.length(); i++ ) {
			    if( nextInput.charAt(i) == '/' ) {
			        counter++;
			    } 
			}
			if (counter !=2){
				this.delegator.delegateIsReadyForNextInputWithPrompt("Wrong format. Please try again. dd/mm/yyyy");
				break;
			}
			String[] eDate = nextInput.split("/");
			String sEDay = eDate[0];
			String sEMonth = eDate[1];
			String sEYear = eDate[2];
		
			
			try{
				int day = Integer.parseInt(sEDay);
				int month = Integer.parseInt(sEMonth);
				int year = Integer.parseInt(sEYear);
				Date test = new Date();
				test.setDate(day);
				test.setMonth(month);
				test.setYear(year);
					if(test.after(this.eventConstructor.startDate)){
						if (year>0){
							this.eventConstructor.endDate.setYear(year-1900);
						}
						if (month >=1 && month <=12){
							this.eventConstructor.endDate.setMonth(month);
						
							Calendar cal = new GregorianCalendar(this.eventConstructor.endDate.getYear(), month, 1);
							daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
							if (day <= daysInMonth && day >0 ){
								this.eventConstructor.endDate.setDate(day);
								this.state = State.ENTER_ECLOCK;
								this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the time of the day hh.mm");
					
						
							}
						}
						else{this.delegator.delegateIsReadyForNextInputWithPrompt("Date does not exist");
						
						}
					}	
				}catch (NumberFormatException e){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please use numbers");
					
			}
			break;
		case ENTER_ECLOCK:
			String[] sETime = nextInput.split("\\.");
			
				
					try{
						int	hour = Integer.parseInt(sETime[0]);
						int min = Integer.parseInt(sETime[1]);
						if(hour >= 0 && hour <=23 && min >= 0 && min <=59){
							Date test = this.eventConstructor.endDate;
							test.setHours(hour);
							test.setMinutes(min);
							if (test.after(this.eventConstructor.startDate)){
							
								this.eventConstructor.endDate.setHours(hour);
								this.eventConstructor.endDate.setMinutes(min);
								this.state = State.ENTER_CALENDAR;
								
								ServerGetCalendarsResult result = this.server.getListOfGroupsTheUserIsPartOf(User.currentUser().username);
								System.out.println(result.calendarnames); //printe ut
								this.delegator.delegateIsReadyForNextInputWithPrompt("Choose the calendar you want to put the event in");
							}
							else{
								this.delegator.delegateIsReadyForNextInputWithPrompt("Time is before the starttime");
							}
						}
					}catch(NumberFormatException e){
							this.delegator.delegateIsReadyForNextInputWithPrompt("Please use numbers hh.mm");
					}
							
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
//					ServerRoomResult result = this.server			//TIME + Duration
				}catch (NumberFormatException e){
					
				}
			
		}
		
	}
	
	public void userAsksForHelp() {
		
	}

}
