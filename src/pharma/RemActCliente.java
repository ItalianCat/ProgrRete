package pharma;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;

public interface RemActCliente extends Remote{
	public String mostraFarmacie() throws RemoteException;
	public Farmacia getFarmacia(String nome) throws RemoteException, ActivationException, IOException, ClassNotFoundException;
	public Prodotto transazioneProdotto(String id, Integer qta) throws RemoteException;
	public String toStringMagazzinoCentrale() throws RemoteException;
}
