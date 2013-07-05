package pharma;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

@SuppressWarnings("serial")
public class ProxyDualServer implements ProxyDualLogin, ProxyDualChiusura, Serializable{
	
	ProxyDualLogin actserveraut = null;
	
	public ProxyDualServer(ProxyDualLogin actserveraut) throws RemoteException{ //perche' non Marshalled?
		this.actserveraut = actserveraut;
		UnicastRemoteObject.exportObject(this);
		PortableRemoteObject.exportObject(this);
	}
	
	@Override
	public MarshalledObject<MobileAgent> login(String user, String psw) throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		return actserveraut.login(user, psw);
	}
	
	@Override
	public boolean registraUtente(String user, UserData data) throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		return actserveraut.registraUtente(user, data);
	}
	
	@Override
	public void spegni() throws RemoteException, NamingException{  //va fatto anche per il server di bootstrap
		try{
			Properties propRmiReg = new Properties();
			propRmiReg.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			propRmiReg.put(Context.PROVIDER_URL, "rmi://localhost:1099");
			InitialContext contextRmiReg = new InitialContext(propRmiReg);
			contextRmiReg.unbind("ProxyDualServer");
			
			Properties propCosnaming = new Properties();
			propCosnaming.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			propCosnaming.put("java.naming.provider.url", "iiop://localhost:5555");
			InitialContext contextCosnaming = new InitialContext(propCosnaming);
			contextCosnaming.unbind("ProxyDualServer");
			
			UnicastRemoteObject.unexportObject(this, true);
			System.exit(0);
		}catch(NoSuchObjectException ex){
			ex.printStackTrace();
		}
	}
	
}
