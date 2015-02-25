package pipeCreateUser;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserCreateUser extends SuperUser {
	
	public UserCreateUser(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserCreateUser class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserCreateUser is done");
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
