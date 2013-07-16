/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.MarshalledObject;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.*;

@SuppressWarnings("serial")
public class ClientFarmacia extends UnicastRemoteObject implements ClientMobileAgent_I, ClientFarmacia_I, Serializable{

	private String nomeFarmacia = "";
	public O_Magazzino stock = null;
	private ServerFarmacia_I remactserver = null;
	private boolean flag = false;
	
	/**
	 * Questo costruttore e' invocato dal server di autenticazione al momento del login.
	 * @param obj e' la referenza al server centrale ottenuta dal server di autenticazione
	 * @param nome e' il nome della farmacia
	 * @throws UnknownHostException 
	 */
	public ClientFarmacia(MarshalledObject<ServerFarmacia_I> obj, String nome) throws RemoteException, UnknownHostException{
		nomeFarmacia = nome;
		stock = new O_Magazzino();
		System.out.println("Il client Farmacia ha creato un magazzino vuoto per la farmacia.");
		try{
			remactserver = (ServerFarmacia_I)obj.get();
			System.out.println("Il client Farmacia ha ottenuto la referenza al server centrale.");
		}catch(Exception ex){
			System.out.println("!!! Errore nell'ottenimento della referenza al server centrale !!!");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Questo metodo visualizza il menu per le farmacie. Al primo login, salva su file il 
	 * magazzino della farmacia. Ai login successivi, carica da file il magazzino della 
	 * farmacia.
	 */
	@SuppressWarnings("unchecked")
	@Override  //da mobile agent interface
	public void act(){
		Integer selezione = -1;
		if(System.getSecurityManager() == null){
			System.setSecurityManager(new RMISecurityManager());
		}
		if(!flag){//se non esiste il file del magazzino, lo creo
			try{
				OutputStream out = new FileOutputStream("Magazzino"+nomeFarmacia);
				ObjectOutputStream outObj = new ObjectOutputStream(out);
				outObj.writeObject(new MarshalledObject<O_Magazzino>(stock));
				outObj.flush();
				outObj.close();
				flag = true;
			}catch(IOException ex){
				System.out.println("!!! Errore nel salvataggio del magazzino della farmacia su file !!!");
				ex.printStackTrace();
			}
		}
		try{//carico il file
			InputStream in = new URL("file://"+Input.percorso+"/javarmi/Magazzino"+nomeFarmacia).openStream();
			ObjectInputStream inObj = new ObjectInputStream(in);
			MarshalledObject<O_Magazzino> objStock = (MarshalledObject<O_Magazzino>)inObj.readObject();
			stock = (O_Magazzino)objStock.get();
			inObj.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		while(true){
			System.out.println("\nMenu Farmacia\nSelezionare l'opzione desiderata:\n"
								+ "\t1. Registrazione della farmacia presso il server centrale\n"
								+ "\t2. Elenco prodotti disponibili presso il server centrale\n"
								+ "\t3. Acquisto di un prodotto dal magazzino centrale\n"
								+ "\t4. Elenco prodotti disponibili presso la farmacia\n"
								+ "\t5. Chiusura definitiva della farmacia\n"
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
					System.out.println("!!! E' necessario inserire un numero tra 1 e 6 !!!");
					continue;
				}	
				switch(selezione){
					case 1: registraPressoMagazzinoCentrale();break;
					case 2: mostraProdottiMagazzinoCentrale();break;
					case 3: compraProdottoMagazzinoCentrale();break;
					case 4: System.out.println(toStringMagazzino());break;
					case 5: chiudiFarmacia();break;
					case 6: esciEsalva();break;
					default: System.out.println("!!! La selezione non e' valida !!!");
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	//TRANSAZIONI COL SERVER CENTRALE
	/**
	 * Questo metodo registra la farmacia presso il server centrale, usando il metodo 
	 * remoto (registra).
	 */
	private void registraPressoMagazzinoCentrale(){
		System.out.println("Si e' scelto di registrare la farmacia presso il magazzino centrale.");
		try{
			if(remactserver.registra(nomeFarmacia, this))
				System.out.println("\nLa registrazione e' avvenuta con successo.");
			else
				System.out.println("!!! Errore nella registrazione della farmacia presso il server centrale !!!");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Questo metodo usa la referenza al server centrale per invocare il metodo
	 * (toStringMagazzinoCentrale) per visualizzare il magazzino centrale.
	 */
	private void mostraProdottiMagazzinoCentrale(){
		System.out.println("Si e' scelto di visualizzare i prodotti disponibili presso il magazzino centrale.");
		try{
			System.out.println(remactserver.toStringMagazzinoCentrale());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Questo metodo usa la referenza la server centrale per acquistare dei prodotti.
	 * Fa uso di tre metodi remoti: il primo (toStringMagazzinoCentrale) mostra i prodotti 
	 * disponibili; il secondo (checkProdottoAMagazzino) controlla se il prodotto scelto 
	 * esiste nel magazzino e l'ultimo (vendiProdotto) vende il prodotto prelevandolo 
	 * dal magazzino centrale. Alla fine si aggiorna il magazzino della farmacia.
	 */
	private void compraProdottoMagazzinoCentrale(){
		String id = "";
		Integer quantita = 0;
		O_Prodotto acquistato = null;
		boolean ok = false;
		System.out.println("Si e' scelto di comprare un prodotto dal magazzino centrale.");
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			do{
				System.out.println("Il magazzino centrale vende i seguenti prodotti:\n"
						+ remactserver.toStringMagazzinoCentrale()
						+ "\nInserire il codice identificativo del prodotto da comprare: ");
				id = userIn.readLine();
				if(remactserver.checkProdottoAMagazzino(id) == null){
					System.out.print("!!! Il codice scelto non e' presente in magazzino !!!\n\n");
				}else{
					ok = true;
				}
			}while(!ok);
			do{
				System.out.print("Inserire la quantita' da comprare: ");
				try{
					quantita = Integer.parseInt(userIn.readLine());
					if(!(quantita >= 1)){
						System.out.println("!!! E' necessario inserire un numero maggiore di 0 !!!");
					}
				}catch(NumberFormatException g){
					System.out.println("!!! E' necessario inserire un numero maggiore di 0 !!!");
				}
			}while(!(quantita>=1));
			acquistato = remactserver.vendiProdotto(id, quantita);
			if(acquistato != null){
				System.out.println("\nIl prodotto e' stato acquistato con successo.");
				stock.compraProdotto(id, acquistato);
				System.out.println("Il magazzino della farmacia e' stato aggiornato con successo.");
			}else{
				System.out.println("!!! La quantita' richiesta non e' disponibile !!!");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Questo metodo consente di chiudere definitivamente una farmacia, de-registrandola 
	 * dal server centrale col metodo remoto (deregistra) e de-esportandola. 
	 */
	private void chiudiFarmacia(){
		System.out.println("Si e' scelto di chiudere definitivamente la farmacia.");
		try{
			if(remactserver.deregistra(nomeFarmacia)){
				UnicastRemoteObject.unexportObject(this, true);
				System.out.println("La deregistrazione dal server centrale e la de-esportazione sono avvenute con successo.");
			}else{
				System.out.println("Si e' verificato un errore nella deregistrazione della farmacia dal server centrale.");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//TRANSAZIONI COL CLIENTE
	
	/**
	 * Questo metodo visualizza una tabella con le disponibilita' di magazzino.
	 */
	@Override
	public String toStringMagazzino(){
		System.out.println("La farmacia sta per visualizzare il proprio magazzino.\n");
		return stock.toStringMagazzino();
	}
	
	/**
	 * Questo metodo consente di vendere un prodotto ad un cliente finale. Invoca il 
	 * metodo vendiProdotto dell'oggetto O_Magazzino.
	 * @param id e' il codice identificativo del prodotto
	 * @param qta e' la quantita' da vendere del prodotto
	 * @return oggetto che rappresenta il prodotto venduto 
	 */
	@Override
	public O_Prodotto venditaProdotto(String id, Integer qta){
		System.out.println("La farmacia sta per vendere " + qta + (qta==1?" pezzo":" pezzi") + " del prodotto " + id + ".");
		return stock.vendiProdotto(id, qta);
	}
	
	/**
	 * Questo metodo consente di verificare se un prodotto e' presente in magazzino, 
	 * invocando il metodo checkProdottoAMagazzino dell'oggetto O_Magazzino.
	 * @param id e' il codice identificativo del prodotto
	 * @return oggetto che rappresenta il prodotto che si e' verificato essere a magazzino, 
	 * null se non e' presente a magazzino 
	 */
	@Override
	public O_Prodotto checkProdottoAMagazzino(String id){ //throws RemoteException?
		System.out.println("La farmacia sta verificare se il prodotto " + id + "e' presente in magazzino.");
		return stock.checkProdottoAMagazzino(id);
	}
	
	//TRANSAZIONE INTERNA ALLA FARMACIA

	/**
	 * Questo metodo consente di effettuare il logout dal sistema della farmacia dopo 
	 * aver salvato su file il magazzino della farmacia, per futuri login.
	 */
	private void esciEsalva() {
		try{
			OutputStream out = new FileOutputStream("Magazzino"+nomeFarmacia);
			ObjectOutputStream outObj = new ObjectOutputStream(out);
			outObj.writeObject(new MarshalledObject<O_Magazzino>(stock));
			outObj.flush();
			outObj.close();
		}catch(IOException ex){
			System.out.println("!!! Errore nel salvataggio del magazzino della farmacia su file !!!");
			ex.printStackTrace();
		}
		System.exit(0);
		
	}

	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof ClientFarmacia) || obj == null){
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
