package pipeSeeInvitations;

import java.util.*;
import static org.junit.Assert.*;
import junit.framework.TestCase;
import org.mockito.*;
import org.junit.Test;
import mainControlStructure.ControllerInterface;
import org.junit.*;  
import static org.junit.Assert.*;
import org.mockito.*;
import dataStructures.User;
import static org.mockito.Mockito.*;

public class TestSeeInvitations {
	
	private UserSeeInvitations viewer;
	private String selectedInvitation = "1";
	private String response = "maybe";
	
	@Mock
	private ControllerInterface controller;
	
	@Before
	public void runBefore() {
		MockitoAnnotations.initMocks(this);
		this.viewer = new UserSeeInvitations(this.controller);
		User.currentUser().username = "marty";
	}
	
	@Test
	public void tester() {
		
	//TESTING THE STANDARD PROCESS
		
	this.viewer.startRunning();
	this.viewer.userAsksForHelp();
	this.viewer.sendNextInput(this.selectedInvitation);
	this.viewer.userAsksForHelp();
	this.viewer.sendNextInput(this.response);
		
	ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
	verify(this.controller, times(5)).delegateIsReadyForNextInputWithPrompt(argumentCaptor.capture());
	
	List<String> capturedArguments = argumentCaptor.getAllValues();

	assertEquals(UserSeeInvitations.PROMPT_SELECT_INVITATION,      capturedArguments.get(0));
	assertEquals(UserSeeInvitations.HELP_SELECT_INVITATION,        capturedArguments.get(1));
	assertEquals(UserSeeInvitations.PROMPT_RESPOND_TO_INVITATIONS, capturedArguments.get(2));
	assertEquals(UserSeeInvitations.HELP_RESPOND_TO_INVITATION,    capturedArguments.get(4));
	
	verify(this.controller, times(1)).delegateIsDone(UserSeeInvitations.DONE_SUCCESS);
	
	}
	

}
