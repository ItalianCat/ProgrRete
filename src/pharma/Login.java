package pharma;

import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;

public interface Login extends Remote{
	public boolean registraUtente(String user, UserData data) throws RemoteException, ActivationException, IOException, ClassNotFoundException;
	public MarshalledObject<MobileAgent> login(String user, String psw) throws RemoteException, ActivationException, IOException, ClassNotFoundException;
}
