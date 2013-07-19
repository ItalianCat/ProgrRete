/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.*;
import java.net.*;
import java.rmi.server.*;
import javax.net.ssl.*;

/**
 * Questa classe estende la configurazione standard dei socket lato client per garantire la 
 * riservatezza dei dati trasmessi con l'uso del protocollo SSL (secure socket layer). 
 * Implementa la classe Serializable in modo che i socket possano essere serializzati insieme 
 * allo stub quando e' passato al client (in questo sistema il server e' il server di autenticazione 
 * e il client e' il server proxy).
 */
@SuppressWarnings("serial")
public class RMISSLClientSocketFactory implements RMIClientSocketFactory, Serializable{ 

	/**
	 * Questo metodo crea un socket sicuro per il client usando l'implementazione predefinita.
	 * @return ritorna il socket creato 
	 */
	@Override
	public Socket createSocket(String host, int port) throws IOException{
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		return (SSLSocket)factory.createSocket(host, port);
	}

	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof RMISSLClientSocketFactory) || obj == null){
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
