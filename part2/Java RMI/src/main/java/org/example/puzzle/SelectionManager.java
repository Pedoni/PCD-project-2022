package org.example.puzzle;

import java.io.Serializable;
import java.util.List;

public class SelectionManager implements Serializable {

	private Tile selectedTile;

	public SelectionManager() {

	}

	public void selectTile(List<Tile> tiles, final Tile tile, final Listener listener) {
		Tile tl = tiles
				.stream()
				.filter(t -> t.getOriginalPosition() == tile.getOriginalPosition())
				.findAny()
				.get();
		if (selectedTile != null) {
			tl.setSelected(false);
			selectedTile.setSelected(false);
			swap(selectedTile, tl);
			listener.onSwapPerformed();
			selectedTile = null;
		} else {
			tl.setSelected(true);
			selectedTile = tl;
		}
	}

	private void swap(final Tile t1, final Tile t2) {
		int pos = t1.getCurrentPosition();
		t1.setCurrentPosition(t2.getCurrentPosition());
		t2.setCurrentPosition(pos);
	}
	
	@FunctionalInterface
	public interface Listener{
		void onSwapPerformed();
	}
}
