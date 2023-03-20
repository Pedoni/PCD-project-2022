package org.example.puzzle;

import java.awt.Image;

public class Tile implements Comparable<Tile> {
	private final Image image;
	private final int originalPosition;
	private int currentPosition;
    private boolean selected;

    public Tile(final Image image, final int originalPosition, final int currentPosition, boolean selected) {
        this.image = image;
        this.originalPosition = originalPosition;
        this.currentPosition = currentPosition;
        this.selected = selected;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    public Image getImage() {
    	return image;
    }
    
    public boolean isInRightPlace() {
    	return currentPosition == originalPosition;
    }
    
    public int getCurrentPosition() {
    	return currentPosition;
    }
    
    public void setCurrentPosition(final int newPosition) {
    	currentPosition = newPosition;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
	public int compareTo(Tile other) {
		return this.currentPosition < other.currentPosition ? -1
				: (this.currentPosition == other.currentPosition ? 0 : 1);
	}

    @Override
    public String toString() {
        return currentPosition + "-" + originalPosition;
    }
}
