package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerFarmacia_I extends Remote{
	public boolean registra(String nome, Remote obj) throws RemoteException;
	public boolean deregistra(String nome) throws RemoteException;
	public O_Prodotto vendiProdotto(String id, Integer qta) throws RemoteException;
	public String toStringMagazzinoCentrale() throws RemoteException;
}
