package pipeLogin;


import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.SuperUser;

public class UserLogin extends SuperUser {
	
	public static final String PROMPT_USERNAME 	       = "Username";
	public static final String PROMPT_PASSWORD		   = "Password";
	public static final String ERROR_TOO_MANY_TRIES    = "You have tried to log in too many times. You will be returned to the home screen";
	public static final String ERROR_WRONG_PW_PART_ONE = "Wrong username or password, you have ";
	public static final String ERROR_WRONG_PW_PART_TWO = " tries left. Try again!\nUsername";
	public static final String HELP_USERNAME		   = "You should type in your username (can't be \"help\")";
	public static final String HELP_PASSWORD		   = "You should type in your password (can't be \"help\")";
	
	private enum State {ENTER_USERNAME, ENTER_PASSWORD};
	private ServerLogin server = new ServerLogin();
	private State state = State.ENTER_USERNAME;
	private String username, password;
	public int retryCount = 3; // Need this for testing as well
	
	public UserLogin(ControllerInterface delegator) {
		this.delegator = delegator;
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt(UserLogin.PROMPT_USERNAME);
	}
	
	public void sendNextInput(String nextInput) {
		switch (this.state) {
		case ENTER_USERNAME:
			this.username = nextInput;
			this.state = State.ENTER_PASSWORD;
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserLogin.PROMPT_PASSWORD);
			break;
		case ENTER_PASSWORD:
			this.password = nextInput;
			ServerAvailabilityResult availability = this.server.LoginWith(this.username, this.password);
			if (availability.didSucceed == true) {
				if (availability.isAvailable == true) {
					this.delegator.delegateDidLogInUser(this.username);
					this.delegator.delegateIsDone(null);
				} else {
					this.retryCount--;
					if (this.retryCount == 0) {
						this.delegator.delegateIsDone(UserLogin.ERROR_TOO_MANY_TRIES);
					} else {
						this.state = State.ENTER_USERNAME;
						this.delegator.delegateIsReadyForNextInputWithPrompt(UserLogin.ERROR_WRONG_PW_PART_ONE + this.retryCount + UserLogin.ERROR_WRONG_PW_PART_TWO);
					}
				}
			} else {
				this.delegator.delegateIsDone("There was an error logging in with the message \"" + availability.errorMessage + "\"");
			}
			break;
		default:
			
			this.delegator.delegateIsDone("Something went wrong with the states");
			
		}
	}
	
	public void userAsksForHelp() {
		switch(this.state) {
		case ENTER_USERNAME:
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserLogin.HELP_USERNAME);
			break;
		case ENTER_PASSWORD:
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserLogin.HELP_PASSWORD);
			break;
		}
	}
}

