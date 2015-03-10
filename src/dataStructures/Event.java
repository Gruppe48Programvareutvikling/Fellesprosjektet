package dataStructures;

import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;

public class Event {
	
//	private ArrayList<User> participants;
//	private ArrayList<Group> groups;
	
	public Integer 	 eventId;
	public String 	 name;
	public String 	 description;
	public Timestamp startDate;
	public Timestamp endDate;
	public String    privateCalendarName;
	public String 	 groupCalendarName;
	public String 	 location;
	public String 	 creator;
	public Integer   roomNumber;
}
