package dataStructures;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
	
	public String name;
	public String description;
	public Date startDate;
	public Date	endDate;
	public Room room;
	public String eventId;
	public List<User> participants = new ArrayList<User>();
	public List<User> group = new ArrayList<User>();
	public double delayedAmount;
	public String location;

}
