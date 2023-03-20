package org.example.puzzle.client;

import java.rmi.RemoteException;

public class Client2 {

    public static void main(String[] args) {
        try {
            new GeneralClient("Pippo").start();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
