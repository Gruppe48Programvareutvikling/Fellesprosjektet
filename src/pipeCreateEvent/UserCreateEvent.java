package pipeCreateEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hamcrest.core.SubstringMatcher;
import org.mockito.cglib.transform.impl.AddDelegateTransformer;

import javafx.scene.web.PromptData;
import dataStructures.Event;
import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerFindGroupResult;
import serverReturnTypes.ServerFindUserResult;
import serverReturnTypes.ServerGetCalendarsResult;
import serverReturnTypes.ServerRoomResult;
import superClasses.ServerResult;
import superClasses.SuperUser;

public class UserCreateEvent extends SuperUser {
	private enum State{ENTER_NAME, ENTER_DESCRIPTION, ENTER_STARTDATE, ENTER_ENDDATE, ENTER_SCLOCK, ENTER_ECLOCK, ENTER_GROUP_NAME,
		ENTER_PRIVATE_CALENDAR_NAME, ENTER_GROUP_CALENDAR_NAME, ENTER_LOCATION, ENTER_Participants, ADD_MORE, ENTER_ROOM_NUMBER, ADD_TO_DATABASE}
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
					}catch(Exception e){
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
					if(compareDates(this.eventConstructor.startDate, test) >= 0){
						
						this.eventConstructor.endDate.setYear(year-1900);
						
						if (month >=1 && month <=12){
							this.eventConstructor.endDate.setMonth(month);
						
							Calendar cal = new GregorianCalendar(this.eventConstructor.endDate.getYear(), month, 1);
							daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
							if (day <= daysInMonth && day >0 ){
								this.eventConstructor.endDate.setDate(day);
								this.state = State.ENTER_ECLOCK;
								this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the endtime of the day hh.mm");
					
						
							}
						}
						else{
							this.delegator.delegateIsReadyForNextInputWithPrompt("Date does not exist");
						
						}
					}else{
						this.delegator.delegateIsReadyForNextInputWithPrompt("Date is before startdate");
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
							if (compareDates(test,this.eventConstructor.startDate)==0 && isTimeAfter(test, this.eventConstructor.startDate)){
							
								this.eventConstructor.endDate.setHours(hour);
								this.eventConstructor.endDate.setMinutes(min);
								this.state = State.ENTER_LOCATION;
								
								
								this.delegator.delegateIsReadyForNextInputWithPrompt("Write the location of your event");
							}
							else{
								this.delegator.delegateIsReadyForNextInputWithPrompt("Time is before the starttime");
							}
						}
					}catch(NumberFormatException e){
							this.delegator.delegateIsReadyForNextInputWithPrompt("Please use numbers hh.mm");
					}
							break;
		case ENTER_LOCATION:
			if (nextInput.length()<=45){
				this.eventConstructor.location = nextInput;
				this.state = State.ENTER_GROUP_NAME;
				ServerFindGroupResult result = this.server.getListOfGroupsTheUserIsPartOf(User.currentUser().username);
				System.out.println(result.groupName); //printe ut, har ikke laget toString
				this.delegator.delegateIsReadyForNextInputWithPrompt("Choose group you want to invite");
				
				
			}else {
				this.delegator.delegateIsReadyForNextInputWithPrompt("The name of your location was too long, please try again");
			}
			break;
		case ENTER_GROUP_NAME:
			if (nextInput.length()<=45){
				if(nextInput != null){
					if(this.server.getListOfGroupsTheUserIsPartOf(User.currentUser().username).groupName.contains(nextInput)){
						ServerFindGroupResult result = this.server.getGroupUsers(nextInput);
						addGroupUsers(result.groupUsers);
						ServerGetCalendarsResult result2 = this.server.getGroupCalendarName(nextInput);
						this.eventConstructor.groupCalendarName = result2.calendarnames;
					}
					
					this.state = State.ENTER_Participants;
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the username of additional the participant");
				}
			}else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The name of the group was too long, try again");
			}
			
			break;
		case ENTER_Participants:
			if (nextInput.length() <= 45){
					//skjekk med server
					ServerFindUserResult result = this.server.findUser(nextInput);
					if (result.userExists == true){
						if (!(existsInList(this.eventConstructor.participants, nextInput))){ //skjekk om bruker allerede er i lista
					
							this.eventConstructor.participants.add(new User(nextInput));
							this.state = State.ADD_MORE;
							this.delegator.delegateIsReadyForNextInputWithPrompt("Do you want to add additional participants Y/N?");
						}
						this.state = state.ADD_MORE;
						this.delegator.delegateIsReadyForNextInputWithPrompt("This user is already invited, want to try again? Y/N");
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
			if (nextInput.equals("Y") || nextInput.equals("y")){
				this.state = State.ENTER_Participants;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the username of the participant");
			}
			if (nextInput.equals("N") || nextInput.equals("n")){
				this.state = State.ENTER_ROOM_NUMBER;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the number of desired seats");
			}else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write Y/N");
			}
			break;
		case ENTER_ROOM_NUMBER:
			if (nextInput.length() <= 11)
				try{
					ServerRoomResult result = this.server.findRoomResult(nextInput);
					if (result.roomIsAvailable){
						
						this.eventConstructor.room.roomNumber = result.roomnumber;
						this.eventConstructor.room.numberOfSeats = Integer.parseInt(nextInput);
						ServerResult theResult = this.server.createEvent(this.eventConstructor);
						this.delegator.delegateIsDone("Event created");
					}
					//int numberOfSeats = Integer.parseInt(nextInput);
//					ServerRoomResult result = this.server			//TIME + Duration
				}catch (NumberFormatException e){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Must write a number");
				}
			
		}
		
	}
	
	
	public boolean isParticipantInvited(String string){
		return false;
	}
	public int compareDates(Date date1, Date date2){
		int year1 = date1.getYear()+1900;
		int month1 = date1.getMonth();
		int day1 = date1.getDate();
		int year2 = date2.getYear();
		int month2 = date2.getMonth();
		int day2 = date2.getDate();
		if(year1>year2){
			return 1;
		}
		if (year1==year2){
			if( year1 == year2 && month1 < month2){
				if (year1 == year2 && month1 == month2 && day1<day2){
				
				
					if(year1 == year2 && month1 == month2 && day1 == day2){
						return 0;
					}
				return 1;
				
				}
			return 1;
			}
		return 0;
		}
		return -1;
	}
	public boolean isTimeAfter(Date date1, Date date2){
		if(date1.getHours()>date2.getHours()){
			return true;
		}
		if(date1.getHours() == date2.getHours()){
			if (date1.getMinutes()> date2.getMinutes()){
				return true;
			}
			
		}
		return false;
	}
	public void addGroupUsers(Collection<String> groupUsers){
		for(String elem: groupUsers){
			this.eventConstructor.participants.add(new User(elem));
			
		}
	}
	public boolean existsInList(List<User> list, String userName){
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).username.equals(userName)){
				return true;
			}
		}
		return false;
	}
	public void userAsksForHelp() {
		
	}

}
