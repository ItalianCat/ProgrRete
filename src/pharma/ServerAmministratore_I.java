package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerAmministratore_I extends Remote{
	public O_Prodotto checkProdottoAMagazzino(String id) throws RemoteException;
	public boolean compraProdotto(String id, O_Prodotto prodotto) throws RemoteException;
	public boolean spegniServer() throws RemoteException;
}
