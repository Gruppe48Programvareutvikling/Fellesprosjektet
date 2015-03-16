package pipeRespondToDistributedInvitations;



import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerInvitationsResult;
import superClasses.SuperUser;

public class UserRespondToDistributedInvitations extends SuperUser {
	
	private enum State {FETCH_INVITATIONS, SELECT_INVITATIONS, RESPONDTO_INVITATIONS, NO_INVITATIONS}
	
	public static final String PROMPT_SELECT_FOREIGN_USERNAME = "Please enter foreign username";
	public static final String PROMPT_SEE_INVITATIONS = "Fetching from server...";
	public static final String PROMPT_SELECT_INVITATION 		 = '\n' + "Select the id of the invitation you want to respond to." + "\n" + "Type 'none' if you don't want to respond at this time";
	public static final String PROMPT_RESPOND_TO_INVITATIONS	 = '\n' + "Sweet, now please enter your response";
	public static final String HELP_SELECT_FOREIGN_USERNAME = "You should enter the username who's invitations you want to respond to";
	public static final String HELP_SELECT_INVITATION		 = "Type the eventId of the event you want to select";
	public static final String HELP_RESPOND_TO_INVITATION		 = "Type 'accept', 'decline' or 'maybe' to respond to the selected invitation";
	public static final String DONE_SUCCESS = "Returning to main menu.";
	private State state = State.FETCH_INVITATIONS;
	private ServerRespondToDistributedInvitations server = new ServerRespondToDistributedInvitations();
	private String selectedUsername;
	private int selectedInvitation;
	private String invitationReply;
	
	public UserRespondToDistributedInvitations(ControllerInterface delegator) {
		this.delegator = delegator;
		
		
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("Please enter foreign username");
		
	}
	
	public void sendNextInput(String nextInput) {
		switch (this.state) {
		case FETCH_INVITATIONS:
			this.selectedUsername = nextInput;
			System.out.println("Fetching from server...");
			System.out.println("\n");
			 ServerInvitationsResult invitationResult = this.server.checkForInvitations(User.currentUser().username, selectedUsername);
			 if (invitationResult.eventids == null){ 
				 this.state = State.NO_INVITATIONS;
				 this.delegator.delegateIsDone("No pending invitations for this username. Exiting to main menu...");
			 }
			 else{
				 System.out.println("Invitations below:" + '\n' + invitationResult.toString());
				 this.state = State.SELECT_INVITATIONS;
				 this.delegator.delegateIsReadyForNextInputWithPrompt('\n' + "Select the id of the invitation you want to respond to." + "\n" + "Type 'none' if you don't want to respond at this time");
			 }
			 break;
		case SELECT_INVITATIONS:
			if (nextInput.equals("none")){
				this.state = State.NO_INVITATIONS;
				this.delegator.delegateIsDone('\n' + "Aight, returning to main menu sir!");
			}
			try{
				this.selectedInvitation = Integer.parseInt(nextInput);
			}
			catch (NumberFormatException n){
				System.out.println("Only integers allowed!");
				this.state = State.SELECT_INVITATIONS;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Try again, moron");
			}
					
			this.state = State.RESPONDTO_INVITATIONS;
			this.delegator.delegateIsReadyForNextInputWithPrompt('\n' + "Sweet, now please enter your response");
			break;
		case RESPONDTO_INVITATIONS:
			this.invitationReply = nextInput.toLowerCase();
		
			if (this.invitationReply.equals("accept") || this.invitationReply.equals("decline") || this.invitationReply.equals("maybe")){
				ServerInvitationsResult selection = this.server.respontoInvitation(selectedInvitation, invitationReply, selectedUsername);
				if (selection.didSucceed = true){
					System.out.println("Changes applied successfully.");
					this.delegator.delegateIsDone("Returning to main menu.");
				}
				else{
					System.out.println("Changes not applied.");
					this.delegator.delegateIsDone("Returning to main menu.");
				}
			}
			else{
				System.out.println("Hey! Wrong input! Type either 'accept', 'decline' or 'maybe'");
				this.state = State.RESPONDTO_INVITATIONS;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Try again");
			}
			break;
		}
	}
	
	public void userAsksForHelp() {
		switch (this.state){
		case FETCH_INVITATIONS:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should enter the username who's invitations you want to respond to");
			break;
		case SELECT_INVITATIONS:
			this.delegator.delegateIsReadyForNextInputWithPrompt("Type the eventId of the event you want to select");
			break;
		case RESPONDTO_INVITATIONS:
			this.delegator.delegateIsReadyForNextInputWithPrompt("Type 'accept', 'decline' or 'maybe' to respond to the selected invitation");
			break;
		}
	}
}
