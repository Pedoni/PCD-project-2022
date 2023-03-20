package org.example.puzzle.client;

import org.example.puzzle.SerializableTile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GeneralClientIF extends Remote {
	void messageFromServer(List<SerializableTile> tiles) throws RemoteException;
}
