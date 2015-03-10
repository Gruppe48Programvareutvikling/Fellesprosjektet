package pipeCreateGroup;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mainControlStructure.ControllerInterface;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;




public class TestCreateGroup {

	private UserCreateGroup creator;
	
	private String groupname;
	private String answere1 = "yes";
	private String answere3 = "group";
	private String answere4 = "person";

	
	@Mock
	private ControllerInterface controller;
	
	
	@Before
	public void runBefore() {
		MockitoAnnotations.initMocks(this);
		this.creator = new UserCreateGroup(this.controller);
	}
	
	@Test
	public void test() {
		
		this.groupname = this.getRandomGroupName();
		this.creator.startRunning();		
		this.creator.userAsksForHelp();
		this.creator.sendNextInput(this.groupname);
		this.creator.userAsksForHelp();
		int x = getRandomGroupMembers();
		List<String> groupOrPersonList = new ArrayList<String>();
		for (int i = 0; i < x; i++) {
			String group = "subgroup";
			String user = "grouptester";
			this.creator.sendNextInput(this.answere1);
			this.creator.userAsksForHelp();
			String groupOrPerson = getRandomAnswere34();
			groupOrPersonList.add(groupOrPerson);
			if (groupOrPerson.equals("group")){
				this.creator.sendNextInput(groupOrPerson);	
				this.creator.userAsksForHelp();
				group += i;
				this.creator.sendNextInput(group);
				this.creator.userAsksForHelp();
			}
			else{
				user += i;
				this.creator.sendNextInput(groupOrPerson);
				this.creator.userAsksForHelp();
				this.creator.sendNextInput(user);
				this.creator.userAsksForHelp();
			}
		}
		this.creator.sendNextInput("no");
		
		//Testing GROUPNAME exist
		this.creator = new UserCreateGroup(this.controller);
		this.creator.startRunning();
		this.creator.sendNextInput(this.groupname);
		
		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
		verify(this.controller, times(x*6+6)).delegateIsReadyForNextInputWithPrompt(argumentCaptor.capture());
		List<String> capturedArguments = argumentCaptor.getAllValues();
		
		assertEquals(UserCreateGroup.PROMPT_GROUPNAME, 	   				capturedArguments.get(0));
		assertEquals(UserCreateGroup.HELP_SET_NAME_FOR_GROUP,   		capturedArguments.get(1));
		assertEquals(UserCreateGroup.PROMPT_MORE_PARTICIPANTS,  		capturedArguments.get(2));
		assertEquals(UserCreateGroup.HELP_ADD_MORE_MEMBERS,   			capturedArguments.get(3));
		for (int i = 0; i < x; i++) {
			assertEquals(UserCreateGroup.PROMPT_GROUP_OR_PERSON,  			capturedArguments.get(i*6+4));
			assertEquals(UserCreateGroup.HELP_ENTER_GROUP_OR_PERSON,   		capturedArguments.get(i*6+5));
			if (groupOrPersonList.get(i).equals("group")){
				assertEquals(UserCreateGroup.PROMPT_ENTER_GROUPNAME,  			capturedArguments.get(i*6+6));
				assertEquals(UserCreateGroup.HELP_ENTER_NAME_OF_GROUP_TO_ADD,   capturedArguments.get(i*6+7));
				assertEquals(UserCreateGroup.PROMPT_MORE_PARTICIPANTS,  		capturedArguments.get(i*6+8));
				assertEquals(UserCreateGroup.HELP_ADD_MORE_MEMBERS,   			capturedArguments.get(i*6+9));
			}
			else{
				assertEquals(UserCreateGroup.PROMPT_ENTER_USERNAME,  			capturedArguments.get(i*6+6));
				assertEquals(UserCreateGroup.HELP_ENTER_NAME_OF_USER_TO_ADD,   	capturedArguments.get(i*6+7));
				assertEquals(UserCreateGroup.PROMPT_MORE_PARTICIPANTS,  		capturedArguments.get(i*6+8));
				assertEquals(UserCreateGroup.HELP_ADD_MORE_MEMBERS,   			capturedArguments.get(i*6+9));
			}
		}
		
		assertEquals(UserCreateGroup.PROMPT_GROUPNAME,       capturedArguments.get((x-1)*6+10));
		assertEquals(UserCreateGroup.PROMPT_TAKEN_GROUPNAME, capturedArguments.get((x-1)*6+11));
		
		verify(this.controller, times(1)).delegateIsDone(UserCreateGroup.PROMPT_DONE_SUCCESS);
	}


	
	public String getRandomGroupName() {
		String groupname = "GROUPtester_";
		Random myRandom = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < 5; ++i) {
			int number = Math.abs(myRandom.nextInt()) % 10;
			groupname += number;
		}
		return groupname;
	}

	public String getRandomAnswere34(){
		Random ran = new Random();
		int x = ran.nextInt(2);
		if (x == 0){
			return answere3;
		}
		else{
			return answere4;
		}
	}
	
	public int getRandomGroupMembers(){
		Random ran = new Random();
		return ran.nextInt(5)+1;
	}
}
