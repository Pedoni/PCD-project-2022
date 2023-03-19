package org.example.puzzle;

import java.io.Serializable;

public class SerializableTile implements Serializable {
    private final int originalPosition;
    private int currentPosition;

    public SerializableTile(final int originalPosition, final int currentPosition) {
        this.originalPosition = originalPosition;
        this.currentPosition = currentPosition;
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

}
