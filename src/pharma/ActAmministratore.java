package pharma;

import java.rmi.RemoteException;

public interface ActAmministratore {
	public Prodotto checkProdottoAMagazzino(String id) throws RemoteException;
	public boolean compraProdotto(String id, Prodotto prodotto) throws RemoteException;
}
