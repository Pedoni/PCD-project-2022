import GUI.PuzzleBoard
import akka.actor.{Actor, ActorLogging, ActorSelection, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, MemberUp}
import com.typesafe.config.ConfigFactory
import model.PuzzleApp

import scala.io.Source

object Player1 extends PuzzleApp("Player1", 2551)
object Player2 extends PuzzleApp("Player2", 2552)
object Player3 extends PuzzleApp("Player3", 2553)
