package pharma;

import java.io.IOException;
import java.rmi.*;
import java.rmi.activation.*;
import java.rmi.server.*;

@SuppressWarnings("serial")
public class ActServer extends Activatable implements ActCliente, ActFarmacia, ActAmministratore, Unreferenced{
	
	public Magazzino magazzinoCentrale = null;
	public ElencoFarmacie elencof = null;
	
	public ActServer(ActivationID id, MarshalledObject<Magazzino> obj1, MarshalledObject<ElencoFarmacie> obj2) throws ActivationException, ClassNotFoundException, IOException{
		super(id, 30000);  //scegliere un numero di porta
		ActivationSystem actS = ActivationGroup.getSystem();
		ActivationDesc actD = actS.getActivationDesc(id);
		if(obj1 != null){
			magazzinoCentrale = (Magazzino)(obj1.get());
		}
		if(obj2 != null){
			elencof = (ElencoFarmacie)(obj2.get());
		}
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
		return magazzinoCentrale.toStringMagazzino();
	}

	@Override
	public Prodotto vendiProdotto(String id, Integer qta) throws RemoteException{
		return magazzinoCentrale.vendiProdotto(id, qta);
	}
	
	//METODI SOLO PER AMMINISTRATORE

	@Override
	public Prodotto checkProdottoAMagazzino(String id) throws RemoteException{
		return magazzinoCentrale.checkProdottoAMagazzino(id);
	}
	
	@Override
	public boolean compraProdotto(String id, Prodotto prodotto) throws RemoteException{
		return magazzinoCentrale.compraProdotto(id, prodotto);
	}
	
	//METODO PER GC
	
	@Override
	public void unreferenced(){
		try{
			if(inactive(getID()))
				System.gc();
		}catch(RemoteException	| ActivationException ex){ //UnknownObject preso da ActivEx
			ex.printStackTrace();
		}
	}

	  
}
