package serverReturnTypes;

import java.util.ArrayList;
import java.util.List;

import superClasses.ServerResult;
import dataStructures.Event;

public class ServerEventsResult extends ServerResult {
	public List<Event> events = new ArrayList<Event>();
}
