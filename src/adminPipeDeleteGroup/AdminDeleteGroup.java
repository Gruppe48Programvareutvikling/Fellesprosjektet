package adminPipeDeleteGroup;

import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.SuperUser;

public class AdminDeleteGroup extends SuperUser {
	
	private ServerDeleteGroup server = new ServerDeleteGroup(); 
	
	public AdminDeleteGroup(ControllerInterface controller) {
		this.delegator = controller;
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("What is the name of the group you want to delete?\nGroup name");
	}
	
	public void sendNextInput(String nextInput) {
		ServerAvailabilityResult result = server.deleteGroup(nextInput);
		if (result.isAvailable) {
			this.delegator.delegateIsDone("The group named \"" + nextInput + "\" was successfully deleted.\nReturning to home screen.");
		} else {
			this.delegator.delegateIsDone("The group name \"" + nextInput + "\" was not found.\nReturning to home screen.");
		}
	}

	public void userAsksForHelp() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("You should enter the name of the group you want to delete.\nGroup name");
	}

}
