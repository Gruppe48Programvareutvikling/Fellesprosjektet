package pipeCheckRSVPStatusForEvents;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserCheckRSVPStatusForEvents extends SuperUser {

	public UserCheckRSVPStatusForEvents(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserCheckRSVPStatusForEvents class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserCheckRSVPStatusForEvents is done");
	}
	
	public void startRunning() {
		
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
