/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.rmi.*;

public interface ClientMobileAgent_I extends Remote{
	public void act() throws RemoteException;
}
