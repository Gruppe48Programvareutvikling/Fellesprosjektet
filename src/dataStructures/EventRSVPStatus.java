package dataStructures;

public class EventRSVPStatus {
	
	public enum Status {ACCEPT, DECLINE, MAYBE, NOT_YET_RESPONDED};
	
	public Event event;
	public String userName;
	public Status status;
	
	public static String stringForStatus(Status status) {
		switch (status) { // No break needed
		case ACCEPT:
			return "Accepted";
		case DECLINE:
			return "Declined";
		case MAYBE:
			return "Maybe";
		case NOT_YET_RESPONDED:
			return "Not yet responded";
		default:
			return null;
		}
	}
}
