/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;
import java.io.*;
import java.rmi.MarshalledObject;

/**
 * Questa classe definisce un mobile agent che gestisce le operazioni di competenza
 * di un cliente finale, quali:
 * - vedere i prodotti presenti nel magazzino centrale;
 * - vedere l'elenco delle farmacie registrate presso il server centrale;
 * - vedere i prodotti presenti in una farmacia;
 * - acquistare un prodotto dal magazzino centrale;
 * - acquistare un prodotti da una farmacia.
 */
@SuppressWarnings("serial")
public class ClientCliente implements ClientMobileAgent_I, Serializable{
	
	private ServerCliente_I remactserver = null;

	/**
	 * Questo costruttore e' invocato dal server di autenticazione al momento del login.
	 * @param obj e' la referenza al server centrale ottenuta dal server di autenticazione
	 */
	public ClientCliente(MarshalledObject<ServerCliente_I> obj){
		try{
			remactserver = (ServerCliente_I)obj.get();
			System.out.println("Il client Cliente ha ottenuto la referenza al server centrale.\nSTUB = " +remactserver);
		}catch(Exception ex){
			System.out.println("Si e' verificato un errore nell'ottenimento della referenza al server centrale.");
			ex.printStackTrace();
		}
	
	}
	
	/**
	 * Questo metodo visualizza il menu per i clienti finali.
	 */
	@Override //da mobile agent interface
	public void act(){
		Integer selezione = -1;
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
					case 1: mostraProdottiMagazzinoCentrale();break;
					case 2: mostraProdottiFarmacia();break;
					case 3: mostraFarmacie();break;
					case 4: compraProdottoMagazzinoCentrale();break;
					case 5: compraProdottoFarmacia();break;
					case 6: System.exit(0);break;
					default: System.out.println("!!! La selezione non e' valida !!!");
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	//TRANSAZIONI COL SERVER CENTRALE

	/**
	 * Questo metodo usa la referenza al server centrale per invocare il metodo
	 * (toStringMagazzinoCentrale) per visualizzare il magazzino centrale.
	 */
	public void mostraProdottiMagazzinoCentrale(){
		System.out.println("Si e' scelto di visualizzare i prodotti disponibili presso il magazzino centrale.");
		try{
			System.out.println(remactserver.toStringMagazzinoCentrale());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Questo metodo usa la referenza al server centrale per invocare il metodo
	 * remoto (mostraFarmacie) per visualizzare l'elenco delle farmacie registrate.
	 */
	private void mostraFarmacie(){
		System.out.println("Si e' scelto di visualizzare l'elenco delle farmacie registrate presso il server centrale.");
		try{
			System.out.println(remactserver.mostraFarmacie());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Questo metodo usa la referenza la server centrale per acquistare dei prodotti.
	 * Fa uso di tre metodi remoti: il primo (toStringMagazzinoCentrale) mostra i prodotti 
	 * disponibili; il secondo (checkProdottoAMagazzino) controlla se il prodotto scelto 
	 * esiste nel magazzino e l'ultimo (vendiProdotto) vende il prodotto prelevandolo 
	 * dal magazzino centrale.
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
						+ "\nInserire il codice identificativo del prodotto da comprare (x per uscire): ");
				id = userIn.readLine();
				if(id.equals("x") || id.equals("X"))
					return;
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
					if(quantita == 0)
						return;
					if(!(quantita >= 1)){
						System.out.println("!!! E' necessario inserire almeno 1 come quantita' !!!");
					}
				}catch(NumberFormatException g){
					System.out.println("!!! E' necessario inserire un numero !!!");
				}
			}while(!(quantita>=1));
			acquistato = remactserver.vendiProdotto(id, quantita);
			if(acquistato != null)
				System.out.println("\nIl prodotto e' stato acquistato con successo.");
			else
				System.out.println("!!! La quantita' richiesta non e' disponibile !!!");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	
	//TRANSAZIONI CON UNA FARMACIA

	/**
	 * Questo metodo usa la referenza al server centrale per invocare il metodo 
	 * (checkFarmaciaRegistrata) per controllare se la farmacia indicata e' registrata e 
	 * poi il metodo (toStringMagazzino) per visualizzare il magazzino della farmacia.
	 */
	private void mostraProdottiFarmacia(){
		String nome = "";
		ClientFarmacia_I farmacia = null;
		System.out.println("Si e' scelto di visualizzare i prodotti disponibili presso una farmacia.");
		System.out.print("\nDigitare il nome della farmacia: ");
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			nome = userIn.readLine();
			if(remactserver.checkFarmaciaRegistrata(nome)){
				farmacia = remactserver.getFarmacia(nome);
				System.out.println("E' stata ottenuta la referenza alla farmacia " + nome);
			}else{
				System.out.println("!!! Il nome indicato non corrisponde ad alcuna farmacia registrata presso il server centrale.");
				return;
			}
			System.out.println("La farmacia " + nome + " vende i seguenti prodotti:\n" + farmacia.toStringMagazzino());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Questo metodo usa la referenza al server centrale per controllare se la farmacia 
	 * e' registrata col metodo (checkFarmaciaRegistrata). Poi usa la referenza ad una 
	 * farmacia per acquistare dei prodotti. A questo scopo invoca tre metodi remoti: 
	 * il primo (toStringMagazzino) mostra i prodotti disponibili presso la farmacia; 
	 * il secondo (checkProdottoAMagazzino) controlla se il prodotto scelto 
	 * esiste nel magazzino e l'ultimo (venditaProdotto) vende il prodotto prelevandolo 
	 * dal magazzino della farmacia.
	 */
	private void compraProdottoFarmacia(){
		String nome = "";
		String id = "";
		Integer quantita = 0;
		ClientFarmacia_I farmacia = null;
		O_Prodotto acquistato = null;
		boolean ok = false;
		System.out.println("Si e' scelto di comprare un prodotto da una farmacia.");
		try{
			System.out.println(remactserver.mostraFarmacie());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.print("Digitare il nome della farmacia: ");
		try{
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
			nome = userIn.readLine();
			if(remactserver.checkFarmaciaRegistrata(nome)){
				farmacia = remactserver.getFarmacia(nome);
				System.out.println("E' stata ottenuta la referenza alla farmacia " + nome);
			}else{
				System.out.println("!!! Il nome indicato non corrisponde ad alcuna farmacia registrata presso il server centrale.");
				return;
			}
			do{
				System.out.print("La farmacia " + nome + " vende i seguenti prodotti:\n"
						+ farmacia.toStringMagazzino()
						+ "\nInserire il codice identificativo del prodotto da comprare (x per uscire): ");
				id = userIn.readLine();
				if(id.equals("x") || id.equals("X"))
					return;
				if(farmacia.checkProdottoAMagazzino(id) == null){
					System.out.print("!!! Il codice scelto non e' presente in magazzino !!!\n");
				}else{
					ok = true;
				}
			}while(!ok);
			do{
				System.out.print("Inserire la quantita' da comprare: ");
				try{
					quantita = Integer.parseInt(userIn.readLine());
					if(quantita == 0)
						return;
					if(!(quantita >= 1)){
						System.out.println("!!! E' necessario inserire almeno 1 come quantita' !!!");
					}
				}catch(NumberFormatException g){
					System.out.println("!!! E' necessario inserire un numero !!!");
				}
			}while(!(quantita>=1));
			acquistato = farmacia.venditaProdotto(id, quantita);
			if(acquistato != null)
				System.out.println("Il prodotto e' stato acquistato con successo.");
			else
				System.out.println("!!! La quantita' richiesta non e' disponibile !!!");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof ClientCliente) || obj == null){
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
