package pipeEventsForTheDay;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserEventsForTheDay extends SuperUser {
	
	public UserEventsForTheDay(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserEventsForTheDay class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserEventsForTheDay is done");
	}
	
	public void startRunning() {
		
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
