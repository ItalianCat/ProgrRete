/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.rmi.*;

/**
 * Questa interfaccia definisce un metodo disponibile a tutti i client (amministratore, 
 * farmacia, cliente).
 */
public interface ClientMobileAgent_I extends Remote{
	
	/**
	 * Questo metodo definisce il comportamento specifico di un determinato client.
	 */
	public void act() throws RemoteException;
}
