package pharma;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;

public interface ServerCliente_I extends Remote{
	public String mostraFarmacie() throws RemoteException;
	public ClientFarmacia_I getFarmacia(String nome) throws RemoteException, ActivationException, IOException, ClassNotFoundException;
	public O_Prodotto vendiProdotto(String id, Integer qta) throws RemoteException;
	public String toStringMagazzinoCentrale() throws RemoteException;
}