package pipeLogin;


import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.SuperUser;

public class UserLogin extends SuperUser {
	private enum State {ENTER_USERNAME, ENTER_PASSWORD};
	private ServerLogin server = new ServerLogin();
	private State state = State.ENTER_USERNAME;
	private String username, password;
	int count = 3;
	
	public UserLogin(ControllerInterface delegator) {
		this.delegator = delegator;
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("Username");
	}
	
	public void sendNextInput(String nextInput) {
		switch (this.state) {
		case ENTER_USERNAME:
			this.username = nextInput;
			this.state = State.ENTER_PASSWORD;
			this.delegator.delegateIsReadyForNextInputWithPrompt("Password");
			break;
		case ENTER_PASSWORD:
			this.password = nextInput;
			ServerAvailabilityResult availability = this.server.LoginWith(this.username, this.password);
			if (availability.didSucceed == true) {
				if (availability.isAvailable == true) {
					this.delegator.delegateDidLogInUser(this.username);
					this.delegator.delegateIsDone(null);
				} else {
					this.count--;
					if (this.count == 0) {
						this.delegator.delegateIsDone("You have tried to log in too many times. You will be returned to the starting screen");
					} else {
						this.state = State.ENTER_USERNAME;
						this.delegator.delegateIsReadyForNextInputWithPrompt("Wrong username or password, you have " + this.count + " tries left\nTry again\nUsername");
					}
				}
			} else {
				this.delegator.delegateIsDone("There was an error logging in with the message \"" + availability.errorMessage + "\"");
			}
			break;
		}
	}
	
	public void userAsksForHelp() {
		switch(this.state) {
		case ENTER_USERNAME:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type in your username (can't be \"help\")");
			break;
		case ENTER_PASSWORD:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type in your password (can't be \"help\")");
			break;
		}
	}
}

