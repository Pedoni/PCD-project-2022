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
import java.util.Iterator;

public final class AnalyzerActor extends AbstractBehavior<SearchAnalyzeProtocol> {

    private Iterator<Path> walkStream;
    private boolean isSearchPaused = false;

    public AnalyzerActor(ActorContext<SearchAnalyzeProtocol> context) {
        super(context);
    }

    @Override
    public Receive<SearchAnalyzeProtocol> createReceive() {
        return newReceiveBuilder()
                .onMessage(SearchAnalyzeProtocol.BootMessage.class, this::onBootMessage)
                .onMessage(SearchAnalyzeProtocol.StepMessage.class, this::onStepMessage)
                .onMessage(SearchAnalyzeProtocol.PauseMessage.class, this::onPauseMessage)
                .onMessage(SearchAnalyzeProtocol.ResumeMessage.class, this::onResumeMessage)
                .build();
    }

    private Behavior<SearchAnalyzeProtocol> onResumeMessage(final SearchAnalyzeProtocol.ResumeMessage message) {
        this.isSearchPaused = false;
        return this;
    }

    private Behavior<SearchAnalyzeProtocol> onPauseMessage(final SearchAnalyzeProtocol.PauseMessage message) {
        this.isSearchPaused = true;
        return this;
    }

    private Behavior<SearchAnalyzeProtocol> onStepMessage(final SearchAnalyzeProtocol.StepMessage message) {
        final Path p = walkStream.next();
        if (p.toFile().isFile() && p.toString().endsWith(".pdf")) {
            message.counter().tell(new CounterProtocol.IncrementFoundMessage());
            ActorSystem.create(SearcherActor.create(), "searcher").tell(
                new SearchAnalyzeProtocol.SearchMessage(p.toString(), message.counter())
            );
        }
        if (!isSearchPaused)
            message.analyzer().tell(new SearchAnalyzeProtocol.StepMessage(message.analyzer(), message.counter()));
        if (!walkStream.hasNext())
            message.counter().tell(new CounterProtocol.Finish());
        return walkStream.hasNext() ? this : Behaviors.stopped();
    }

    public static Behavior<SearchAnalyzeProtocol> create() {
        return Behaviors.setup(AnalyzerActor::new);
    }

    private Behavior<SearchAnalyzeProtocol> onBootMessage(final SearchAnalyzeProtocol.BootMessage message) {
        try {
            walkStream = Files.walk(Paths.get(Data.path)).iterator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(!isSearchPaused)
            message.analyzer().tell(new SearchAnalyzeProtocol.StepMessage(message.analyzer(), message.counter()));
        return this;
    }
}
