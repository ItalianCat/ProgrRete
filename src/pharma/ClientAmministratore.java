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
import javax.rmi.PortableRemoteObject;

/**
 * Questa classe definisce un mobile agent che gestisce le operazioni di competenza
 * dell'amministratore del sistema, quali:
 * - controllare le disponibilita' presenti nel magazzino centrale;
 * - rifornire il magazzino centrale con nuovi prodotti o con quantita' aggiuntive
 * di prodotti esistenti;
 * - eliminare un prodotto presente nel magazzino centrale (per esempio, in caso di 
 * errata codifica si puo' eliminare un prodotto e reinserirlo correttamente);
 * - caricare un magazzino centrale di esempio con dieci tipi di prodotti con cui 
 * effettuare dei test sulle funzionalita' del sistema.; 
 * - spegnere tutti i server (per esempio, in caso di problemi legati alla sicurezza).
 */

@SuppressWarnings("serial")
public class ClientAmministratore implements ClientMobileAgent_I, Serializable{
	
	private ServerAmministratore_I remactserver = null;

	/**
	 * Questo costruttore e' invocato dal server di autenticazione al momento del login.
	 * @param obj e' la referenza al server centrale ottenuta dal server di autenticazione
	 */
	public ClientAmministratore(MarshalledObject<ServerAmministratore_I> obj){
		try{
			remactserver = (ServerAmministratore_I)obj.get();
			System.out.println("Il client Amministratore ha ottenuto la referenza al server centrale.");
		}catch(Exception ex){
			System.out.println("Si e' verificato un errore nell'ottenimento della referenza al server centrale.");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Questo metodo visualizza il menu per l'amministratore.
	 */
	@Override
	public void act(){
		Integer selezione = -1;
		while(true){
			System.out.println("\nMenu Amministratore\nSelezionare l'opzione desiderata:\n"
								+ "\t1. Elenco prodotti disponibili presso il magazzino centrale\n"
								+ "\t2. Inserimento prodotti e quantita'\n"
								+ "\t3. Elimina prodotti\n"
								+ "\t4. (Carica dati di esempio)\n" //proxy e boot. gli altri hanno unreferenced 
								+ "\t5. Spegnimento del sistema ed uscita\n"
								+ "\t6. Uscita\n");
			try{
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				try{
					selezione = Integer.parseInt(userIn.readLine());
					if(!(selezione >= 1 & selezione <=6)){
						System.out.println("!!! E' necessario inserire un numero tra 1 e 6 !!!");
						continue;
					}
				}catch(NumberFormatException g){
					System.out.println("!!! E' necessario inserire un numero tra 1 e 5 !!!");
					continue;
				}
				switch(selezione){
				case 1: mostraProdottiMagazzinoCentrale();break;
				case 2: rifornisciMagazzino();break;
				case 3: eliminaProdotto();break;
				case 4: caricaEsempio();break;
				case 5:	spegniTutto();break;
				case 6: System.exit(0);break;
				default: System.out.println("!!! La selezione non e' valida !!!");
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	//TRANSAZIONI COL SERVER CENTRALE

	/**
	 * Questo metodo usa la referenza al server centrale per invocare il metodo
	 * (toStringMagazzinoCentrale) per visualizzare il magazzino centrale.
	 */
	private void mostraProdottiMagazzinoCentrale(){
		try{
			System.out.println("Si e' scelto di visualizzare i prodotti disponibili presso il magazzino centrale.");
			System.out.println(remactserver.toStringMagazzinoCentrale());
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
	}
		
	/**
	 * Questo metodo consente di inserire un nuovo prodotto nel magazzino centrale; in tal 
	 * caso vengono richiesti tutti i dati per codificarlo. Oppure si puo' aggiungere una 
	 * quantita' ad un prodotto gia' a magazzino; in tal caso, oltre all'identificativo del 
	 * prodotto, e' richiesta solo la quantita' da aggiungere.
	 * Fa uso di due metodi invocati sulla referenza al magazzino centrale: il primo 
	 * (checkProdottoAMagazzino) per controllare se un prodotto e' gia' codificato e 
	 * l'altro (compraProdotto) per aggiornare il magazzino centrale.
	 */
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
				System.out.print("\nInserire il formato (compresse, sciroppo, supposte, bustine, crema): ");
				formato = userIn.readLine();
				do{
					System.out.print("\nInserire la quantita' da mettere a magazzino: ");
					try{
						quantita = Integer.parseInt(userIn.readLine());
						if(!(quantita >= 1)){
							System.out.println("!!! E' necessario inserire un numero maggiore di 0 !!!");
						}
					}catch(NumberFormatException g){
						System.out.println("!!! E' necessario inserire un numero maggiore di 0 !!!");
					}
				}while(!(quantita>=1));
				prodotto = new O_Prodotto(nome, eccipiente, produttore, formato, quantita);
			}else{
				System.out.print("\nInserire la quantita' da aggiungere a magazzino: ");
				quantita = Integer.parseInt(userIn.readLine());
				prodotto = new O_Prodotto(prodotto, quantita);
			}
			if(remactserver.compraProdotto(id, prodotto))
				System.out.println("L'aggiornamento del magazzino centrale e' stato eseguito correttamente.");
			else
				System.out.println("Si e' verificato un errore nell'aggiornamento del magazzino centrale.\n");
		}catch(RemoteException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Questo metodo consente di eliminare un prodotto dal magazzino centrale. Prima 
	 * visualizza l'elenco dei prodotti a magazzino con (toStringMagazzinoCentrale). Poi 
	 * chiede all'amministratore quale vuole eliminare e controlla se esiste con 
	 * (checkProdottoAMagazzino). Infine lo elimina con (eliminaProdotto).
	 */
	private void eliminaProdotto() {
		System.out.println("Si e' scelto di modificare la codifica di un prodotto nel magazzino centrale.");
		String id = "";
		O_Prodotto prodotto = null;
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println(remactserver.toStringMagazzinoCentrale() + 
					"\nInserire il codice identificativo del prodotto da eliminare: ");
			id = userIn.readLine();
			prodotto = remactserver.checkProdottoAMagazzino(id);
			if(prodotto == null){
				System.out.println("!!! Il codice scelto non e' presente in magazzino !!!\n");
				return;
			}
			remactserver.eliminaProdotto(id);
			System.out.println("Il prodotto e' stato eliminato con successo dal magazzino centrale.");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Questo metodo consente di caricare nel magazzino centrale un magazzino precostruito 
	 * con dieci tipi di prodotti con varie quantita'. Usa il metodo remoto (caricaEsempio).
	 */
	private void caricaEsempio(){
		try{
			System.out.println("Si e' scelto di caricare dei dati di esempio nel magazzino centrale.");
			remactserver.caricaEsempio();
			System.out.println("\nI dati di esempio sono stati caricati correttamente nel magazzino centrale.\n");
		}catch(RemoteException ex){
			ex.printStackTrace();
		}
	}
	
	
	//ALTRE TRANSAZIONI
	
	/**
	 * Questo metodo consente di spegnere tutti i server. Prima fa una lookup sul servizio 
	 * di CosNaming per ottenere una referenza al server proxy. Poi invoca il metodo del 
	 * proxy (spegniPBA) che si occupa di de-registrare e de-esportare il server proxy, il 
	 * server di bootstrap e il server di autenticazione. Infine usa la referenza al 
	 * server centrale per invocare il suo metodo (spegniServer()) che lo de-esporta e lo 
	 * deregistra dal sistema di attivazione. In questo modo non sara' piu' attivabile da 
	 * eventuali client che ne abbiano conservato una referenza.
	 */
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
			ServProxy_I proxydual = (ServProxy_I)PortableRemoteObject.narrow(obj, ServProxy_I.class);
			
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
				
		}catch(Exception ex){
				ex.printStackTrace();
		}
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof ClientAmministratore) || obj == null){
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
