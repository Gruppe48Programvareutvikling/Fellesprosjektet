package pipeSeeInvitations;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserSeeInvitations extends SuperUser {
	
	public UserSeeInvitations(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserSeeInvitations class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserSeeInvitations is done");
	}
	
	public void startRunning() {
		
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
