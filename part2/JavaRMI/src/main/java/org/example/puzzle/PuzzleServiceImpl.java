package org.example.puzzle;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuzzleServiceImpl implements PuzzleService {

    final private Map<Integer, List<SerializableTile>> tilesMap = new HashMap<>();

    @Override
    public List<SerializableTile> registerClient(int id, List<SerializableTile> tiles) throws RemoteException {
        if (!tilesMap.isEmpty()) {
            final List<SerializableTile> other = (List<SerializableTile>) tilesMap.values().toArray()[0];
            tilesMap.put(id, other);
            return other;
        }
        tilesMap.put(id, tiles);
        return new ArrayList<>();
    }

}
