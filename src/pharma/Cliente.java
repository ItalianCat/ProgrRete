package pharma;

import java.io.*;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;

@SuppressWarnings("serial")
public class Cliente implements MobileAgent, Serializable{
	
	private ActCliente remactserver = null;
	
	public Cliente(MarshalledObject<ActCliente> obj){
		try{
			remactserver = (ActCliente)obj.get();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override //da mobile agent interface
	public void act(){
		String selezione = "";
		while(true){
			System.out.println("Menu Cliente:\n"
								+ "\t1. Elenco prodotti disponibili presso il magazzino centrale\n"
								+ "\t2. Elenco prodotti disponibili presso una farmacia\n"
								+ "\t3. Elenco farmacie\n"
								+ "\t4. Acquista un prodotto dal magazzino centrale\n"
								+ "\t5. Acquista un prodotto da una farmacia\n"
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
					default: System.out.println("La selezione non e' valida\n");
				}
			}catch(RemoteException ex){
				ex.printStackTrace();
			}catch(IOException ex){
				ex.printStackTrace();
			}catch(ClassNotFoundException ex){
				ex.printStackTrace();
			}catch(ActivationException ex){
				ex.printStackTrace();
			}
		}
	}
	
	//TRANSAZIONI COL SERVER CENTRALE
	
	private void mostraProdottiMagazzinoCentrale() throws RemoteException{
		System.out.println(remactserver.toStringMagazzinoCentrale());
	}
	
	private void mostraFarmacie() throws RemoteException{
		System.out.println(remactserver.mostraFarmacie());
	}
	
	private void compraProdottoMagazzinoCentrale()throws RemoteException{
		String id = "";
		Integer qta = 0;
		Prodotto acquistato = null;
		System.out.println("\nIl magazzino centrale vende i seguenti prodotti:\n");
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println(remactserver.toStringMagazzinoCentrale()
								+ "\n\nInserisci il codice identificativo del prodotto da comprare: ");
						id = userIn.readLine();
			System.out.println("\nInserisci la quantita' che vuoi comprare: ");
			qta = Integer.parseInt(userIn.readLine());
			acquistato = remactserver.vendiProdotto(id, qta);
			if(acquistato != null)
				System.out.println("\nIl prodotto e' stato acquistato con successo");
			else
				System.out.println("\nLa quantita' richiesta non e' disponibile");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	
	//TRANSAZIONI CON UNA FARMACIA
	
	private void mostraProdottiFarmacia() throws RemoteException, ClassNotFoundException, ActivationException{
		String nome = "";
		Farmacia farmacia = null;
		System.out.println("\nDigita il nome della farmacia: ");
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			nome = userIn.readLine();
			farmacia = remactserver.getFarmacia(nome);
		}catch(IOException ex){
			ex.printStackTrace();
		}
		System.out.println(farmacia.toStringMagazzino());
	}

	private void compraProdottoFarmacia() throws RemoteException, ClassNotFoundException, ActivationException{
		String nome = "";
		String id = "";
		Integer qta = 0;
		Farmacia farmacia = null;
		Prodotto acquistato = null;
		System.out.println("\nDigita il nome della farmacia: ");
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			nome = userIn.readLine();
			farmacia = remactserver.getFarmacia(nome);
			System.out.println("\nLa farmacia selezionata vende i seguenti prodotti:\n"
							+ farmacia.toStringMagazzino()
							+ "\n\nInserisci il codice identificativo del prodotto da comprare: ");
			id = userIn.readLine();
			System.out.println("\nInserisci la quantita' che vuoi comprare: ");
			qta = Integer.parseInt(userIn.readLine());
			acquistato = farmacia.venditaProdotto(id, qta);
			if(acquistato != null)
				System.out.println("\nIl prodotto e' stato acquistato con successo");
			else
				System.out.println("\nLa quantita' richiesta non e' disponibile");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	
}
