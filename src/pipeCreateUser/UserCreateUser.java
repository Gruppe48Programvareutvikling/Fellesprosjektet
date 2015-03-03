package pipeCreateUser;

import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerResult;
import superClasses.SuperUser;

public class UserCreateUser extends SuperUser {
	
	public static final String PROMPT_USERNAME 		 = "What do you want your username to be";
	public static final String PROMPT_PASSWORD 		 = "What do you want your password to be";
	public static final String PROMPT_MAIL     		 = "What is your email address";
	public static final String PROMPT_PHONE   		 = "What is your phone number";
	public static final String ERROR_USERNAME_EXISTS = "That username already exists. Try another one";
	public static final String DONE_SUCCESS   		 = "You have successfully created a user";
	public static final String HELP_USERNAME		 = "You should type in what you want your username to be (can't be \"help\")";
	public static final String HELP_PASSWORD		 = "You should type in what you want your password to be (can't be \"help\")";
	public static final String HELP_MAIL			 = "What is your email address";
	public static final String HELP_PHONE			 = "What is your phone number";
	
	private enum State {ENTER_USERNAME, ENTER_PASSWORD, ENTER_MAIL, ENTER_PHONE_NUMBER}
	
	private State state = State.ENTER_USERNAME;
	private User userConstructor = new User();
	private ServerCreateUser server = new ServerCreateUser();
	
	public UserCreateUser(ControllerInterface delegator) {
		this.delegator = delegator;
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.PROMPT_USERNAME);
	}
	
	public void sendNextInput(String nextInput) {
		switch (this.state) {
		case ENTER_USERNAME:
			ServerAvailabilityResult availability = this.server.checkIfUsernameIsAvailable(nextInput);
			if (availability.isAvailable) {
				this.userConstructor.username = nextInput;
				this.state = State.ENTER_PASSWORD;
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.PROMPT_PASSWORD);
			} else {
				if (availability.didSucceed) {
					this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.ERROR_USERNAME_EXISTS);
				} else {
					this.delegator.delegateIsReadyForNextInputWithPrompt("There was an error with the message \"" + availability.errorMessage + "\"\nTry again");
				}
			}
			break;
		case ENTER_PASSWORD:
			this.userConstructor.password = nextInput;
			this.state = State.ENTER_MAIL;
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.PROMPT_MAIL);
			break;
		case ENTER_MAIL:
			this.userConstructor.mail = nextInput;
			this.state = State.ENTER_PHONE_NUMBER;
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.PROMPT_PHONE);
			break;
		case ENTER_PHONE_NUMBER:
			this.userConstructor.phoneNumber = nextInput;
			ServerResult result = this.server.createUser(this.userConstructor);
			if (result.didSucceed) {
				this.delegator.delegateIsDone(UserCreateUser.DONE_SUCCESS);
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
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.HELP_USERNAME);
			break;
		case ENTER_PASSWORD:
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.HELP_PASSWORD);
			break;
		case ENTER_MAIL:
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.HELP_MAIL);
			break;
		case ENTER_PHONE_NUMBER:
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.HELP_PHONE);
			break;
		}
	}
}
