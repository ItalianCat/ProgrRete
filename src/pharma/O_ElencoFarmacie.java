/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class O_ElencoFarmacie implements Serializable{
	public final Map<String, Remote> farmacie;
	
	public O_ElencoFarmacie(){
		farmacie = new HashMap<String, Remote>();
	}
	
	public boolean putFarmacia(String nome, Remote obj){
		if(!farmacie.containsKey(nome)){
			farmacie.put(nome, obj);
			return true;
		}
		return false;
	}
	
	public boolean removeFarmacia(String nome){
		if(farmacie.containsKey(nome)){
			farmacie.remove(nome);
			return true;
		}
		return false;
	}
	
	public Remote getFarmaciaStub(String nome){
		return farmacie.get(nome);
	}
	
	public String toStringFarmacie(){
		String risultato = "";
		for(String farmacia: farmacie.keySet()){
			risultato += farmacia + "\n"; 
		}
		return risultato;			
	}
	
	public boolean checkFarmaciaRegistrata(String nome){
		if(farmacie.containsKey(nome))
			if(farmacie.get(nome) != null)
				return true;
		return false;
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
