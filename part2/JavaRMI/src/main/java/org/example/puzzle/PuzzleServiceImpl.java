package org.example.puzzle;

import java.rmi.RemoteException;

public class PuzzleServiceImpl implements PuzzleService {

    final int n = 3;
    final int m = 5;
    final String imagePath = "src/main/java/org/example/puzzle/bletchley-park-mansion.jpg";
    final PuzzleBoard puzzle = new PuzzleBoard(n, m, imagePath);

    @Override
    public void showPuzzle() throws RemoteException {
        this.puzzle.setVisible(true);
    }

}
