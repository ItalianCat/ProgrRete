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
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

@SuppressWarnings("serial")
public class ServProxy implements ServProxy_I, ServProxyOff_I, Serializable{
	
	ServAutenticazione_I actserveraut = null;
	
	public ServProxy(ServAutenticazione_I actserveraut) throws RemoteException{ //perche' non Marshalled?
		this.actserveraut = actserveraut;
		UnicastRemoteObject.exportObject(this);
		PortableRemoteObject.exportObject(this);
	}
	
	@Override
	public boolean registraUtente(String user, O_UserData data) throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		return actserveraut.registraUtente(user, data);
	}
	@Override
	public MarshalledObject<ClientMobileAgent_I> login(String user, String psw) throws RemoteException, ActivationException, IOException, ClassNotFoundException{
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
				
			//server bootstrap
			ServBootstrap boot = (ServBootstrap)contextRmiReg.lookup("BootstrapServer");
			contextRmiReg.unbind("BootstrapServer");
			UnicastRemoteObject.unexportObject(boot, true);
			contextCosnaming.unbind("BootstrapServer");
			PortableRemoteObject.unexportObject(boot);
			
			//server autenticazione e centrale senza proxy sono irraggiungibili però se il client ha salvato la referenza...
			actserveraut.spegni();
		
			System.exit(0);
		}catch(NamingException | RemoteException ex){
			ex.printStackTrace();
		}
		return false;
	}
	
}
