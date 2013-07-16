/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.*;
import java.net.*;
import java.rmi.server.*;
import javax.net.ssl.*;

@SuppressWarnings("serial")
public class RMISSLClientSocketFactory implements RMIClientSocketFactory, Serializable{ 

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
