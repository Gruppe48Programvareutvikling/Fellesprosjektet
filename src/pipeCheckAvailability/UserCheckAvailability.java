package pipeCheckAvailability;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserCheckAvailability extends SuperUser {
	
	public UserCheckAvailability(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserCheckAvailability class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserCheckAvailability is done");
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
