package org.example.puzzle.server;

import org.example.puzzle.SerializableTile;
import org.example.puzzle.client.GeneralClientIF;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server extends UnicastRemoteObject implements PuzzleService {

	final private Map<User, List<SerializableTile>> tilesMap = new HashMap<>();

	public Server() throws RemoteException {
		super();
	}

	public static void main(final String[] args) {
		startRMIRegistry();
		String hostName = "localhost";
		String serviceName = "PuzzleBoardService";
		if (args.length == 2) {
			hostName = args[0];
			serviceName = args[1];
		}
		try {
			PuzzleService hello = new Server();
			Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
			System.out.println("Puzzle Board RMI Server is running...");
		} catch(Exception e) {
			System.out.println("Server had problems starting");
		}
	}

	private static void startRMIRegistry() {
		try {
			LocateRegistry.createRegistry(1099);
			System.out.println("RMI Server ready");
		} catch(RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<SerializableTile> registerClient(String[] details, List<SerializableTile> tiles) throws RemoteException {
		GeneralClientIF nextClient;
		try {
			nextClient = (GeneralClientIF) Naming.lookup("rmi://" + details[1] + "/" + details[2]);
		} catch (NotBoundException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
		if (!tilesMap.isEmpty()) {
			final List<SerializableTile> other = (List<SerializableTile>) tilesMap.values().toArray()[0];
			tilesMap.put(new User(details[0], nextClient), other);
			return other;
		}
		tilesMap.put(new User(details[0], nextClient), tiles);
		return new ArrayList<>();
	}

	@Override
	public void updateTiles(List<SerializableTile> tiles) throws RemoteException {
		sendToAll(tiles);
	}

	private void sendToAll(List<SerializableTile> tiles) {
		for (User u : tilesMap.keySet()) {
			try {
				u.getClient().messageFromServer(tiles);
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}
// mappa non serve davvero
