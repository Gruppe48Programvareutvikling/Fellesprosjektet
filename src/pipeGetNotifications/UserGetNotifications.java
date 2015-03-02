package pipeGetNotifications;

import dataStructures.Notification;
import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserGetNotifications extends SuperUser {
	
	private Notification notification = new Notification();
	
	public UserGetNotifications(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserGetNotifications class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserGetNotifications is done");
	}
	
	public void startRunning() {
		
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
