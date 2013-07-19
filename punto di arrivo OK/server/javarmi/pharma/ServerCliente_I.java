/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Questa interfaccia rappresenta i metodi del server centrale che sono accessibili ad un 
 * utente di tipo cliente finale.
 */
public interface ServerCliente_I extends Remote{

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
	
	/**
	 * Questo metodo consente di visualizzare l'elenco delle farmacie registrate.
	 * @return ritorna una stringa che rappresenta l'elenco delle farmacie registrate
	 */
	public String mostraFarmacie() throws RemoteException;
	
	/**
	 *Questo metodo consente di controllare se una farmacia indicata da un cliente finale e' 
	 *effettivamente presente nell'elenco delle farmacie registrate.
	 * @param nome e' il nome della farmacia di cui si vuole controllare la presenza in elenco
	 * @return ritorna true se la farmacia e' presente in elenco, false altrimenti
	 */
	public boolean checkFarmaciaRegistrata(String nome) throws RemoteException;
	
	/**
	 * Questo metodo consente di restituire la referenza remota di una farmacia ad un cliente.
	 * @param nome e' il nome della farmacia di cui si vuole recuperare la referenze remota
	 * @return ritorna la referenza remota alla farmacia
	 */
	public ClientFarmacia_I getFarmacia(String nome) throws RemoteException;
}
