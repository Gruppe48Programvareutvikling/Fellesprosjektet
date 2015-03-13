package serverReturnTypes;

//import java.util.ArrayList;
import java.util.List;

//import dataStructures.Event;
import dataStructures.EventRSVPStatus;
import superClasses.ServerResult;

public class ServerRSVPStatusesResult extends ServerResult {
	
	public List<EventRSVPStatus> rsvpStatuses;
	
//	public boolean isAvailable;
//	public List<String> eventnames = new ArrayList<String>();
//	public List<String> usernames = new ArrayList<String>();
//	public List<String> status = new ArrayList<String>();
//	
//	public String toString() {
//		String ret = "";
//		for (int i = 0; i < status.size(); i++) {
//			ret += eventnames.get(i) + "  ";
//			ret += usernames.get(i) + "  ";
//			ret += status.get(i) + "\n";
//		}
//		return ret;
//	}
}
