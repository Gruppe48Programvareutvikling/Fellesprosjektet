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
	
	public static final String ERROR_SHORT_USERNAME  = "Your username should be at least 1 character long. Try again!\n" + UserCreateUser.PROMPT_USERNAME;
	public static final String ERROR_USERNAME_EXISTS = "That username already exists. Try another one\n" + UserCreateUser.PROMPT_USERNAME;
	public static final String ERROR_SHORT_PASSWORD  = "Your password should be at least 4 characters. Try again!\n" + UserCreateUser.PROMPT_PASSWORD;
	public static final String ERROR_BAD_MAIL_FORMAT = "That is not a valid email format. Try again!\n" + UserCreateUser.PROMPT_MAIL;
	public static final String ERROR_BAD_PHONE_FORMAT= "A phone number should be exactly 8 characters long. Try again!\n" + UserCreateUser.PROMPT_PHONE;
	
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
			if (nextInput.length() > 0) {
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
			} else {
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.ERROR_SHORT_USERNAME);
			}
			break;
		case ENTER_PASSWORD:
			if (nextInput.length() > 3) {
				this.userConstructor.password = nextInput;
				this.state = State.ENTER_MAIL;
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.PROMPT_MAIL);
			} else {
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.ERROR_SHORT_PASSWORD);
			}
			break;
		case ENTER_MAIL:
			if (nextInput.matches("[a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")) {
				this.userConstructor.mail = nextInput;
				this.state = State.ENTER_PHONE_NUMBER;
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.PROMPT_PHONE);
			} else {
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.ERROR_BAD_MAIL_FORMAT);
			}
			break;
		case ENTER_PHONE_NUMBER:
			if (nextInput.matches("[0-9]{8}")) {
				this.userConstructor.phoneNumber = nextInput;
				ServerResult result = this.server.createUser(this.userConstructor);
				if (result.didSucceed) {
					this.delegator.delegateIsDone(UserCreateUser.DONE_SUCCESS);
				} else {
					this.delegator.delegateIsDone("There was an error creating the user with the message \"" + result.errorMessage + "\"");
				}
			} else {
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateUser.ERROR_BAD_PHONE_FORMAT);
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
