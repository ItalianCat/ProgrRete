package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bootstrap extends Remote{
	public Runnable getClientRMIRegistry() throws RemoteException;
	public Runnable getClientCosNaming() throws RemoteException;
}
