package dataStructures;

public class User {
	public String username;
	public String password; 	// can be null
	public String mail; 		// can be null
	public String phoneNumber;	// can be null
	
	public User() {}
	
	public User(String username){
		this.username = username;
	}
	
	public User(String username, String password, String mail, String phoneNumber) {
		this.username = username;
		this.password = password;
		this.mail = mail;
		this.phoneNumber = phoneNumber;
	}
	
	private static User currentUserSingleton = null;
	public static User currentUser() {
		if (currentUserSingleton == null) {
	         currentUserSingleton = new User();
	    }
		return currentUserSingleton;
	}
}
