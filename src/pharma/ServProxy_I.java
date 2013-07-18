/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;

/**
 * Questa interfaccia rappresenta i metodi del server proxy che sono accessibili alle istanze 
 * della classe ClientRunnable e gli ultimi due all'utente amministratore. 
 */
public interface ServProxy_I extends Remote{
	
	/**
	 * Questo metodo consente di chiedere al server proxy di chiedere al server di autenticazione 
	 * di registrare un nuovo utente nel sistema.
	 * @param user e' lo username dell'utente da registrare
	 * @param data sono i dati rilevanti che descrivono un utente, tra cui la password
	 * @return ritorna true se la registrazione e' andata a buon fine, false altrimenti
	 */
	public boolean registraUtente(String user, O_UserData data) throws RemoteException, ActivationException, IOException, ClassNotFoundException;
	
	/**
	 * Questo metodo consente di chiedere al server proxy di chiedere al server di autenticazione 
	 * di loggare un utente nel sistema.
	 * @param user e' lo username dell'utente da loggare
	 * @param psw e' la password fornita dall'utente che si vuole registrare
	 * @return ritorna true se l'utente e' stato loggato correttamente, false altrimenti
	 */
	public MarshalledObject<ClientMobileAgent_I> login(String user, String psw) throws RemoteException, ActivationException, IOException, ClassNotFoundException;
	
	/**
	 * Questo metodo consente di chiedere al server proxy di chiedere al server di autenticazione 
	 * di visualizzare la lista degli utenti registrati.
	 * @return ritorna una stringa che rappresenta l'elenco degli utenti registrati presso il 
	 * server di autenticazione.
	 */
	public String elencaUtenti() throws RemoteException;
	
	/**
	 * Questo metodo consente di de-registrare dai sistemi di naming e de-esportare il server 
	 * proxy e il server di bootstrap e di chiedere al server di autenticazione di 
	 * de-esportarsi e de-registrarsi dal sistema di attivazione.
	 * @return ritorna true se lo spegnimento e' andato a buon fine, false altrimenti
	 */
	public boolean spegniPBA() throws RemoteException;
}
