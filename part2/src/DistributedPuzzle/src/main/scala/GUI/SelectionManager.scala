package GUI

import model.PuzzleActor

import java.awt.Color
import javax.swing.{BorderFactory, JButton}

object SelectionManager:
    @FunctionalInterface private[GUI] trait Listener {
        def onSwapPerformed(): Unit
    }

class SelectionManager(val actor: PuzzleActor):

    var selectionActive: Boolean = false
    var selectedTile: Tile = null
    var tileToBeSelected: Tile = null
    
    def selectTile(tile: Tile, listener: SelectionManager.Listener, toSend: Boolean): Unit =
        if (toSend)
            actor.sendSelection(tile.currentPosition)
        if (selectionActive)
            selectionActive = false
            swap(selectedTile, tile)
            listener.onSwapPerformed()
        else
            selectionActive = true
            selectedTile = tile
            val button: JButton = actor.puzzle.board.getComponent(tile.currentPosition).asInstanceOf[JButton]
            button.setBorder(BorderFactory.createLineBorder(Color.red))

    private def swap(t1: Tile, t2: Tile): Unit =
        val pos = t1.getCurrentPosition
        t1.setCurrentPosition(t2.getCurrentPosition)
        t2.setCurrentPosition(pos)
        