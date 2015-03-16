package pipeCreateEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.ArrayList;
import java.util.List;
import mainControlStructure.ControllerInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pipeCreateGroup.UserCreateGroup;

public class TestCreateEvent {
	
private UserCreateEvent creator;
	
	private String event;

	
	@Mock
	private ControllerInterface controller;
	
	
	@Before
	public void runBefore() {
		MockitoAnnotations.initMocks(this);
		this.creator = new UserCreateEvent(this.controller);
	}
	
	@Test
	public void test() {
		this.creator.startRunning();		
		verify(this.controller, times(1)).delegateIsDone("UserCreateEvent is done");
	}

}
