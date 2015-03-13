package serverReturnTypes;


import java.util.Date;

import java.util.ArrayList;



import superClasses.ServerResult;
import dataStructures.Event;

public class ServerEventsResult extends ServerResult {

	public int eventId;
	public String name;
	public String description;
	public Date startDate;
	public Date endDate;
	public String location;
	public int roomNumber;
	public ArrayList<Event> events = new ArrayList<Event>();
}
