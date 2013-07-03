package pharma;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemActFarmacia extends Remote{
	public boolean registra(String nome, Remote obj) throws RemoteException;
	public boolean deregistra(String nome) throws RemoteException;
	public Prodotto transazioneProdotto(String id, Integer qta) throws RemoteException;
	public String toStringMagazzinoCentrale() throws RemoteException;
}
