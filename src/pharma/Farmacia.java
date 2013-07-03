package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Farmacia extends Remote{
	public String toStringMagazzino() throws RemoteException;
	public Prodotto venditaProdotto(String id, Integer quantita) throws RemoteException;
}
