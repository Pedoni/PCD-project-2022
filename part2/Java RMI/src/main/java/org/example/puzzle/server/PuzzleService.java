package org.example.puzzle.server;

import org.example.puzzle.SerializableTile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PuzzleService extends Remote {
    List<SerializableTile> registerClient(String[] details, List<SerializableTile> tiles) throws RemoteException;
    void updateTiles(List<SerializableTile> tiles) throws RemoteException;

}
