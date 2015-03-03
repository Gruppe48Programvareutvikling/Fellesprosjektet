package serverReturnTypes;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.ArrayList;

import superClasses.ServerResult;

public class ServerAvailabilityResult extends ServerResult {
	public boolean isAvailable;
	
	public List<String> name=new ArrayList<String>();
	public List<String> description=new ArrayList<String>();
	public List<Date> startDate=new ArrayList<Date>();
	public List<Time> startTime=new ArrayList<Time>();
	public List<Date> endDate=new ArrayList<Date>();
	public List<Time> endTime=new ArrayList<Time>();
	public List<String> location=new ArrayList<String>();
	public List<String> roomNumber=new ArrayList<String>();
	
	
	public String toString(){
		
		String printer="";
		for (int i=0;i<name.size();i++){
			printer+="Name: "+"\t\t"+name.get(i)+"\n";
			printer+="Description: "+"\t"+description.get(i)+"\n";
			printer+="Location: "+"\t"+location.get(i)+"\n";
			if (roomNumber.get(i)==null){
				printer+="Roomnumber: "+"\t-\n";
			}
			else{
				printer+="Roomnumber: "+"\t"+roomNumber.get(i)+"\n";
			}
			printer+="Start: "+"\t\t"+startDate.get(i).toString()+" "+startTime.get(i).toString()+"\n";
			printer+="End: "+"\t\t"+endDate.get(i).toString()+" "+endTime.get(i).toString()+"\n";
			printer+=("-----------------------------------------------\n");
		
		}
		return printer;
	}
}
