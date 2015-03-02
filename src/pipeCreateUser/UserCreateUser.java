package pipeCreateUser;

import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerResult;
import superClasses.SuperUser;

public class UserCreateUser extends SuperUser {
	
	public static final String PROMPT_AFTER_START_RUNNING = "What do you want your username to be";
	
	private enum State {ENTER_USERNAME, ENTER_PASSWORD, ENTER_MAIL, ENTER_PHONE_NUMBER}
	
	private State state = State.ENTER_USERNAME;
	private User userConstructor = new User();
	private ServerCreateUser server = new ServerCreateUser();
	
	public UserCreateUser(ControllerInterface delegator) {
		this.delegator = delegator;
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt(this.PROMPT_AFTER_START_RUNNING);
	}
	
	public void sendNextInput(String nextInput) {
		switch (this.state) {
		case ENTER_USERNAME:
			ServerAvailabilityResult availability = this.server.checkIfUsernameIsAvailable(nextInput);
			if (availability.isAvailable) {
				this.userConstructor.username = nextInput;
				this.state = State.ENTER_PASSWORD;
				this.delegator.delegateIsReadyForNextInputWithPrompt("What do you want your password to be");
			} else {
				if (availability.didSucceed) {
					this.delegator.delegateIsReadyForNextInputWithPrompt("That username already exists. Try another one");
				} else {
					this.delegator.delegateIsReadyForNextInputWithPrompt("There was an error with the message \"" + availability.errorMessage + "\"\nTry again");
				}
			}
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
			ServerResult result = this.server.createUser(this.userConstructor);
			if (result.didSucceed) {
				this.delegator.delegateIsDone("You have successfully created a user");
			} else {
				this.delegator.delegateIsDone("There was an error creating the user with the message \"" + result.errorMessage + "\"");
			}
			break;
		default:
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
