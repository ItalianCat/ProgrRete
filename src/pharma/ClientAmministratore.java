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
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Scanner;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/**Il Client Amministratore e' un mobile agent che gestisce le operazioni di competenza
 * dell'amministratore del sistema, quali:
 * - rifornire il magazzino centrale con nuovi prodotti o con quantita' aggiuntive
 * di prodotti esistenti;
 * - spegnere tutti i server (per esempio in caso di problemi legati alla sicurezza);
 * - caricare un magazzino centrale di esempio con cui effettuare dei test sulle funzionalita'
 * del sistema.
 * */

@SuppressWarnings("serial")
public class ClientAmministratore implements ClientMobileAgent_I, Serializable{
	
	private ServerAmministratore_I remactserver = null;

	public ClientAmministratore(MarshalledObject<ServerAmministratore_I> obj){
		try{
			remactserver = (ServerAmministratore_I)obj.get();
			System.out.println("Il client Amministratore ha ottenuto la referenza al server centrale.");
		}catch(ClassNotFoundException | IOException ex){
			System.out.println("Si e' verificato un errore nell'ottenimento della referenza al server centrale.");
			ex.printStackTrace();
		}
	}
	
	@Override
	public void act(){
		String selezione = "";
		while(true){
			System.out.println("\nMenu Amministratore\nSelezionare l'opzione desiderata:\n"
								+ "\t1. Inserimento prodotti e quantita'\n"
								+ "\t2. Spegnimento del sistema\n" //proxy e boot. gli altri hanno unreferenced 
								+ "\t3. (Carica dati di esempio)\n"
								+ "\t4. Uscita\n");
			try{
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				selezione = userIn.readLine();
			}catch(IOException ex){
				ex.printStackTrace();
			}
			switch(Integer.parseInt(selezione)){
				case 1: rifornisciMagazzino();break;
				case 2: spegniTutto();break;
				case 3:	caricaEsempio();break;
				case 4: System.exit(0);break;
				default: System.out.println("La selezione non e' valida.");
			}
		}
	}
	
	//TRANSAZIONI COL SERVER CENTRALE
	
	private void rifornisciMagazzino(){
		System.out.println("Si e' scelto di rifornire il magazzino centrale.");
		String id = "";
		String nome = "";
		String eccipiente = "";
		String produttore = "";
		String formato = "";
		Integer quantita = 0;
		O_Prodotto prodotto = null;
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Inserire il codice identificativo del prodotto: ");
			id = userIn.readLine();
			prodotto = remactserver.checkProdottoAMagazzino(id); 
			if(prodotto == null){
			System.out.println("Questo prodotto non risulta a magazzino quindi e' necessario codificarlo.");
				System.out.print("\nInserire il nome del prodotto: ");
				nome = userIn.readLine();
				System.out.print("\nInserire il nome dell'eccipiente: ");
				eccipiente = userIn.readLine();
				System.out.print("\nInserire il nome del produttore: ");
				produttore = userIn.readLine();
				System.out.print("\nInserire il formato (compresse, sciroppo, supposte, bustine): ");
				formato = userIn.readLine();
				System.out.print("\nInserire la quantita' da mettere a magazzino: ");
				quantita = Integer.parseInt(userIn.readLine());
				prodotto = new O_Prodotto(nome, eccipiente, produttore, formato, quantita);
			}else{
				System.out.print("\nInserire la quantita' da aggiungere a magazzino: ");
				quantita = Integer.parseInt(userIn.readLine());
				prodotto = new O_Prodotto(prodotto, quantita);
			}
			if(remactserver.compraProdotto(id, prodotto))
				System.out.println("L'aggiornamento del magazzino centrale e' stato eseguito correttamente.\n");
			else
				System.out.println("Si e' verificato un errore nell'aggiornamento del magazzino centrale.\n");
		}catch(RemoteException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	private void caricaEsempio(){
		try{
			System.out.println("Si e' scelto di caricare dei dati di esempio nel magazzino centrale.");
			remactserver.caricaEsempio();
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
	}
	
	
	//ALTRE TRANSAZIONI
	
	private void spegniTutto(){
		try{
			System.out.println("Si e' scelto di spegnere tutti i server.");
			String ip ="";
			try{
				Scanner scan = new Scanner(new File("IPserver.txt"));
				ip += scan.nextLine();
				scan.close();
			}catch(FileNotFoundException ex){
				ex.printStackTrace();
			}			
			Properties propCosnaming = new Properties();
			propCosnaming.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			propCosnaming.put("java.naming.provider.url", "iiop:"+ip+":5555");
			InitialContext contextCosnaming = new InitialContext(propCosnaming);
			Object obj = contextCosnaming.lookup("ProxyDualServer");
			ServProxyOff_I proxydual = (ServProxyOff_I)PortableRemoteObject.narrow(obj, ServProxyOff_I.class);
			
			if(proxydual.spegniPBA()){
				System.out.println("Il server Proxy e' stato deregistrato dai servizi di naming e de-esportato.");
				System.out.println("Il server di Bootstrap e' stato deregistrato dai servizi di naming e de-esportato.");
				System.out.println("Il server di Autenticazione e' stato de-esportato e deregistrato dal sistema di attivazione.");
			}else{
				System.out.println("Si e' verificato un errore nello spegnimento dei server.");
			}
			if(remactserver.spegniServer())
				System.out.println("Il server Centrale e' stato de-esportato e deregistrato dal sistema di attivazione.");
			else{
				System.out.println("Si e' verificato un errore nello spegnimento del server centrale.");
			}			
				
		}catch(RemoteException | NamingException ex){
				ex.printStackTrace();
		}
	}
		
}
