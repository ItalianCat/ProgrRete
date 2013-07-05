package pharma;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

@SuppressWarnings("serial")
public class BootstrapServer implements Bootstrap, ProxyDualChiusura, Serializable{

	public BootstrapServer() throws RemoteException{
		UnicastRemoteObject.exportObject(this);
		PortableRemoteObject.exportObject(this);
	}
	
	@Override
	public Runnable getClientRMIRegistry() throws RemoteException{
		return new Client(true);
	}
	
	@Override
	public Runnable getClientCosNaming() throws RemoteException{
		return new Client(false);
	}

	@Override
	public void spegni() throws RemoteException, NamingException{  //GESTIRE NEL PROXY? chi lo lancia lo spegne!
		UnicastRemoteObject.unexportObject(this, true);
		PortableRemoteObject.unexportObject(this);
	}

//equals e hashcode?	
	
}
