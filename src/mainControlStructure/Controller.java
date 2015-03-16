package mainControlStructure;

import java.util.Scanner;

import adminPipeDeleteGroup.AdminDeleteGroup;
import adminPipeDeleteUser.AdminDeleteUser;
import dataStructures.User;
import pipeCheckAvailability.UserCheckAvailability;
import pipeCheckRSVPStatusForEvents.UserCheckRSVPStatusForEvents;
import pipeCreateCalander.UserCreateCalendar;
import pipeCreateEvent.UserCreateEvent;
import pipeCreateGroup.UserCreateGroup;
import pipeCreateUser.UserCreateUser;
import pipeEditEvent.UserEditEvent;
import pipeEventsForTheDay.UserEventsForTheDay;
import pipeGetNotifications.UserGetNotifications;
import pipeLogin.UserLogin;
import pipeRespondToDistributedInvitations.UserRespondToDistributedInvitations;
import pipeSeeInvitations.UserSeeInvitations;


public class Controller implements ControllerInterface {
	
	// All of these should be in lowercase
	public static final String COMMAND_CREATE_USER 							= "create user";
	public static final String COMMAND_CREATE_CALENDAR 						= "create calendar";
	public static final String COMMAND_CHECK_AVAILABILITY 					= "check availability";
	public static final String COMMAND_CREATE_EVENT							= "create event";
	public static final String COMMAND_SEE_INVITATIONS						= "see invitations";
	public static final String COMMAND_LOGIN								= "login";
	public static final String COMMAND_CREATE_GROUP							= "create group";
	public static final String COMMAND_EDIT_EVENT							= "edit event";
	public static final String COMMAND_CHECK_RSVP_STATUS_FOR_EVENTS			= "check rsvp status for events";
	public static final String COMMAND_EVENTS_FOR_THE_DAY					= "events for the day";
	public static final String COMMAND_GET_NOTIFICATIONS					= "see notifications";
	public static final String COMMAND_RESPOND_TO_DISTRIBUTED_INVITATIONS  	= "respond to distributed invitations";
	
	public static final String ADMIN_COMMAND_DELETE_USER					= "delete user";
	public static final String ADMIN_COMMAND_DELETE_GROUP					= "delete group";
	
	public static final String COMMAND_LOGOUT								= "logout";
	public static final String COMMAND_HELP									= "help";
	
	
	
	private boolean userIsLoggedIn = false;
	private boolean currentUserIsAdmin = false;
	private DelegateInterface delegate = null;
	private Scanner inputScanner = new Scanner(System.in);
	
	
	
	
	
	public Controller() {
		System.out.println("*** Type \"help\" at any point to see what options you have ***");
		this.mainLoopWithPrompt(null);
	}
	
	public void delegateIsDone(String successMessage) {
		this.delegate = null;
		if (successMessage!= null && successMessage.length() > 0){
			System.out.println(successMessage);
		}
		
		this.mainLoopWithPrompt(null);
	}
	
	public void delegateIsReadyForNextInputWithPrompt(String promptToUser) {
		this.mainLoopWithPrompt(promptToUser);
	}
	
	public void delegateDidLogInUser(String username) {
		this.userIsLoggedIn = true;
		if (username.equals("admin")) {
			this.currentUserIsAdmin = true;
		}
		User.currentUser().username = username;
		System.out.println("You are logged in as \"" + username + "\"");
	}
	
	public void delegateIsWaitingForServerWithMessage(String message) {
		System.out.println(message);
	}
	
	
	
	
	
	private void mainLoopWithPrompt(String promptToUser) {
		if (promptToUser != null) System.out.print(promptToUser + ": ");
		String nextInput = this.inputScanner.nextLine();
		
		if (this.delegate != null) {
			// The help command comes before everything else
			if (nextInput.equals(COMMAND_HELP)) {
				this.delegate.userAsksForHelp();
			} else { 
				this.delegate.sendNextInput(nextInput);
			}
		} else {
			// nextInput is not going to be used for anything other than to check against valid commands.
			// That will be easier to do if everything is lowercase.
			nextInput = nextInput.toLowerCase();
			if (this.userIsLoggedIn) {
				switch (nextInput) {
				case ADMIN_COMMAND_DELETE_USER:
					if (this.currentUserIsAdmin == true) {
						this.delegate = new AdminDeleteUser(this);
					} else {
						System.out.println("You are not an admin.");
						this.mainLoopWithPrompt(null);
					}
					break;
				case ADMIN_COMMAND_DELETE_GROUP:
					if (this.currentUserIsAdmin == true) {
						this.delegate = new AdminDeleteGroup(this);
					} else {
						System.out.println("You are not an admin");
						this.mainLoopWithPrompt(null);
					}
					break;
				case COMMAND_CHECK_AVAILABILITY:
					this.delegate = new UserCheckAvailability(this);
					break;
				case COMMAND_CREATE_EVENT:
					this.delegate = new UserCreateEvent(this);
					break;
				case COMMAND_SEE_INVITATIONS:
					this.delegate = new UserSeeInvitations(this);
					break;
				case COMMAND_CREATE_GROUP:
					this.delegate = new UserCreateGroup(this);
					break;
				case COMMAND_CREATE_CALENDAR:
					this.delegate = new UserCreateCalendar(this);
					break;
				case COMMAND_EDIT_EVENT:
					this.delegate = new UserEditEvent(this);
					break;
				case COMMAND_CHECK_RSVP_STATUS_FOR_EVENTS:
					this.delegate = new UserCheckRSVPStatusForEvents(this);
					break;
				case COMMAND_EVENTS_FOR_THE_DAY:
					this.delegate = new UserEventsForTheDay(this);
					break;
				case COMMAND_GET_NOTIFICATIONS:
					this.delegate = new UserGetNotifications(this);
					break;
				case COMMAND_RESPOND_TO_DISTRIBUTED_INVITATIONS:
					this.delegate = new UserRespondToDistributedInvitations(this);
					break;
				case COMMAND_LOGOUT:
					this.userIsLoggedIn = false;
					this.currentUserIsAdmin = false;
					User.currentUser().username = null;
					System.out.println("You have successfully logged out");
					this.mainLoopWithPrompt(null);
					break;
				case COMMAND_HELP:
					this.userAsksForHelp();
					break;
				default:
					System.out.println("The command \"" + nextInput + "\" is not available at the moment. Type \"help\" to see what options you have");
					this.mainLoopWithPrompt(null);
					break;
				}
			} else {
				// User is not logged in
				switch (nextInput) {
				case COMMAND_CREATE_USER:
					this.delegate = new UserCreateUser(this);
					break;
				case COMMAND_LOGIN:
					this.delegate = new UserLogin(this);
					break;
				case COMMAND_HELP:
					this.userAsksForHelp();
					break;
				default:
					System.out.println("The command \"" + nextInput + "\" is not available at the moment. Type \"help\" to see what options you have");
					this.mainLoopWithPrompt(null);
					break;
				}
			}
			
			if (this.delegate != null) {
				this.delegate.startRunning();
			}
		}
	}
	
	private void userAsksForHelp() {
		if (this.delegate != null) {
			// The user is in the middle of performing some action, so ask the delegate what the user can do.
			this.delegate.userAsksForHelp();
		} else {
			String currentOptions = "You currently have the following options:\n";
			
			if (this.userIsLoggedIn) {
				if (this.currentUserIsAdmin) {
					currentOptions += "- " + ADMIN_COMMAND_DELETE_USER + "\n";
					currentOptions += "- " + ADMIN_COMMAND_DELETE_GROUP + "\n";
				}
				currentOptions += "- " + COMMAND_CHECK_AVAILABILITY + "\n";
				currentOptions += "- " + COMMAND_CREATE_EVENT + "\n";
				currentOptions += "- " + COMMAND_CREATE_CALENDAR + "\n";
				currentOptions += "- " + COMMAND_SEE_INVITATIONS + "\n";
				currentOptions += "- " + COMMAND_CREATE_GROUP + "\n";
				currentOptions += "- " + COMMAND_EDIT_EVENT + "\n";
				currentOptions += "- " + COMMAND_CHECK_RSVP_STATUS_FOR_EVENTS + "\n";
				currentOptions += "- " + COMMAND_EVENTS_FOR_THE_DAY + "\n";
				currentOptions += "- " + COMMAND_GET_NOTIFICATIONS + "\n";
				currentOptions += "- " + COMMAND_RESPOND_TO_DISTRIBUTED_INVITATIONS + "\n";
				currentOptions += "- " + COMMAND_LOGOUT;
			} else {
				currentOptions += "- " + COMMAND_CREATE_USER + "\n";
				currentOptions += "- " + COMMAND_LOGIN;
			}
			
			System.out.println(currentOptions);
		}
		
		this.mainLoopWithPrompt(null);
	}
}
