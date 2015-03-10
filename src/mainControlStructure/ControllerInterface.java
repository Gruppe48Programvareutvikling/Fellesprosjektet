package mainControlStructure;

public interface ControllerInterface {
	public void delegateIsDone(String successMessage);
	public void delegateIsReadyForNextInputWithPrompt(String promptToUser);
	public void delegateDidLogInUser(String username);
	public void delegateIsWaitingForServerWithMessage(String message);
}
