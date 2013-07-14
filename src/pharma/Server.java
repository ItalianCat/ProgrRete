/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
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

@SuppressWarnings("serial")
public class Server extends Activatable implements ServerCliente_I, ServerFarmacia_I, ServerAmministratore_I, Unreferenced{
	
	public O_Magazzino magazzinoCentrale = null;
	public O_ElencoFarmacie elencof = null;
	
	public Server(ActivationID id, MarshalledObject<Vector<Object>> data) throws ActivationException, ClassNotFoundException, IOException{
		super(id, 0);  //scegliere un numero di porta
		if(data == null){
			System.out.println("Il server centrale e' alla sua prima attivazione, pertanto, " +
					"vengono creati un elenco farmacie e un magazzino prodotti vuoti.");
			magazzinoCentrale = new O_Magazzino();
			elencof = new O_ElencoFarmacie();
			Vector<Object> contenitoreOut = new Vector<Object>();
			contenitoreOut.add(0, magazzinoCentrale);
			contenitoreOut.add(1, elencof);	
			ActivationSystem actS = ActivationGroup.getSystem();
			ActivationDesc actD = actS.getActivationDesc(id);
			ActivationDesc actDdefault = new ActivationDesc(actD.getGroupID(), actD.getClassName(), actD.getLocation(), new MarshalledObject<Vector<Object>>(contenitoreOut));
			actD = actS.setActivationDesc(id, actDdefault);
			System.out.println("L'ActivationDesc del server centrale e' stato aggiornato in modo " +
					"da contenere le nuove informazioni di default per le future attivazioni.");
		}else{
			System.out.println("Il server centrale e' gia' stato attivato in passato, quindi, " +
					"l'elenco delle farmacie registrate e il magazzino dei prodotti" +
					"sono estratti dal parametro MarshalledObject.");
			Vector<Object> contenitoreIn = (Vector<Object>)(data.get());
			magazzinoCentrale = (O_Magazzino)contenitoreIn.elementAt(0);
			elencof = (O_ElencoFarmacie)contenitoreIn.elementAt(1);
		}
		System.out.println("E' stato creato il server centrale.");
	}

	//METODI SOLO PER CLIENTE
	
	@Override
	public String mostraFarmacie() throws RemoteException{
		String risultato = "";
		System.out.println("Il server centrale sta per passare al client l'elenco delle farmacie registrate.");
		risultato += "\nElenco delle farmacie registrate presso il server centrale:\n" + elencof.toStringFarmacie() + "\n";
		return risultato;
	}

	@Override
	public ClientFarmacia_I getFarmacia(String nome) throws ActivationException, IOException, ClassNotFoundException, RemoteException{
		System.out.println("Il server centrale sta per passare al client lo stub della farmacia richiesta.");
		return (ClientFarmacia_I)(elencof.getFarmaciaStub(nome));
	}
	
	//METODI SOLO PER FARMACIA
	
	@Override
	public boolean registra(String nome, Remote obj) throws RemoteException{
		if(elencof.putFarmacia(nome, (Remote)obj)){
			System.out.println("Il server centrale ha registrato la farmacia " + nome);
			return true;
		}else
			return false;
	}
	
	@Override
	public boolean deregistra(String nome) throws RemoteException{
		if(elencof.removeFarmacia(nome)){
			System.out.println("Il server centrale ha deregistrato la farmacia " + nome);
			return true;
		}else
			return false;
	}
		
	//METODI PER CLIENTE E FARMACIA
	
	@Override
	public String toStringMagazzinoCentrale() throws RemoteException{
		System.out.println("Il server centrale sta per visualizzare il proprio magazzino.");
		return magazzinoCentrale.toStringMagazzino();
	}

	@Override
	public O_Prodotto vendiProdotto(String id, Integer qta) throws RemoteException{
		System.out.println("sono qui");
		System.out.println("Il server centrale sta per vendere " + qta + (qta==1?" pezzo":" pezzi") + " del prodotto " + id + ".");
		return magazzinoCentrale.vendiProdotto(id, qta);
	}
	
	//METODI SOLO PER AMMINISTRATORE

	@Override
	public O_Prodotto checkProdottoAMagazzino(String id) throws RemoteException{
		System.out.println("Il server centrale sta verificare se il prodotto " + id + "e' gia' codificato a magazzino.");
		return magazzinoCentrale.checkProdottoAMagazzino(id);
	}
	
	@Override
	public boolean compraProdotto(String id, O_Prodotto prodotto) throws RemoteException{
		System.out.println("Il server centrale sta per acquisire " + prodotto.quantita + (prodotto.quantita==1?" pezzo":" pezzi") + " del prodotto " + id + ".");
		return magazzinoCentrale.compraProdotto(id, prodotto);
	}

	@Override
	public void caricaEsempio() throws RemoteException {
		Esempio es = new Esempio();
		magazzinoCentrale = es.magazzinoCentrale;
	}
	
	//METODO PER GC
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
	
	
	@Override
	public void unreferenced(){
		try{
			if(inactive(getID()))
				System.gc();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


	  
}
