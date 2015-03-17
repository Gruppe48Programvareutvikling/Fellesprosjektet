package serverReturnTypes;

import java.util.ArrayList;
import java.util.Collections;

import superClasses.ServerResult;

public class ServerFindUserResult extends ServerResult {
	public boolean userExists;
	public String userName;
	public ArrayList<String> participants = new ArrayList<String>();
}
