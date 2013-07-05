package pharma;

import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;

@SuppressWarnings("serial")
public class ActServerAutenticazione extends Activatable implements ProxyDualLogin, Unreferenced{
	public Remote stubServerCentrale = null; //SERVE UN VALORE
	public ElencoUser elencou = null;

	public ActServerAutenticazione(ActivationID id, MarshalledObject<Remote> obj1, MarshalledObject<ElencoUser> obj2) throws ActivationException, ClassNotFoundException, IOException{
		super(id, 35000, new RMISSLClientSocketFactory(), new RMISSLServerSocketFactory());  //numero!!!
		ActivationSystem actS = ActivationGroup.getSystem();
		ActivationDesc actD = actS.getActivationDesc(id);
		if(obj1 != null){
			stubServerCentrale = (Remote)(obj1.get());
		}
		if(obj2 != null){
			elencou = (ElencoUser)(obj2.get());
		}
		ActivationDesc actDdefault = new ActivationDesc(actD.getGroupID(), actD.getClassName(), actD.getLocation(), new MarshalledObject<Remote>(stubServerCentrale));
		actD = actS.setActivationDesc(id, actDdefault);
	}

	@Override
	public boolean registraUtente(String user, UserData data) throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		if(elencou.putUser(user, data))
			return true;
		else
			return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public MarshalledObject<MobileAgent> login(String user, String psw)	throws RemoteException, ActivationException, IOException, ClassNotFoundException{
		String categoria = "";
		MobileAgent agent = null;
		Remote refserver = null; /*REF SERVER*/
		if(elencou.checkLogin(user, psw)){
			elencou.aggiornaNAccessi(user);
			categoria = elencou.getCategoria(user);
			if(categoria.equals("cliente")){
				agent = new Cliente(new MarshalledObject(refserver));
			}else if(categoria.equals("farmacia")){
				agent = new FarmaciaImpl(new MarshalledObject(refserver), user);
				UnicastRemoteObject.unexportObject(agent, true);  //era stato esportato automaticamente
			}else if(categoria.equals("amministratore")){
				agent = new Amministratore(new MarshalledObject(refserver));
			}
			return new MarshalledObject<MobileAgent>(agent);
		}
		return null;
	}
	
	@Override
	public void unreferenced(){
		try {
			if(inactive(getID()))
				System.gc();
		}catch(RemoteException	| ActivationException ex){ //UnknownObject preso da ActivEx
			ex.printStackTrace();
		}
	}

}
