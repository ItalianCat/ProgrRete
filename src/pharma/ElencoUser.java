package pharma;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ElencoUser implements Serializable{
	public final Map<String, UserData> users;
	
	public ElencoUser(){
		users = new HashMap<String, UserData>();
	}
		
	public boolean putUser(String user, UserData data){
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
	
	public String toStringUsers(){
		String risultato = "";
		for(String user: users.keySet()){
			risultato += user + "\n"; 
		}
		return risultato;			
	}
	
	
}
