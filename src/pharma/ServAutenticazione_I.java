/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;

public interface ServAutenticazione_I extends Remote{
	public boolean registraUtente(String user, O_UserData data) throws RemoteException, ActivationException, IOException, ClassNotFoundException;
	public MarshalledObject<ClientMobileAgent_I> login(String user, String psw) throws RemoteException, ActivationException, IOException, ClassNotFoundException;
	public boolean spegni() throws RemoteException;
}
