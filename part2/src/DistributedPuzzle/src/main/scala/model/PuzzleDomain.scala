package model

import GUI.{PuzzleBoard, SerializableTile, Tile}

import java.time.LocalDateTime
import scala.collection.mutable.ListBuffer

object PuzzleDomain:
    case class ChatMessage(name: String, contents: String)
    case class UserMessage(contents: String)
    case class EnterRoom(fullAddress: String, name: String)
    case class TileSelected(currentPosition: Int)
    case class PuzzleMessage(tiles: ListBuffer[SerializableTile])
    case class ApproveOrDenyCriticalSection(remoteAddress: String, timestamp: LocalDateTime)
    case class EnterInCriticalSectionApproved()
    case class EnterInCriticalSectionNotApproved()
