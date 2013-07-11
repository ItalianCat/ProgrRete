/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

public class MainClientJRMP{
	static final String refserver = null;
	public static void main(String[] args){
		
		/*String ip ="";
		try{
			Scanner scan = new Scanner(new File("IPserver.txt"));
			ip += scan.nextLine();
			scan.close();
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
		}*/

		try{
			/*String indirizzo = ip.toString()+":1099";
			System.out.println(indirizzo);

			Registry registry = null;
			try {
				registry = LocateRegistry.getRegistry("//192.168.0.203", 1099);
				System.out.println(registry);
				for (String name : registry.list()) {
				    System.out.println(name);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			
            if (System.getSecurityManager() == null)
                System.setSecurityManager(new RMISecurityManager());
			/*Properties propRmiReg = new Properties();
			propRmiReg.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			propRmiReg.put("java.rmi.server.hostname", "198.162.0.203");
			propRmiReg.put(Context.PROVIDER_URL, indirizzo);
			InitialContext contextRmiReg = new InitialContext(propRmiReg);
			Object obj = contextRmiReg.lookup("BootstrapServer");
			ServBootstrap_I bootstrap = (ServBootstrap_I)obj;*/
			ServBootstrap_I bootstrap = (ServBootstrap_I)Naming.lookup("//192.168.0.203:1099/BootstrapServer");
			System.out.println("Il client minimale ha eseguito la lookup per ottenere lo stub " +
					"del server di Bootstrap dal registro RMI." + bootstrap);
			Runnable client = bootstrap.getClientJRMP();
			System.out.println("Il client ha ottenuto dal server di Bootstrap un'istanza della" +
					"classe specifica per client su protocollo JRMP e la lancia con il metodo run().");
			client.run();
		}catch(RemoteException | MalformedURLException | NotBoundException ex){
			ex.printStackTrace();
		}
	}
}