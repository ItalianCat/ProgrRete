package pharma;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationSystem;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Main {
	public static void main(String args[]){
		String policy = "/home/accounts/studenti/vr351643/pharma/file.policy"; //mettere il file giusto
		String codebase = "file:///home/accounts/studenti/vr351643/public_html/common/";
		String classeServ = "pharma.ActServer";
		String classeServAut = "pharma.ActServerAutenticazione";
		Remote refServ = null;
		ProxyDualLogin refServAut = null;
		
		System.setSecurityManager(new RMISecurityManager());
						
		Properties prop1 = new Properties();
		prop1.put("java.security.policy", policy);
		prop1.put("pharma.codebase", codebase);
		prop1.put("java.class.path", "no_classpath");
		try{			
			ActivationSystem sistAtt = ActivationGroup.getSystem();
			
			ActivationGroupDesc gruppoServ = new ActivationGroupDesc(prop1, null);
			ActivationGroupID IDgruppoServ = sistAtt.registerGroup(gruppoServ);
			ActivationDesc serv = new ActivationDesc(IDgruppoServ, classeServ, codebase, null);
			refServ = (Remote)Activatable.register(serv);
			//backup su file
			
			ActivationGroupDesc gruppoServAut = new ActivationGroupDesc(prop1, null);
			ActivationGroupID IDgruppoServAut = sistAtt.registerGroup(gruppoServAut);
			ActivationDesc servAut = new ActivationDesc(IDgruppoServAut, classeServAut, codebase, null);
			refServAut = (ProxyDualLogin)Activatable.register(servAut);
			//backup su file
			
		}catch(ActivationException | RemoteException ex){
			ex.printStackTrace();
		}
		try{
			System.setProperty("javax.net.ssl.trustStore", "truststore.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "giuliana");
			ProxyDualServer proxyServ = new ProxyDualServer(refServAut);
			Properties prop2 = new Properties();
			prop2.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			prop2.put(Context.PROVIDER_URL, "rmi://localhost:1099");
			InitialContext context = new InitialContext(prop2);
			context.rebind("ProxyDualServer", proxyServ);
			//binding su CosNaming
		}catch(RemoteException | NamingException ex){
			ex.printStackTrace();
		}
	}
}
