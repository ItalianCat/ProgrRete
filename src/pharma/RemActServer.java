package pharma;

import java.io.IOException;
import java.rmi.*;
import java.rmi.activation.*;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class RemActServer extends Activatable implements RemActCliente, RemActFarmacia, RemActAmministratore, Unreferenced{
	
	public Map<String, Prodotto> magazzinoCentrale;
	public ElencoFarmacie elencof = null;
	
	public RemActServer(ActivationID id, MarshalledObject<ElencoFarmacie> obj) throws ActivationException, ClassNotFoundException, IOException {
		super(id, 30000);  //scegliere un numero di porta
		ActivationSystem actS = ActivationGroup.getSystem();
		ActivationDesc actD = actS.getActivationDesc(id);
		if(obj != null){
			elencof = (ElencoFarmacie)(obj.get());
		}
		magazzinoCentrale = new HashMap<String, Prodotto>();
		ActivationDesc actDdefault = new ActivationDesc(actD.getGroupID(), actD.getClassName(), actD.getLocation(), new MarshalledObject<ElencoFarmacie>(elencof));
		actD = actS.setActivationDesc(id, actDdefault);
	}

	//METODI SOLO PER CLIENTE
	
	@Override
	public String mostraFarmacie() throws RemoteException{
		String risultato = "";
		risultato += "\nElenco delle farmacie registrate presso il server centrale:\n" + elencof.toStringFarmacie() + "\n";
		return risultato;
	}

	@Override //check eccezioni
	public Farmacia getFarmacia(String nome) throws ActivationException, IOException, ClassNotFoundException, RemoteException{
		return (Farmacia)(elencof.getFarmaciaStub(nome));
	}
	
	//METODI SOLO PER FARMACIA
	
	@Override
	public boolean registra(String nome, Remote obj) throws RemoteException{
		if(elencof.putFarmacia(nome, (Remote)obj))
			return true;
		else
			return false;
	}
	
	@Override
	public boolean deregistra(String nome) throws RemoteException{
		if(elencof.removeFarmacia(nome))
			return true;
		else
			return false;
	}
		
	//METODI PER CLIENTE E FARMACIA
	
	@Override
	public String toStringMagazzinoCentrale() throws RemoteException{
		String risultato = "\nProdotti disponibili presso il magazzino centrale:\n"
				+ "ID\t\tNome\t\tEccipiente\t\tProduttore\t\tFormato\t\tQuantita Disponibile\n";
		for(String id: magazzinoCentrale.keySet()){
			risultato += id + "\t\t" + magazzinoCentrale.get(id).toStringProdotto() + "\n";
		}
		return risultato;
	}

	@Override
	public Prodotto transazioneProdotto(String id, Integer qta) throws RemoteException{
		if(aggiornaMagazzino(id, magazzinoCentrale.get(id), false))
			return new Prodotto(magazzinoCentrale.get(id), qta);
		else
			return null;
	}
	
	//METODI SOLO PER AMMINISTRATORE

	@Override
	public Prodotto checkProdottoAMagazzino(String id) throws RemoteException{
		if(magazzinoCentrale.containsKey(id))
			return magazzinoCentrale.get(id);
		return null;
	}
	
	@Override
	public boolean rifornisciMagazzino(String id, Prodotto prodotto) throws RemoteException{
		if(aggiornaMagazzino(id, prodotto, true))
			return true;
		else
			return false;
	}
	
	//ALTRO
	
	@Override
	public void unreferenced(){
		// TODO Auto-generated method stub
		
	}
	
	//TRANSAZIONE INTERNA AL SERVER CENTRALE
	
	private boolean aggiornaMagazzino(String id, Prodotto prodotto, boolean segno){
		if(segno){ //aggiungi qta
			if(!magazzinoCentrale.containsKey(id))
				magazzinoCentrale.put(id, prodotto);
			else
				magazzinoCentrale.get(id).quantita += prodotto.quantita;
		}else{ //togli qta
			if(prodotto.quantita <= magazzinoCentrale.get(id).quantita){
				magazzinoCentrale.get(id).quantita -= prodotto.quantita;
				if(magazzinoCentrale.get(id).quantita == 0)
					magazzinoCentrale.remove(id);
			}else{
				return false;
			}
		}
		return true;
	}
	
	  
}
