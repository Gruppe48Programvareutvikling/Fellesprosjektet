package serverReturnTypes;

import java.util.ArrayList;
import java.util.Collection;

import superClasses.ServerResult;

public class ServerRoomResult extends ServerResult {
	public Collection<Integer> roomNumber = new ArrayList<Integer>();
	public boolean roomIsAvailable;
	
	
}
