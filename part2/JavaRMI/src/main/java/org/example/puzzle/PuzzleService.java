package org.example.puzzle;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;

public interface PuzzleService extends Remote {
    List<SerializableTile> registerClient(int id, List<SerializableTile> tiles) throws RemoteException;
}
