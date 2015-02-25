package pipeCreateUser;

import dataStructures.User;
import mainControlStructure.ControllerInterface;
import superClasses.SuperUser;

public class UserCreateUser extends SuperUser {
	
	private enum State {ENTER_USERNAME, ENTER_PASSWORD, ENTER_MAIL, ENTER_PHONE_NUMBER}
	
	private State state = State.ENTER_USERNAME;
	private User userConstructor = new User();
	private ServerCreateUser server = new ServerCreateUser();
	
	public UserCreateUser(ControllerInterface delegator) {
		this.delegator = delegator;
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("What do you want your username to be");
	}
	
	public void sendNextInput(String nextInput) {
		switch (this.state) {
		case ENTER_USERNAME:
			// CHECK WITH THE SERVER IF THE USERNAME ALREADY EXISTS. CURRENTLY ASSUMING IT'S AVAILABLE
			this.userConstructor.username = nextInput;
			this.state = State.ENTER_PASSWORD;
			this.delegator.delegateIsReadyForNextInputWithPrompt("What do you want your password to be");
			break;
		case ENTER_PASSWORD:
			this.userConstructor.password = nextInput;
			this.state = State.ENTER_MAIL;
			this.delegator.delegateIsReadyForNextInputWithPrompt("What is your email address");
			break;
		case ENTER_MAIL:
			this.userConstructor.mail = nextInput;
			this.state = State.ENTER_PHONE_NUMBER;
			this.delegator.delegateIsReadyForNextInputWithPrompt("What is your phone number");
			break;
		case ENTER_PHONE_NUMBER:
			this.userConstructor.phoneNumber = nextInput;
			// SEND THIS USER TO THE SERVER
			this.delegator.delegateIsDone("You have successfully created a user");
			break;
		}
	}
	
	public void userAsksForHelp() {
		switch (this.state) {
		case ENTER_USERNAME:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type in what you want your username to be (can't be \"help\")");
			break;
		case ENTER_PASSWORD:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type in what you want your password to be (can't be \"help\")");
			break;
		case ENTER_MAIL:
			this.delegator.delegateIsReadyForNextInputWithPrompt("What is your email address");
			break;
		case ENTER_PHONE_NUMBER:
			this.delegator.delegateIsReadyForNextInputWithPrompt("What is your phone number");
			break;
		}
	}
}
