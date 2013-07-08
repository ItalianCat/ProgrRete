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
public class ClientRunnable implements Runnable, Serializable{
	private boolean categoria;
	private ServProxy_I proxy = null;
	private String user = "";
	private String password = "";
	private String tipo = "";
	
	public ClientRunnable(boolean categoria){
		this.categoria = categoria;
	}
	
	@Override
	public void run() {
		String selezione = "";
		try{
			if(categoria){
				System.out.println("\nIl client usa il protocollo JRMP.");
				proxy = (ServProxy_I)Naming.lookup("percorsoProxy");//percorso proxy
			}else{
				System.out.println("\nIl client usa il protocollo IIOP.");
				Properties propCosnaming = new Properties();
				propCosnaming.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
				propCosnaming.put("java.naming.provider.url", "iiop://localhost:5555");  //CAMBIA
				InitialContext contextCosnaming = new InitialContext(propCosnaming);
				Object obj = contextCosnaming.lookup("ProxyDualServer");
				proxy = (ServProxy_I)PortableRemoteObject.narrow(obj, ServProxy_I.class);
			}
			System.out.println("\nE' stata ottenuta una referenza al Proxy con una lookup.");
		}catch(NamingException | MalformedURLException | RemoteException | NotBoundException ex){
			System.out.println("\nSi e' verificato un errore nell'ottenimento della referenza al server Proxy.");
			ex.printStackTrace();
		}
		while(true){
			System.out.println("\nBenvenuti nel sistema Pharma. Selezionare l'operazione da eseguire:"
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
				default: System.out.println("\nLa selezione non e' valida.");
			}
		}
	}

	private void registra(){
		boolean flag = false;
		while(!flag){
			try{
				System.out.println("\nSi e' scelto di registrare un nuovo utente.");
				System.out.println("\nDigitare lo username che si vorrebbe usare: ");
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				user = userIn.readLine();
				System.out.println("\nDigitare la password che si vorrebbe usare: ");
				password = userIn.readLine();
				while(true){
					System.out.println("\nDigitare il tipo di utente che si vorrebbe usare (cliente, farmacia, amministratore): ");
					tipo = userIn.readLine();
					if(tipo.equalsIgnoreCase("cliente") || 
						tipo.equalsIgnoreCase("farmacia") ||
						tipo.equalsIgnoreCase("amministratore"))
						break;
					System.out.println("Attenzione: le opzioni accettate sono solo cliente, farmacia e amministratore.");
				}
				flag = proxy.registraUtente(user, new O_UserData(password, tipo));
				if(flag)
					System.out.println("\nLa registrazione e' avvenuta con successo.");
				else
					System.out.println("\nImpossibile completare la registrazione. Lo user risulta gia' presente nel sistema.");
			}catch(IOException | ClassNotFoundException | ActivationException ex){
				ex.printStackTrace();
			}
		}
	}

	private void login(){
		try {
			System.out.println("\nSi e' scelto si eseguire il login al sistema.");
			System.out.println("\nUser: ");
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			user = userIn.readLine();
			System.out.println("\nPassword: ");
			password = userIn.readLine();
			MarshalledObject<ClientMobileAgent_I> obj = (MarshalledObject<ClientMobileAgent_I>)proxy.login(user, password);
			ClientMobileAgent_I agent = (ClientMobileAgent_I)obj.get();
			System.out.println("\nE' stato ottenuto il mobile agent dal server Proxy.");
			System.out.println("\nIl mobile agent viene mandato in esecuzione presso il client.");
			agent.act();
		}catch(IOException | ClassNotFoundException | ActivationException ex){
			ex.printStackTrace();
		}
	}

	/*Coi Serializable  va fatto l'override di equals() e hashCode() solo se necessario. Non serve
	 * se il server non controlla mai l'uguaglianza tra le istanze e nemmeno le immagazzina in un
	 * oggetto contenitore che si basa sui loro hashcode*/
}
