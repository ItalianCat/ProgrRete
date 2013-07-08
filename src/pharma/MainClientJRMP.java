package pharma;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MainClientJRMP{
	static final String refserver = null;
	public static void main(String[] args){
		try{
			System.setSecurityManager(new RMISecurityManager());
			Properties propRmiReg = new Properties();
			propRmiReg.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			propRmiReg.put(Context.PROVIDER_URL, "rmi://localhost:1099");  //CAMBIA
			InitialContext contextRmiReg = new InitialContext(propRmiReg);
			Object obj = contextRmiReg.lookup("BootstrapServer");
			ServBootstrap_I bootstrap = (ServBootstrap_I)obj;
			// oppure solo: Bootstrap bootstrap = (Bootstrap)Naming.lookup(refserver);
			System.out.println("\nIl client minimale ha eseguito la lookup per ottenere lo stub " +
					"del server di Bootstrap dal registro RMI.");
			Runnable client = bootstrap.getClientJRMP();
			System.out.println("\nIl client ha ottenuto dal server di Bootstrap un'istanza della" +
					"classe specifica per client su protocollo JRMP e la lancia con il metodo run().");
			client.run();
		}catch(RemoteException | NamingException ex){
			ex.printStackTrace();
		}
	}
}
