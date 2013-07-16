/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.rmi.MarshalledObject;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.activation.*;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

/**Questo programma deve essere lanciato con il seguente comando:
 * java -cp :/home/giuli/public_html/common/ \
 *   -Djava.security.policy=setup.policy        \
 *   -Djava.rmi.server.codebase=http://192.168.0.201:8000/common/    \
 *   pharma.Setup
 *   dove.....
 *   Nota: prima di lanciare questo programma, vanno lanciati rmid, rmiregistry e tnameserv
 *   sulle loro porte di default.
 */

public class Setup{
	private Setup(){} //evito istanziazioni
	public static void main(String args[]){
		
		String policyGroup = Input.percorso + "/javarmi/pharma/group.policy";
		String implCodebase = "file://"+Input.percorso+"/public_html/common/"; 
		//String implCodebase = "file://"+Input.percorso+"/javarmi/"; //IMPORTANTE la barra dopo javarmi
		//ERA "http://192.168.0.201:8000/common/";
			
		Remote stubServ = null;
		Remote stubServAut = null;
		//ServAutenticazione_I stubServAut = null;
		String ip = "";
		try{
			ip = java.net.InetAddress.getLocalHost().getHostAddress();
			System.out.println("\nL'host dei server e': " + ip);
		}catch(UnknownHostException ex){
			ex.printStackTrace();
		}
		
		System.setSecurityManager(new RMISecurityManager());
		
		try{
			Properties prop0 = new Properties();
			prop0.put("java.security.policy", policyGroup);
			prop0.put("pharma.impl.codebase", implCodebase);
			//prop0.put("java.rmi.server.hostname", ip);
			prop0.put("java.class.path", "no_classpath");
			
			ActivationSystem sistAtt = ActivationGroup.getSystem();
			System.out.println("\nE' stato ottenuto il riferimento al sistema di attivazione.");
			
			ActivationGroupDesc gruppoServ = new ActivationGroupDesc(prop0, null);
			System.out.println("\nSERVER CENTRALE attivabile:\n- E' stato creato il gruppo di attivazione per il server centrale.");

			ActivationGroupID IDgruppoServ = sistAtt.registerGroup(gruppoServ);
			System.out.println("- Il gruppo di attivazione del server centrale e' stato registrato col sistema di attivazione.");
			
			ActivationDesc serv = new ActivationDesc(IDgruppoServ, "pharma.Server", implCodebase, null);
			System.out.println("- E' stato creato l'Activation Description associato al server centrale.");
			
			stubServ = (Remote)Activatable.register(serv);
			System.out.println("- Il server centrale e' stato registrato col sistema di attivazione. " +
					"Ora e' possibile accedere al server centrale attraverso lo stub:\n" + stubServ);
			
			try{	//lo usa il server di autenticazione alla prima attivazione
				OutputStream out = new FileOutputStream("StubServerCentrale");
				ObjectOutputStream outObj = new ObjectOutputStream(out);
				outObj.writeObject(new MarshalledObject<Remote>(stubServ));
				outObj.flush();
				outObj.close();
			}catch(IOException ex){
				System.out.println("!!! Errore nel salvataggio dello stub del server centrale su file !!!");
				ex.printStackTrace();
			}
			System.out.println("- E' stato eseguito il salvataggio su file dello stub del server centrale. " +
					"Nota: il server centrale non e' stato registrato su alcun servizio di naming. " +
					"I client otterranno lo stub dal server Proxy.");
			
			
			ActivationGroupDesc gruppoServAut = new ActivationGroupDesc(prop0, null);
			System.out.println("\nSERVER DI AUTENTICAZIONE attivabile:\n- E' stato creato il gruppo di attivazione per il server di autenticazione.");
			ActivationGroupID IDgruppoServAut = sistAtt.registerGroup(gruppoServAut);
			System.out.println("- Il gruppo di attivazione del server di autenticazione e' stato registrato col sistema di attivazione.");
			ActivationDesc servAut = new ActivationDesc(IDgruppoServAut, "pharma.ServAutenticazione", implCodebase, null);
			System.out.println("- E' stato creato l'Activation Description associato al server di autenticazione.");
			stubServAut = (Remote)Activatable.register(servAut);
			//stubServAut = (ServAutenticazione_I)Activatable.register(servAut);
			System.out.println("- Il server di autenticazione e' stato registrato col sistema di attivazione. " +
					"Ora e' possibile accedere al server di autenticazione attraverso lo stub:\n" + stubServAut);
			try{
				OutputStream out = new FileOutputStream("StubServerAutenticazione");
				ObjectOutputStream outObj = new ObjectOutputStream(out);
				outObj.writeObject(new MarshalledObject<Remote>(stubServAut));
				outObj.flush();
				outObj.close();
			}catch(IOException ex){
				System.out.println("!!! Errore nel salvataggio dello stub del server di autenticazione su file !!!");
				ex.printStackTrace();
			}
			System.out.println("- E' stato eseguito il salvataggio su file dello stub del server di autenticazione. " +
					"Nota: il server di autenticazione non e' stato registrato su alcun servizio di naming. " +
					"I client otterranno lo stub dal server Proxy.");
			
		}catch(Exception ex){
			System.out.println("!!! Si e' verificato un errore nel lancio dei server attivabili !!!");
			ex.printStackTrace();
		}
		
		
		try{
			System.setProperty("javax.net.ssl.trustStore", "truststore.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "giuliana");
			ServProxy proxyServ = new ServProxy((ServAutenticazione_I)stubServAut);
			System.out.println("\nSERVER PROXY:\n- E' stato creato un server Proxy con lo stub del " +
					"server di autenticazione ed e' stato esportato dualmente.\n" + stubServAut);
			
			Properties prop3 = new Properties();
			prop3.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			prop3.put(Context.PROVIDER_URL, "rmi://localhost:1099");
			prop3.put("java.rmi.server.hostname", ip);
						
			InitialContext context3 = new InitialContext(prop3);
			context3.rebind("ProxyDualServer", proxyServ);
			System.out.println("- Il server Proxy e' stato registrato sul registro RMI.");

			Properties prop4 = new Properties();
			prop4.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			prop4.put("java.naming.provider.url", "iiop://localhost:5555");
			prop4.put("java.rmi.server.hostname", ip);	
						
			InitialContext context4 = new InitialContext(prop4);
			context4.rebind("ProxyDualServer", proxyServ);
			System.out.println("- Il server Proxy e' stato registrato col servizio di CosNaming.");
			
			ServBootstrap_I bootServ = new ServBootstrap();
			System.out.println("\nSERVER DI BOOTSTRAP:\n- E' stato creato un server di Bootstrap " +
					"ed e' stato esportato dualmente.");
			
			Properties prop1 = new Properties();
			prop1.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			prop1.put(Context.PROVIDER_URL, "rmi://localhost:1099");
			prop1.put("java.rmi.server.hostname", ip);	//serve?
			
			InitialContext context1 = new InitialContext(prop1);
			context1.rebind("BootstrapServer", bootServ);
			System.out.println("- Il server di Bootstrap e' stato registrato sul registro RMI.");
			
			Properties prop2 = new Properties();
			prop2.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			prop2.put("java.naming.provider.url", "iiop://localhost:5555");
			prop2.put("java.rmi.server.hostname", ip);	//serve?
			
			InitialContext context2 = new InitialContext(prop2);
			context2.rebind("BootstrapServer", bootServ);
			System.out.println("- Il server di Bootstrap e' stato registrato col servizio di CosNaming.");
			
			
			
			
			
		}catch(Exception ex){
			System.out.println("\n!!! Si e' verificato un errore nel lancio dei server Proxy e di Bootstrap !!!");
			ex.printStackTrace();
		}
		
	}
}
