package pipeEditEvent;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import dataStructures.Event;
import dataStructures.Invitation;
import dataStructures.Notification;
import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerEventsResult;
import serverReturnTypes.ServerFindUserResult;
import serverReturnTypes.ServerGetCalendarsResult;
import serverReturnTypes.ServerRoomResult;
import superClasses.ServerResult;
import superClasses.SuperUser;

public class UserEditEvent extends SuperUser {
	private enum State{ENTER_EVENTID,ENTER_OPTION, ENTER_NAME, ENTER_DESCRIPTION, ENTER_STARTDATE, ENTER_ENDDATE, ENTER_SCLOCK, ENTER_ECLOCK, ENTER_PRIVATE_CALENDAR,
		ENTER_PRIVATE_CALENDAR_NAME, ENTER_DELETE, ENTER_GROUP_CALENDAR_NAME, ENTER_LOCATION, ENTER_Participants, ADD_MORE, ENTER_NUMBER_OF_SEATS, 
		ENTER_ROOM_NUMBER, ENTER_MORE}
	State state = State.ENTER_EVENTID;
	private ArrayList<Integer> possibleRoomNumber = new ArrayList<Integer>();
	public ServerEditEvent server = new ServerEditEvent();
	private ArrayList<Event> listOfEvents = new ArrayList<Event>();
	private Event eventEditor = new Event();
	private ArrayList<Integer> listOfEventId = new ArrayList<Integer>();
	private Notification notificationConstructor = new Notification();
	private Invitation invitationConstructor = new Invitation();
	public UserEditEvent(ControllerInterface delegator) {
		this.delegator = delegator;
		
		
	}
	
	public void startRunning() {
		ServerEventsResult result = this.server.getListOfEvents(User.currentUser().username);
		printEvents(result.events);
		listOfEvents = result.events;
		listOfEventId = createListOfEventId(result.events);
		if (listOfEventId.size() == 0){
			this.delegator.delegateIsDone("You have not created any events");
		}
		this.delegator.delegateIsReadyForNextInputWithPrompt("Enter the event id of the event you want to edit");
	}
	
	public void sendNextInput(String nextInput) {
		if (nextInput == null){
			this.delegator.delegateIsDone("Exiting editmode");
		}
		switch (this.state){
		case ENTER_EVENTID:
			try{
				int num = Integer.parseInt(nextInput);
				if (listOfEventId.contains(num)){
				
					eventEditor = getEvent( listOfEvents, num);
					
					this.state = State.ENTER_OPTION;
					this.delegator.delegateIsReadyForNextInputWithPrompt("What do you want to edit?");
				}
				else{
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the correct eventId");
				}
			}catch (NumberFormatException e) {
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the correct eventId");
				}
			break;
		
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
			case "date":
				this.state = State.ENTER_STARTDATE;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new startdate dd/mm/yyyy");
				break;
			case "privatecalendarname":
				this.state = State.ENTER_PRIVATE_CALENDAR;
				ServerGetCalendarsResult result = this.server.findPrivateCalendar(User.currentUser().username);
				printCalendarNames(result.privateCalendarNames);
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter the name of one of your private calendars");
				break;
			case "delete":
				this.state = State.ENTER_DELETE;
				ServerFindUserResult theResult = this.server.Participants(this.eventEditor.eventId);
				printCalendarNames(theResult.participants);
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter the name of the participant you want removed");
				break;
//			case "endclock":
//				this.state = State.ENTER_ENDDATE;
//				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new end time hh/mm");
//				break;
			case "add":
				this.state = State.ENTER_Participants;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter the username of the additional participant"); //vaere med?
				break;
//			case "group":
//				this.state = State.ENTER_GROUP_NAME;
//				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new groupname");
//				break;
			case "location":
				this.state = State.ENTER_LOCATION;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new location");
				break;
			case "room":
				this.state = State.ENTER_NUMBER_OF_SEATS;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter desired number of seats");
				break;
			default:
				this.delegator.delegateIsDone("Exiting edit mode");
				break;
			}
		
		case ENTER_NAME:
			if (nextInput.length() <= 45){ 
				
				this.state = State.ENTER_OPTION;
				this.eventEditor.name = nextInput;
				ServerResult result = this.server.editEvent(this.eventEditor);
				//ServerResult result = this.server.editName(nextInput, eventEditor.eventId);
				
				if(result.didSucceed == true){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Name has been changed, enter another action");
				}else{
					this.delegator.delegateIsReadyForNextInputWithPrompt("Error while changing the name of the event");
				}
				
			}else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The name was too long");
			}
		
			break;
		case ENTER_DESCRIPTION:
			
			if (nextInput.length() <=200){
				
				this.eventEditor.description = nextInput;
				this.state = State.ENTER_OPTION;
				
				ServerResult result = this.server.editEvent(this.eventEditor);
				this.delegator.delegateIsReadyForNextInputWithPrompt("Description has been changed, enter another action");
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The description was too long. Please write a shorter name");
			}
			break;
		case ENTER_STARTDATE:
			if (nextInput.length() == 0){
				this.state = State.ENTER_SCLOCK;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new start time");
			}
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
				this.eventEditor.startDate.setYear(year-1900);
				}
				if (month >=1 && month <=12){
					this.eventEditor.startDate.setMonth(month-1);
				
					Calendar cal = new GregorianCalendar(this.eventEditor.startDate.getYear(), month, 1);
					int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					if (day <= daysInMonth && day >0 ){
						this.eventEditor.startDate.setDate(day);
						
						this.state = State.ENTER_SCLOCK;
						this.delegator.delegateIsReadyForNextInputWithPrompt("Start date has been changed. Enter new start time");	
						
						
					}
				}
				else{this.delegator.delegateIsReadyForNextInputWithPrompt("Month does not exist");
				
				}
				
				}catch (NumberFormatException e){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please use numbers");
				}catch (NullPointerException e) {
					// TODO: handle exception
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write it as dd/mm/yy");
				}
			break;
		case ENTER_SCLOCK:
			if (nextInput.length() == 0){
				this.state = State.ENTER_ENDDATE;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter end date. dd/mm/yyyy");
			}
			String[] sTime = nextInput.split("\\."); //maa lage test
			
			
			try{
				int	hour = Integer.parseInt(sTime[0]);
				int min = Integer.parseInt(sTime[1]);
				if(hour >= 0 && hour <=23 && min >= 0 && min <=59){
					
					this.eventEditor.startDate.setHours(hour);
					this.eventEditor.startDate.setMinutes(min);
					this.state = State.ENTER_ENDDATE;
					this.delegator.delegateIsReadyForNextInputWithPrompt("Write the enddate of the event");
			
					
				}
			}catch(NumberFormatException e){
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write it as hh.mm");
			}catch (NullPointerException e) {
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write it as hh.mm");
				
			}
			
			break;
		
		case ENTER_Participants:
			
			if (nextInput.length() <= 45){
					//skjekk med server
					ServerFindUserResult result = this.server.findUser(nextInput);
					if (result.userExists == true){
						ServerFindUserResult theResult = this.server.isUserInvited(eventEditor);
						if (!(theResult.userExists)){ //skjekk om bruker allerede er i lista
							
							this.notificationConstructor.date = this.eventEditor.startDate;
							this.notificationConstructor.username = nextInput;
							this.notificationConstructor.message = "You got invited to an event by" + " " + this.eventEditor.creator;
							this.server.createNotification(this.notificationConstructor);
							
							
							ServerResult creator = this.server.createPrivateCalendarEvent(nextInput + "'s Calendar", this.eventEditor.eventId);
							
							
							this.invitationConstructor.id = this.eventEditor.eventId;
							this.invitationConstructor.invitert = new User(nextInput);
							this.server.createInvitation(this.invitationConstructor);
							
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
		case ENTER_DELETE:
			if (nextInput.length() == 0){
				this.state = State.ENTER_OPTION;
				this.delegator.delegateIsReadyForNextInputWithPrompt("No deletion was done, enter new action");
			}
			if (nextInput.length() <= 45){
				ServerFindUserResult result = this.server.findUser(nextInput);
				if(result.userExists){
					ServerFindUserResult theResult = this.server.isUserInvited(this.eventEditor);
					if(theResult.userExists){
						ServerResult gotResult = server.deleteParticipant(nextInput, this.eventEditor.eventId);
						if (gotResult.didSucceed == true) {
							
						}
					}
					
				}
			}
			break;
		case ENTER_ENDDATE:
			if (nextInput.length() == 0){
				if (compareDates(this.eventEditor.startDate, this.eventEditor.endDate) >= 0) {
					this.state = State.ENTER_ECLOCK;
					if (isTimeAfter(this.eventEditor.startDate, this.eventEditor.endDate)){
						this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new end time");
					}
						this.delegator.delegateIsReadyForNextInputWithPrompt("Start time is after end time. Please enter new end time.");
				}
				this.delegator.delegateIsReadyForNextInputWithPrompt("Cannot skip this action because the end date is before the start date. Please enter the new end date dd/mm/yyyy");
			}
			counter = 0;
			for( int i=0; i<nextInput.length(); i++ ) {
			    if( nextInput.charAt(i) == '/' ) {
			        counter++;
			    } 
			}
			if (counter !=2){
				this.delegator.delegateIsReadyForNextInputWithPrompt("Wrong format. Please try again. dd/mm/yyyy");
				
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
				test.setYear(year-1900);
					if(compareDates(this.eventEditor.startDate, test) >= 0){
						
						this.eventEditor.endDate.setYear(year-1900);
						
						if (month >=1 && month <=12){
							this.eventEditor.endDate.setMonth(month-1);
						
							Calendar cal = new GregorianCalendar(this.eventEditor.endDate.getYear(), month, 1);
							int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
							if (day <= daysInMonth && day >0 ){
								this.eventEditor.endDate.setDate(day);
								this.state = State.ENTER_ECLOCK;
								this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the end time of the event hh.mm");
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
			if(nextInput.length() == 0){
				if((compareDates(this.eventEditor.startDate, this.eventEditor.endDate) == 0 && isTimeAfter(this.eventEditor.startDate, this.eventEditor.endDate)) || compareDates(this.eventEditor.startDate, this.eventEditor.endDate) == 1){
					ServerResult result = this.server.editEvent(this.eventEditor);
					this.state = State.ENTER_OPTION;
					this.delegator.delegateIsReadyForNextInputWithPrompt("Dates and time has been changed, enter new action");
				}
			}
			String[] sETime = nextInput.split("\\.");
			
				
					try{
						int	hour = Integer.parseInt(sETime[0]);
						int min = Integer.parseInt(sETime[1]);
						if(hour >= 0 && hour <=23 && min >= 0 && min <=59){
							Date test = this.eventEditor.endDate;
							test.setHours(hour);
							test.setMinutes(min);
							if (compareDates(test,this.eventEditor.startDate)==0 && isTimeAfter(this.eventEditor.startDate, test)){
							
								this.eventEditor.endDate.setHours(hour);
								this.eventEditor.endDate.setMinutes(min);
								this.state = State.ENTER_LOCATION;
								
								ServerResult result = this.server.editEvent(this.eventEditor);
								this.state = State.ENTER_OPTION;
								this.delegator.delegateIsReadyForNextInputWithPrompt("Dates and time has been changed, enter new action");
							}
							else{
								this.delegator.delegateIsReadyForNextInputWithPrompt("Time is before the starttime");
							}
						}
					}catch(NumberFormatException e){
							this.delegator.delegateIsReadyForNextInputWithPrompt("Please use numbers hh.mm");
					}
							break;
		case ENTER_PRIVATE_CALENDAR:
			
		case ENTER_LOCATION:
			if (nextInput.length() <=200){
				this.eventEditor.location = nextInput;
				this.state = State.ENTER_OPTION;
				ServerResult result = this.server.editEvent(this.eventEditor);
				if (result.didSucceed == true) {
				this.delegator.delegateIsReadyForNextInputWithPrompt("Location has been changed, enter another action");
				}else {
					this.delegator.delegateIsReadyForNextInputWithPrompt("SQL error");
				}
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The name of the location was too long. Please write a shorter name");
			}
			break;
		case ADD_MORE:
			if (nextInput.equals("Y") || nextInput.equals("y")){
				this.state = State.ENTER_Participants;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the username of the participant");
			}
			if (nextInput.equals("N") || nextInput.equals("n")){
				this.state = State.ENTER_OPTION;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new action");
			}else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("Please write Y/N");
			}
			break;
		
		case ENTER_NUMBER_OF_SEATS:
			if (nextInput.length() <= 11){
				try{if (nextInput.length() == 0){
					this.eventEditor.roomNumber = 0;
					ServerResult result = this.server.editEvent(this.eventEditor);
					this.state = State.ENTER_OPTION;
					this.delegator.delegateIsReadyForNextInputWithPrompt("Room has been removed, enter more actions");
					
					
				}
					ServerRoomResult result = this.server.findRoomResult(nextInput, this.eventEditor.startDate, this.eventEditor.endDate);
					if (result.didSucceed == true){	
						if (result.roomIsAvailable){	
							result.printRoomNumbersWithSeats(result.roomNumber, result.numberOfSeats);	
							possibleRoomNumber = result.roomNumber;
							this.state = State.ENTER_ROOM_NUMBER;
							
							this.delegator.delegateIsReadyForNextInputWithPrompt("Choose room number");

						}else{
							this.delegator.delegateIsReadyForNextInputWithPrompt("No room is available with the specified number of seats. Try another number");
						}
						
					}else{
						this.delegator.delegateIsReadyForNextInputWithPrompt("Problem with the server");
					}
					//int numberOfSeats = Integer.parseInt(nextInput);
	//					ServerRoomResult result = this.server			//TIME + Duration
				}catch (NumberFormatException e){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Must write a number");
				}
			}
			break;	
		case ENTER_ROOM_NUMBER:
			
			if (nextInput.length() <= 11){
				try{ 
					int num = Integer.parseInt(nextInput);
					if (possibleRoomNumber.contains(num)){ //maa endres
						this.eventEditor.roomNumber = num;
							
						this.server.editEvent(this.eventEditor);
							
							
							
							
						}else{
							this.delegator.delegateIsReadyForNextInputWithPrompt("try again");
						}
				}catch(NumberFormatException e){
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write a number");
				}
			}
			break;
		}
		
		
		
	}
	public void printEvents(ArrayList<Event> eventsToPrint){
		for (int i = 0; i < eventsToPrint.size(); i++) {
			System.out.println("Event id:" + eventsToPrint.get(i).eventId);
			System.out.println("Name:" + eventsToPrint.get(i).name);
			System.out.println("Description:" + eventsToPrint.get(i).description);
			System.out.println("Start date:" + eventsToPrint.get(i).startDate.toString());
			System.out.println("End date:" + eventsToPrint.get(i).endDate.toString());
			System.out.println("Private calendar name:" + eventsToPrint.get(i).privateCalendarName);
			System.out.println("Group Calendar Name:" + eventsToPrint.get(i).groupCalendarName);
			System.out.println("Location:" + eventsToPrint.get(i).location);
			System.out.println("Creator:" + eventsToPrint.get(i).creator);
			System.out.printf("Room number:" + "%h \n", eventsToPrint.get(i).roomNumber);
			System.out.println("-----------------------------------------------------");
		}
	}
	public void printCalendarNames(ArrayList<String> calendarNames){
		for (int i = 0; i < calendarNames.size(); i++) {
			System.out.println(calendarNames.get(i));
		}
	}
	public ArrayList<Integer> createListOfEventId(ArrayList<Event> events){
		ArrayList<Integer> eventIds = new ArrayList<Integer>();
		for (int i = 0; i < events.size(); i++) {
			eventIds.add(events.get(i).eventId);
			
		}
		return eventIds;
	}
	public Event getEvent(ArrayList<Event> events, int eventId){
		int i = 0;
		while (events.get(i).eventId != eventId){
			i++;
		}
		
		return events.get(i);
	}
	
	public int compareDates(Date date1, Date date2){
		int year1 = date1.getYear()+1900;
		int month1 = date1.getMonth();
		int day1 = date1.getDate();
		int year2 = date2.getYear()+1900;
		int month2 = date2.getMonth();
		int day2 = date2.getDate();
		if(year1<year2){
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
		if(date1.getHours()<date2.getHours()){
			return true;
		}
		if(date1.getHours() == date2.getHours()){
			if (date1.getMinutes()< date2.getMinutes()){
				return true;
			}
			
		}
		return false;
	}
	
	public void userAsksForHelp() {
		switch(this.state){
		
		}
	}
}
