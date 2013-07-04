package pharma;

import java.rmi.RMISecurityManager;

public class Main {
	public static void main(String args[]){
		System.setSecurityManager(new RMISecurityManager());
	}
}
