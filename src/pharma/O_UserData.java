/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.Serializable;
import java.util.*;

/**
 * Questa classe rappresenta un utente presente nella lista degli utenti registrati presso 
 * il server di autenticazione. Contiene tutti i dati rilevanti relativi ad un utente quali: 
 * password, categoria (cliente, farmacia, amministratore), data della registrazione, numero 
 * di accessi al sistema effettuati.
 */
@SuppressWarnings("serial")
public class O_UserData implements Serializable{
	public final String password;
	public final String categoria;
	public final Date dataRegistrazione;
	public Integer nAccessi;
	
	/**
	 * Questo costruttore crea il set dei dati di un utente.
	 */
	public O_UserData(String password, String categoria){
		this.password = password;
		this.categoria = categoria;
		dataRegistrazione = new Date();
		nAccessi = 0;
	}
	 
	/**
	 * Questo metodo consente di visualizzare una riga con tutte le informazioni relative 
	 * ad un utente.
	 * @return ritorna una stringa che rappresenta una riga della tabella degli utenti  
	 * registrati presso il server di autenticazione
	 */
	public String toStringUtente(){
		String risultato = password + fill(password.length()) + categoria + fill(categoria.length()) + dataRegistrazione + fill(dataRegistrazione.toString().length()) + nAccessi;
		return risultato;
	}
	
	/**
	 * Questo metodo consente di allineare i campi della tabella contenente gli utenti 
	 * registrati presso il server centrale.
	 * @param lung e' la lunghezza della stringa che va inserita in un campo della tabella
	 * @return ritorna una stringa che rappresenta gli spazi vuoti che devono essere interposti 
	 * tra due valori successivi di una riga della tabella
	 */
	private String fill(Integer lung){
		String risultato = "";
		Integer nVolte = 17 - lung;
		while (nVolte-- > 0)
			risultato += " ";
		return risultato;
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof O_UserData){
			O_UserData otherUD = (O_UserData)other;
			return otherUD.password.equals(password) && otherUD.categoria.equals(categoria)
						&& otherUD.dataRegistrazione.equals(dataRegistrazione)
						&& otherUD.nAccessi.equals(nAccessi);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return password.hashCode() + categoria.hashCode() + dataRegistrazione.hashCode() + nAccessi.hashCode();
	}
}
