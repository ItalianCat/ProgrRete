package pharma;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class BootstrapClientCosNaming{
	static final String refserver = null;
	public static void main(String[] args){
		try{
			System.setSecurityManager(new SecurityManager());
			Properties propCosnaming = new Properties();
			propCosnaming.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			propCosnaming.put("java.naming.provider.url", "iiop://localhost:5555");  //CAMBIA
			InitialContext contextCosnaming = new InitialContext(propCosnaming);
			Object obj = contextCosnaming.lookup("BootstrapServer");
			Bootstrap bootstrap = (Bootstrap)PortableRemoteObject.narrow(obj, Bootstrap.class);
			Runnable client = bootstrap.getClientCosNaming();
			client.run();
		}catch(RemoteException | NamingException ex){
			ex.printStackTrace();
		}
	}
}
