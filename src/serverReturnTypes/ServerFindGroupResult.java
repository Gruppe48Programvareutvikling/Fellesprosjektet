package serverReturnTypes;

import java.util.ArrayList;
import java.util.Collection;

import superClasses.ServerResult;

public class ServerFindGroupResult extends ServerResult {
	public Collection<String> groupNames = new ArrayList<String>();
	public Collection<String> groupUsers = new ArrayList<String>();
	public String groupName;
	
	public void addGroupName(String groupName){
		this.groupName.add(groupName);
	}
	public void addGroupUsers(String groupUser){
		this.groupUsers.add(groupUser);
	}
	
	
}
