/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Questa interfaccia definisce i metodi del mobile server Farmacia che sono accessibile 
 * ad un client di tipo Cliente.
 */
public interface ClientFarmacia_I extends Remote{
		
	/**
	 * Questo metodo visualizza una tabella con le disponibilita' di magazzino.
	 */
	public String toStringMagazzino() throws RemoteException;
	

	/**
	 * Questo metodo consente la vendita di un prodotto ad un cliente finale.
	 * @return ritorna un oggetto che rappresenta il prodotto frutto della vendita 
	 */
	public O_Prodotto venditaProdotto(String id, Integer quantita) throws RemoteException;
	
	/**
	 * Questo metodo consente di verificare se un prodotto e' presente in magazzino.
	 * @param id e' il codice identificativo del prodotto
	 * @return ritorna un oggetto che rappresenta il prodotto che si e' verificato essere a 
	 * magazzino, null se non e' presente a magazzino 
	 */
	public O_Prodotto checkProdottoAMagazzino(String id) throws RemoteException;
}
