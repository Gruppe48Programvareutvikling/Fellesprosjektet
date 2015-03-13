package pipeEditEvent;


import java.util.ArrayList;



import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;





import com.sun.javafx.scene.EnteredExitedHandler;


import dataStructures.Event;
import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerEventsResult;
import serverReturnTypes.ServerGetCalendarsResult;
import superClasses.ServerResult;
import superClasses.SuperUser;

public class UserEditEvent extends SuperUser {
	private enum State{ENTER_EVENTID,ENTER_OPTION, ENTER_NAME, ENTER_DESCRIPTION, ENTER_STARTDATE, ENTER_ENDDATE, ENTER_SCLOCK, ENTER_ECLOCK, ENTER_GROUP_NAME,
		ENTER_PRIVATE_CALENDAR_NAME, ENTER_GROUP_CALENDAR_NAME, ENTER_LOCATION, ENTER_Participants, ADD_MORE, ENTER_NUMBER_OF_SEATS, 
		ENTER_ROOM_NUMBER, ENTER_MORE}
	State state = State.ENTER_EVENTID;
	
	public ServerEditEvent server = new ServerEditEvent();
	private int eventId;
	private ArrayList<Integer> listOfEventId = new ArrayList<Integer>();
	public UserEditEvent(ControllerInterface delegator) {
		this.delegator = delegator;
		
		
	}
	
	public void startRunning() {
		ServerEventsResult result = this.server.getListOfEvents(User.currentUser().username);
		printEvents(result.events);
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
				
					eventId = num;
					this.state = State.ENTER_OPTION;
					this.delegator.delegateIsReadyForNextInputWithPrompt("What do you want to edit?");
				}
				else{
					this.delegator.delegateIsReadyForNextInputWithPrompt("Please write the corret eventId");
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
				break;
			case "group":
				this.state = State.ENTER_GROUP_NAME;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter new groupname");
				break;
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
				ServerResult result = this.server.editName(nextInput, eventId);
				
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
				
				this.state = State.ENTER_OPTION;
				ServerResult result = this.server.editDescription(nextInput, eventId);
				this.delegator.delegateIsReadyForNextInputWithPrompt("Description has been changed, enter another action");
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The description was too long. Please write a shorter name");
			}
			break;
		case ENTER_STARTDATE:
			Timestamp startDate = new Timestamp();
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
				startDate.setYear(year-1900);
				}
				if (month >=1 && month <=12){
					this.eventConstructor.startDate.setMonth(month-1);
				
					Calendar cal = new GregorianCalendar(this.eventConstructor.startDate.getYear(), month, 1);
					int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					if (day <= daysInMonth && day >0 ){
						this.eventConstructor.startDate.setDate(day);
						this.state = State.ENTER_OPTION;
						this.delegator.delegateIsReadyForNextInputWithPrompt("Start date has been changed. Enter another action");			
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
		case ENTER_LOCATION:
			if (nextInput.length() <=200){
				
				this.state = State.ENTER_OPTION;
				ServerResult result = this.server.editLocation(nextInput, eventId);
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
	public ArrayList<Integer> createListOfEventId(ArrayList<Event> events){
		ArrayList<Integer> eventIds = new ArrayList<Integer>();
		for (int i = 0; i < events.size(); i++) {
			eventIds.add(events.get(i).eventId);
			
		}
		return eventIds;
	}
	
	public void userAsksForHelp() {
		
	}
}
