/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Questa classe rappresenta un magazzino di prodotti, ognuno salvato con un suo codice identificativo 
 * e un oggetto che contiene tutte le informazioni rilevanti relative al prodotto. Il server 
 * centrale e le farmacie usano questo elenco per salvare l'elenco dei prodotti di cui dispongono.
 */
@SuppressWarnings("serial")
public class O_Magazzino implements Serializable{
	public final Map<String, O_Prodotto> magazzino;
	
	/**
	 * Questo costruttore crea una map di prodotti in cui la chiave e' costituita dal codice 
	 * identificativo del prodotto e il valore da un oggetto di tipo O_Prodotto che contiene 
	 * tutti i dati rilevanti del prodotto.
	 */
	public O_Magazzino(){
		magazzino = new HashMap<String, O_Prodotto>();
	}
	
	/**
	 * Questo metodo controlla se un prodotto di cui si riceve il codice identificativo, e' 
	 * presente nel magazzino.
	 * @param id e' il codice identificativo del prodotto di cui si vuol verificare la presenza 
	 * in magazzino
	 * @return ritorna un oggetto che rappresenta il prodotto presente nel magazzino, null se 
	 * il prodotto non e' presente nel magazzino
	 */
	public O_Prodotto checkProdottoAMagazzino(String id){
		if(magazzino.containsKey(id))
			return magazzino.get(id);
		return null;
	}
	
	/**
	 * Questo metodo consente di acquistare un prodotto dal magazzino, aggiornandone la quantita 
	 * residua ed eventualmente rimuovendolo dal magazzino se la quantita' arriva a zero.
	 * @param id e' il codice identificativo del prodotto da vendere
	 * @param qta e' la quantita' che si vuole vendere
	 * @return ritorna un oggetto che rappresenta il prodotto venduto, null se la vendita non 
	 * e' stata possibile per mancanza di pezzi sufficienti in magazzino
	 */
	public O_Prodotto vendiProdotto(String id, Integer qta){
		O_Prodotto prodotto = null;
		if(aggiornaMagazzino(id, new O_Prodotto(magazzino.get(id),qta), false)){
			if(magazzino.get(id).quantita == 0){
				prodotto = new O_Prodotto(magazzino.get(id), qta);
				magazzino.remove(id);
				return prodotto;
			}
			return new O_Prodotto(magazzino.get(id), qta);
		}else
			return null;
	}
	
	/**
	 * Questo metodo consente di rifornire il magazzino con nuovi prodotti o quantita aggiuntive 
	 * di prodotti gia' presenti.
	 * @param id e' il codice identificativo del prodotto da acquistare
	 * @param prodotto e' l'oggetto che rappresenta il prodotto da inserire a magazzino, 
	 * comprensivo della quantita' acquistata
	 * @return ritorna true se l'acquisto e' andato a buon fine, false altrimenti
	 */
	public boolean compraProdotto(String id, O_Prodotto prodotto){
		if(aggiornaMagazzino(id, prodotto, true))
			return true;
		else
			return false;
	}
	
	/**
	 * Questo metodo consente di visualizzare le disponibilita' di magazzino.
	 * @return ritorna una stringa che rappresenta la tabella coi dati dei prodotti
	 */
	public String toStringMagazzino(){
		String risultato = "\nProdotti disponibili presso il magazzino:\n"
				+ "ID       Nome          Eccipiente    Produttore    Formato       QtaDispo     \n";
		for(String id: magazzino.keySet()){
			risultato += id + "     " + magazzino.get(id).toStringProdotto() + "\n";
		}
		return risultato;
	}
	
	/**
	 * Questo metodo consente di aggiornare il magazzino in caso di acquisti e vendite di 
	 * prodotti. Nel caso degli acquisti gestisce sia l'ipotesi di nuovo prodotto che quella 
	 * di prodotto gia' inserito di cui si aggiunge una quantita'.
	 * @param id e' il codice identificativo del prodotto oggetto della transazione
	 * @param prodotto e' l'oggetto che rappresenta il prodotto oggetto della transazione
	 * @param segno identifica se la transazione e' un acquisto (true) o una vendita (false)
	 * @return ritorna true se l'operazione e' andata a buon fine, false altrimenti
	 */
	private boolean aggiornaMagazzino(String id, O_Prodotto prodotto, boolean segno){
		if(segno){ //aggiungi qta
			if(!magazzino.containsKey(id))
				magazzino.put(id, prodotto);
			else
				magazzino.get(id).quantita += prodotto.quantita;
		}else{ //togli qta
			if(prodotto.quantita <= magazzino.get(id).quantita){
				magazzino.get(id).quantita -= prodotto.quantita;
			}else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Questo metodo consente di eliminare un prodotto dal magazzino. Utile in caso di 
	 * errata codifica.
	 * @param id e' il codice identificativo del prodotto da eliminare dal magazzino
	 */
	public void eliminaProdotto(String id){
		magazzino.remove(id);
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof O_Magazzino){
			O_Magazzino otherM = (O_Magazzino)other;
			return otherM.magazzino.equals(magazzino);
		}
		return false;
	}
		
	@Override
	public int hashCode(){
		return magazzino.hashCode();
	}

}
