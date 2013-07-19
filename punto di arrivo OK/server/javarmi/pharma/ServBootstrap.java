/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.PortableRemoteObject;

/**
 * Questa classe definisce i metodi del server di bootstrap che sono accessibili ai client 
 * minimali per ottenere il codice vero e proprio dei client, da caricare dinamicamente.
 * Il server viene esportato dualmente quindi non estende ne' UnicastRemoteObject, ne' 
 * PortableRemoteObject, ne' Activatable. 
 */
public class ServBootstrap implements ServBootstrap_I{ //check se con esportaz duale di mette hash e equ

	/**
	 * Questo costruttore esporta dualmente il server di bootstrap per i due protocolli 
	 * JRMP e IIOP.
	 */
	public ServBootstrap() throws RemoteException{
		UnicastRemoteObject.exportObject(this,0);
		PortableRemoteObject.exportObject(this);
	}
	
	/**
	 * Questo metodo fornisce ad un client che usa il protocollo JRMP il codice da eseguire.
	 * @return ritorna un'istanza della classe ClientRunnable che definisce la prima interfaccia 
	 * tra un utente e il sistema
	 */
	@Override
	public Runnable getClientJRMP() throws RemoteException{
		return new ClientRunnable(true);
	}

	/**
	 * Questo metodo fornisce ad un client che usa il protocollo IIOP il codice da eseguire.
	 * @return ritorna un'istanza della classe ClientRunnable che definisce la prima interfaccia 
	 * tra un utente e il sistema
	 */
	@Override
	public Runnable getClientIIOP() throws RemoteException{
		return new ClientRunnable(false);
	}
	
}
