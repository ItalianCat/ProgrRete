/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

/**
 * Questa classe definisce i metodi del server proxy che sono accessibili alle istanze della 
 * classe ClientRunnable e gli ultimi due all'utente amministratore.
 * Il server viene esportato dualmente quindi non estende ne' UnicastRemoteObject, ne' 
 * PortableRemoteObject, ne' Activatable. 
 */
public class ServProxy implements ServProxy_I{
	
	ServAutenticazione_I actserveraut = null;
	
	/**
	 * Questo costruttore esporta dualmente il server proxy per i due protocolli 
	 * JRMP e IIOP.
	 */
	public ServProxy(ServAutenticazione_I actserveraut) throws RemoteException{
		this.actserveraut = actserveraut;
		UnicastRemoteObject.exportObject(this,0);
		PortableRemoteObject.exportObject(this);
	}
	
	/**
	 * Questo metodo consente di chiedere al server proxy di chiedere al server di autenticazione 
	 * di registrare un nuovo utente nel sistema.
	 * @param user e' lo username dell'utente da registrare
	 * @param data sono i dati rilevanti che descrivono un utente, tra cui la password
	 * @return ritorna true se la registrazione e' andata a buon fine, false altrimenti
	 */
	@Override
	public boolean registraUtente(String user, O_UserData data) throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		System.out.println("\nIl server Proxy sta per chiedere al server di autenticazione " +
				"la registrazione di un nuovo utente.");
		return actserveraut.registraUtente(user, data);
	}
	
	/**
	 * Questo metodo consente di chiedere al server proxy di chiedere al server di autenticazione 
	 * di loggare un utente nel sistema.
	 * @param user e' lo username dell'utente da loggare
	 * @param psw e' la password fornita dall'utente che si vuole registrare
	 * @return ritorna true se l'utente e' stato loggato correttamente, false altrimenti
	 */
	@Override
	public MarshalledObject<ClientMobileAgent_I> login(String user, String psw) throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		System.out.println("\nIl server Proxy sta per chiedere al server di autenticazione " +
				"di effettuare il login per un utente.");
		return actserveraut.login(user, psw);
	}

	/**
	 * Questo metodo consente di chiedere al server proxy di chiedere al server di autenticazione 
	 * di visualizzare la lista degli utenti registrati.
	 * @return ritorna una stringa che rappresenta l'elenco degli utenti registrati presso il 
	 * server di autenticazione.
	 */
	@Override
	public String elencaUtenti(){
		String risultato = "";
		try{
			 risultato = actserveraut.elencaUtenti();
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
		return risultato;
	}
	
	/**
	 * Questo metodo consente di de-registrare dai sistemi di naming e de-esportare il server 
	 * proxy e il server di bootstrap e di chiedere al server di autenticazione di 
	 * de-esportarsi e de-registrarsi dal sistema di attivazione.
	 * @return ritorna true se lo spegnimento e' andato a buon fine, false altrimenti
	 */
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
			System.out.println("\nIl server Proxy sta per chiedere lo spegnimento del server di autenticazione.");
			actserveraut.spegni();
		
			System.exit(0);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}	
}
