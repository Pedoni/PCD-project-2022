package GUI

import java.awt.Image

case class Tile(var image: Image, var originalPosition: Int, var currentPosition: Int) extends Comparable[Tile] with Serializable:

    def getImage: Image = image

    def isInRightPlace: Boolean = currentPosition == originalPosition

    def getCurrentPosition: Int = currentPosition

    def setCurrentPosition(newPosition: Int): Unit = currentPosition = newPosition

    override def compareTo(other: Tile): Int = other.getCurrentPosition match
        case x if x > this.currentPosition => -1
        case x if x == this.currentPosition => 0
        case _ => 1
