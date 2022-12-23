package GUI

import com.fasterxml.jackson.module.scala.deser.overrides.MutableList
import model.PuzzleActor

import java.awt.event.{MouseAdapter, MouseEvent}
import java.awt.image.{BufferedImage, CropImageFilter, FilteredImageSource}
import java.awt.{BorderLayout, Color, GridLayout, Image}
import java.io.{File, IOException}
import java.util
import java.util.stream.IntStream
import java.util.{ArrayList, Collections}
import javax.imageio.ImageIO
import javax.swing.{BorderFactory, ImageIcon, JButton, JFrame, JOptionPane, JPanel, WindowConstants}
import scala.:+
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.util.Random

class PuzzleBoard(var rows: Int, var columns: Int, var imagePath: String, val actor: PuzzleActor) extends JFrame:
    
    var tiles: ListBuffer[Tile] = ListBuffer()
    val selectionManager: SelectionManager = SelectionManager(actor)

    setTitle("Puzzle")
    setResizable(false)
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

    val board: JPanel = JPanel()
    board.setBorder(BorderFactory.createLineBorder(Color.gray))
    board.setLayout(GridLayout(rows, columns, 0, 0))
    getContentPane.add(board, BorderLayout.CENTER)
    
    createTiles(imagePath)
    paintPuzzle(board)
    
    this.setVisible(true)

    def createTiles(imagePath: String): Unit =
        val image = ImageIO.read(File(imagePath))
        if (image == null)
            JOptionPane.showMessageDialog(this, "Could not load image", "Error", JOptionPane.ERROR_MESSAGE)
            return
        val w = image.getWidth(null)
        val h = image.getHeight(null)
        var p = 0
        val randomPositions: List[Int] = Random.shuffle(List.range(0, rows * columns))
        for (i <- 0 until rows; j <- 0 until columns)
            val imagePortion: Image = createImage(
                FilteredImageSource(
                    image.getSource,
                    CropImageFilter(
                        j * w / columns,
                        i * h / rows,
                        w / columns,
                        h / rows
                    )
                )
            )
            this.tiles = this.tiles :+ Tile(imagePortion, p, randomPositions(p))
            p = p + 1

    def paintPuzzle(board: JPanel): Unit =
        board.removeAll()
        this.tiles = this.tiles.sortWith(_.getCurrentPosition < _.getCurrentPosition)
        this.tiles.foreach(tile => {
            val button: JButton = JButton(new ImageIcon(tile.getImage))
            button.addActionListener(_ => {
                this.selectionManager.selectTile(tile, () => {
                    paintPuzzle(board)
                    checkSolution()
                }, true)
            })
            board.add(button)
        })
        pack()
        setLocationRelativeTo(null)
        
    def repaintPuzzle(): Unit =
        paintPuzzle(this.board)

    def checkSolution(): Unit =
        if (this.tiles.forall(tile => tile.isInRightPlace))
            JOptionPane.showMessageDialog(this, "Puzzle Completed!", "", JOptionPane.INFORMATION_MESSAGE)
