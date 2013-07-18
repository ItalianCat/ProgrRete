/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Questa classe rappresenta una tabella di utenti, ognuna salvato col suo username e con un 
 * oggetto che contiene tutti i dati rilevanti relativi all'utente. Il server di 
 * autenticazione usa questo elenco per salvare gli utenti che si sono registrati e 
 * controllare le credenziali ad ogni accesso.
 */
@SuppressWarnings("serial")
public class O_ElencoUser implements Serializable{
	
	public final Map<String, O_UserData> users;
	
	/**
	 * Questo costruttore crea una map di utenti in cui la chiave e' costituita dallo username 
	 * dell'utente e il valore da un oggetto di tipo O_UserData che contiene tutti i dati 
	 * rilevanti dell'utente, tra cui la password.
	 */
	public O_ElencoUser(){
		users = new HashMap<String, O_UserData>();
	}
	
	/**
	 * Questo metodo consente di inserire un utente nella tabella.
	 * @param user e' lo username scelto dall'utente
	 * @param data e' l'oggetto che contiene tutte le informazioni disponibili sull'utente
	 * @return ritorna true se l'operazione e' andata a buon fine, false altrimenti 
	 */
	public boolean putUser(String user, O_UserData data){
		if(!users.containsKey(user)){
			users.put(user, data);
			return true;
		}
		return false;
	}
	
	/**
	 * Questo metodo consente di controllare se lo username fornito dal client al momento 
	 * del login corrisponde effettivamente ad uno user registrato presso il server di 
	 * autenticazione e, in caso positivo, controlla se la password fornita e' corretta.	
	 * @param user e' lo username dell'utente che vuole effettuare il login
	 * @param psw e' la password fornita dall'utente che vuole effettuare il login
	 * @return
	 */
	public boolean checkLogin(String user, String psw){
		if(users.containsKey(user)){
			if(users.get(user).password.equals(psw))
				return true;
			else
				return false;
		}else
			return false;
	}
	
	/**
	 * Questo metodo restituisce la categoria di un utente. I valori possibili sono cliente, 
	 * farmacia e amministratore. E' invocato dal server di autenticazione per capire quale 
	 * costruttore mobile agent costruire al momento di un login.
	 * @param user e' lo username dell'utente di cui si vuole conoscere la categoria
	 * @return ritorna una stringa che rappresenta la categoria dell'utente
	 */
	public String getCategoria(String user){
		return users.get(user).categoria;
	}
	
	/**
	 * Questo metodo consente di aggiornare la statistica relativa al numero di accessi 
	 * effettuati da un utente. E' incrementato dal server di autenticazione ad ogni login.
	 * @param user e' lo username dell'utente di cui si vuole aggiornare la statistica sugli accessi
	 */
	public void aggiornaNAccessi(String user){
		users.get(user).nAccessi++;
	}
		
	/**
	 * Questo metodo consente di visualizzare un elenco di tutti gli utenti registati presso 
	 * il server di autenticazione.
	 * @return ritorna una stringa che rappresenta l'elenco degli utenti registrati
	 */
	public String toStringUtenti(){
		String risultato = "\nUtenti registrati presso il sistema Pharma:\n"
				+ "Username         Password         Categoria        DataRegistr      NAccessi\n";
		for(String user: users.keySet()){
			Integer nVolte = 17 - user.length();
			risultato += user;
			while (nVolte-- > 0){
				risultato += " ";
			}
			risultato += users.get(user).toStringUtente() + "\n";
		}
		return risultato;			
	}	
	
	@Override
	public boolean equals(Object other){
		if(other instanceof O_ElencoUser){
			O_ElencoUser otherE = (O_ElencoUser)other;
			return otherE.users.equals(users);
		}
		return false;
	}
		
	@Override
	public int hashCode(){
		return users.hashCode();
	}
	
}
