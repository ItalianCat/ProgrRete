package pharma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

@SuppressWarnings("serial")
public class Client implements Runnable, Serializable{
	private boolean categoria;
	private ProxyDualLogin proxy = null;
	private String user = "";
	private String password = "";
	private String tipo = "";
	
	public Client(boolean categoria){
		this.categoria = categoria;
	}
	
	@Override
	public void run() {
		String selezione = "";
		try{
			if(categoria){//client RMIRegistry
				proxy = (ProxyDualLogin)Naming.lookup("percorsoProxy");//percorso proxy
			}else{ //client CosNaming
				Properties propCosnaming = new Properties();
				propCosnaming.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
				propCosnaming.put("java.naming.provider.url", "iiop://localhost:5555");  //CAMBIA
				InitialContext contextCosnaming = new InitialContext(propCosnaming);
				Object obj = contextCosnaming.lookup("ProxyDualServer");
				proxy = (ProxyDualLogin)PortableRemoteObject.narrow(obj, ProxyDualLogin.class);
			}
		}catch(NamingException | MalformedURLException | RemoteException | NotBoundException ex){
			ex.printStackTrace();
		}
		while(true){
			System.out.println("\nBenvenuto nel sistema Pharma. Seleziona l'operazione da eseguire:"
								+ "\n\t1. Registrazione nuovo utente"
								+ "\n\t2. Login"
								+ "\n\t3. Uscita\n");
			try{
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				selezione = userIn.readLine();
			}catch(IOException ex){
				ex.printStackTrace();
			}
			switch(Integer.parseInt(selezione)){
				case 1: registra();break;
				case 2: login();break;
				case 3: System.exit(0);break;
				default: System.out.println("La selezione non e' valida\n");
			}
		}
	}

	private void registra(){
		boolean flag = false;
		while(!flag){
			try{
				System.out.println("\nDigita lo username che vorresti usare: ");
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				user = userIn.readLine();
				System.out.println("\nDigita la password che vorresti usare: ");
				password = userIn.readLine();
				while(true){
					System.out.println("\nDigita il tipo di utente che vorresti usare (cliente,farmacia,amministratore): ");
					tipo = userIn.readLine();
					if(tipo.equalsIgnoreCase("cliente") || 
						tipo.equalsIgnoreCase("farmacia") ||
						tipo.equalsIgnoreCase("amministratore"))
						break;
					System.out.println("Attenzione: le opzioni accettate sono solo cliente, farmacia e amministratore");
				}
				flag = proxy.registraUtente(user, new UserData(password, tipo));
				if(flag)
					System.out.println("\nLa registrazione e' avvenuta con successo");
				else
					System.out.println("\nImpossibile completare la registrazione. Lo user risulta gia' presente nel sistema");
			}catch(IOException | ClassNotFoundException | ActivationException ex){
				ex.printStackTrace();
			}
		}
	}

	private void login(){
		try {
			System.out.println("\nUser: ");
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			user = userIn.readLine();
			System.out.println("\nPassword: ");
			password = userIn.readLine();
			MarshalledObject<MobileAgent> obj = (MarshalledObject<MobileAgent>)proxy.login(user, password);
			MobileAgent agent = (MobileAgent)obj.get();
			agent.act();
		}catch(IOException | ClassNotFoundException | ActivationException ex){
			ex.printStackTrace();
		}
	}

	/*Coi Serializable  va fatto l'override di equals() e hashCode() solo se necessario. Se il
	 * server non controlla mai l'uguaglianza tra le istanze e nemmeno le immagazzina in un
	 * oggetto contenitore che si basa sui loro hashcode*/
}
