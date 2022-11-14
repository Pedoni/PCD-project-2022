package actors.performance;

import actors.actors.AnalyzerActor;
import actors.actors.CounterActor;
import actors.controller.Data;
import actors.controller.FlowController;
import actors.protocols.CounterProtocol;
import actors.protocols.SearchAnalyzeProtocol;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class PerformanceActor extends AbstractBehavior<PerformanceProtocol> {

    private final Chrono chrono;

    public PerformanceActor(final ActorContext<PerformanceProtocol> context) {
        super(context);
        this.chrono = new Chrono();
    }

    @Override
    public Receive<PerformanceProtocol> createReceive() {
        return newReceiveBuilder()
                .onMessage(PerformanceProtocol.PerformMessage.class, this::onPerformMessage)
                .onMessage(PerformanceProtocol.KillMessage.class, this::onKillMessage)
                .build();
    }

    private Behavior<PerformanceProtocol> onPerformMessage(final PerformanceProtocol.PerformMessage message) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Data.path = "C:/Users/emala/Desktop/PCD";
        System.out.println("Type the word to search: ");
        try {
            Data.word = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ActorSystem<SearchAnalyzeProtocol> analyzer =
                ActorSystem.create(AnalyzerActor.create(new FlowController()), "analyzer");

        final ActorSystem<CounterProtocol> counter =
                ActorSystem.create(CounterActor.create(null, getContext().getSelf()), "counter");

        analyzer.tell(new SearchAnalyzeProtocol.BootMessage(counter));
        this.chrono.start();
        return this;
    }

    private Behavior<PerformanceProtocol> onKillMessage(final PerformanceProtocol.KillMessage message) {
        final long time = this.chrono.getTime();
        System.out.println("TOTAL TIME: " + (time/1000) + " seconds");
        return Behaviors.stopped();
    }

    public static Behavior<PerformanceProtocol> create() {
        return Behaviors.setup(PerformanceActor::new);
    }

}
