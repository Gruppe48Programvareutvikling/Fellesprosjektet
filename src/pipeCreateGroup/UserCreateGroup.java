package pipeCreateGroup;

import dataStructures.Group;
import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerResult;
import superClasses.SuperUser;

public class UserCreateGroup extends SuperUser {

	public static final String PROMPT_GROUPNAME = "Type in a name for the group";
	public static final String PROMPT_TAKEN_GROUPNAME = "The groupname is allready taken, try a new one";
	public static final String PROMPT_MORE_PARTICIPANTS = "Do you want to add more participants for your group?";
	public static final String PROMPT_GROUP_OR_PERSON = "Is the participant a group or a person?";
	public static final String PROMPT_ENTER_USERNAME = "Enter username you want to add";
	public static final String PROMPT_ENTER_GROUPNAME = "Enter groupname you want to add";
	public static final String PROMPT_USERNAME_DOESENT_EXIST = "The username doesent exist, try once again";
	public static final String PROMPT_GROUPNAME_DOESENT_EXIST = "The groupname doesent exist, try once again";
	public static final String PROMPT_DONE_SUCCESS = "You have successfully created a group";
	public static final String HELP_SET_NAME_FOR_GROUP = "You should type in what you want your groupname to be (can't be \"help\")";
	public static final String HELP_ADD_MORE_MEMBERS = "You should type in yes or no (can't be \"help\")";
	public static final String HELP_ENTER_GROUP_OR_PERSON = "You should type group if you want to add a group to your group, and person if you want to add a person to your group (can't be \"help\")";
	public static final String HELP_ENTER_NAME_OF_USER_TO_ADD = "You shold type in the username of the person you want to add to yor group (can't be \"help\")";
	public static final String HELP_ENTER_NAME_OF_GROUP_TO_ADD = "You should type in groupname of the group you want to add to your group (can't be \"help\")";
	
	
	private enum State {SET_NAME_FOR_GROUP, ADD_MORE_MEMBERS, ENTER_GROUP_OR_PERSON, ENTER_NAME_OF_USER_TO_ADD, ENTER_NAME_OF_GROUP_TO_ADD}

	private State state = State.SET_NAME_FOR_GROUP;
	private Group groupConstructor = new Group();
	private ServerCreateGroup server = new ServerCreateGroup();

	public UserCreateGroup(ControllerInterface delegator) {
		this.delegator = delegator;
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.PROMPT_GROUPNAME);
	}

	public void sendNextInput(String nextInput) {
		switch(this.state){
		case SET_NAME_FOR_GROUP:
			ServerAvailabilityResult availability = this.server.checkIfGroupNameIsAvailable(nextInput);
			if(availability.isAvailable){
				this.groupConstructor.groupName = nextInput;
				this.state = State.ADD_MORE_MEMBERS;
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.PROMPT_MORE_PARTICIPANTS);
			}
			else {
				if (availability.didSucceed) {
					this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.PROMPT_TAKEN_GROUPNAME);
				} else {
					this.delegator.delegateIsReadyForNextInputWithPrompt("There was an error with the message \"" + availability.errorMessage + "\"\nTry again");
				}
			}
			break;
		case ADD_MORE_MEMBERS:
			if (nextInput.equals("yes")){
				this.state = State.ENTER_GROUP_OR_PERSON;
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.PROMPT_GROUP_OR_PERSON);
			}
			else if (nextInput.equals("no")){
				ServerResult result = this.server.createGroup(this.groupConstructor);
				if (result.didSucceed){
					this.delegator.delegateIsDone(UserCreateGroup.PROMPT_DONE_SUCCESS);					
				}
				else{
					this.delegator.delegateIsDone("There was an error creating the group with the message \"" + result.errorMessage + "\"");
				}
				break;
			}
			else{
				this.userAsksForHelp();
			}
			break;
		case ENTER_GROUP_OR_PERSON:
			if (nextInput.equals("person")){
				this.state = State.ENTER_NAME_OF_USER_TO_ADD;
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.PROMPT_ENTER_USERNAME);
			}
			else if (nextInput.equals("group")){
				this.state = State.ENTER_NAME_OF_GROUP_TO_ADD;
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.PROMPT_ENTER_GROUPNAME);
			}
			else{
				this.userAsksForHelp();
			}
			break;
		case ENTER_NAME_OF_USER_TO_ADD:
			ServerAvailabilityResult userInDatabase = this.server.checkIfUserOrGroupIsInDatabse(nextInput, "person");
			if (userInDatabase.isAvailable){
				this.groupConstructor.groupMembers.add(new User(nextInput));
				this.state = State.ADD_MORE_MEMBERS;
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.PROMPT_MORE_PARTICIPANTS);
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.PROMPT_USERNAME_DOESENT_EXIST);
			}
			break;
		case ENTER_NAME_OF_GROUP_TO_ADD:
			ServerAvailabilityResult groupInDatabase = this.server.checkIfUserOrGroupIsInDatabse(nextInput, "group");
			if (groupInDatabase.isAvailable){
				this.groupConstructor.subGroups.add(new Group(nextInput, groupConstructor.groupName));
				this.state = State.ADD_MORE_MEMBERS;
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.PROMPT_MORE_PARTICIPANTS);				
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.PROMPT_GROUPNAME_DOESENT_EXIST);				
			}
			break;
		default:
			break;
		}
	}

	public void userAsksForHelp() {
		switch (this.state) {
		case SET_NAME_FOR_GROUP:
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.HELP_SET_NAME_FOR_GROUP);
			break;
		case ADD_MORE_MEMBERS:
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.HELP_ADD_MORE_MEMBERS);
			break;
		case ENTER_GROUP_OR_PERSON:
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.HELP_ENTER_GROUP_OR_PERSON);
			break;
		case ENTER_NAME_OF_USER_TO_ADD:
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.HELP_ENTER_NAME_OF_USER_TO_ADD);
			break;
		case ENTER_NAME_OF_GROUP_TO_ADD:
			this.delegator.delegateIsReadyForNextInputWithPrompt(UserCreateGroup.HELP_ENTER_NAME_OF_GROUP_TO_ADD);
			break;
		}
			
	}
}
