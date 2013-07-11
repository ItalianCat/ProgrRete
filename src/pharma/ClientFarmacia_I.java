/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientFarmacia_I extends Remote{
	public String toStringMagazzino() throws RemoteException;
	public O_Prodotto venditaProdotto(String id, Integer quantita) throws RemoteException;
}
