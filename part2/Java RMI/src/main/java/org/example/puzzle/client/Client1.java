package org.example.puzzle.client;

import java.rmi.RemoteException;

public class Client1 {

    public static void main(String[] args) {
        try {
            new GeneralClient("Pedoni").start();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}

