package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientFarmacia_I extends Remote{
	public String toStringMagazzino() throws RemoteException;
	public O_Prodotto venditaProdotto(String id, Integer quantita) throws RemoteException;
}
