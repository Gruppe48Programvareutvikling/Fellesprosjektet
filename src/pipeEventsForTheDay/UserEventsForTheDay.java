package pipeEventsForTheDay;

import dataStructures.Event;
import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.SuperUser;

public class UserEventsForTheDay extends SuperUser {
	
	private Event event =new Event();
	private ServerEventsForTheDay server=new ServerEventsForTheDay();
	
	public UserEventsForTheDay(ControllerInterface delegator) {
		this.delegator = delegator;
		System.out.println("Here is todays event:\n\n");
		
	}
	
	public void startRunning() {
		ServerAvailabilityResult event = this.server.getEvent(User.currentUser().username);
		if (!event.isAvailable){
			if (event.didSucceed){
				this.delegator.delegateIsDone("You have no events today");				
			}
			else{
				this.delegator.delegateIsDone("There was an error retreaving the event the message \"" + event.errorMessage + "\"");
			}
		}
		else{
			if (event.didSucceed){
				
				this.delegator.delegateIsDone(event.toString()+"\n\nEvents sucsessfully retreaved ");
			}
			else{
				this.delegator.delegateIsDone("There was an error retreaving the event the message \"" + event.errorMessage + "\"");
			}
		}
		
		
		
	
		
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
