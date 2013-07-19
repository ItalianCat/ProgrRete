/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.Vector;

/**
 * Questa classe definisce le attivita' svolte dal server di autenticazione tramite i metodi 
 * per la registrazione di un nuovo utente, per il login di un utente al sistema, per la 
 * visualizzazione degli utenti registrati e per lo spegnimento forzoso del server.
 */
@SuppressWarnings("serial")
public class ServAutenticazione extends Activatable implements ServAutenticazione_I, Unreferenced{
	public Remote stubServerCentrale = null;
	public O_ElencoUser elencou = null;
	public ActivationID id = null;
	public ActivationSystem actS = null;
	public ActivationDesc actD = null;
	public ActivationDesc actDdefault = null;

	/**
	 * Questo costruttore attiva il server di autenticazione. Alla prima attivazione il 
	 * parametro obj e' vuoto e quindi: la referenza al server centrale e' letta da file, 
	 * la lista di utenti viene creata vuota e viene settato l'oggetto obj nell'Activation 
	 * Descriptor. Alle successive attivazioni referenza e lista sono lette dall'oggetto obj.
	 * @param id e' il codice identificativo del gruppo di attivazione del server di 
	 * autenticazione presso il sistema di attivazione
	 * @param data e' lo stream di byte con la rappresentazione serializzata di un vettore 
	 * che contiene la referenza al server centrale e la lista degli utenti registrati
	 */
	@SuppressWarnings("unchecked")
	public ServAutenticazione(ActivationID id, MarshalledObject<Vector<Object>> data) throws ActivationException, ClassNotFoundException, IOException{
		super(id, 0, new RMISSLClientSocketFactory(), new RMISSLServerSocketFactory()); //numero?
		this.id = id;
		if(data == null){ //PRIMA ATTIVAZIONE
			System.out.println("Il server di autenticazione e' alla sua prima attivazione, pertanto, " +
					"la referenza al server centrale viene letta da file e viene creato un elenco " +
					"degli utenti vuoto.");
			try{
				InputStream in = new URL("file://"+Input.percorso+"/javarmi/StubServerCentrale").openStream();
				ObjectInputStream inObj = new ObjectInputStream(in);
				stubServerCentrale = ((MarshalledObject<Remote>)inObj.readObject()).get();
				inObj.close();
			}catch(IOException ex){
				System.out.println("Errore nel recupero dello stub del server centrale da file.");
				ex.printStackTrace();
			}
			elencou = new O_ElencoUser();
			Vector<Object> contenitoreOut = new Vector<Object>();
			contenitoreOut.add(0, stubServerCentrale);
			contenitoreOut.add(1, elencou);
			actS = ActivationGroup.getSystem();
			actD = actS.getActivationDesc(id);
			actDdefault = new ActivationDesc(actD.getGroupID(), actD.getClassName(), actD.getLocation(), new MarshalledObject<Vector<Object>>(contenitoreOut));
			actD = actS.setActivationDesc(id, actDdefault);
			System.out.println("L'ActivationDesc del server di autenticazione e' stato aggiornato " +
					"in modo da contenere le nuove informazioni di default per le future attivazioni.");
		}else{	//SUCCESSIVA ATTIVAZIONE
			System.out.println("Il server di autenticazione e' gia' stato attivato in passato, quindi, " +
					"la referenza al server centrale e la lista degli utenti sono estratti dal parametro " +
					"MarshalledObject.");
			Vector<Object> contenitoreIn = (Vector<Object>)(data.get());
			stubServerCentrale = (Remote)contenitoreIn.elementAt(0);
			elencou = (O_ElencoUser)contenitoreIn.elementAt(1);
		}
		System.out.println("E' stato creato il server di Autenticazione.");
	}
	
	/**
	 * Questo metodo consente di registrare un nuovo utente presso il server di autenticazione
	 * @param user e' lo username scelto dall'utente
	 * @param data e' l'oggetto che contiene i dati rilevanti dell'utente, tra cui la password
	 * @return ritorna true se la registrazione e' andata a buon fine, false altrimenti
	 */
	@Override
	public boolean registraUtente(String user, O_UserData data) throws RemoteException{
		if(elencou.putUser(user, data)){
			System.out.println("Il server di autenticazione ha registrato il nuovo utente.");
			return true;
		}else
			return false;
	}
	
	/**
	 * Questo metodo consente di loggare un utente al sistema tramite il controllo delle credenziali 
	 * di accesso.
	 * @param user e' lo username dell'utente che vuole accedere al sistema
	 * @param psw e' la password fornita dall'utente che vuole accedere al sistema
	 * @return ritorna uno stream di byte che contengono il mobile agent specifico per la 
	 * categoria a cui appartiene l'utente, null se il login non e' andato a buon fine
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public MarshalledObject<ClientMobileAgent_I> login(String user, String psw)	throws RemoteException, IOException{
		String categoria = "";
		ClientMobileAgent_I agent = null;
		if(elencou.checkLogin(user, psw)){
			elencou.aggiornaNAccessi(user);
			categoria = elencou.getCategoria(user);
			if(categoria.equals("cliente")){
				agent = new ClientCliente(new MarshalledObject((ServerCliente_I)stubServerCentrale));
			}else if(categoria.equals("farmacia")){
				agent = new ClientFarmacia(new MarshalledObject((ServerFarmacia_I)stubServerCentrale), user);
				UnicastRemoteObject.unexportObject(agent, true);
			}else if(categoria.equals("amministratore")){
				agent = new ClientAmministratore(new MarshalledObject((ServerAmministratore_I)stubServerCentrale));
			}
			System.out.println("Il server di autenticazione ha passato il mobile agent " +
					"al client con il riferimento al server centrale.");
			return new MarshalledObject<ClientMobileAgent_I>(agent);
		}
		return null;
	}

	/**
	 * Questo metodo consente all'ammistratore di vedere gli utenti registrati tramite una 
	 * chiamata al server proxy che a sua volta invoca questo metodo del server di autenticazione.
	 * @return ritorna una stringa che rappresenta una tabella con i dati degli utenti registrati.
	 */
	@Override
	public String elencaUtenti() throws RemoteException{
		System.out.println("Il server di autenticazione sta per visualizzare la lista degli " +
				"utenti registrati.");
		return elencou.toStringUtenti();
	}
	
	/**
	 * Questo metodo consente all'amministratore di spegnere definitivamente il sistema tramite 
	 * una chiamata al proxy che a sua volta invoca questo metodo del server di autenticazione 
	 * per farlo spegnere, dopo aver salvato su disco l'elenco degli utenti registrati.
	 * @return ritorna true se lo spegnimento e' andato a buon fine, false altrimenti
	 */
	@Override
	public boolean spegni() throws RemoteException{
		if(unexportObject(this, true)){
			try{
				inactive(this.getID());
				unregister(this.getID());
			}catch(ActivationException ex){
				ex.printStackTrace();
			}
			try{	//backup elenco utenti
				OutputStream out = new FileOutputStream("ElencoUtenti");
				ObjectOutputStream outObj = new ObjectOutputStream(out);
				outObj.writeObject(new MarshalledObject<O_ElencoUser>(elencou));
				outObj.flush();
				outObj.close();
			}catch(IOException ex){
				System.out.println("Errore nel salvataggio dell'elenco utenti su file.");
				ex.printStackTrace();
			}
			System.out.println("Il server di autenticazione e' stato de-esportato e de-registrato" +
					"dal sistema di attivazione. L'elenco utenti e' stato salvato su file.");
			return true;
		}else
			return false;
	}
	
	/**
	 * Questo metodo definisce il comportamento del server di autenticazione qualora il sistema 
	 * DGC avvisa che non ci sono piu' client remoti con referenze del server. Il metodo e' 
	 * invocato dal runtime RMI.
	 */
	@Override
	public void unreferenced(){
		try {
			if(inactive(getID())){
				System.out.println("Il server di autenticazione sta per essere garbage collected. Viene " +
						"aggiornato l'ActivationDesc con l'elenco degli utenti" +
						"registrati attuali per passare i dati alla prossima attivazione.");
				Vector<Object> contenitore = new Vector<Object>();
				contenitore.add(0, stubServerCentrale);
				contenitore.add(1, elencou);
				actDdefault = new ActivationDesc(actD.getGroupID(), actD.getClassName(), actD.getLocation(), new MarshalledObject<Vector<Object>>(contenitore));
				actD = actS.setActivationDesc(id, actDdefault);
				System.gc();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
