/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.*;
import java.rmi.activation.*;
import java.rmi.server.*;
import java.util.Vector;

/**
 * Questa classe definisce le attivita' del server centrale quali la registrazione e la 
 * deregistrazione di una farmacia, la visualizzazione delle farmacie registrate, il passaggio 
 * ad un cliente finale della referenza remota di una farmacia, la verifica della presenza 
 * di una farmacia tra quelle registrate, la compravendita di prodotti, l'eliminazione di un 
 * prodotto, la verifica della presenza di un prodotto tra quelli a magazzino, la 
 * visualizzazione del magazzino centrale, il caricamento di un magazzino di esempio, lo 
 * spegnimento forzoso del server e quello per unreference. 
 */
@SuppressWarnings("serial")
public class Server extends Activatable implements ServerCliente_I, ServerFarmacia_I, ServerAmministratore_I, Unreferenced{
	
	public O_Magazzino magazzinoCentrale = null;
	public O_ElencoFarmacie elencof = null;
	public ActivationID id = null;
	public ActivationSystem actS = null;
	public ActivationDesc actD = null;
	public ActivationDesc actDdefault = null;
	
	/**
	 * Questo costruttore attiva il server centrale. Alla prima attivazione il parametro data 
	 * e' vuoto e quindi il magazzino e la lista delle farmacie sono create vuote e viene 
	 * settato l'oggetto data nell'Activation Descriptor. Alle successive attivazioni magazzino 
	 * e farmacie sono lette dall'oggetto data.
	 * @param id e' il codice identificativo del gruppo di attivazione del server centrale 
	 * presso il sistema di attivazione
	 * @param data e' lo stream di byte con la rappresentazione serializzata di un vettore 
	 * che contiene il magazzino centrale e la lista delle farmacie registrate
	 */
	public Server(ActivationID id, MarshalledObject<Vector<Object>> data) throws ActivationException, ClassNotFoundException, IOException{
		super(id, 0);  //scegliere un numero di porta
		this.id = id;
		if(data == null){
			System.out.println("Il server centrale e' alla sua prima attivazione, pertanto, " +
					"vengono creati un elenco farmacie e un magazzino prodotti vuoti.");
			magazzinoCentrale = new O_Magazzino();
			elencof = new O_ElencoFarmacie();
			Vector<Object> contenitoreOut = new Vector<Object>();
			contenitoreOut.add(0, magazzinoCentrale);
			contenitoreOut.add(1, elencof);	
			actS = ActivationGroup.getSystem();
			actD = actS.getActivationDesc(id);
			actDdefault = new ActivationDesc(actD.getGroupID(), actD.getClassName(), actD.getLocation(), new MarshalledObject<Vector<Object>>(contenitoreOut));
			actD = actS.setActivationDesc(id, actDdefault);
			System.out.println("L'ActivationDesc del server centrale e' stato aggiornato in modo " +
					"da contenere le nuove informazioni di default per le future attivazioni.");
		}else{
			System.out.println("Il server centrale e' gia' stato attivato in passato, quindi, " +
					"l'elenco delle farmacie registrate e il magazzino dei prodotti " +
					"sono estratti dal parametro MarshalledObject.");
			Vector<Object> contenitoreIn = (Vector<Object>)(data.get());
			magazzinoCentrale = (O_Magazzino)contenitoreIn.elementAt(0);
			elencof = (O_ElencoFarmacie)contenitoreIn.elementAt(1);
		}
		System.out.println("E' stato creato il server centrale.");
	}
	
	/**
	 * Questo metodo consente di registrare una farmacia con la sua referenza remota.
	 * @param nome e' il nome della farmacia da registrare
	 * @param obj e' la referenza remota della farmacia da registrare
	 * @return ritorna true se la registrazione e' andata a buon fine, false altrimenti
	 */
	@Override
	public boolean registra(String nome, Remote obj) throws RemoteException{
		if(elencof.putFarmacia(nome, (Remote)obj)){
			System.out.println("Il server centrale ha registrato la farmacia " + nome);
			return true;
		}else
			return false;
	}
	
	/**
	 * Questo metodo consente di deregistrare una farmacia dall'elenco delle farmacie registrate.
	 * @param nome e' il nome della farmacia da deregistrare
	 * @return ritorna true se la deregistrazione e' andata a buon fine, false altrimenti
	 */
	@Override
	public boolean deregistra(String nome) throws RemoteException{
		if(elencof.removeFarmacia(nome)){
			System.out.println("Il server centrale ha deregistrato la farmacia " + nome);
			return true;
		}else
			return false;
	}
	
	/**
	 *Questo metodo consente di controllare se una farmacia indicata da un cliente finale e' 
	 *effettivamente presente nell'elenco delle farmacie registrate.
	 * @param nome e' il nome della farmacia di cui si vuole controllare la presenza in elenco
	 * @return ritorna true se la farmacia e' presente in elenco, false altrimenti
	 */
	@Override
	public boolean checkFarmaciaRegistrata(String nome) throws RemoteException{
		if(elencof.checkFarmaciaRegistrata(nome))
			return true;
		return false;
	}
	
	/**
	 * Questo metodo consente di visualizzare l'elenco delle farmacie registrate.
	 * @return ritorna una stringa che rappresenta l'elenco delle farmacie registrate
	 */
	@Override
	public String mostraFarmacie() throws RemoteException{
		String risultato = "";
		System.out.println("Il server centrale sta per passare al client l'elenco delle farmacie registrate.");
		risultato += "\nElenco delle farmacie registrate presso il server centrale:\n" + elencof.toStringFarmacie() + "\n";
		return risultato;
	}

	/**
	 * Questo metodo consente di restituire la referenza remota di una farmacia ad un cliente.
	 * @param nome e' il nome della farmacia di cui si vuole recuperare la referenze remota
	 * @return ritorna la referenza remota alla farmacia
	 */
	@Override
	public ClientFarmacia_I getFarmacia(String nome) throws RemoteException{
		System.out.println("Il server centrale sta per passare al client lo stub della farmacia richiesta.");
		return (ClientFarmacia_I)(elencof.getFarmaciaStub(nome));
	}

	/**
	 * Questo metodo consente di vendere un prodotto ad una farmacia o ad un cliente finale.
	 * @param id e' il codice identificativo del prodotto da vendere
	 * @param qta e' la quantita' di prodotto da vendere
	 * @return ritorna l'istanza del prodotto venduto
	 */
	@Override
	public O_Prodotto vendiProdotto(String id, Integer qta) throws RemoteException{
		System.out.println("Il server centrale sta per vendere " + qta + (qta==1?" pezzo":" pezzi") + " del prodotto " + id + ".");
		return magazzinoCentrale.vendiProdotto(id, qta);
	}

	/**
	 * Questo metodo consente di rifornire il magazzino centrale di prodotti.
	 * @param id e' il codice identificativo del prodotto da acquistare
	 * @param prodotto e' l'oggetto che rappresenta il prodotto da acquistare
	 * @return ritorna true se il rifornimento e' andato a buon fine, false altrimenti
	 */
	@Override
	public boolean compraProdotto(String id, O_Prodotto prodotto) throws RemoteException{
		System.out.println("Il server centrale sta per acquisire " + prodotto.quantita + (prodotto.quantita==1?" pezzo":" pezzi") + " del prodotto " + id + ".");
		return magazzinoCentrale.compraProdotto(id, prodotto);
	}

	/**
	 * Questo metodo consente di eliminare un prodotto dal magazzino centrale
	 * @param id e' il codice identificativo del prodotto da eliminare
	 */
	@Override
	public void eliminaProdotto(String id) throws RemoteException{
		magazzinoCentrale.eliminaProdotto(id);
	}
	
	/**
	 * Questo metodo consente di controllare se un prodotto e' effettivamente presente nel 
	 * magazzino centrale.
	 * @param id e' il codice identificativo del prodotto di cui si vuole controllare la presenza
	 * @return ritorna true se il prodotto e' presente in magazzino, false altrimenti
	 */
	@Override
	public O_Prodotto checkProdottoAMagazzino(String id) throws RemoteException{
		System.out.println("Il server centrale sta verificare se il prodotto " + id + "e' presente in magazzino.");
		return magazzinoCentrale.checkProdottoAMagazzino(id);
	}

	/**
	 * Questo metodo consente di visualizzare il magazzino centrale.
	 * @return ritorna una stringa che rappresenta l'elenco dei prodotti a magazzino
	 */
	@Override
	public String toStringMagazzinoCentrale() throws RemoteException{
		System.out.println("Il server centrale sta per visualizzare il proprio magazzino.");
		return magazzinoCentrale.toStringMagazzino();
	}

	/**
	 * Questo metodo consente di caricare un magazzino di esempio per effettuare dei test.
	 */
	@Override
	public void caricaEsempio() throws RemoteException {
		Esempio es = new Esempio();
		magazzinoCentrale = es.magazzinoCentrale;
	}

	/**
	 * Questo metodo consente di de-esportare il server centrale e segnalare al sistema di 
	 * attivazione che il server e' inattivo e poi di richiedere la deregistrazione dal sistema 
	 * di attivazione. Al contempo sono salvati su file gli elenchi delle farmacie registrate e 
	 * dei prodotti a magazzino.
	 * @return ritorna true se lo spegnimento e' andato a buon fine, false altrimenti 
	 */
	@Override
	public boolean spegniServer() throws RemoteException{
		if(unexportObject(this, true)){
			try{
				inactive(this.getID());
				unregister(this.getID());
			}catch(ActivationException ex){
				ex.printStackTrace();
			}
			try{	//backup magazzino e elenco farmacie
				OutputStream out1 = new FileOutputStream("Magazzino");
				ObjectOutputStream outObj1 = new ObjectOutputStream(out1);
				outObj1.writeObject(new MarshalledObject<O_Magazzino>(magazzinoCentrale));
				outObj1.flush();
				outObj1.close();
				OutputStream out2 = new FileOutputStream("ElencoFarmacie");
				ObjectOutputStream outObj2 = new ObjectOutputStream(out2);
				outObj2.writeObject(new MarshalledObject<O_ElencoFarmacie>(elencof));
				outObj2.flush();
				outObj2.close();
			}catch(IOException ex){
				System.out.println("Errore nel salvataggio del magazzino e dell'elenco delle farmacie su file.");
				ex.printStackTrace();
			}
			System.out.println("Il server centrale e' stato de-esportato e de-registrato" +
					"dal sistema di attivazione. Il magazzino e l'elenco delle farmacie sono " +
					"stati salvati su file.");
			return true;
		}
		return false;
	}
	
	/**
	 * Questo metodo definisce il comportamento del server centrale qualora il sistema 
	 * DGC avvisa che non ci sono piu' client remoti con referenze del server. Il metodo e' 
	 * invocato dal runtime RMI.
	 */
	@Override
	public void unreferenced(){
		try{
			if(inactive(getID())){
				System.out.println("Il server centrale sta per essere garbage collected. Viene " +
						"aggiornato l'ActivationDesc con il magazzino centrale e l'elenco farmacie" +
						"registrate attuali per passare i dati alla prossima attivazione.");
				Vector<Object> contenitoreOut = new Vector<Object>();
				contenitoreOut.add(0, magazzinoCentrale);
				contenitoreOut.add(1, elencof);	
				actDdefault = new ActivationDesc(actD.getGroupID(), actD.getClassName(), actD.getLocation(), new MarshalledObject<Vector<Object>>(contenitoreOut));
				actD = actS.setActivationDesc(id, actDdefault);
				System.gc();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	  
}
