package pharma;

import java.rmi.RemoteException;

public interface RemActAmministratore {
	public Prodotto checkProdottoAMagazzino(String id) throws RemoteException;
	public boolean rifornisciMagazzino(String id, Prodotto prodotto) throws RemoteException;
}
