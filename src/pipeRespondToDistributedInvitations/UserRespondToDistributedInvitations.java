package pipeRespondToDistributedInvitations;



import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerInvitationsResult;
import superClasses.SuperUser;

public class UserRespondToDistributedInvitations extends SuperUser {
	
	private enum State {FETCH_INVITATIONS, SELECT_INVITATIONS, RESPONDTO_INVITATIONS, NO_INVITATIONS}
	private State state = State.FETCH_INVITATIONS;
	private ServerRespondToDistributedInvitations server = new ServerRespondToDistributedInvitations();
	private String selectedUsername;
	private int selectedInvitation;
	private String invitationReply;
	
	public UserRespondToDistributedInvitations(ControllerInterface delegator) {
		this.delegator = delegator;
		
		
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("Please enter the username who's invitations you want to respond to");
		
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
		case RESPONDTO_INVITATIONS:
			this.invitationReply = nextInput.toLowerCase();
		
			if (this.invitationReply.equals("accept") || this.invitationReply.equals("decline") || this.invitationReply.equals("maybe")){
				ServerInvitationsResult selection = this.server.respontoInvitation(selectedInvitation, invitationReply, User.currentUser().username);
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
		}
	}
	
	public void userAsksForHelp() {
		switch (this.state){
		case SELECT_INVITATIONS:
			this.delegator.delegateIsReadyForNextInputWithPrompt("Type the eventId of the event you want to select");
			break;
		case RESPONDTO_INVITATIONS:
			this.delegator.delegateIsReadyForNextInputWithPrompt("Type 'accept', 'decline' or 'maybe' to respond to the selected invitation");
			break;
		}
	}
}
