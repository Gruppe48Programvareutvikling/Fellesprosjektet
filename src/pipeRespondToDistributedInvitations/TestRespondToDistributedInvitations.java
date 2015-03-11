package pipeRespondToDistributedInvitations;

import java.util.*;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.mockito.*;
import org.junit.Before;
import org.junit.Test;

import mainControlStructure.ControllerInterface;

import org.junit.*;  

import static org.junit.Assert.*;

import org.mockito.*;

import pipeSeeInvitations.UserSeeInvitations;
import dataStructures.User;
import static org.mockito.Mockito.*;

public class TestRespondToDistributedInvitations {
	
	private UserRespondToDistributedInvitations viewer;
	private String selectedUsername = "marty";
	private String selectedInvitation = "1";
	private String response = "maybe";
	
	@Mock
	private ControllerInterface controller;
	
	@Before
	public void runBefore() {
		MockitoAnnotations.initMocks(this);
		this.viewer = new UserRespondToDistributedInvitations(this.controller);
		User.currentUser().username = "vegard";
	}
	
	@Test
	public void tester() {
		
	//TESTING THE STANDARD PROCESS
		
	this.viewer.startRunning();
	this.viewer.userAsksForHelp();
	this.viewer.sendNextInput(selectedUsername);
	this.viewer.userAsksForHelp();
	this.viewer.sendNextInput(this.selectedInvitation);
	this.viewer.userAsksForHelp();
	this.viewer.sendNextInput(this.response);
		
	ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
	verify(this.controller, times(6)).delegateIsReadyForNextInputWithPrompt(argumentCaptor.capture());
	
	List<String> capturedArguments = argumentCaptor.getAllValues();

	assertEquals(UserRespondToDistributedInvitations.PROMPT_SELECT_FOREIGN_USERNAME,      capturedArguments.get(0));
	assertEquals(UserRespondToDistributedInvitations.HELP_SELECT_FOREIGN_USERNAME,        capturedArguments.get(1));
	assertEquals(UserRespondToDistributedInvitations.PROMPT_SELECT_INVITATION,            capturedArguments.get(2));
	assertEquals(UserRespondToDistributedInvitations.HELP_SELECT_INVITATION,              capturedArguments.get(3));
	assertEquals(UserRespondToDistributedInvitations.PROMPT_RESPOND_TO_INVITATIONS,       capturedArguments.get(4));
	assertEquals(UserRespondToDistributedInvitations.HELP_RESPOND_TO_INVITATION,          capturedArguments.get(5));
	
	verify(this.controller, times(1)).delegateIsDone(UserSeeInvitations.DONE_SUCCESS);
	
	}
	

}
