package org.example.puzzle;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PuzzleService extends Remote {
    void showPuzzle() throws RemoteException;
}
