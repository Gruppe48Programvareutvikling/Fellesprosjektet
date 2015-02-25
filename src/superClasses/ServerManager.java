package superClasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class ServerManager {
	private final String USERNAME = "fohauske";
	private final String PASSWORD = "pikk1234";
	private final String CONN_STRING = "jdbc:mysql://mysql.stud.ntnu.no/vegarsth_gruppe48";
	
	/**
	 * Gets a connection to the MySQL database with the correct username and password
	 * @return The connection
	 * @throws SQLException
	 */
	protected Connection getDataBaseConnection() throws SQLException
	{
		return DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
	}
	
	/**
	 * Prints an SQLException to the console
	 * @param e The exception to print
	 */
	protected static void processSQLException(SQLException e) {
		System.err.println("Error message: " + e.getMessage());
		System.err.println("Error code: " + e.getErrorCode());
		System.err.println("SQL state: " + e.getSQLState());
	}
}
