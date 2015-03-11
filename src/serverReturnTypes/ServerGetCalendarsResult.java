package serverReturnTypes;

import java.util.ArrayList;
import java.util.Collection;

import com.mysql.fabric.xmlrpc.base.Array;

import superClasses.ServerResult;

public class ServerGetCalendarsResult extends ServerResult {
	public String groupCalendarName;
	public String privateCalendarName;
}
