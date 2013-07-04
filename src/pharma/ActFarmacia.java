package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ActFarmacia extends Remote{
	public boolean registra(String nome, Remote obj) throws RemoteException;
	public boolean deregistra(String nome) throws RemoteException;
	public Prodotto vendiProdotto(String id, Integer qta) throws RemoteException;
	public String toStringMagazzinoCentrale() throws RemoteException;
}
