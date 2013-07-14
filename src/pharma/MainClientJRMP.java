/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Scanner;

public class MainClientJRMP{
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
            if (System.getSecurityManager() == null)
                System.setSecurityManager(new RMISecurityManager());
			ServBootstrap_I bootstrap = (ServBootstrap_I)Naming.lookup(ip + "/BootstrapServer");
			System.out.println("\nCLIENT JRMP\n- Il client minimale ha eseguito la lookup per ottenere lo stub " +
					"del server di Bootstrap dal registro RMI:\n" + bootstrap);
			Runnable client = bootstrap.getClientJRMP();
			System.out.println("- Il client ha ottenuto dal server di Bootstrap un'istanza della " +
					"classe specifica per client su protocollo JRMP e la lancia con il metodo run().");
			client.run();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}