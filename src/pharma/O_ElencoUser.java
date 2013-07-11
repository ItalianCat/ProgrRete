/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class O_ElencoUser implements Serializable{
	public final Map<String, O_UserData> users;
	
	public O_ElencoUser(){
		users = new HashMap<String, O_UserData>();
	}
		
	public boolean putUser(String user, O_UserData data){
		if(!users.containsKey(user)){
			users.put(user, data);
			return true;
		}
		return false;
	}
	
	public boolean removeUser(String user){
		if(users.containsKey(user)){
			users.remove(user);
			return true;
		}
		return false;
	}
	
	public boolean checkLogin(String user, String psw){
		if(users.containsKey(user) & users.get(user).password.equals(psw))
			return true;
		else
			return false;
	}
	
	public String getCategoria(String user){
		return users.get(user).categoria;
	}
		
	public void aggiornaNAccessi(String user){
		users.get(user).nAccessi++;
	}
	
	public String toStringUsers(){
		String risultato = "";
		for(String user: users.keySet()){
			risultato += user + "\n"; 
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
