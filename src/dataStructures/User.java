package dataStructures;

public class User {
	public String username;
	public String password; //can be null
	public String mail; //can be null
	public String phoneNumber; //can be null
	
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
}
