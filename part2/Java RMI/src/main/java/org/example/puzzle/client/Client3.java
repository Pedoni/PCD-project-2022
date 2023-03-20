package org.example.puzzle.client;

import java.rmi.RemoteException;

public class Client3 {

    public static void main(String[] args) {
        try {
            new GeneralClient("Malloc").start();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
