/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Scanner;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

/**
 * Questa classe definisce il client minimale per utente che utilizzano il protocollo IIOP.
 * Partendo da un file "IPserver.txt", che deve essere inserito nella cartella "javarmi",
 * estrae l'indirizzo IP dell'host dei server e lo usa fare una lookup del server di 
 * bootstrap sul servizio di CosNaming. Poi invoca il metodo getClientIIOP sulla referenza 
 * del server di Bootstrap per ottenere un'istanza della classe ClientRunnable, ovvero per 
 * caricare dinamicamente il codice di cui ha bisogno per funzionare.
 */
public class MainClientIIOP{
	
	static final String refserver = null;
	
	public static void main(String[] args){
		
		String ip ="";
		try{
			Scanner scan = new Scanner(new File("IPserver.txt"));
			ip += scan.nextLine();
			scan.close();
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
		}

		try{
			System.setSecurityManager(new SecurityManager());
			Properties propCosnaming = new Properties();
			propCosnaming.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			propCosnaming.put("java.naming.provider.url", "iiop:"+ip+":5555");
			InitialContext contextCosnaming = new InitialContext(propCosnaming);
			Object obj = contextCosnaming.lookup("BootstrapServer");
			ServBootstrap_I bootstrap = (ServBootstrap_I)PortableRemoteObject.narrow(obj, ServBootstrap_I.class);
			System.out.println("\nCLIENT IIOP\n- Il client minimale ha eseguito la lookup per ottenere lo stub " +
					"del server di Bootstrap dal servizio CosNaming.");
			Runnable client = bootstrap.getClientIIOP();
			System.out.println("- Il client ha ottenuto dal server di Bootstrap un'istanza della " +
					"classe specifica per client su protocollo IIOP e la lancia con il metodo run().");
			client.run();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
