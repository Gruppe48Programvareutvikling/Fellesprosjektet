package pipeCreateGroup;

import dataStructures.Group;
import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerResult;
import superClasses.SuperUser;

public class UserCreateGroup extends SuperUser {

	private enum State {SET_NAME_FOR_GROUP, ADD_MORE_MEMBERS, ENTER_GROUP_OR_PERSON, ENTER_NAME_OF_USER_TO_ADD, ENTER_NAME_OF_GROUP_TO_ADD}

	private State state = State.SET_NAME_FOR_GROUP;
	private Group groupConstructor = new Group();
	private ServerCreateGroup server = new ServerCreateGroup();


	public UserCreateGroup(ControllerInterface delegator) {
		this.delegator = delegator;

		System.out.println("The UserCreateGroup class was initialized. Will return immediately");
		this.delegator.delegateIsDone("UserCreateGroup is done");
	}

	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("Type in a name for the group");
	}

	public void sendNextInput(String nextInput) {
		switch(this.state){
		case SET_NAME_FOR_GROUP:
			ServerAvailabilityResult availability = this.server.checkIfGroupNameIsAvailable(nextInput);
			if(availability.isAvailable){
				this.groupConstructor.groupName = nextInput;
				this.state = State.ADD_MORE_MEMBERS;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Do you want to add participants to your group?");
			}
			else {
				if (availability.didSucceed) {
					this.delegator.delegateIsReadyForNextInputWithPrompt("That groupname already exists. Try another one");
				} else {
					this.delegator.delegateIsReadyForNextInputWithPrompt("There was an error with the message \"" + availability.errorMessage + "\"\nTry again");
				}
			}
			break;
		case ADD_MORE_MEMBERS:
			if (nextInput.equals("yes")){
				this.state = State.ENTER_GROUP_OR_PERSON;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Is the participant a group or a person?");
			}
			else if (nextInput.equals("no")){
				ServerResult result = this.server.createGroup(this.groupConstructor);
				if (result.didSucceed){
					
					this.delegator.delegateIsDone("You have successfully created a group");					
				}
				else{
					this.delegator.delegateIsDone("There was an error creating the user with the message \"" + result.errorMessage + "\"");
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
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter username you want to add");
			}
			else if (nextInput.equals("group")){
				this.state = State.ENTER_NAME_OF_GROUP_TO_ADD;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Enter groupname you want to add");
			}
			else{
				this.userAsksForHelp();
			}
		case ENTER_NAME_OF_USER_TO_ADD:
			ServerAvailabilityResult userInDatabase = this.server.checkIfUserOrGroupIsInDatabse(nextInput, "person");
			if (userInDatabase.isAvailable){
				this.groupConstructor.groupMembers.add(new User(nextInput));
				this.state = State.ADD_MORE_MEMBERS;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Do you want to add more participants for your group?");
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The username doesent exist, try once again");
			}
			break;
		case ENTER_NAME_OF_GROUP_TO_ADD:
			ServerAvailabilityResult groupInDatabase = this.server.checkIfUserOrGroupIsInDatabse(nextInput, "group");
			if (groupInDatabase.isAvailable){
				this.groupConstructor.subGroups.add(new Group(nextInput, groupConstructor.groupName));
				this.state = State.ADD_MORE_MEMBERS;
				this.delegator.delegateIsReadyForNextInputWithPrompt("Do you want to add more participants for your group?");				
			}
			else{
				this.delegator.delegateIsReadyForNextInputWithPrompt("The groupname doesent exist, try once again");				
			}
		default:
			break;
		}
	}

	public void userAsksForHelp() {
		switch (this.state) {
		case SET_NAME_FOR_GROUP:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type in what you want your groupname to be (can't be \"help\")");
			break;
		case ADD_MORE_MEMBERS:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type in yes or no (can't be \"help\")");
			break;
		case ENTER_GROUP_OR_PERSON:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type group if you want to add a group to your group, and person if you want to add a person to your group (can't be \"help\")");
			break;
		case ENTER_NAME_OF_USER_TO_ADD:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You shold type in the username of the person you want to add to yor group (can't be \"help\")");
			break;
		case ENTER_NAME_OF_GROUP_TO_ADD:
			this.delegator.delegateIsReadyForNextInputWithPrompt("Your should tupe in groupname of the group you want to add to your group (can't be \"help\")");
			break;
		}
			
	}
}
