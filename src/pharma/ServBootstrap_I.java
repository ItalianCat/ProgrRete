/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServBootstrap_I extends Remote{
	public Runnable getClientJRMP() throws RemoteException;
	public Runnable getClientIIOP() throws RemoteException;
}
