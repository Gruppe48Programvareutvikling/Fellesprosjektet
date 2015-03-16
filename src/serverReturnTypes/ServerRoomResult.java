package serverReturnTypes;

import java.util.ArrayList;
import java.util.Collection;

import superClasses.ServerResult;

public class ServerRoomResult extends ServerResult {
	public ArrayList<Integer> roomNumber = new ArrayList<Integer>();
	public boolean roomIsAvailable;
	public ArrayList<Integer> numberOfSeats = new ArrayList<Integer>();
	
	public void printRoomNumbersWithSeats(ArrayList<Integer> roomNumber, ArrayList<Integer> numberOfSeats){
		System.out.println("Room Number" + " " + "Number of Seats");
		for (int i = 0; i < roomNumber.size(); i++) {
			System.out.printf("     " + "%h" + "              " + "%h\n",roomNumber.get(i),numberOfSeats.get(i));
		}
	}
	
}
