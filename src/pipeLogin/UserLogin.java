package pipeLogin;

import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserLogin extends SuperUser {
	
	public UserLogin(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserLogin class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserLogin is done");
	}
	
	public void startRunning() {
		
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
