/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.MarshalledObject;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.*;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

public class MainServer {
	public static void main(String args[]){
		String policyGroup = "/home/giuli/javarmi/pharma/group.policy";
		String codebase = "file:///home/giuli/public_html/common/";
		String classeServ = "pharma.Server";
		String classeServAut = "pharma.ServAutenticazione";
		Remote stubServ = null;
		ServAutenticazione_I stubServAut = null;

		System.setSecurityManager(new RMISecurityManager());
		try{	//server centrale 
			Properties prop0 = new Properties();
			prop0.put("java.rmi.server.hostname", "192.168.0.203");
			prop0.put("java.security.policy", policyGroup);
			prop0.put("pharma.impl.codebase", codebase);
			prop0.put("java.class.path", "no_classpath");
			
			ActivationSystem sistAtt = ActivationGroup.getSystem();
			System.out.println("E' stato ottenuto il riferimento al sistema di attivazione.");
			
			ActivationGroupDesc gruppoServ = new ActivationGroupDesc(prop0, null);
			System.out.println("E' stato creato il gruppo di attivazione per il server centrale: " + gruppoServ);
			ActivationGroupID IDgruppoServ = sistAtt.registerGroup(gruppoServ);
			System.out.println("Il gruppo di attivazione del server centrale e' stato registrato col sistema di attivazione.");
			ActivationDesc serv = new ActivationDesc(IDgruppoServ, classeServ, codebase, null);
			System.out.println("E' stato creato l'Activation Description associato al server centrale.");
			stubServ = (Remote)Activatable.register(serv);
			System.out.println("Il server centrale e' stato registrato col sistema di attivazione.");
			System.out.println("Ora e' possibile accedere al server centrale attraverso lo stub: " + stubServ);
			try{	//lo usa il server di autenticazione alla prima attivazione
				OutputStream out = new FileOutputStream("StubServerCentrale");
				ObjectOutputStream outObj = new ObjectOutputStream(out);
				outObj.writeObject(new MarshalledObject<Remote>(stubServ));
				outObj.close();
			}catch(IOException ex){
				System.out.println("Errore nel salvataggio dello stub del server centrale su file.");
				ex.printStackTrace();
			}
			System.out.println("E' stato eseguito il salvataggio su file dello stub del server centrale.");
			System.out.println("Nota: il server centrale NON e' stato registrato su alcun servizio di naming. " +
					"I clienti otterranno lo stub dal server Proxy.");
			
			// server autenticazione
			ActivationGroupDesc gruppoServAut = new ActivationGroupDesc(prop0, null);
			System.out.println("E' stato creato il gruppo di attivazione per il server di autenticazione: " + gruppoServAut);
			ActivationGroupID IDgruppoServAut = sistAtt.registerGroup(gruppoServAut);
			System.out.println("Il gruppo di attivazione del server di autenticazione e' stato registrato col sistema di attivazione.");
			ActivationDesc servAut = new ActivationDesc(IDgruppoServAut, classeServAut, codebase, null);
			System.out.println("E' stato creato l'Activation Description associato al server di autenticazione.");
			stubServAut = (ServAutenticazione_I)Activatable.register(servAut);
			System.out.println("Il server di autenticazione e' stato registrato col sistema di attivazione.");
			System.out.println("Ora e' possibile accedere al server di autenticazione attraverso lo stub: " + stubServAut);
			try{	//chi lo usa?
				OutputStream out = new FileOutputStream("StubServerAutenticazione");
				ObjectOutputStream outObj = new ObjectOutputStream(out);
				outObj.writeObject(new MarshalledObject<Remote>(stubServAut));
				outObj.flush();
				outObj.close();
			}catch(IOException ex){
				System.out.println("Errore nel salvataggio dello stub del server di autenticazione su file.");
				ex.printStackTrace();
			}
			System.out.println("E' stato eseguito il salvataggio su file dello stub del server di autenticazione.");
			System.out.println("Nota: il server di autenticazione NON e' stato registrato su alcun servizio di naming. " +
					"I clienti otterranno lo stub dal server Proxy.");
			
		}catch(ActivationException | RemoteException ex){
			System.out.println("Si e' verificato un errore nel lancio dei server attivabili.");
			ex.printStackTrace();
		}
		try{	//server proxy
			System.setProperty("javax.net.ssl.trustStore", "truststore.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "giuliana");
			ServProxy proxyServ = new ServProxy(stubServAut);
			System.out.println("E' stato creato un server Proxy con lo stub del server di autenticazione.");
			
			Properties prop1 = new Properties();
			prop1.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			prop1.put(Context.PROVIDER_URL, "rmi://localhost:1099");
			prop1.put("java.rmi.server.hostname", "192.168.0.203");
			prop1.put("java.rmi.server.codebase", "file:///home/giuli/public_html/common/");
			prop1.put("java.class.path", "/home/giuli/javarmi/pharma/");
			InitialContext context1 = new InitialContext(prop1);
			context1.rebind("ProxyDualServer", proxyServ);
			System.out.println("Il server Proxy e' stato registrato sul registro RMI.");

			Properties prop2 = new Properties();
			prop2.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			prop2.put("java.naming.provider.url", "iiop://localhost:5555");
			prop2.put("java.rmi.server.hostname", "192.168.0.203");
			InitialContext context2 = new InitialContext(prop2);
			context2.rebind("ProxyDualServer", proxyServ);
			System.out.println("Il server Proxy e' stato registrato col servizio di CosNaming.");
			
			//server bootstrap
			ServBootstrap_I bootServ = new ServBootstrap();
			System.out.println("E' stato creato un server di Bootstrap.");
			
			Properties prop3 = new Properties();
			prop3.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			prop3.put(Context.PROVIDER_URL, "rmi://localhost:1099");
			prop3.put("java.rmi.server.hostname", "192.168.0.203");
			InitialContext context3 = new InitialContext(prop3);
			context3.rebind("BootstrapServer", bootServ);
			System.out.println("Il server di Bootstrap e' stato registrato sul registro RMI."+bootServ);
			
			Properties prop4 = new Properties();
			prop4.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			prop4.put("java.naming.provider.url", "iiop://localhost:5555");
			prop4.put("java.rmi.server.hostname", "192.168.0.203");			
			InitialContext context4 = new InitialContext(prop4);
			context4.rebind("BootstrapServer", bootServ);
			System.out.println("Il server di Bootstrap e' stato registrato col servizio di CosNaming.");
		
		}catch(Exception ex){
			System.out.println("Si e' verificato un errore nel lancio dei server Proxy e di Bootstrap.");
			ex.printStackTrace();
		}
		
	}
}
