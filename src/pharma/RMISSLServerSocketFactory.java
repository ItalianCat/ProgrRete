/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import java.security.*;
import java.security.cert.CertificateException;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class RMISSLServerSocketFactory implements RMIServerSocketFactory {

	/*When the client invokes the sayHello() method the server will send its certificate to the client.
	The client will then verify it against its truststore to see if it is a trusted certificate.
	If true, the method invocation goes on. Otherwise, the SSL handshake fails and an exception is thrown.*/
	/*Una tipica connessione TLS/SSL (per esempio via browser internet) prevede
	un tipo di autenticazione denominata unilaterale : solo il server e' autenticato
	(il client conosce l'identita' del server), ma non vice-versa (il client rimane 
	anonimo e non autenticato). Il client (browser web, EmailUI, Java Client, etc)
	valida il certificato del server controllando la firma digitale dei certificati
	del server, verificando che questa sia valida e riconosciuta da una
	Certificate Authority conosciuta utilizzando una cifratura a chiave pubblica.
	       
	La piattaforma Java utilizza un sistema denominato Java Keystore per la gestione della sicurezza.
	L'implementazione di default del Java Keystore e' basata su file, 
	quest'ultimo e' in un formato proprietario denominato JKS. 
	La piattaforma Java prevede due tipi di Java Keystore
	- Server side: Java Keystore che contiene solitamente le coppie di chiavi 
	pubbliche/private dei certificati utilizzati dall'applicazione server.
	Il nome solitamente attribuito a questo repository e' keystore;
	- Client side: Java Keystore che contiene i soli certificati utilizzati dalle
 	applicazioni che agiscono come client. 
 	Il nome solitamente attribuito a questo repository e' trustStore.
 	La locazione predefinita di entrambi i keystore e' <java-home>/lib/security/
	*/
	
	
	
	@Override
	public ServerSocket createServerSocket(int port) throws IOException{
		ServerSocketFactory ssf = null;
		try{
			SSLContext context = SSLContext.getInstance("SSLv3"); //e' un'implementazione di protocollo secure socket
																//nomi di alg: SSL, SSLv2, SSLv3, TLS, TLSv1, TLSv1.1
			KeyManagerFactory keymng = KeyManagerFactory.getInstance("RSA"); //SunX509e' il default key manager alg per le sun jvm
			KeyStore keystr = KeyStore.getInstance("JKS"); //system will return the most preferred implementation of the specified keystore type available in the environment
															//altrimenti KeyStore.getDefaultType()
			keystr.load(new FileInputStream("keystore.jks"), "giuliana".toCharArray());
			keymng.init(keystr, "giuliana".toCharArray());
			context.init(keymng.getKeyManagers(), null, null);
			ssf = context.getServerSocketFactory();
		}catch(Exception ex){
			ex.printStackTrace();
		}	
		return ssf.createServerSocket(port);
	}

}
