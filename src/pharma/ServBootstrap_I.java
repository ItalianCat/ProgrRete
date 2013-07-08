package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServBootstrap_I extends Remote{
	public Runnable getClientJRMP() throws RemoteException;
	public Runnable getClientIIOP() throws RemoteException;
}
