package pipeCreateGroup;

import static org.junit.Assert.*;

//import org.junit.Before;
import org.junit.*;

public class TestCreateGroup {

	@Before
	public void runBefore() {
		System.out.println("Before");
	}
	
	@Test
	public void test() {
		System.out.println("Test");
	}

	
	@After
	public void runAfter() {
		System.out.println("After");
	}
}
