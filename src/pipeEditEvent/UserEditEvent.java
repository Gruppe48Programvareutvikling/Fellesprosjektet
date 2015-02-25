package pipeEditEvent;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserEditEvent extends SuperUser {
	
	public UserEditEvent(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserEditEvent class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserEditEvent is done");
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
