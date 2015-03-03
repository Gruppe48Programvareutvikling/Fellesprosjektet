package serverReturnTypes;

import java.util.ArrayList;
import java.util.Collection;

import superClasses.ServerResult;

public class ServerInvitationsResult extends ServerResult {
	public Collection invitations = new ArrayList<String>();
}
