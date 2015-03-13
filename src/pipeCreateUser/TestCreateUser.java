package pipeCreateUser;


import static org.junit.Assert.*;
import org.mockito.*;
import org.junit.Test;
import mainControlStructure.ControllerInterface;
import org.junit.*;  
import static org.mockito.Mockito.*;
//import org.junit.runners.MethodSorters;
import java.util.*;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCreateUser {
	
	// TO DELETE THESE TEST USER, EXECUTE THE FOLLOWING QUERY
	// DELETE FROM User WHERE userName LIKE 'tester_%';
	
	private UserCreateUser createUserPipe;
	
	private String username;
	private String password = "tester";
	private String mail     = "tester@mail.com";
	private String phone    = "12345678";
	
	@Mock 
	private ControllerInterface controller;
	
	@Before
	public void runBefore() {		
		MockitoAnnotations.initMocks(this);
		this.username = this.getRandomUsername();
		this.createUserPipe = new UserCreateUser(this.controller);
	}
	
	@Test
	public void tester() {
		
		// TESTING THE STANDARD PROCESS
		this.createUserPipe.startRunning();		
		this.createUserPipe.userAsksForHelp();
		this.createUserPipe.sendNextInput(this.username);
		this.createUserPipe.userAsksForHelp();
		this.createUserPipe.sendNextInput(this.password);
		this.createUserPipe.userAsksForHelp();
		this.createUserPipe.sendNextInput(this.mail);
		this.createUserPipe.userAsksForHelp();
		this.createUserPipe.sendNextInput(this.phone);
		
		// TESTING USERNAME EXISTS
		this.createUserPipe = new UserCreateUser(this.controller);
		this.createUserPipe.startRunning();
		this.createUserPipe.sendNextInput(this.username);
		
		
		ArgumentCaptor<String> delegateIsReadyCaptor = ArgumentCaptor.forClass(String.class);
		verify(this.controller, times(10)).delegateIsReadyForNextInputWithPrompt(delegateIsReadyCaptor.capture());
		List<String> delegateIsReadyArguments = delegateIsReadyCaptor.getAllValues();
		
		assertEquals("delegateIsReady... received unexpected argument after startRunning(). Should be prompting for username", 
				UserCreateUser.PROMPT_USERNAME, 	   delegateIsReadyArguments.get(0));
		
		assertEquals("delegateIsReady... received unexpected argument after asking for help about username", 
				UserCreateUser.HELP_USERNAME,   	   delegateIsReadyArguments.get(1));
		
		assertEquals("delegateIsReady... received unexpected argument after entering username", 
				UserCreateUser.PROMPT_PASSWORD, 	   delegateIsReadyArguments.get(2));
		
		assertEquals("delegateIsReady... received unexpected argument after asking for help about password", 
				UserCreateUser.HELP_PASSWORD,   	   delegateIsReadyArguments.get(3));
		
		assertEquals("delegateIsReady... received unexpected argument after entering password", 
				UserCreateUser.PROMPT_MAIL,     	   delegateIsReadyArguments.get(4));
		
		assertEquals("delegateIsReady... received unexpected argument after asking for help about mail", 
				UserCreateUser.HELP_MAIL,       	   delegateIsReadyArguments.get(5));
		
		assertEquals("delegateIsReady... received unexpected argument after entering mail", 
				UserCreateUser.PROMPT_PHONE,    	   delegateIsReadyArguments.get(6));
		
		assertEquals("delegateIsReady... received unexpected argument after asking for help about phone number", 
				UserCreateUser.HELP_PHONE,      	   delegateIsReadyArguments.get(7));
		
		
		assertEquals("delegateIsReady... received unexpected argument after startRunning(). Should be prompting for username", 
				UserCreateUser.PROMPT_USERNAME,       delegateIsReadyArguments.get(8));
		
		assertEquals("delegateIsReady... received unexpected argument after entering a username that already exists", 
				UserCreateUser.ERROR_USERNAME_EXISTS, delegateIsReadyArguments.get(9));
		
		
		ArgumentCaptor<String> delegateIsDoneCaptor = ArgumentCaptor.forClass(String.class);
		verify(this.controller, times(1)).delegateIsDone(delegateIsDoneCaptor.capture());
		List<String> delegateIsDoneArguments = delegateIsDoneCaptor.getAllValues();
		
		assertEquals("delegateIsDone received unexpected argument when done creating user", 
				UserCreateUser.DONE_SUCCESS, delegateIsDoneArguments.get(0));
	}

	public String getRandomUsername() {
		String username = "tester_";
		Random myRandom = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < 5; ++i) {
			int number = Math.abs(myRandom.nextInt()) % 10;
			username += number;
		}
		
		return username;
	}
	
	@After 
	public void theMethodDirectlyUnderAtBeforeWillForSomeIncomprehensibleReasonNotBeCalledIfSomeMethod_ThisMethod_IsNotPresentDirectlyUnderAtAfter() { }
}
