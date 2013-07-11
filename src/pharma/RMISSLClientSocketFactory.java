/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

//Java.rmi.server package ha due classi RMIClientSocketFactory e RMIServerSocketFactory
//che possono essere estese per creare dei socket SSL

@SuppressWarnings("serial")
public class RMISSLClientSocketFactory implements RMIClientSocketFactory, Serializable{ //ser cosi i socket possono essere serializzati insieme agli stub quando vengono passati al client 

	@Override
	public Socket createSocket(String host, int port) throws IOException{
		SocketFactory factory = SSLSocketFactory.getDefault();
		Socket socket = factory.createSocket(host, port);
		return socket;
	}
	
}
