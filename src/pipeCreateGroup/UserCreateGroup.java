package pipeCreateGroup;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserCreateGroup extends SuperUser {
	
	public UserCreateGroup(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserCreateGroup class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserCreateGroup is done");
	}
	
	public void startRunning() {
		
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
