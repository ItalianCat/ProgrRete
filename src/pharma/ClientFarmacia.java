/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ClientFarmacia extends UnicastRemoteObject implements ClientMobileAgent_I, ClientFarmacia_I, Serializable{

	private String nomeFarmacia = "";
	private Map<String, O_Prodotto> stock;
	private ServerFarmacia_I remactserver = null;
	
	public ClientFarmacia(MarshalledObject<ServerFarmacia_I> obj, String nome) throws RemoteException{
		nomeFarmacia = nome;		//PERSISTENZA MAGAZZINO?
		stock = new HashMap<String, O_Prodotto>();
		System.out.println("Il client Farmacia ha creato un magazzino vuoto per la farmacia.");
		try{
			remactserver = (ServerFarmacia_I)obj.get();
			System.out.println("Il client Farmacia ha ottenuto la referenza al server centrale.");
		}catch(Exception ex){
			System.out.println("Si e' verificato un errore nell'ottenimento della referenza al server centrale.");
			ex.printStackTrace();
		}
	}
	
	@Override  //da mobile agent interface
	public void act(){
		if(System.getSecurityManager() == null){
			System.setSecurityManager(new RMISecurityManager());
		}
		String selezione = "";
		while(true){
			System.out.println("\nMenu Farmacia\nSelezionare l'opzione desiderata:\n"
								+ "\t1. Registrazione della farmacia presso il server centrale\n"
								+ "\t2. Elenco prodotti disponibili presso il server centrale\n"
								+ "\t3. Acquisto di un prodotto dal magazzino centrale\n"
								+ "\t4. Chiusura definitiva della farmacia\n"
								+ "\t5. Uscita\n");
			try{
				BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
				selezione = userIn.readLine();
				switch(Integer.parseInt(selezione)){
					case 1: registraPressoMagazzinoCentrale();break;
					case 2: mostraProdottiMagazzinoCentrale();break;
					case 3: compraProdottoMagazzinoCentrale();break;
					case 4: chiudiFarmacia();break;
					case 5: System.exit(0);break;
					default: System.out.println("La selezione non e' valida.");
				}
			}catch(RemoteException ex){
				ex.printStackTrace();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	//TRANSAZIONI COL SERVER CENTRALE
		
	private void registraPressoMagazzinoCentrale() throws RemoteException{
		System.out.println("Si e' scelto di registrare la farmacia presso il magazzino centrale.");
		if(remactserver.registra(nomeFarmacia, this))
			System.out.println("La registrazione e' avvenuta con successo.");
		else
			System.out.println("Errore nella registrazione della farmacia presso il server centrale.");
	}
	
	private void mostraProdottiMagazzinoCentrale() throws RemoteException{
		System.out.println("Si e' scelto di visualizzare i prodotti disponibili presso il magazzino centrale.");
		System.out.println(remactserver.toStringMagazzinoCentrale());
	}
	
	private void compraProdottoMagazzinoCentrale() throws RemoteException{
		String id = "";
		Integer quantita = 0;
		O_Prodotto acquistato = null;
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Si e' scelto di comprare un prodotto dal magazzino centrale.");
			System.out.print("Inserire il codice identificativo del prodotto da comprare: ");
			id = userIn.readLine();
			System.out.print("Inserire la quantita' da comprare: ");
			quantita = Integer.parseInt(userIn.readLine());
			acquistato = remactserver.vendiProdotto(id, quantita);
			if(acquistato != null){
				System.out.println("\nIl prodotto e' stato acquistato con successo.");
				aggiornaStock(id, acquistato, true);
				System.out.println("Il magazzino della farmacia e' stato aggiornato con successo.");
			}else{
				System.out.println("La quantita' richiesta non e' disponibile.");
			}
		}catch(RemoteException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	private void chiudiFarmacia() throws RemoteException{
		System.out.println("Si e' scelto di chiudere definitivamente la farmacia.");
		if(remactserver.deregistra(nomeFarmacia)){
			UnicastRemoteObject.unexportObject(this, true);
			System.out.println("La deregistrazione dal server centrale e la de-esportazione sono avvenute con successo.");
		}else
			System.out.println("Si e' verificato un errore nella deregistrazione della farmacia dal server centrale.");
	}
	
	//TRANSAZIONI COL CLIENTE
	
	@Override //da farmacia
	public String toStringMagazzino(){
		String risultato = "ID\t\tNome\t\tEccipiente\t\tProduttore\t\tFormato\t\tQuantita Disponibile\n";
		for(String id: stock.keySet()){
			risultato += id + "\t\t" + stock.get(id).toStringProdotto() + "\n";
		}
		return risultato;
	}
	
	@Override //da farmacia
	public O_Prodotto venditaProdotto(String id, Integer quantita){
		if(aggiornaStock(id, stock.get(id), false)){
			System.out.println("Il magazzino della farmacia e' stato aggiornato con successo.");
			return new O_Prodotto(stock.get(id), quantita);
		}
		else
			return null;
	}
	
	//TRANSAZIONE INTERNA ALLA FARMACIA
	
	private boolean aggiornaStock(String id, O_Prodotto prodotto, boolean segno){
		if(segno){ //aggiungi qta
			if(!stock.containsKey(id))
				stock.put(id, prodotto);
			else
				stock.get(id).quantita += prodotto.quantita;
		}else{ //togli qta
			if(prodotto.quantita <= stock.get(id).quantita){
				stock.get(id).quantita -= prodotto.quantita;
				if(stock.get(id).quantita == 0)
					stock.remove(id);
			}else{
				return false;
			}
		}
		return true;
	}
	
}
