package serverReturnTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import superClasses.ServerResult;

public class ServerInvitationsResult extends ServerResult {
	
	public List<String> eventids = new ArrayList<String>();
	public List<String> names = new ArrayList<String>();
	public List<String> descriptions = new ArrayList<String>();
	public List<String> startdates = new ArrayList<String>();
	public List<String> enddates = new ArrayList<String>();
	public List<String> locations = new ArrayList<String>();
	public List<String> usernames = new ArrayList<String>();
	public List<String> statuses = new ArrayList<String>();
	
	public String toString(){
		String ret = "";
		for (int i = 0; i < eventids.size(); i++) {
			ret += "###################################" + "\n";
			ret += "EventId: " + eventids.get(i) + "\n";
			ret += "Name: " + names.get(i) + "\n";
			ret += "Description: " + descriptions.get(i) + "\n";
			ret += "Start date: " + startdates.get(i) + "\n";
			ret += "End date: " + enddates.get(i) + "\n";
			ret += "Location: " + locations.get(i) + "\n";
			ret += "Username: " + usernames.get(i) + "\n";
			ret += "Current status: " + statuses.get(i) + "\n";
			ret += "###################################" + "\n";
		}
		return ret;
	}
}
