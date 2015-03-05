package dataStructures;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
	
	public String name;
	public String description;
	public Date startDate = new Date();
	public Date	endDate = new Date();
	public Room room; //
	public String eventId; //
	public List<User> participants = new ArrayList<User>();
	public List<Group> group = new ArrayList<Group>();  //Skal det være user?
	public double delayedAmount; //
	public String location;
	public List<String> calendarNames = new ArrayList<String>();
	public String groupCalendarName; //gjøres til list
	
	public String getDate(Date date){
		return null;
	}
	

}
