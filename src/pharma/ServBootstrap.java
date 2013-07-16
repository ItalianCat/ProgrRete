/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.PortableRemoteObject;

public class ServBootstrap implements ServBootstrap_I{ //, Serializable

	public ServBootstrap() throws RemoteException{
		UnicastRemoteObject.exportObject(this,0);
		PortableRemoteObject.exportObject(this);
	}
	
	@Override
	public Runnable getClientJRMP() throws RemoteException{
		return new ClientRunnable(true);
	}
	
	@Override
	public Runnable getClientIIOP() throws RemoteException{
		return new ClientRunnable(false);
	}
	
}
