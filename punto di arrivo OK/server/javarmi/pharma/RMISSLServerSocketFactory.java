/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.*;
import java.net.*;
import java.rmi.server.*;
import javax.net.ssl.*;
import java.security.KeyStore;

/**
 * Questa classe estende la configurazione standard dei socket lato server per garantire la 
 * riservatezza dei dati trasmessi con l'uso del protocollo SSL (secure socket layer). 
 * Implementa la classe Serializable in modo che i socket possano essere serializzati insieme 
 * allo stub quando e' passato al client (in questo sistema il server e' il server di autenticazione 
 * e il client e' il server proxy).
 */
public class RMISSLServerSocketFactory implements RMIServerSocketFactory {

	private SSLServerSocketFactory ssf = null;
	
	/**
	 * Questo costruttore crea un contesto da cui estrarre una socket factory personalizzata 
	 * in modo da usare l'algoritmo TLS, il gestore delle chiavi SunX509 inizializzato con un 
	 * keystore di tipo jks creato con lo strumento keytool e una password. 
	 */
	public RMISSLServerSocketFactory(){
		try{
			//specifico l'algoritmo da usare
			SSLContext context = SSLContext.getInstance("TLS");
			
			//oggetto per le chiavi usate, SunX509e' il default key manager alg per le sun jvm
			KeyManagerFactory keymng = KeyManagerFactory.getInstance("SunX509");
			
			//oggetto keystore, ovvero database di chiavi e certificati
			KeyStore keystr = KeyStore.getInstance("JKS");
			
			//metto nel keystore le chiavi e i certificati caricati da file
			keystr.load(new FileInputStream("keystore.jks"), "giuliana".toCharArray());
			
			//inizializzo il keymanagerfactory con il keystore e la password
			keymng.init(keystr, "giuliana".toCharArray());
			
			//inizializzo il sslcontext
			context.init(keymng.getKeyManagers(), null, null);
			
			ssf = context.getServerSocketFactory();
		
		}catch(Exception ex){
			ex.printStackTrace();
		}	
	}

	/**
	 * Questo metodo crea un socket a partire dalla socket factory costruita con il contesto 
	 * definito dal costruttore della classe.
	 * @return ritorna il socket creato
	 */
	@Override
	public ServerSocket createServerSocket(int port) throws IOException{
		return ssf.createServerSocket(port);
	}

	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof RMISSLServerSocketFactory) || obj == null){
			return false;
		}else{
			return true;
		}
	}
	
	@Override
	public int hashCode(){
		return getClass().hashCode();
	}
	
}
