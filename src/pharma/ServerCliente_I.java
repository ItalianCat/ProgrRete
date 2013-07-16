/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
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
	public O_Prodotto checkProdottoAMagazzino(String id) throws RemoteException;
	boolean checkFarmaciaRegistrata(String nome) throws RemoteException;
}
