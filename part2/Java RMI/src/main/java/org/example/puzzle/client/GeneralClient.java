package org.example.puzzle.client;

import org.example.puzzle.*;
import org.example.puzzle.server.PuzzleService;
import org.example.puzzle.utils.Pair;
import org.example.puzzle.utils.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneralClient extends UnicastRemoteObject implements GeneralClientIF  {

    private String hostName = "localhost";
    private String serviceName = "PuzzleBoardService";
    private String clientServiceName;
    private String name;
    private PuzzleService ps;
    PuzzleBoard pb;

    final private List<User> userList;

    public GeneralClient(final String name) throws RemoteException {
        super();
        this.name = name;
        this.clientServiceName = "ClientListenService_" + name;
        this.userList = new ArrayList<>();
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
        this.pb = new PuzzleBoard(3, 5, imagePath, new ArrayList<>(), new SelectionManager(), userList, ps);
        List<SerializableTile> stl = pb
                .getTiles()
                .stream()
                .map(t -> new SerializableTile(
                    t.getOriginalPosition(),
                    t.getCurrentPosition(),
                    t.isSelected()
                ))
                .toList();
        Pair<List<User>, List<SerializableTile>> response = registerWithServer(details, stl);
        if (!response.isEmpty()) {
            userList.addAll(response.getA());
            List<SerializableTile> sTiles = response.getB();
            List<Tile> tiles = sTiles
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

    public Pair<List<User>, List<SerializableTile>> registerWithServer(String[] details, List<SerializableTile> stl) {
        Pair<List<User>, List<SerializableTile>> response = null;
        try {
            response = ps.registerClient(details, stl);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void message(List<SerializableTile> tiles) throws RemoteException {
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
        this.pb.setTiles(new ArrayList<>(ts));
        this.pb.repaint();
        this.pb.checkSolution();
    }

    @Override
    public void registerNewUser(User newUser) throws RemoteException {
        if (!newUser.getName().equals(name)) {
            userList.add(newUser);
        }
    }

}
