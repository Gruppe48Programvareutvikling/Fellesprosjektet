package pipeCreateGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import pipeCreateUser.ServerCreateUser;
import dataStructures.Group;
import serverReturnTypes.ServerAvailabilityResult;
import superClasses.ServerManager;
import superClasses.ServerResult;

public class ServerCreateGroup extends ServerManager {

	private final String SQL_CHECK_IF_GROUP_IS_AVAILABLE = "SELECT groupName FROM Group WHERE groupName=?";
	private final String SQL_CHECK_IF_USER_IS_IN_DB = "SELECT userName FROM User WHERE userName=?";
	private final String SQL_CHECK_IF_GROUP_IS_IN_DB = "SELECT groupName FROM Group WHERE groupName=?";
	private final String SQL_CREATE_GROUP			= "INSERT INTO Group(groupName) VALUES (?)";
	private final String SQL_INSERT_SUPERGROUP		= "update `Group` set superGroup = ? where groupName = ?";


	public ServerAvailabilityResult checkIfGroupNameIsAvailable(String groupNameToCheck){
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();

		ResultSet result = null;
		try(
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CHECK_IF_GROUP_IS_AVAILABLE, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				)
				{
			statement.setString(1, groupNameToCheck);
			result = statement.executeQuery();

			boolean gotResult = false;

			while (result.next()){
				gotResult = true;
				theResult.isAvailable = false;
				theResult.didSucceed = true;
			}

			if (gotResult == false){
				theResult.isAvailable = true;
				theResult.didSucceed = true;
			}

				}catch (SQLException e){
					System.out.println("Got exception");
					ServerManager.processSQLException(e);
					theResult.isAvailable = false;
					theResult.didSucceed = false;
					theResult.errorMessage = e.getMessage();
				}
		return theResult;
	}
	
	public ServerAvailabilityResult checkIfUserOrGroupIsInDatabse(String nameToCheck, String groupOrPerson){
		ServerAvailabilityResult theResult = new ServerAvailabilityResult();
		String SQL_QUESTION = "";
		ResultSet result = null;
		if (groupOrPerson.equals("person")){
			SQL_QUESTION = SQL_CHECK_IF_USER_IS_IN_DB;
		}
		else if (groupOrPerson.equals("group")){
			SQL_QUESTION = SQL_CHECK_IF_GROUP_IS_IN_DB;			
		}
		else{
			System.out.println("There where an error while asking if the question was group or person");
		}
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_QUESTION, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			) 
		{
			statement.setString(1, nameToCheck);
			result = statement.executeQuery();
			
			boolean gotResult = false;
			
			while (result.next()) {
				gotResult = true;
				theResult.isAvailable = true;
				theResult.didSucceed = true;
			} 
			
			if (gotResult == false) {
				theResult.isAvailable = false;
				theResult.didSucceed = true;
			}
		} catch (SQLException e) {
			System.out.println("Got exception");
			ServerManager.processSQLException(e);
			
			theResult.isAvailable = false;
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		
		return theResult;
	}
	
	public ServerResult createGroup(Group groupToCreate) {
		ServerResult theResult = new ServerResult();
		
		try (
				Connection connection = this.getDataBaseConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_CREATE_GROUP, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			) 
		{
			statement.setString(1, groupToCreate.groupName);
			int affected = statement.executeUpdate();
			
			if (affected == 1) {
				theResult.didSucceed = true;
			} else {
				theResult.didSucceed = false;
				theResult.errorMessage = "Unknown error while creating group";
			}
		} catch (SQLException e) {
			ServerCreateUser.processSQLException(e);
			
			theResult.didSucceed = false;
			theResult.errorMessage = e.getMessage();
		}
		
		if (theResult.didSucceed){
			return updateSuperGroup(groupToCreate);
		}
		else{
			return theResult;			
		}
	}
	
	private ServerResult updateSuperGroup(Group groupToUpdate){
		ServerResult theResult = new ServerResult();
		for (Group group : groupToUpdate.subGroups) {
			try (
					Connection connection = this.getDataBaseConnection();
					PreparedStatement statement = connection.prepareStatement(SQL_INSERT_SUPERGROUP, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					) 
					{
				group.supergroup = groupToUpdate.groupName;
				statement.setString(1, group.supergroup);
				statement.setString(2, group.groupName);
				
				int affected = statement.executeUpdate();
				if (affected == 1) {
					theResult.didSucceed = true;
				} else {
					theResult.didSucceed = false;
					theResult.errorMessage = "Unknown error while creating group";
				}
				if (theResult.didSucceed == false){
					return theResult;
				}
					}catch (SQLException e) {
						ServerCreateUser.processSQLException(e);
						
						theResult.didSucceed = false;
						theResult.errorMessage = e.getMessage();
					}

		}
		return theResult;
	}

}
