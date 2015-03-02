package pipeGetNotifications;

import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.SuperUser;

public class UserGetNotifications extends SuperUser {
	
	private ServerGetNotifications server = new ServerGetNotifications();
	private String username = "username";

	
	public UserGetNotifications(ControllerInterface delegator) {
		this.delegator = delegator;
		
		System.out.println("The UserGetNotifications class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserGetNotifications is done");
	}
	
	public void startRunning() {
		ServerAvailabilityResult notifications = this.server.getNotifications(username);
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
				this.delegator.delegateIsDone("This is all your notifications");
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
