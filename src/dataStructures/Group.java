package dataStructures;

import java.util.ArrayList;
import java.util.List;

public class Group {
	
	public String groupName;
	public List<User> groupMembers = new ArrayList<User>();
	public List<Group> subGroups = new ArrayList<Group>();
	public String supergroup;
	
	public Group(){
	}
	
	public Group(String groupName, String supergroup){
		this.groupName = groupName;
		this.supergroup = supergroup;
	}
	

}
