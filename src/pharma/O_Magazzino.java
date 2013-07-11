/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class O_Magazzino implements Serializable{
	public final Map<String, O_Prodotto> magazzino;
	
	public O_Magazzino(){
		magazzino = new HashMap<String, O_Prodotto>();
	}
	
	public O_Prodotto checkProdottoAMagazzino(String id){
		if(magazzino.containsKey(id))
			return magazzino.get(id);
		return null;
	}
		
	public O_Prodotto vendiProdotto(String id, Integer qta){
		if(aggiornaMagazzino(id, magazzino.get(id), false))
			return new O_Prodotto(magazzino.get(id), qta);
		else
			return null;
	}
	
	public boolean compraProdotto(String id, O_Prodotto prodotto){
		if(aggiornaMagazzino(id, prodotto, true))
			return true;
		else
			return false;
	}
	
	public String toStringMagazzino(){
		String risultato = "\nProdotti disponibili presso il magazzino centrale:\n"
				+ "ID\t\tNome\t\tEccipiente\t\tProduttore\t\tFormato\t\tQuantita Disponibile\n";
		for(String id: magazzino.keySet()){
			risultato += id + "\t\t" + magazzino.get(id).toStringProdotto() + "\n";
		}
		return risultato;
	}
		
	private boolean aggiornaMagazzino(String id, O_Prodotto prodotto, boolean segno){
		if(segno){ //aggiungi qta
			if(!magazzino.containsKey(id))
				magazzino.put(id, prodotto);
			else
				magazzino.get(id).quantita += prodotto.quantita;
		}else{ //togli qta
			if(prodotto.quantita <= magazzino.get(id).quantita){
				magazzino.get(id).quantita -= prodotto.quantita;
				if(magazzino.get(id).quantita == 0)
					magazzino.remove(id);
			}else{
				return false;
			}
		}
		return true;
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
