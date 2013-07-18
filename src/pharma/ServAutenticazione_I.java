/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Questa interfaccia definisce i metodi del server di autenticazione che sono accessibili dal 
 * server proxy.
 */
public interface ServAutenticazione_I extends Remote{
	
	/**
	 * Questo metodo consente di registrare un nuovo utente presso il server di autenticazione
	 * @param user e' lo username scelto dall'utente
	 * @param data e' l'oggetto che contiene i dati rilevanti dell'utente, tra cui la password
	 * @return ritorna true se la registrazione e' andata a buon fine, false altrimenti
	 */
	public boolean registraUtente(String user, O_UserData data) throws RemoteException;
	
	/**
	 * Questo metodo consente di loggare un utente al sistema tramite il controllo delle credenziali 
	 * di accesso.
	 * @param user e' lo username dell'utente che vuole accedere al sistema
	 * @param psw e' la password fornita dall'utente che vuole accedere al sistema
	 * @return ritorna uno stream di byte che contengono il mobile agent specifico per la 
	 * categoria a cui appartiene l'utente, null se il login non e' andato a buon fine
	 */
	public MarshalledObject<ClientMobileAgent_I> login(String user, String psw) throws RemoteException, IOException;

	/**
	 * Questo metodo consente all'ammistratore di vedere gli utenti registrati tramite una 
	 * chiamata al server proxy che a sua volta invoca questo metodo del server di autenticazione.
	 * @return ritorna una stringa che rappresenta una tabella con i dati degli utenti registrati.
	 */
	public String elencaUtenti() throws RemoteException;
	
	/**
	 * Questo metodo consente all'amministratore di spegnere definitivamente il sistema tramite 
	 * una chiamata al proxy che a sua volta invoca questo metodo del server di autenticazione 
	 * per farlo spegnere, dopo aver salvato su disco l'elenco degli utenti registrati.
	 * @return ritorna true se lo spegnimento e' andato a buon fine, false altrimenti
	 */
	public boolean spegni() throws RemoteException;
	
}
