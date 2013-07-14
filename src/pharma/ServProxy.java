/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

@SuppressWarnings("serial")
public class ServProxy implements ServProxy_I, ServProxyOff_I, Serializable{
	
	ServAutenticazione_I actserveraut = null;
	
	public ServProxy(ServAutenticazione_I actserveraut) throws RemoteException{
		this.actserveraut = actserveraut;
		UnicastRemoteObject.exportObject(this,0);
		PortableRemoteObject.exportObject(this);
	}
	
	@Override
	public boolean registraUtente(String user, O_UserData data) throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		System.out.println("\nIl server Proxy sta per chiedere al server di autenticazione " +
				"la registrazione di un nuovo utente.");
		return actserveraut.registraUtente(user, data);
	}
	@Override
	public MarshalledObject<ClientMobileAgent_I> login(String user, String psw) throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		System.out.println("\nIl server Proxy sta per chiedere al server di autenticazione " +
				"di effettuare il login per un utente.");
		return actserveraut.login(user, psw);
	}

	@Override
	public boolean spegniPBA() throws RemoteException {
		try{	//server proxy
			Properties propRmiReg = new Properties();
			propRmiReg.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			propRmiReg.put(Context.PROVIDER_URL, "rmi://localhost:1099");
			InitialContext contextRmiReg = new InitialContext(propRmiReg);
							
			Properties propCosnaming = new Properties();
			propCosnaming.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			propCosnaming.put("java.naming.provider.url", "iiop://localhost:5555");
			InitialContext contextCosnaming = new InitialContext(propCosnaming);

			contextRmiReg.unbind("ProxyDualServer");	
			UnicastRemoteObject.unexportObject(this, true);
			contextCosnaming.unbind("ProxyDualServer");
			PortableRemoteObject.unexportObject(this);
			System.out.println("\nIl server Proxy e' stato deregistrato dai servizi di naming " +
					"e de-esportato dualmente.");
				
			//server bootstrap
			ServBootstrap boot = (ServBootstrap)contextRmiReg.lookup("BootstrapServer");
			contextRmiReg.unbind("BootstrapServer");
			UnicastRemoteObject.unexportObject(boot, true);
			contextCosnaming.unbind("BootstrapServer");
			PortableRemoteObject.unexportObject(boot);
			System.out.println("\nIl server Proxy ha deregistrato dai servizi di naming e " +
					"de-esportato dualmente il server di Bootstrap.");
			
			//server autenticazione e centrale senza proxy sono irraggiungibili pero' se il client ha salvato la referenza...
			System.out.println("\nIl server Proxy sta per chiedere lo spegnimento del server di autenticazione.");
			actserveraut.spegni();
		
			System.exit(0);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	
}
