/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Scanner;

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
		String ip ="";
		try{
			Scanner scan = new Scanner(new File("IPserver.txt"));
			ip += scan.nextLine();
			scan.close();
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
				
		String selezione = "";
		try{
			if(categoria){ 	//JRMP
				System.out.println("Il client usa il protocollo JRMP.");
				proxy = (ServProxy_I)Naming.lookup(ip+":1099/ProxyDualServer");
			}else{			//IIOP
				System.out.println("Il client usa il protocollo IIOP.");
				Properties propCosnaming = new Properties();
				propCosnaming.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
				propCosnaming.put("java.naming.provider.url", "iiop:"+ip+":5555");
				InitialContext contextCosnaming = new InitialContext(propCosnaming);
				Object obj = contextCosnaming.lookup("ProxyDualServer");
				proxy = (ServProxy_I)PortableRemoteObject.narrow(obj, ServProxy_I.class);
			}
			System.out.println("E' stata ottenuta una referenza al Proxy con una lookup.");
		}catch(NamingException | MalformedURLException | RemoteException | NotBoundException ex){
			System.out.println("Si e' verificato un errore nell'ottenimento della referenza al server Proxy.");
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
				default: System.out.println("La selezione non e' valida.");
			}
		}
	}

	private void registra(){
		boolean flag = false;
		while(!flag){
			try{
				System.out.println("Si e' scelto di registrare un nuovo utente.");
				System.out.print("Digitare lo username che si vorrebbe usare: ");
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				user = userIn.readLine();
				System.out.print("\nDigitare la password che si vorrebbe usare: ");
				password = userIn.readLine();
				while(true){
					if(categoria){  //JRMP
						System.out.print("\nDigitare il tipo di utente che si vorrebbe usare (cliente o farmacia): ");
						tipo = userIn.readLine();
						if(tipo.equalsIgnoreCase("cliente") || 
							tipo.equalsIgnoreCase("farmacia"))
							break;
						System.out.println("Attenzione: le opzioni accettate per i client JRMP sono solo cliente e farmacia.");
					
					}else{		//IIOP
						System.out.println("Il tipo di utente sara' amministratore (default per client su protocollo IIOP).");
						tipo = "amministratore";
					}					
				}
				flag = proxy.registraUtente(user, new O_UserData(password, tipo));
				if(flag)
					System.out.println("La registrazione e' avvenuta con successo.");
				else
					System.out.println("Impossibile completare la registrazione. Lo user risulta gia' presente nel sistema.");
			}catch(IOException | ClassNotFoundException | ActivationException ex){
				ex.printStackTrace();
			}
		}
	}

	private void login(){
		try {
			System.out.println("Si e' scelto di eseguire il login al sistema.");
			System.out.print("User: ");
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			user = userIn.readLine();
			System.out.print("\nPassword: ");
			password = userIn.readLine();
			MarshalledObject<ClientMobileAgent_I> obj = (MarshalledObject<ClientMobileAgent_I>)proxy.login(user, password);
			ClientMobileAgent_I agent = (ClientMobileAgent_I)obj.get();
			System.out.println("\nE' stato ottenuto il mobile agent dal server Proxy.");
			System.out.println("Il mobile agent viene mandato in esecuzione presso il client.");
			agent.act();
		}catch(IOException | ClassNotFoundException | ActivationException ex){
			ex.printStackTrace();
		}
	}

	/*Coi Serializable  va fatto l'override di equals() e hashCode() solo se necessario. Non serve
	 * se il server non controlla mai l'uguaglianza tra le istanze e nemmeno le immagazzina in un
	 * oggetto contenitore che si basa sui loro hashcode*/
}
