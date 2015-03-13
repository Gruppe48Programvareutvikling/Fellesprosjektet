package pipeLogin;

import mainControlStructure.ControllerInterface;
import org.junit.*;  
import static org.junit.Assert.*;
import org.mockito.*;
import static org.mockito.Mockito.*;
import java.util.*;

public class TestLogin {

	private String username        = "vegather";
	private String wrongPassword   = "This is wrong";
	private String correctPassword = "1234";
	
	private UserLogin loginPipe;
	
	@Mock 
	private ControllerInterface controller;
	
	@Before
	public void runBefore() {		
		MockitoAnnotations.initMocks(this);
		this.loginPipe = new UserLogin(this.controller);
	}
	
	@Test
	public void tester() {
		
		// Starting
		this.loginPipe.startRunning();		
		this.loginPipe.userAsksForHelp();
		
		// First try
		this.loginPipe.sendNextInput(this.username);
		this.loginPipe.userAsksForHelp();
		this.loginPipe.sendNextInput(this.wrongPassword);
		this.loginPipe.userAsksForHelp();
		
		// Second try
		this.loginPipe.sendNextInput(this.username);
		this.loginPipe.userAsksForHelp();
		this.loginPipe.sendNextInput(this.wrongPassword);
		this.loginPipe.userAsksForHelp();
		
		// Third try
		this.loginPipe.sendNextInput(this.username);
		this.loginPipe.userAsksForHelp();
		this.loginPipe.sendNextInput(this.wrongPassword);
		
		// Starting over
		this.loginPipe = new UserLogin(this.controller);
		this.loginPipe.startRunning();

		// Correctly logging in
		this.loginPipe.sendNextInput(this.username);
		this.loginPipe.sendNextInput(this.correctPassword);
		
		
		ArgumentCaptor<String> readyForNextInputCaptor = ArgumentCaptor.forClass(String.class);
		verify(this.controller, times(14)).delegateIsReadyForNextInputWithPrompt(readyForNextInputCaptor.capture());
		List<String> readyForNextInputArguments = readyForNextInputCaptor.getAllValues();
		
		assertEquals("delegateIsReady... received wrong string after startRunning",
				UserLogin.PROMPT_USERNAME, 	   readyForNextInputArguments.get(0));
		
		assertEquals("delegateIsReady... received wrong string after asking for help after startRunning",
				UserLogin.HELP_USERNAME,   	   readyForNextInputArguments.get(1));
		
		assertEquals("delegateIsReady... received wrong string after entering username",
				UserLogin.PROMPT_PASSWORD, 	   readyForNextInputArguments.get(2));
		
		assertEquals("delegateIsReady... received wrong string after asking for help after entering username",
				UserLogin.HELP_PASSWORD,   	   readyForNextInputArguments.get(3));
		
		String _2attemptsLeftRegex = UserLogin.ERROR_WRONG_PW_PART_ONE + "2" +  UserLogin.ERROR_WRONG_PW_PART_TWO;
		assertEquals("delegateIsReady... received wrong string after entering first wrong password. 2 retries left", 
				_2attemptsLeftRegex, readyForNextInputArguments.get(4));

		assertEquals("delegateIsReady... received wrong string after asking for help after entering first wrong password",
				UserLogin.HELP_USERNAME, 	   readyForNextInputArguments.get(5));
		
		assertEquals("delegateIsReady... received wrong string after entering username",
				UserLogin.PROMPT_PASSWORD, 	   readyForNextInputArguments.get(6));
		
		assertEquals("delegateIsReady... received wrong string after asking for help after entering username",
				UserLogin.HELP_PASSWORD,   	   readyForNextInputArguments.get(7));
		
		String _1attemptLeftRegex = UserLogin.ERROR_WRONG_PW_PART_ONE + "1" +  UserLogin.ERROR_WRONG_PW_PART_TWO;
		assertEquals("delegateIsReady... received wrong string after entering second wrong password. 1 retry left", 
				_1attemptLeftRegex, readyForNextInputArguments.get(8));

		assertEquals("delegateIsReady... received wrong string after asking for help after entering second wrong password",
				UserLogin.HELP_USERNAME, 	   readyForNextInputArguments.get(9));
		
		assertEquals("delegateIsReady... received wrong string after entering username",
				UserLogin.PROMPT_PASSWORD, 	   readyForNextInputArguments.get(10));
		
		assertEquals("delegateIsReady... received wrong string after asking for help after entering username",
				UserLogin.HELP_PASSWORD,   	   readyForNextInputArguments.get(11));
		
		
		// Starting over
		assertEquals("delegateIsReady... received wrong string after startRunning",
				UserLogin.PROMPT_USERNAME, 	   readyForNextInputArguments.get(12));
		
		assertEquals("delegateIsReady... received wrong string after entering username",
				UserLogin.PROMPT_PASSWORD, 	   readyForNextInputArguments.get(13));
		
		
		
		ArgumentCaptor<String> doneCaptor = ArgumentCaptor.forClass(String.class);
		verify(this.controller, times(2)).delegateIsDone(doneCaptor.capture());		
		List<String> doneArguments = doneCaptor.getAllValues();
		
		assertEquals(UserLogin.ERROR_TOO_MANY_TRIES, doneArguments.get(0));
		assertEquals(null, doneArguments.get(1));		
		
		
		
		verify(this.controller, times(1)).delegateDidLogInUser(this.username);
	}
}
