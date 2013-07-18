/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Questa interfaccia definisce i metodi del server di bootstrap che sono accessibili ai 
 * client minimali per ottenere il codice vero e proprio dei client, da caricare dinamicamente.
 */
public interface ServBootstrap_I extends Remote{
	
	/**
	 * Questo metodo fornisce ad un client che usa il protocollo JRMP il codice da eseguire.
	 * @return ritorna un'istanza della classe ClientRunnable che definisce la prima interfaccia 
	 * tra un utente e il sistema
	 */
	public Runnable getClientJRMP() throws RemoteException;

	/**
	 * Questo metodo fornisce ad un client che usa il protocollo IIOP il codice da eseguire.
	 * @return ritorna un'istanza della classe ClientRunnable che definisce la prima interfaccia 
	 * tra un utente e il sistema
	 */
	public Runnable getClientIIOP() throws RemoteException;
}
