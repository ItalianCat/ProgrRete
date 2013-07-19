/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Questa interfaccia rappresenta i metodi del server centrale che sono accessibili all'utente 
 * amministratore.
 */
public interface ServerAmministratore_I extends Remote{
	
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
	 * Questo metodo consente di rifornire il magazzino centrale di prodotti.
	 * @param id e' il codice identificativo del prodotto da acquistare
	 * @param prodotto e' l'oggetto che rappresenta il prodotto da acquistare
	 * @return ritorna true se il rifornimento e' andato a buon fine, false altrimenti
	 */
	public boolean compraProdotto(String id, O_Prodotto prodotto) throws RemoteException;
	
	/**
	 * Questo metodo consente di eliminare un prodotto dal magazzino centrale
	 * @param id e' il codice identificativo del prodotto da eliminare
	 */
	public void eliminaProdotto(String id) throws RemoteException;
	
	/**
	 * Questo metodo consente di caricare un magazzino di esempio per effettuare dei test.
	 */
	public void caricaEsempio() throws RemoteException;
	
	/**
	 * Questo metodo consente di de-esportare il server centrale e segnalare al sistema di 
	 * attivazione che il server e' inattivo e poi di richiedere la deregistrazione dal sistema 
	 * di attivazione. Al contempo sono salvati su file gli elenchi delle farmacie registrate e 
	 * dei prodotti a magazzino.
	 * @return ritorna true se lo spegnimento e' andato a buon fine, false altrimenti 
	 */
	public boolean spegniServer() throws RemoteException;
}
