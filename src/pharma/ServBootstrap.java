package pharma;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.PortableRemoteObject;

@SuppressWarnings("serial")
public class ServBootstrap implements ServBootstrap_I, Serializable{

	public ServBootstrap() throws RemoteException{
		UnicastRemoteObject.exportObject(this);
		PortableRemoteObject.exportObject(this);
		System.out.println("\nIl server di Bootstrap e' stato esportato dualmente.");
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
