package pharma;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class BootstrapClientRMIRegistry{
	static final String refserver = null;
	public static void main(String[] args){
		try{
			System.setSecurityManager(new RMISecurityManager());
			Properties propRmiReg = new Properties();
			propRmiReg.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			propRmiReg.put(Context.PROVIDER_URL, "rmi://localhost:1099");  //CAMBIA
			InitialContext contextRmiReg = new InitialContext(propRmiReg);
			Object obj = contextRmiReg.lookup("BootstrapServer");
			Bootstrap bootstrap = (Bootstrap)obj;
			// oppure solo: Bootstrap bootstrap = (Bootstrap)Naming.lookup(refserver);
			Runnable client = bootstrap.getClientRMIRegistry();
			client.run();
		}catch(RemoteException | NamingException ex){
			ex.printStackTrace();
		}
	}
}
