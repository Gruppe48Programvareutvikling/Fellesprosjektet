package serverReturnTypes;

import java.util.ArrayList;
import java.util.List;

import superClasses.ServerResult;

public class ServerNotificationsResult extends ServerResult {
	
	public boolean isAvailable;
	public List<String> date = new ArrayList<String>();
	public List<String> time = new ArrayList<String>();
	public List<String> message = new ArrayList<String>();
	
	public String toString(){
		String ret = "";
		for (int i = 0; i < message.size(); i++) {
			ret += date.get(i) + "  ";
			ret += time.get(i) + "  ";
			ret += message.get(i) + "\n";
		}
		return ret;
	}

}
