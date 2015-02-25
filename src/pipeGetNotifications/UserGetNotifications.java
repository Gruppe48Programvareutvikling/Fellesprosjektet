package pipeGetNotifications;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserGetNotifications extends SuperUser {
	
	public UserGetNotifications(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserGetNotifications class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserGetNotifications is done");
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
