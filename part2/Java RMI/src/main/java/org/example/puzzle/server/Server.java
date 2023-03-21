package org.example.puzzle.server;

import org.example.puzzle.SerializableTile;
import org.example.puzzle.client.GeneralClientIF;
import org.example.puzzle.utils.Pair;
import org.example.puzzle.utils.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server extends UnicastRemoteObject implements PuzzleService {

	private List<SerializableTile> tileList = new ArrayList<>();
	final private List<User> userList = new ArrayList<>();

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
	public Pair<List<User>, List<SerializableTile>> registerClient(String[] details, List<SerializableTile> tiles) throws RemoteException {
		GeneralClientIF nextClient;
		try {
			nextClient = (GeneralClientIF) Naming.lookup("rmi://" + details[1] + "/" + details[2]);
		} catch (NotBoundException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
		if (!userList.isEmpty()) {
			Pair<List<User>, List<SerializableTile>> pair = new Pair<>(userList, tileList);
			User u = new User(details[0], nextClient);
			sendToAll(u);
			userList.add(u);
			return pair;
		}
		tileList = tiles;
		userList.add(new User(details[0], nextClient));
		return new Pair<>(null, null);
	}

	@Override
	public void refreshMap(List<SerializableTile> tiles) {
		this.tileList = tiles;
	}

	private void sendToAll(User newUser) {
		for (User u : userList) {
			try {
				u.getClient().registerNewUser(newUser);
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}
