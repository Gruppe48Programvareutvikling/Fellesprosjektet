package pipeCreateEvent;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserCreateEvent extends SuperUser {
	
	public UserCreateEvent(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserCreateEvent class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserCreateEvent is done");
	}
	
	public void startRunning() {
		
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
