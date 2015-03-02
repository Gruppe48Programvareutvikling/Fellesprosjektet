package pipeCreateUser;

import mainControlStructure.ControllerInterface;

import org.junit.*;
//import org.mockito.*;

import junit.framework.TestCase;

public class TestCreateUser extends TestCase implements ControllerInterface {

	UserCreateUser creator;
	
	private enum TestState {FIRST, SECOND, THIRD};
	private TestState state = TestState.FIRST;
	
	@Before
	public void before() {
		this.creator = new UserCreateUser(this);
//		this.mainLoop();
	}
	
	private void mainLoop() {
		switch (this.state) {
		case FIRST:
			this.creator.startRunning();
		case SECOND:
		case THIRD:
		default:
			break;
		}
	}

	@Test
	public void test() {
		assertEquals(true, true);
//		this.mainLoop();
	}

//	@After
//	public void after() {
//		
//	}
	
	
	public void delegateIsDone(String successMessage) {
		
	}
	
	@Test
	public void delegateIsReadyForNextInputWithPrompt(String promptToUser) {
		switch (this.state) {
		case FIRST:
//			assertEquals("This should match", UserCreateUser.PROMPT_AFTER_START_RUNNING, promptToUser);
			
		case SECOND:
		case THIRD:
		default:
			break;
		}
	}
	
	public void delegateDidLogInUser(String username) {}
}
