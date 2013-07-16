/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.*;
import java.net.*;
import java.rmi.server.*;
import javax.net.ssl.*;
import java.security.KeyStore;

public class RMISSLServerSocketFactory implements RMIServerSocketFactory {

	private SSLServerSocketFactory ssf = null;
	
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
