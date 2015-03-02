package pipeGetNotifications;

import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.SuperUser;

public class UserGetNotifications extends SuperUser {
	
	private ServerGetNotifications server = new ServerGetNotifications();

	
	public UserGetNotifications(ControllerInterface delegator) {
		this.delegator = delegator;
		System.out.println("Here is your current notifications");
	}
	
	public void startRunning() {
		ServerAvailabilityResult notifications = this.server.getNotifications(User.currentUser().username);
		if (!notifications.isAvailable){
			if (notifications.didSucceed){
				this.delegator.delegateIsDone("You have none recent notifications");				
			}
			else{
				this.delegator.delegateIsDone("There was an error creating the user with the message \"" + notifications.errorMessage + "\"");
			}
		}
		else{
			if (notifications.didSucceed){
				this.delegator.delegateIsDone(" ");
			}
			else{
				this.delegator.delegateIsDone("There was an error creating the user with the message \"" + notifications.errorMessage + "\"");
			}
		}
	}
	
	public void sendNextInput(String nextInput) {
		
	}
	
	public void userAsksForHelp() {
		
	}
}
