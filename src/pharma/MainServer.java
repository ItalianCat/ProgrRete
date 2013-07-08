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
import javax.naming.NamingException;

public class MainServer {
	public static void main(String args[]){
		String policy = "/home/accounts/studenti/vr351643/pharma/file.policy"; //mettere il file giusto
		String codebase = "file:///home/accounts/studenti/vr351643/public_html/common/";
		String classeServ = "pharma.ActServer";
		String classeServAut = "pharma.ActServerAutenticazione";
		Remote stubServ = null;
		ServAutenticazione_I stubServAut = null;
		
		System.setSecurityManager(new RMISecurityManager());
		try{	//server centrale 
			Properties prop0 = new Properties();
			prop0.put("java.security.policy", policy);
			prop0.put("pharma.codebase", codebase);
			prop0.put("java.class.path", "no_classpath");
			
			ActivationSystem sistAtt = ActivationGroup.getSystem();
			System.out.println("\nE' stato ottenuto il riferimento al sistema di attivazione.");
			
			ActivationGroupDesc gruppoServ = new ActivationGroupDesc(prop0, null);
			System.out.println("\nE' stato creato il gruppo di attivazione per il server centrale: " + gruppoServ);
			ActivationGroupID IDgruppoServ = sistAtt.registerGroup(gruppoServ);
			System.out.println("\nIl gruppo di attivazione del server centrale e' stato registrato col sistema di attivazione.");
			ActivationDesc serv = new ActivationDesc(IDgruppoServ, classeServ, codebase, null);
			System.out.println("\nE' stato creato l'Activation Description associato al server centrale.");
			stubServ = (Remote)Activatable.register(serv);
			System.out.println("\nIl server centrale e' stato registrato col sistema di attivazione.");
			System.out.println("\nOra e' possibile accedere al server centrale attraverso lo stub: " + stubServ);
			try{	//lo usa il server di autenticazione alla prima attivazione
				OutputStream out = new FileOutputStream("StubServerCentrale");
				ObjectOutputStream outObj = new ObjectOutputStream(out);
				outObj.writeObject(new MarshalledObject<Remote>(stubServ));
				outObj.close();
			}catch(IOException ex){
				System.out.println("\nErrore nel salvataggio dello stub del server centrale su file.");
				ex.printStackTrace();
			}
			System.out.println("\nE' stato eseguito il salvataggio su file dello stub del server centrale.");
			System.out.println("\nNota: il server centrale NON e' stato registrato su alcun servizio di naming. " +
					"I clienti otterranno lo stub dal server Proxy.");
			
			// server autenticazione
			ActivationGroupDesc gruppoServAut = new ActivationGroupDesc(prop0, null);
			System.out.println("\n\nE' stato creato il gruppo di attivazione per il server di autenticazione: " + gruppoServAut);
			ActivationGroupID IDgruppoServAut = sistAtt.registerGroup(gruppoServAut);
			System.out.println("\nIl gruppo di attivazione del server di autenticazione e' stato registrato col sistema di attivazione.");
			ActivationDesc servAut = new ActivationDesc(IDgruppoServAut, classeServAut, codebase, null);
			System.out.println("\nE' stato creato l'Activation Description associato al server di autenticazione.");
			stubServAut = (ServAutenticazione_I)Activatable.register(servAut);
			System.out.println("\nIl server di autenticazione e' stato registrato col sistema di attivazione.");
			System.out.println("\nOra e' possibile accedere al server di autenticazione attraverso lo stub: " + stubServAut);
			try{	//chi lo usa?
				OutputStream out = new FileOutputStream("StubServerAutenticazione");
				ObjectOutputStream outObj = new ObjectOutputStream(out);
				outObj.writeObject(new MarshalledObject<Remote>(stubServAut));
				outObj.flush();
				outObj.close();
			}catch(IOException ex){
				System.out.println("\nErrore nel salvataggio dello stub del server di autenticazione su file.");
				ex.printStackTrace();
			}
			System.out.println("\nE' stato eseguito il salvataggio su file dello stub del server di autenticazione.");
			System.out.println("\nNota: il server di autenticazione NON e' stato registrato su alcun servizio di naming. " +
					"I clienti otterranno lo stub dal server Proxy.");
			
		}catch(ActivationException | RemoteException ex){
			System.out.println("\nSi e' verificato un errore nel lancio dei server attivabili.");
			ex.printStackTrace();
		}
		try{	//server proxy
			System.setProperty("javax.net.ssl.trustStore", "truststore.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "giuliana");
			ServProxy proxyServ = new ServProxy(stubServAut);  //perche' non ServProxy_I?
			System.out.println("\n\nE' stato creato un server Proxy con lo stub del server di autenticazione.");
			
			Properties prop1 = new Properties();
			prop1.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			prop1.put(Context.PROVIDER_URL, "rmi://localhost:1099");
			InitialContext context1 = new InitialContext(prop1);
			context1.rebind("ProxyDualServer", proxyServ);
			System.out.println("\nIl server Proxy e' stato registrato sul registro RMI.");

			Properties prop2 = new Properties();
			prop2.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			prop2.put("java.naming.provider.url", "iiop://localhost:5555");
			InitialContext context2 = new InitialContext(prop2);
			context2.rebind("ProxyDualServer", proxyServ);
			System.out.println("\nIl server Proxy e' stato registrato col servizio di CosNaming.");
			
			//server bootstrap
			ServBootstrap_I bootServ = new ServBootstrap();
			System.out.println("\n\nE' stato creato un server di Bootstrap.");
			
			Properties prop3 = new Properties();
			prop3.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			prop3.put(Context.PROVIDER_URL, "rmi://localhost:1099");
			InitialContext context3 = new InitialContext(prop3);
			context3.rebind("BootstrapServer", bootServ);
			System.out.println("\nIl server di Bootstrap e' stato registrato sul registro RMI.");
			
			Properties prop4 = new Properties();
			prop4.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			prop4.put("java.naming.provider.url", "iiop://localhost:5555");
			InitialContext context4 = new InitialContext(prop4);
			context4.rebind("BootstrapServer", bootServ);
			System.out.println("\nIl server di Bootstrap e' stato registrato col servizio di CosNaming.");
		
		}catch(RemoteException | NamingException ex){
			System.out.println("\nSi e' verificato un errore nel lancio dei server Proxy e di Bootstrap.");
			ex.printStackTrace();
		}
		
		
	}
}
