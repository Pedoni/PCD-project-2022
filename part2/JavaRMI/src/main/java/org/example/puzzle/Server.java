package org.example.puzzle;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

	public static void main(final String[] args) {

		try {
			PuzzleService puzzle = new PuzzleServiceImpl();
			PuzzleService puzzleStub = (PuzzleService) UnicastRemoteObject.exportObject(puzzle, 0);

			Registry registry = LocateRegistry.createRegistry(1099);

			// Bind the remote object's stub in the registry
			//Registry registry = LocateRegistry.getRegistry();
			registry.rebind("puzzle", puzzleStub);

			System.out.println("Puzzle registered.");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}

	}
}
