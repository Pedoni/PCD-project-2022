package org.example.puzzle;

import org.example.puzzle.server.PuzzleService;
import org.example.puzzle.utils.User;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class PuzzleBoard extends JFrame implements Serializable {

    private final int rows;
    private final int columns;
    private final JPanel board;
	private List<Tile> tiles;
	private final SelectionManager selectionManager;
    List<User> userList;
    PuzzleService ps;

    public PuzzleBoard(
        final int rows,
        final int columns,
        final String imagePath,
        final List<Tile> tiles,
        final SelectionManager selectionManager,
        final List<User> userList,
        final PuzzleService ps
    ) {
    	this.rows = rows;
		this.columns = columns;
        this.tiles = tiles;
        this.selectionManager = selectionManager;
        this.userList = userList;
        this.ps = ps;
    	
    	setTitle("Puzzle");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        board = new JPanel();
        board.setBorder(BorderFactory.createLineBorder(Color.gray));
        board.setLayout(new GridLayout(rows, columns, 0, 0));
        getContentPane().add(board, BorderLayout.CENTER);

        createTiles(imagePath);
        paintPuzzle();
    }

    
    private void createTiles(final String imagePath) {
		final BufferedImage image;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load image", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);

        int position = 0;
        
        final List<Integer> randomPositions = new ArrayList<>();
        IntStream.range(0, rows * columns).forEach(randomPositions::add);
        Collections.shuffle(randomPositions);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
            	final Image imagePortion = createImage(new FilteredImageSource(image.getSource(),
                    new CropImageFilter(
                        j * imageWidth / columns,
                        i * imageHeight / rows,
                        (imageWidth / columns),
                        imageHeight / rows
                    ))
                );
                tiles.add(new Tile(imagePortion, position, randomPositions.get(position), false));
                position++;
            }
        }
	}
    
    private void paintPuzzle() {
    	board.removeAll();
    	
    	Collections.sort(tiles);
    	
    	tiles.forEach(tile -> {
    		final TileButton btn = new TileButton(tile);
            board.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            if (tile.isSelected()) {
                btn.setBorder(BorderFactory.createLineBorder(Color.red));
            }
            btn.addActionListener(actionListener -> {
            	selectionManager.selectTile(tiles, tile, () -> {
            		paintPuzzle();
                	checkSolution();
            	});
                List<SerializableTile> sList = this.tiles
                    .stream()
                    .map(t -> new SerializableTile(
                            t.getOriginalPosition(),
                            t.getCurrentPosition(),
                            t.isSelected()
                    ))
                    .toList();
                try {
                    ps.refreshMap(sList);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                userList.forEach(
                    user -> {
                        try {
                            user.getClient().message(sList);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                );
            });
    	});
    	pack();
        setLocationRelativeTo(null);
    }

    public void repaint() {
        System.out.println("Chiamato repaint");
        paintPuzzle();
    }

    public void checkSolution() {
    	if (tiles.stream().allMatch(Tile::isInRightPlace)) {
    		JOptionPane.showMessageDialog(this, "Puzzle Completed!", "", JOptionPane.INFORMATION_MESSAGE);
    	}
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }



}
