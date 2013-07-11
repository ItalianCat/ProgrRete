/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings("serial")
public class O_UserData implements Serializable{
	public final String password;
	public final String categoria;
	public final Date dataRegistrazione;
	public Integer nAccessi;
	
	public O_UserData(String password, String categoria){
		this.password = password;
		this.categoria = categoria;
		dataRegistrazione = new Date();
		nAccessi = 0;
	}
	  
	public String toStringProdotto(){
		String risultato = password + "\t\t" + categoria + "\t\t" + dataRegistrazione + "\t\t" + nAccessi;
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
