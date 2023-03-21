package org.example.puzzle.server;

import org.example.puzzle.SerializableTile;
import org.example.puzzle.utils.Pair;
import org.example.puzzle.utils.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface PuzzleService extends Remote {
    Pair<List<User>, List<SerializableTile>> registerClient(String[] details, List<SerializableTile> tiles) throws RemoteException;
    void refreshMap(List<SerializableTile> tiles) throws RemoteException;

}
