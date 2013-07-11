/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
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

@SuppressWarnings("serial")
public class ServAutenticazione extends Activatable implements ServAutenticazione_I, Unreferenced{
	public Remote stubServerCentrale = null;
	public O_ElencoUser elencou = null;

	@SuppressWarnings("unchecked")
	public ServAutenticazione(ActivationID id, MarshalledObject<Vector<Object>> obj) throws ActivationException, ClassNotFoundException, IOException{
		super(id, 35001);//, new RMISSLClientSocketFactory(), new RMISSLServerSocketFactory());  //numero!!!
		if(obj == null){
			System.out.println("Il server di autenticazione e' alla sua prima attivazione, pertanto, " +
					"la referenza al server centrale viene letta da file e viene creato un elenco " +
					"degli utenti vuoto.");
			try{
				InputStream in = new URL("file://" + System.getProperty("user.dir") + "/" + "javarmi/StubServerCentrale").openStream();
				ObjectInputStream inObj = new ObjectInputStream(in);
				stubServerCentrale = ((MarshalledObject<Remote>)inObj.readObject()).get();
				inObj.close();
			}catch(IOException ex){
				System.out.println("Errore nel recupero dello stub del server centrale da file.");
				ex.printStackTrace();
			}
			elencou = new O_ElencoUser();
			Vector<Object> contenitore = new Vector<Object>();
			contenitore.add(0, stubServerCentrale);
			contenitore.add(1, elencou);
			ActivationSystem actS = ActivationGroup.getSystem();
			ActivationDesc actD = actS.getActivationDesc(id);
			ActivationDesc actDdefault = new ActivationDesc(actD.getGroupID(), actD.getClassName(), actD.getLocation(), new MarshalledObject<Vector<Object>>(contenitore));
			actD = actS.setActivationDesc(id, actDdefault);
			System.out.println("L'ActivationDesc del server di autenticazione e' stato aggiornato " +
					"in modo da contenere le nuove informazioni di default per le future attivazioni.");
		}else{
			System.out.println("Il server di autenticazione e' gia' stato attivato in passato, quindi, " +
					"la referenza al server centrale e la lista degli utenti sono estratti dal parametro " +
					"MarshalledObject.");
			Vector<Object> data = (Vector<Object>)(obj.get());
			stubServerCentrale = (Remote)data.elementAt(0);
			elencou = (O_ElencoUser)data.elementAt(1);
		}
		System.out.println("!!!!! E' stato creato il server di Autenticazione.");
	}

	@Override
	public boolean registraUtente(String user, O_UserData data) throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		if(elencou.putUser(user, data)){
			System.out.println("Il server di autenticazione ha registrato il nuovo utente.");
			return true;
		}else
			return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public MarshalledObject<ClientMobileAgent_I> login(String user, String psw)	throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		String categoria = "";
		ClientMobileAgent_I agent = null;
		if(elencou.checkLogin(user, psw)){
			elencou.aggiornaNAccessi(user);
			categoria = elencou.getCategoria(user);
			if(categoria.equals("cliente")){
				agent = new ClientCliente(new MarshalledObject(stubServerCentrale));
			}else if(categoria.equals("farmacia")){
				agent = new ClientFarmacia(new MarshalledObject(stubServerCentrale), user);
				UnicastRemoteObject.unexportObject(agent, true);  //era stato esportato automaticamente
			}else if(categoria.equals("amministratore")){
				agent = new ClientAmministratore(new MarshalledObject(stubServerCentrale));
			}
			System.out.println("Il server di autenticazione sta per fornire il mobile agent " +
					"al client con il riferimento al server centrale.");
			return new MarshalledObject<ClientMobileAgent_I>(agent);
		}
		return null;
	}
	
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
		
	@Override
	public void unreferenced(){
		try {
			if(inactive(getID()))
				System.gc();
		}catch(RemoteException	| ActivationException ex){
			ex.printStackTrace();
		}
	}
	
}
