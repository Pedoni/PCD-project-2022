package org.example.puzzle;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client2 {

    private Client2() {}

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            PuzzleService obj = (PuzzleService) registry.lookup("puzzle");
            obj.showPuzzle();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
