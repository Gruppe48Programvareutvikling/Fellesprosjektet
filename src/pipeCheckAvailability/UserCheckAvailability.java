package pipeCheckAvailability;


import dataStructures.User;
import mainControlStructure.ControllerInterface;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.SuperUser;

public class UserCheckAvailability extends SuperUser {
	
	private enum State {SET_START_DATE, SET_END_DATE}
	
	private State state = State.SET_START_DATE;
	private ServerCheckAvailability server = new ServerCheckAvailability();
	String starttime;
	String endtime;
	
	public UserCheckAvailability(ControllerInterface delegator) {
		this.delegator = delegator;
	}
	
	public void startRunning() {
		this.delegator.delegateIsReadyForNextInputWithPrompt("Type in startdate (YYYY-MM-DD HH:MM:SS)");
	}
	
	public void sendNextInput(String nextInput) {
		switch (this.state){
		case SET_START_DATE:
			java.util.Date startdate = new java.util.Date();
			java.text.SimpleDateFormat sdf = 
			     new java.text.SimpleDateFormat(nextInput);
			this.starttime = sdf.format(startdate);
			if (Integer.parseInt(starttime.substring(5, 7))>12 || Integer.parseInt(starttime.substring(5, 7))<0
					|| Integer.parseInt(starttime.substring(8, 10)) > 31 || Integer.parseInt(starttime.substring(8, 10)) < 0
					|| Integer.parseInt(starttime.substring(11,13)) > 23 ||Integer.parseInt(starttime.substring(11, 13)) < 0
					|| Integer.parseInt(starttime.substring(14,16)) > 59 ||Integer.parseInt(starttime.substring(14, 16)) < 0
					|| Integer.parseInt(starttime.substring(17,19)) > 59 ||Integer.parseInt(starttime.substring(17, 19)) < 0){
				this.userAsksForHelp();
			}
			this.state = State.SET_END_DATE;
			this.delegator.delegateIsReadyForNextInputWithPrompt("Type in endtime (YYYY-MM-DD TT:MM:SS)");
			break;
		case SET_END_DATE:
			java.util.Date enddate = new java.util.Date();
			java.text.SimpleDateFormat sdfend = 
			     new java.text.SimpleDateFormat(nextInput);
			this.endtime = sdfend.format(enddate);
			if (Integer.parseInt(endtime.substring(5, 7))>12 || Integer.parseInt(endtime.substring(5, 7))<0
					|| Integer.parseInt(endtime.substring(8, 10)) > 31 || Integer.parseInt(endtime.substring(8, 10)) < 0
					|| Integer.parseInt(endtime.substring(11,13)) > 23 ||Integer.parseInt(endtime.substring(11, 13)) < 0
					|| Integer.parseInt(endtime.substring(14,16)) > 59 ||Integer.parseInt(endtime.substring(14, 16)) < 0
					|| Integer.parseInt(endtime.substring(17,19)) > 59 ||Integer.parseInt(endtime.substring(17, 19)) < 0){
				this.userAsksForHelp();
			}
			ServerAvailabilityResult availability = this.server.checkIfAvailable(this.starttime, this.endtime, User.currentUser().username);
			if (availability.isAvailable){
				this.delegator.delegateIsDone("You are available");				
			}
			else{
				this.delegator.delegateIsDone("You are busy sir\n" + "Eventname: \t" + availability.eventname 
						+ "\n" + "Startdate: \t" + availability.startdate + "\n" + "Enddate: \t" + availability.enddate);
			}
			break;
		}
	}
	
	public void userAsksForHelp() {
		switch (this.state){
		case SET_START_DATE:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type in what you want your " 
					+ "startdate to be, month cant be > 12 or < 1, date cant be > 31 or < 1" 
					+ ",\n hours cant be > 23 or < 0, min cant be > 59 or < 0 and second cant be > 59 or < 0 (can't be \"help\")"
					+ "\n Try one more time (YYYY-MM-DD HH:MM:SS)");
			break;
		case SET_END_DATE:
			this.delegator.delegateIsReadyForNextInputWithPrompt("You should type in what you want your " 
					+ "startdate to be, month cant be > 12 or < 1, date cant be > 31 or < 1" 
					+ ",\n hours cant be > 23 or < 0, min cant be > 59 or < 0 and second cant be > 59 or < 0 (can't be \"help\")"
					+ "\n Try one more time (YYYY-MM-DD HH:MM:SS)");
			break;
		}
	}
}
