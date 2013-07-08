package pharma;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class MainClientIIOP{
	static final String refserver = null;
	public static void main(String[] args){
		try{
			System.setSecurityManager(new SecurityManager());
			Properties propCosnaming = new Properties();
			propCosnaming.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			propCosnaming.put("java.naming.provider.url", "iiop://localhost:5555");  //CAMBIA
			InitialContext contextCosnaming = new InitialContext(propCosnaming);
			Object obj = contextCosnaming.lookup("BootstrapServer");
			ServBootstrap_I bootstrap = (ServBootstrap_I)PortableRemoteObject.narrow(obj, ServBootstrap_I.class);
			System.out.println("\nIl client minimale ha eseguito la lookup per ottenere lo stub " +
					"del server di Bootstrap dal servizio CosNaming.");
			Runnable client = bootstrap.getClientIIOP();
			System.out.println("\nIl client ha ottenuto dal server di Bootstrap un'istanza della" +
					"classe specifica per client su protocollo IIOP e la lancia con il metodo run().");
			client.run();
		}catch(RemoteException | NamingException ex){
			ex.printStackTrace();
		}
	}
}
