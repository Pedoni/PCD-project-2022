package model

import PuzzleDomain.*
import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, MemberUp}
import GUI.{PuzzleBoard, SerializableTile, Tile}

import java.awt.Image
import java.time.LocalDateTime
import java.util.Optional
import scala.collection.mutable.ListBuffer

object PuzzleActor:
    def props(name: String, port: Int): Props = Props(new PuzzleActor(name, port))

class PuzzleActor(name: String, port: Int) extends Actor with ActorLogging:

    val cluster: Cluster = Cluster(context.system)
    var puzzle: PuzzleBoard = PuzzleBoard(3, 5, "src/main/resources/images/bletchley-park-mansion.jpg", this)
    var actors: ListBuffer[String] = ListBuffer()
    var localTimestamp: Optional[LocalDateTime] = Optional.empty()
    var actorsOk: Int = 0
    var actorsNotOk: Int = 0

    override def preStart(): Unit =
        cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent])

    override def postStop(): Unit = cluster.unsubscribe(self)

    override def receive: Receive = online(Map())

    def online(puzzleRoom: Map[String, String]): Receive =
        case MemberUp(member) =>
            val remotePuzzleActorSelection = getPuzzleActor(member.address.toString)
            remotePuzzleActorSelection ! EnterRoom(s"${self.path.address}@127.0.0.1:$port", name)
            if (member.address.toString != s"${self.path.address}@127.0.0.1:$port")
                println(s"Dentro if, member: ${member.address}, me: ${self.path.address}")
                actors.addOne(member.address.toString)
        case MemberRemoved(member, _) =>
            val remoteName = puzzleRoom(member.address.toString)
            log.info(s"$remoteName left the room.")
            context.become(online(puzzleRoom - member.address.toString))
        case EnterRoom(remoteAddress, remoteName) =>
            if (remoteName != name) {
                log.info(s"$remoteName entered the room.")
                context.become(online(puzzleRoom + (remoteAddress -> remoteName)))
                getPuzzleActor(remoteAddress) ! PuzzleMessage(
                    puzzle.tiles.map(t => SerializableTile(t.originalPosition, t.currentPosition))
                )
            }
        case UserMessage(contents) =>
            puzzleRoom.keys.foreach { remoteAddressAsString =>
                getPuzzleActor(remoteAddressAsString) ! ChatMessage(name, contents)
            }
        case ChatMessage(remoteName, contents) => log.info(s"[$remoteName] $contents")
        case ApproveOrDenyCriticalSection(remoteAddress, remoteTimestamp) =>
            println(s"Timestamp ricevuto: $remoteTimestamp")
            println(s"Timestamp locale: $localTimestamp")
            println(s"L'indirizzo dell'attore che mi ha mandato il messaggio è $remoteAddress")
            if (localTimestamp.isEmpty ||
                LocalDateTime.parse(localTimestamp.get().toString).isAfter(remoteTimestamp))
                println("In teoria qui dovrebbe mandare il messaggio di approvazione")
                val remotePuzzleActorSelection = getPuzzleActor(remoteAddress)
                remotePuzzleActorSelection ! EnterInCriticalSectionApproved()
            else // da aggiornare col caso negativo
                getPuzzleActor(remoteAddress) ! EnterInCriticalSectionNotApproved()
        case EnterInCriticalSectionApproved() =>
            println("Ricevuta un'approvazione")
            this.actorsOk += 1
            if (this.actorsOk == actors.size)
                this.actorsOk = 0
                println("Ricevuti tutti gli ok, aggiorno")
                this.localTimestamp = Optional.empty()
                this.puzzle.modifyPuzzleOk()
            else checkAllOkReceived()
        case EnterInCriticalSectionNotApproved() =>
            this.actorsNotOk += 1
            this.localTimestamp = Optional.empty()
            checkAllOkReceived()
        case TileSelected(currentPosition: Int) =>
            println(s"Chiamata TileSelected. Timestamp: ${LocalDateTime.now()}")
            println(s"Dopo? ${LocalDateTime.parse("2024-03-17T16:01:52.097362100").isAfter(LocalDateTime.now)}")
            val tile: Tile = this.puzzle.tiles.find(p => p.currentPosition == currentPosition).get
            puzzle.selectionManager.selectTile(tile, () => {
                puzzle.repaintPuzzle()
                puzzle.checkSolution()
            }, false)
        case PuzzleMessage(tiles) =>
            this.puzzle.tiles = tiles.map(t =>
                val image: Image = this.puzzle.tiles.find(p => p.originalPosition == t.originalPosition).get.image
                Tile(image, t.originalPosition, t.currentPosition))
            this.puzzle.repaintPuzzle()
            this.puzzle.checkSolution()

    def getPuzzleActor(memberAddress: String): ActorSelection =
        println(s"Member address: ${memberAddress}")
        context.actorSelection(s"$memberAddress/user/puzzleActor")

    def getPort: Int = this.port

    def checkAllOkReceived(): Unit =
        if (this.actorsOk + this.actorsNotOk == actors.size)
            this.actorsOk = 0
            this.actorsNotOk = 0

    def sendSelection(currentPosition: Int): Unit =
        println("Chiamata sendSelection")
        actors.foreach(a => getPuzzleActor(a) ! TileSelected(currentPosition))

