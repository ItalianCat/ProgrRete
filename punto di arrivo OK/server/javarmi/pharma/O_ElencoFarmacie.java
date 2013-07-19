/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;

/**
 * Questa classe rappresenta una tabella di farmacie, ognuna salvata col suo nome e con la sua 
 * referenza remota. Il server centrale usa questo elenco per salvare le farmacie che si 
 * sono registrate. Inoltre il server centrale passa le referenze remote ai clienti finali che 
 * vogliono interagire con una farmacia.  
 */
@SuppressWarnings("serial")
public class O_ElencoFarmacie implements Serializable{
	
	public final Map<String, Remote> farmacie;
	
	/**
	 * Questo costruttore crea una map di farmacie in cui la chiave e' costituita dal nome 
	 * della farmacia e il valore dalla referenza remota alla farmacia.
	 */
	public O_ElencoFarmacie(){
		farmacie = new HashMap<String, Remote>();
	}
	
	/**
	 * Questo metodo consente di inserire una farmacia nella tabella.
	 * @param nome e' il nome della farmacia da registrare (coincide con lo username della farmacia)
	 * @param obj e'la referenza remota alla farmacia
	 * @return ritorna true se l'operazione e' andata a buon fine, false altrimenti
	 */
	public boolean putFarmacia(String nome, Remote obj){
		if(!farmacie.containsKey(nome)){
			farmacie.put(nome, obj);
			return true;
		}
		return false;
	}
	
	/**
	 * Questo metodo consente di eliminare una farmacia dalla tabella, su richiesta della farmacia 
	 * stessa, qualora questa voglia togliersi definitivamente dal sistema.
	 * @param nome e' il nome della farmacia da eliminare
	 * @return ritorna true se l'operazione e' andata a buon fine, false altrimenti
	 */
	public boolean removeFarmacia(String nome){
		if(farmacie.containsKey(nome)){
			farmacie.remove(nome);
			return true;
		}
		return false;
	}

	/**
	 * Questo metodo consente di controllare se il nome di farmacia fornito dal cliente 
	 * corrisponde effettivamente ad una farmacia registrata presso il server centrale.
	 * @param nome e' il nome della farmacia di cui si vuol verificare l'esistenza in elenco
	 * @return ritorna true se la farmacia e' presente in elenco, false altrimenti
	 */
	public boolean checkFarmaciaRegistrata(String nome){
		if(farmacie.containsKey(nome))
			if(farmacie.get(nome) != null)
				return true;
		return false;
	}
	
	/**
	 * Questo metodo consente di passare la referenza remota di una farmacia al cliente che 
	 * l'ha richiesta.
	 * @param nome e' il nome della farmacia di cui si vuole la referenza remota
	 * @return ritorna la referenza remota della farmacia da usare per invocarne i metodi
	 */
	public Remote getFarmaciaStub(String nome){
		return farmacie.get(nome);
	}
	
	/**
	 * Questo metodo consente di visualizzare un elenco di tutte le farmacie registate presso 
	 * il server centrale.
	 * @return ritorna una stringa che rappresenta l'elenco delle farmacie registrate
	 */
	public String toStringFarmacie(){
		String risultato = "";
		for(String farmacia: farmacie.keySet()){
			risultato += farmacia + "\n"; 
		}
		return risultato;			
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof O_ElencoFarmacie){
			O_ElencoFarmacie otherE = (O_ElencoFarmacie)other;
			return otherE.farmacie.equals(farmacie);
		}
		return false;
	}
		
	@Override
	public int hashCode(){
		return farmacie.hashCode();
	}
}
