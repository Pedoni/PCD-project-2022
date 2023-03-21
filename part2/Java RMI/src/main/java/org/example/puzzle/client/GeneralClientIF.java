package org.example.puzzle.client;

import org.example.puzzle.SerializableTile;
import org.example.puzzle.utils.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GeneralClientIF extends Remote {
	void message(List<SerializableTile> tiles) throws RemoteException;
	void registerNewUser(User newUser) throws RemoteException;
}
