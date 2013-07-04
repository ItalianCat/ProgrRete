package pharma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;

@SuppressWarnings("serial")
public class Amministratore implements MobileAgent, Serializable {
	
	private ActAmministratore remactserver = null;

	public Amministratore(MarshalledObject<ActAmministratore> obj){
		try{
			remactserver = (ActAmministratore)obj.get();
		}catch(Exception ex){ //IOException se non si riesce ad accedere al server e ClassNotFound se non si trova la classe del server
			ex.printStackTrace();
		}
	}
	
	@Override
	public void act(){
		String selezione = "";
		while(true){
			System.out.println("Menu Amministratore:\n"
								+ "\t1. Inserimento prodotti e quantita'\n"
								+ "\t2. Spegnimento del proxy\n"
								+ "\t3. Uscita\n");
			try{
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				selezione = userIn.readLine();
			}catch(IOException ex){
				ex.printStackTrace();
			}
			switch(Integer.parseInt(selezione)){
				case 1: rifornisciMagazzino();break;
				case 2: spegniProxy();break;
				case 3: System.exit(0);break;
				default: System.out.println("La selezione non e' valida\n");
			}
		}
	}
	
	//TRANSAZIONI COL SERVER CENTRALE
	
	private void rifornisciMagazzino(){
		String id = "";
		String nome = "";
		String eccipiente = "";
		String produttore = "";
		String formato = "";
		Integer quantita = 0;
		Prodotto prodotto = null;
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("\nInserisci il codice identificativo del prodotto: ");
			id = userIn.readLine();
			prodotto = remactserver.checkProdottoAMagazzino(id); 
			if(prodotto == null){
				System.out.println("\nInserisci il nome del prodotto: ");
				nome = userIn.readLine();
				System.out.println("\nInserisci il nome dell'eccipiente: ");
				eccipiente = userIn.readLine();
				System.out.println("\nInserisci il nome del produttore: ");
				produttore = userIn.readLine();
				System.out.println("\nInserisci il formato (compresse, sciroppo, supposte, bustine): ");
				formato = userIn.readLine();
				System.out.println("\nInserisci la quantita' da mettere a magazzino: ");
				quantita = Integer.parseInt(userIn.readLine());
				prodotto = new Prodotto(nome, eccipiente, produttore, formato, quantita);
			}else{				
				System.out.println("\nInserisci la quantita' da aggiungere al magazzino per il prodotto indicato: ");
				quantita = Integer.parseInt(userIn.readLine());
				prodotto = new Prodotto(prodotto, quantita);
			}
			if(remactserver.compraProdotto(id, prodotto))
				System.out.println("\nL'aggiornamento del magazzino centrale e' stato eseguito correttamente\n");
			else
				System.out.println("\nErrore nell'aggiornamento del magazzino centrale\n");
		}catch(RemoteException ex){
			ex.printStackTrace();
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	//TRANSAZIONI COL PROXY
	
	private void spegniProxy(){
		// TODO Auto-generated method stub
		
	}
}
