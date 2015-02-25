package pipeRespondToDistributedInvitations;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserRespondToDistributedInvitations extends SuperUser {
	
	public UserRespondToDistributedInvitations(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserRespondToDistributedInvitations class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserRespondToDistributedInvitations is done");
	}
	
	public void startRunning() {
		
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
