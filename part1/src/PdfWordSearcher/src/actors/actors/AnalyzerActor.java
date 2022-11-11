package actors.actors;

import actors.controller.Data;
import actors.protocols.CounterProtocol;
import actors.protocols.SearchAnalyzeProtocol;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class AnalyzerActor extends AbstractBehavior<SearchAnalyzeProtocol> {

    public AnalyzerActor(ActorContext<SearchAnalyzeProtocol> context) {
        super(context);
    }

    @Override
    public Receive<SearchAnalyzeProtocol> createReceive() {
        return newReceiveBuilder()
                .onMessage(SearchAnalyzeProtocol.BootMessage.class, this::onBootMessage)
                .build();
    }

    public static Behavior<SearchAnalyzeProtocol> create() {
        return Behaviors.setup(AnalyzerActor::new);
    }

    private Behavior<SearchAnalyzeProtocol> onBootMessage(SearchAnalyzeProtocol.BootMessage message) {
        try (Stream<Path> walkStream = Files.walk(Paths.get(Data.path))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                if (f.toString().endsWith("pdf")) {
                    message.counter().tell(new CounterProtocol.IncrementFoundMessage());
                    ActorSystem.create(SearcherActor.create(), "searcher").tell(
                            new SearchAnalyzeProtocol.SearchMessage(
                                    f.toString(),
                                    message.counter()
                            )
                    );
                }
            });
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        message.counter().tell(new CounterProtocol.Finish());
        return Behaviors.stopped();
    }
}
