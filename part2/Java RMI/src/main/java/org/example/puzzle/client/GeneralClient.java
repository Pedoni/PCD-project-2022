package org.example.puzzle.client;

import org.example.puzzle.*;
import org.example.puzzle.server.PuzzleService;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class GeneralClient extends UnicastRemoteObject implements GeneralClientIF  {

    private String hostName = "localhost";
    private String serviceName = "PuzzleBoardService";
    private String clientServiceName;
    private String name;
    private PuzzleService ps;
    PuzzleBoard pb;

    public GeneralClient(final String name) throws RemoteException {
        super();
        this.name = name;
        this.clientServiceName = "ClientListenService_" + name;
    }

    public void start() {
        String[] details = {name, hostName, clientServiceName};
        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            ps = (PuzzleService) Naming.lookup("rmi://" + hostName + "/" + serviceName);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        String imagePath = "src/main/java/org/example/puzzle/bletchley-park-mansion.jpg";
        this.pb = new PuzzleBoard(3, 5, imagePath, new ArrayList<>(), new SelectionManager(), ps);
        List<SerializableTile> stl = pb
                .getTiles()
                .stream()
                .map(t -> new SerializableTile(
                    t.getOriginalPosition(),
                    t.getCurrentPosition(),
                    t.isSelected()
                ))
                .toList();
        List<SerializableTile> response = registerWithServer(details, stl);
        if (!response.isEmpty()) {
            List<Tile> tiles = response
                    .stream()
                    .map(t -> new Tile(
                        pb.getTiles()
                            .stream()
                            .filter(p -> t.getOriginalPosition() == p.getOriginalPosition())
                            .findAny()
                            .get()
                            .getImage(),
                        t.getOriginalPosition(),
                        t.getCurrentPosition(),
                        t.isSelected()
                    ))
                    .toList();
            pb.setTiles(new ArrayList<>(tiles));
        }
        pb.repaint();
        pb.setVisible(true);

    }

    public List<SerializableTile> registerWithServer(String[] details, List<SerializableTile> stl) {
        List<SerializableTile> response = null;
        try {
            response = ps.registerClient(details, stl);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void messageFromServer(List<SerializableTile> tiles) throws RemoteException {
        List<Tile> ts = tiles
            .stream()
            .map(t -> new Tile(
                    pb.getTiles()
                            .stream()
                            .filter(p -> t.getOriginalPosition() == p.getOriginalPosition())
                            .findAny()
                            .get()
                            .getImage(),
                    t.getOriginalPosition(),
                    t.getCurrentPosition(),
                    t.isSelected()
            ))
            .toList();
        System.out.println("Lista TS: " + ts);
        System.out.println("Mia lista: " + this.pb.getTiles());
        this.pb.setTiles(new ArrayList<>(ts));
        this.pb.repaint();
        this.pb.checkSolution();
    }

}
