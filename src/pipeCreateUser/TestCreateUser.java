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
	
	private UserCreateUser creator;
	
	private String username;
	private String password = "tester";
	private String mail     = "tester@mail.com";
	private String phone    = "12345678";
	
	@Mock 
	private ControllerInterface controller;
	
	@Before
	public void runBefore() {		
		MockitoAnnotations.initMocks(this);
		this.creator = new UserCreateUser(this.controller);
	}
	
	@Test
	public void tester() {
		
		// TESTING THE STANDARD PROCESS
		this.username = this.getRandomUsername();
		this.creator.startRunning();		
		this.creator.userAsksForHelp();
		this.creator.sendNextInput(this.username);
		this.creator.userAsksForHelp();
		this.creator.sendNextInput(this.password);
		this.creator.userAsksForHelp();
		this.creator.sendNextInput(this.mail);
		this.creator.userAsksForHelp();
		this.creator.sendNextInput(this.phone);
		
		// TESTING USERNAME EXISTS
		this.creator = new UserCreateUser(this.controller);
		this.creator.startRunning();
		this.creator.sendNextInput(this.username);
		
		
		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
		verify(this.controller, times(10)).delegateIsReadyForNextInputWithPrompt(argumentCaptor.capture());

		List<String> capturedArguments = argumentCaptor.getAllValues();
		assertEquals(UserCreateUser.PROMPT_USERNAME, 	   capturedArguments.get(0));
		assertEquals(UserCreateUser.HELP_USERNAME,   	   capturedArguments.get(1));
		assertEquals(UserCreateUser.PROMPT_PASSWORD, 	   capturedArguments.get(2));
		assertEquals(UserCreateUser.HELP_PASSWORD,   	   capturedArguments.get(3));
		assertEquals(UserCreateUser.PROMPT_MAIL,     	   capturedArguments.get(4));
		assertEquals(UserCreateUser.HELP_MAIL,       	   capturedArguments.get(5));
		assertEquals(UserCreateUser.PROMPT_PHONE,    	   capturedArguments.get(6));
		assertEquals(UserCreateUser.HELP_PHONE,      	   capturedArguments.get(7));
		
		assertEquals(UserCreateUser.PROMPT_USERNAME,       capturedArguments.get(8));
		assertEquals(UserCreateUser.ERROR_USERNAME_EXISTS, capturedArguments.get(9));
		
		verify(this.controller, times(1)).delegateIsDone(UserCreateUser.DONE_SUCCESS);
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
	
	@After public void theMethodDirectlyUnderAtBeforeWillForSomeIncomprehensibleReasonNotBeCalledIfSomeMethod_ThisMethod_IsNotPresentDirectlyUnderAtAfter() { }
}
