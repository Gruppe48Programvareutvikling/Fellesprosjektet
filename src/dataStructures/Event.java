package dataStructures;
import java.sql.Timestamp;

public class Event {
	
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
