package org.example.puzzle;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class TileButton extends JButton implements Serializable {

	public TileButton(final Tile tile) {
		super(new ImageIcon(tile.getImage()));
		
		addMouseListener(new MouseAdapter() {            
            @Override
            public void mouseClicked(MouseEvent e) {
            	setBorder(BorderFactory.createLineBorder(Color.red));
            }
        });
	}
}
