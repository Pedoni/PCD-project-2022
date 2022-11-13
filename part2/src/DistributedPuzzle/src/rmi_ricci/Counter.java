package rmi_ricci;

import java.rmi.*;

public interface Counter extends Remote {
	void inc() throws RemoteException;
	int getValue() throws RemoteException;
}
