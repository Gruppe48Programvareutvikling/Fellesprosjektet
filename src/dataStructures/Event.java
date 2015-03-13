package dataStructures;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


	
//	public String name;
//	public String description;
//	public Date startDate = new Date();
//	public Date	endDate = new Date();
//	public Room room = new Room(); 
//	public int eventId; //
//	public List<User> participants = new ArrayList<User>();
//	public List<Group> groups = new ArrayList<Group>(); 
//	public List<User> groupUsers = new ArrayList<User>();
//	public double delayedAmount; //
//	public String location;
//	public List<String> calendarNames = new ArrayList<String>();
//	public String groupCalendarName; //gjøres til list
//	public String privateCalendarName;
//	
//	public String getDate(Date date){
//		return null;
//	}

import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;

public class Event {
	
//	private ArrayList<User> participants;
//	private ArrayList<Group> groups;

	
	public Integer 	 eventId;
	public String 	 name;
	public String 	 description;
	public Timestamp startDate = new Timestamp(0);
	public Timestamp endDate = new Timestamp(0);
	public String    privateCalendarName;
	public String 	 groupCalendarName;
	public String 	 location;
	public String 	 creator;
	public Integer   roomNumber;
}
