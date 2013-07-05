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
public class FarmaciaImpl extends UnicastRemoteObject implements MobileAgent, Farmacia, Serializable{

	private String nomeFarmacia = "";
	private Map<String, Prodotto> stock;
	private ActFarmacia remactserver = null;
	
	public FarmaciaImpl(MarshalledObject<ActFarmacia> obj, String nome) throws RemoteException{
		nomeFarmacia = nome;
		stock = new HashMap<String, Prodotto>();
		try{
			remactserver = (ActFarmacia)obj.get();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override  //da mobile agent interface
	public void act(){
		if(System.getSecurityManager() == null){ //Quando??
			System.setSecurityManager(new RMISecurityManager());
		}
		String selezione = "";
		while(true){
			System.out.println("Menu Farmacia:\n"
								+ "\t1. Registra la farmacia presso il server centrale\n"
								+ "\t2. Elenco prodotti disponibili presso il server centrale\n"
								+ "\t3. Acquista un prodotto dal magazzino centrale\n"
								+ "\t4. Chiudi definitivamente la farmacia\n"
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
					default: System.out.println("La selezione non e' valida\n");
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
		if(remactserver.registra(nomeFarmacia, this))
			System.out.println("\nRegistrazione avvenuta con successo");
		else
			System.out.println("Errore nella registrazione della farmacia presso il server centrale");
	}
	
	private void mostraProdottiMagazzinoCentrale() throws RemoteException{
		System.out.println(remactserver.toStringMagazzinoCentrale());
	}
	
	private void compraProdottoMagazzinoCentrale() throws RemoteException{
		String id = "";
		Integer quantita = 0;
		Prodotto acquistato = null;
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("\nInserisci il codice identificativo del prodotto da comprare: ");
			id = userIn.readLine();
			System.out.println("\nInserisci la quantita' da comprare: ");
			quantita = Integer.parseInt(userIn.readLine());
			acquistato = remactserver.vendiProdotto(id, quantita);
			if(acquistato != null){
				System.out.println("\nIl prodotto e' stato acquistato con successo");
				aggiornaStock(id, acquistato, true);
			}else{
				System.out.println("\nLa quantita' richiesta non e' disponibile");
			}
		}catch(RemoteException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	private void chiudiFarmacia() throws RemoteException{
		if(remactserver.deregistra(nomeFarmacia)){
			UnicastRemoteObject.unexportObject(this, true);
			System.out.println("\nDeregistrazione avvenuta con successo");
		}else
			System.out.println("Errore nella deregistrazione della farmacia dal server centrale");
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
	public Prodotto venditaProdotto(String id, Integer quantita){
		if(aggiornaStock(id, stock.get(id), false))
			return new Prodotto(stock.get(id), quantita);
		else
			return null;
	}

	//TRANSAZIONI COL PROXY
	//shutdown
	
	//TRANSAZIONE INTERNA ALLA FARMACIA
	
	private boolean aggiornaStock(String id, Prodotto prodotto, boolean segno){
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
