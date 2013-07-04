package pharma;

import java.io.Serializable;
import java.util.Date;

public class UserData implements Serializable{
	public final String password;
	public final String categoria;
	public final Date dataRegistrazione;
	public Integer nAccessi;
	
	public UserData(String password, String categoria){
		this.password = password;
		this.categoria = categoria;
		dataRegistrazione = Time();
		nAccessi = 0;
	}
	  
	public String toStringProdotto(){
		String risultato = password + "\t\t" + categoria + "\t\t" + dataRegistrazione + "\t\t" + nAccessi;
		return risultato;
	}
}
