package adminPipeDeleteUser;

import serverReturnTypes.ServerAvailabilityResult;
import superClasses.SuperUser;
import mainControlStructure.ControllerInterface;

// This class will only have one state, so no state attribute is needed
public class AdminDeleteUser extends SuperUser {
	
	private ServerDeleteUser server = new ServerDeleteUser();
	
	public AdminDeleteUser(ControllerInterface controller) {
		this.delegator = controller;
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("What is the username for the user you want to delete? \nUsername");
	}
	
	public void sendNextInput(String nextInput) {
		ServerAvailabilityResult result = server.deleteUser(nextInput);
		if (result.isAvailable) {
			this.delegator.delegateIsDone("The user \"" + nextInput + "\" was successfully deleted.\nReturning to home screen.");
		} else {
			this.delegator.delegateIsDone("The username \"" + nextInput + "\" was not found.\nReturning to home screen.");
		}
	}
	
	public void userAsksForHelp() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("You should enter the username for the user you want to delete. \nUsername");
	}
}
