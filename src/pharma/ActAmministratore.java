package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ActAmministratore extends Remote{
	public Prodotto checkProdottoAMagazzino(String id) throws RemoteException;
	public boolean compraProdotto(String id, Prodotto prodotto) throws RemoteException;
}
