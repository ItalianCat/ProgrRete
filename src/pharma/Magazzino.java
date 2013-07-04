package pharma;

import java.util.HashMap;
import java.util.Map;

public class Magazzino {
	public final Map<String, Prodotto> magazzino;
	
	public Magazzino(){
		magazzino = new HashMap<String, Prodotto>();
	}
	
	public Prodotto checkProdottoAMagazzino(String id){
		if(magazzino.containsKey(id))
			return magazzino.get(id);
		return null;
	}
		
	public Prodotto vendiProdotto(String id, Integer qta){
		if(aggiornaMagazzino(id, magazzino.get(id), false))
			return new Prodotto(magazzino.get(id), qta);
		else
			return null;
	}
	
	public boolean compraProdotto(String id, Prodotto prodotto){
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
		
	private boolean aggiornaMagazzino(String id, Prodotto prodotto, boolean segno){
		if(segno){ //aggiungi qta
			if(!magazzino.containsKey(id))
				magazzino.put(id, prodotto);
			else
				magazzino.get(id).quantita += prodotto.quantita;  //creo metodo sotto Prodotto???
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
	
}
