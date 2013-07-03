package pharma;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ElencoFarmacie implements Serializable{
	public final Map<String, Remote> farmacie;
	
	public ElencoFarmacie(){
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
	
	public int getNFarmacie(){
		return farmacie.size();
	}
	
}
