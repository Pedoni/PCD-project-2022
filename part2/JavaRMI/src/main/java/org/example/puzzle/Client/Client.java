package org.example.puzzle.Client;

import org.example.puzzle.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private final int id;

    public Client(final int id) {
        this.id = id;
    }

    public void start() {
        try {
            Registry registry = LocateRegistry.getRegistry(null);
            PuzzleService ps = (PuzzleService) registry.lookup("puzzle");
            String imagePath = "src/main/java/org/example/puzzle/bletchley-park-mansion.jpg";
            PuzzleBoard pb = new PuzzleBoard(3, 5, imagePath, new ArrayList<>(), new SelectionManager());
            List<SerializableTile> stl = pb
                    .getTiles()
                    .stream()
                    .map(t -> new SerializableTile(t.getOriginalPosition(), t.getCurrentPosition()))
                    .toList();
            List<SerializableTile> response = ps.registerClient(id, stl);
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
                            t.getCurrentPosition()
                        ))
                        .toList();
                pb.setTiles(new ArrayList<>(tiles));
            }
            pb.repaint();
            pb.setVisible(true);
        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }
}
