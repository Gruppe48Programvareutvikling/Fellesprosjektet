package serverReturnTypes;

import java.util.Date;

import superClasses.ServerResult;

public class ServerEventsResult extends ServerResult {
	public int eventId;
	public String name;
	public String description;
	public Date startDate;
	public Date endDate;
	public String location;
	public int roomNumber;
}
