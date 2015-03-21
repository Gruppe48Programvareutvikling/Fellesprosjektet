package pipeCreateEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import mainControlStructure.ControllerInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestCreateEvent {
	private UserCreateEvent creator;
	@Mock private ControllerInterface controller;

	@Before
	public void runBefore() {
		MockitoAnnotations.initMocks(this);
		this.creator = new UserCreateEvent(this.controller);
	}
	
	@Test
	public void test() {
		this.creator.startRunning();		
		verify(this.controller, times(1)).delegateIsReadyForNextInputWithPrompt("What is the name of your event?");
	}
}
