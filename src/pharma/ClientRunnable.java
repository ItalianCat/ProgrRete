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
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.util.Properties;
import java.util.Scanner;

import javax.naming.InitialContext;
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
				
		Integer selezione = -1;
		try{
			if(categoria){
				System.out.println("\nRUNNABLE\n- Il client usa il protocollo JRMP.");
				proxy = (ServProxy_I)Naming.lookup(ip+":1099/ProxyDualServer");
			}else{	
				System.out.println("\nRUNNABLE\n- Il client usa il protocollo IIOP.");
				Properties propCosnaming = new Properties();
				propCosnaming.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
				propCosnaming.put("java.naming.provider.url", "iiop:"+ip+":5555");
				InitialContext contextCosnaming = new InitialContext(propCosnaming);
				Object obj = contextCosnaming.lookup("ProxyDualServer");
				proxy = (ServProxy_I)PortableRemoteObject.narrow(obj, ServProxy_I.class);
			}
			System.out.println("- E' stata ottenuta una referenza al Proxy con una lookup.");
		}catch(Exception ex){
			System.out.println("!!! Si e' verificato un errore nell'ottenimento della referenza al server Proxy !!!");
			ex.printStackTrace();
		}
		System.out.println("\nBenvenuti nel sistema Pharma. ");
		while(true){
			System.out.println("Selezionare l'operazione da eseguire:"
								+ "\n\t1. Registrazione nuovo utente"
								+ "\n\t2. Login"
								+ "\n\t3. Uscita");
			try{
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				try{
					selezione = Integer.parseInt(userIn.readLine());
					if(!(selezione >= 1 & selezione <=3)){
						System.out.println("!!! E' necessario inserire un numero tra 1 e 3 !!!");
						continue;
					}
				}catch(NumberFormatException g){
					System.out.println("!!! E' necessario inserire un numero tra 1 e 3 !!!");
					continue;
				}
				switch(selezione){
					case 1: registra();break;
					case 2: login();break;
					case 3: System.exit(0);break;
					default: System.out.println("La selezione non e' valida.");
				}
			}catch(IOException ex){
				ex.printStackTrace();
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
				System.out.print("Digitare la password che si vorrebbe usare: ");
				password = userIn.readLine();
				while(true){
					if(categoria){  //JRMP
						System.out.print("Digitare il tipo di utente che si vorrebbe usare (cliente o farmacia): ");
						tipo = userIn.readLine();
						if(tipo.equalsIgnoreCase("cliente") || 
							tipo.equalsIgnoreCase("farmacia"))
							break;
						System.out.println("!!! Le opzioni accettate per i client JRMP sono solo cliente e farmacia !!!");
					
					}else{		//IIOP
						System.out.println("\nIl tipo di utente sara' amministratore (default per client su protocollo IIOP).");
						tipo = "amministratore";
						break;
					}					
				}
				flag = proxy.registraUtente(user, new O_UserData(password, tipo));
				if(flag)
					System.out.println("\nLa registrazione e' avvenuta con successo.\n");
				else
					System.out.println("Impossibile completare la registrazione. Lo user risulta gia' presente nel sistema.");
			}catch(Exception ex){
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
			System.out.print("Password: ");
			password = userIn.readLine();
			MarshalledObject<ClientMobileAgent_I> obj = (MarshalledObject<ClientMobileAgent_I>)proxy.login(user, password);
			if(obj == null){
				System.out.println("!!! Lo user o la password indicati sono errati !!!\n");
				return; //nullpointerex se errato
			}
			ClientMobileAgent_I agent = (ClientMobileAgent_I)obj.get();
			System.out.println("\nE' stato ottenuto il mobile agent dal server Proxy.");
			System.out.println("Il mobile agent viene mandato in esecuzione presso il client.");
			agent.act();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof ClientRunnable) || obj == null){
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
