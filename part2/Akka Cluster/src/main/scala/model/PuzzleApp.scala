package model

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import model.PuzzleDomain._

import scala.io.Source

class PuzzleApp(name: String, port: Int) extends App:
    val config = ConfigFactory.parseString(
        s"""
           |akka.remote.artery.canonical.port = $port
        """.stripMargin).withFallback(ConfigFactory.load("application.conf"))
    val system = ActorSystem("ClusterSystem", config)
    val puzzleActor = system.actorOf(PuzzleActor.props(name, port), "puzzleActor")
    Source.stdin.getLines().foreach { line => puzzleActor ! UserMessage(line) }
