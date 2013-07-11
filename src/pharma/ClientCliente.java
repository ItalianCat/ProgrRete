/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.*;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;

@SuppressWarnings("serial")
public class ClientCliente implements ClientMobileAgent_I, Serializable{
	
	private ServerCliente_I remactserver = null;  //dove dico che e' IIOP o RMI?
	
	public ClientCliente(MarshalledObject<ServerCliente_I> obj){
		try{
			remactserver = (ServerCliente_I)obj.get();
			System.out.println("Il client Cliente ha ottenuto la referenza al server centrale.");
		}catch(ClassNotFoundException | IOException ex){
			System.out.println("Si e' verificato un errore nell'ottenimento della referenza al server centrale.");
			ex.printStackTrace();
		}
	
	}
	
	@Override //da mobile agent interface
	public void act(){
		String selezione = "";
		while(true){
			System.out.println("\nMenu Cliente\nSelezionare l'opzione desiderata:\n"
								+ "\t1. Elenco prodotti disponibili presso il magazzino centrale\n"
								+ "\t2. Elenco prodotti disponibili presso una farmacia\n"
								+ "\t3. Elenco farmacie\n"
								+ "\t4. Acquisto di un prodotto dal magazzino centrale\n"
								+ "\t5. Acquisto di un prodotto da una farmacia\n"
								+ "\t6. Uscita\n");
			try{
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				selezione = userIn.readLine();
				switch(Integer.parseInt(selezione)){
					case 1: mostraProdottiMagazzinoCentrale();break;
					case 2: mostraProdottiFarmacia();break;
					case 3: mostraFarmacie();break;
					case 4: compraProdottoMagazzinoCentrale();break;
					case 5: compraProdottoFarmacia();break;
					case 6: System.exit(0);break;
					default: System.out.println("La selezione non e' valida.");
				}
			}catch(RemoteException | ClassNotFoundException | ActivationException ex){
				ex.printStackTrace();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	//TRANSAZIONI COL SERVER CENTRALE
	
	private void mostraProdottiMagazzinoCentrale() throws RemoteException{
		System.out.println("Si e' scelto di visualizzare i prodotti disponibili presso il magazzino centrale.");
		System.out.println(remactserver.toStringMagazzinoCentrale());
	}
	
	private void mostraFarmacie() throws RemoteException{
		System.out.println("Si e' scelto di visualizzare l'elenco delle farmacie registratre presso il server centrale.");
		System.out.println(remactserver.mostraFarmacie());
	}
	
	private void compraProdottoMagazzinoCentrale()throws RemoteException{
		String id = "";
		Integer qta = 0;
		O_Prodotto acquistato = null;
		System.out.println("Si e' scelto di comprare un prodotto dal magazzino centrale.");
		System.out.println("Il magazzino centrale vende i seguenti prodotti:\n");
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.print(remactserver.toStringMagazzinoCentrale()
								+ "\nInserire il codice identificativo del prodotto da comprare: ");
						id = userIn.readLine();
			System.out.print("\nInserire la quantita' che vuoi comprare: ");
			qta = Integer.parseInt(userIn.readLine());
			acquistato = remactserver.vendiProdotto(id, qta);
			if(acquistato != null)
				System.out.println("\nIl prodotto e' stato acquistato con successo.");
			else
				System.out.println("La quantita' richiesta non e' disponibile.");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	
	//TRANSAZIONI CON UNA FARMACIA
	
	private void mostraProdottiFarmacia() throws RemoteException, ClassNotFoundException, ActivationException{
		String nome = "";
		ClientFarmacia_I farmacia = null;
		System.out.println("Si e' scelto di visualizzare i prodotti disponibili presso una farmacia.");
		System.out.print("\nDigitare il nome della farmacia: ");
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			nome = userIn.readLine();
			farmacia = remactserver.getFarmacia(nome);
			System.out.println("E' stata ottenuta la referenza alla farmacia " + nome);
		}catch(IOException ex){
			ex.printStackTrace();
		}
		System.out.println("La farmacia " + nome + " vende i seguenti prodotti:\n" + farmacia.toStringMagazzino());
	}

	private void compraProdottoFarmacia() throws RemoteException, ClassNotFoundException, ActivationException{
		String nome = "";
		String id = "";
		Integer qta = 0;
		ClientFarmacia_I farmacia = null;
		O_Prodotto acquistato = null;
		System.out.println("Si e' scelto di comprare un prodotto da una farmacia.");
		System.out.print("Digitare il nome della farmacia: ");
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			nome = userIn.readLine();
			farmacia = remactserver.getFarmacia(nome);
			System.out.println("\nE' stata ottenuta la referenza alla farmacia " + nome);
			System.out.print("La farmacia " + nome + " vende i seguenti prodotti:\n"
							+ farmacia.toStringMagazzino()
							+ "\nInserire il codice identificativo del prodotto da comprare: ");
			id = userIn.readLine();
			System.out.print("Inserire la quantita' da comprare: ");
			qta = Integer.parseInt(userIn.readLine());
			acquistato = farmacia.venditaProdotto(id, qta);
			if(acquistato != null)
				System.out.println("Il prodotto e' stato acquistato con successo.");
			else
				System.out.println("La quantita' richiesta non e' disponibile.");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	
}
