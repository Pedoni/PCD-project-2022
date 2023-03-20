package org.example.puzzle;

import java.io.Serializable;

public class SerializableTile implements Serializable {
    private final int originalPosition;
    private int currentPosition;
    private boolean selected;

    public SerializableTile(final int originalPosition, final int currentPosition, boolean selected) {
        this.originalPosition = originalPosition;
        this.currentPosition = currentPosition;
        this.selected = selected;
    }

    public int getOriginalPosition() {
        return originalPosition;
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

}
