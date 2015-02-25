package superClasses;

import mainControlStructure.ControllerInterface;
import mainControlStructure.DelegateInterface;

public abstract class SuperUser implements DelegateInterface {
	protected ControllerInterface delegator = null;
	
//	public final void setDelegator(ControllerInterface delegator) {
//		this.delegator = delegator;
//	}
}
