/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Questa interfaccia rappresenta i metodi del server centrale che sono accessibili ad un 
 * utente di tipo farmacia.
 */
public interface ServerFarmacia_I extends Remote{
	/**
	 * Questo metodo consente di registrare una farmacia con la sua referenza remota.
	 * @param nome e' il nome della farmacia da registrare
	 * @param obj e' la referenza remota della farmacia da registrare
	 * @return ritorna true se la registrazione e' andata a buon fine, false altrimenti
	 */
	public boolean registra(String nome, Remote obj) throws RemoteException;
	
	/**
	 * Questo metodo consente di deregistrare una farmacia dall'elenco delle farmacie registrate.
	 * @param nome e' il nome della farmacia da deregistrare
	 * @return ritorna true se la deregistrazione e' andata a buon fine, false altrimenti
	 */
	public boolean deregistra(String nome) throws RemoteException;
	
	/**
	 * Questo metodo consente di visualizzare il magazzino centrale.
	 * @return ritorna una stringa che rappresenta l'elenco dei prodotti a magazzino
	 */
	public String toStringMagazzinoCentrale() throws RemoteException;
	
	/**
	 * Questo metodo consente di controllare se un prodotto e' effettivamente presente nel 
	 * magazzino centrale.
	 * @param id e' il codice identificativo del prodotto di cui si vuole controllare la presenza
	 * @return ritorna true se il prodotto e' presente in magazzino, false altrimenti
	 */
	public O_Prodotto checkProdottoAMagazzino(String id) throws RemoteException;
	
	/**
	 * Questo metodo consente di vendere un prodotto ad una farmacia o ad un cliente finale.
	 * @param id e' il codice identificativo del prodotto da vendere
	 * @param qta e' la quantita' di prodotto da vendere
	 * @return ritorna l'istanza del prodotto venduto
	 */
	public O_Prodotto vendiProdotto(String id, Integer qta) throws RemoteException;
}
