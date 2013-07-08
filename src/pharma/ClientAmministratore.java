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

@SuppressWarnings("serial")
public class ClientAmministratore implements ClientMobileAgent_I, Serializable{
	
	private ServerAmministratore_I remactserver = null;

	public ClientAmministratore(MarshalledObject<ServerAmministratore_I> obj){
		try{
			remactserver = (ServerAmministratore_I)obj.get();
			System.out.println("\nIl client Amministratore ha ottenuto la referenza al server centrale.");
		}catch(ClassNotFoundException | IOException ex){
			System.out.println("\nSi e' verificato un errore nell'ottenimento della referenza al server centrale.");
			ex.printStackTrace();
		}
	}
	
	@Override
	public void act(){
		String selezione = "";
		while(true){
			System.out.println("\n\nMenu Amministratore\nSelezionare l'opzione desiderata:\n"
								+ "\t1. Inserimento prodotti e quantita'\n"
								+ "\t2. Spegnimento del sistema\n" //proxy e boot. gli altri hanno unreferenced 
								+ "\t3. Uscita\n");
			try{
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				selezione = userIn.readLine();
			}catch(IOException ex){
				ex.printStackTrace();
			}
			switch(Integer.parseInt(selezione)){
				case 1: rifornisciMagazzino();break;
				case 2: spegniTutto();break;
				case 3: System.exit(0);break;
				default: System.out.println("\nLa selezione non e' valida.");
			}
		}
	}
	
	//TRANSAZIONI COL SERVER CENTRALE
	
	private void rifornisciMagazzino(){
		System.out.println("\nSi e' scelto di rifornire il magazzino centrale.");
		String id = "";
		String nome = "";
		String eccipiente = "";
		String produttore = "";
		String formato = "";
		Integer quantita = 0;
		O_Prodotto prodotto = null;
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("\nInserire il codice identificativo del prodotto: ");
			id = userIn.readLine();
			prodotto = remactserver.checkProdottoAMagazzino(id); 
			if(prodotto == null){
			System.out.println("\nQuesto prodotto non risulta a magazzino quindi e' necessario codificarlo.");
				System.out.println("\nInserire il nome del prodotto: ");
				nome = userIn.readLine();
				System.out.println("\nInserire il nome dell'eccipiente: ");
				eccipiente = userIn.readLine();
				System.out.println("\nInserire il nome del produttore: ");
				produttore = userIn.readLine();
				System.out.println("\nInserire il formato (compresse, sciroppo, supposte, bustine): ");
				formato = userIn.readLine();
				System.out.println("\nInserire la quantita' da mettere a magazzino: ");
				quantita = Integer.parseInt(userIn.readLine());
				prodotto = new O_Prodotto(nome, eccipiente, produttore, formato, quantita);
			}else{
				System.out.println("\nInserire la quantita' da aggiungere a magazzino: ");
				quantita = Integer.parseInt(userIn.readLine());
				prodotto = new O_Prodotto(prodotto, quantita);
			}
			if(remactserver.compraProdotto(id, prodotto))
				System.out.println("\nL'aggiornamento del magazzino centrale e' stato eseguito correttamente.\n");
			else
				System.out.println("\nSi e' verificato un errore nell'aggiornamento del magazzino centrale.\n");
		}catch(RemoteException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	//ALTRE TRANSAZIONI
	
	private void spegniTutto(){
		try{
			System.out.println("\nSi e' scelto di spegnere tutti i server.");
			ServProxyOff_I proxydual = (ServProxyOff_I)Naming.lookup("ProxyDualServer");
			if(proxydual.spegniPBA()){
				System.out.println("\nIl server Proxy e' stato deregistrato dai servizi di naming e de-esportato.");
				System.out.println("\nIl server di Bootstrap e' stato deregistrato dai servizi di naming e de-esportato.");
				System.out.println("\nIl server di Autenticazione e' stato de-esportato e deregistrato dal sistema di attivazione.");
			}else{
				System.out.println("\nSi e' verificato un errore nello spegnimento dei server.");
			}
			if(remactserver.spegniServer())
				System.out.println("\nIl server Centrale e' stato de-esportato e deregistrato dal sistema di attivazione.");
			else{
				System.out.println("\nSi e' verificato un errore nello spegnimento del server centrale.");
			}			
				
		}catch(RemoteException | MalformedURLException | NotBoundException ex){
				ex.printStackTrace();
		}
	}
		
}
