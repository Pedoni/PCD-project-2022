package actors.actors;

import actors.protocols.CounterProtocol;
import actors.protocols.SearchAnalyzeProtocol;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public final class CounterActor extends AbstractBehavior<CounterProtocol> {

    private static ActorRef<SearchAnalyzeProtocol> viewer;
    private int found;
    private int matching;
    private int analyzed;
    private boolean isMasterFinished;

    public CounterActor(final ActorContext<CounterProtocol> context) {
        super(context);
        this.found = 0;
        this.matching = 0;
        this.analyzed = 0;
        this.isMasterFinished = false;
    }

    @Override
    public Receive<CounterProtocol> createReceive() {
        return newReceiveBuilder()
                .onMessage(CounterProtocol.IncrementFoundMessage.class, this::onIncrementFoundMessage)
                .onMessage(CounterProtocol.IncrementMatchingMessage.class, this::onIncrementMatchingMessage)
                .onMessage(CounterProtocol.IncrementAnalyzedMessage.class, this::onIncrementAnalyzedMessage)
                .onMessage(CounterProtocol.Finish.class, this::onFinish)
                .build();
    }

    public static Behavior<CounterProtocol> create(final ActorRef<SearchAnalyzeProtocol> viewer) {
        if (viewer != null)
            CounterActor.viewer = viewer;
        return Behaviors.setup(CounterActor::new);
    }

    private Behavior<CounterProtocol> onFinish(final CounterProtocol.Finish message) {
        this.isMasterFinished = true;
        return this;
    }

    private Behavior<CounterProtocol> onIncrementFoundMessage(
            final CounterProtocol.IncrementFoundMessage message) {
        this.found++;
        if (viewer != null)
            viewer.tell(new SearchAnalyzeProtocol.UpdateGuiMessage(found, matching, analyzed));
        return this;
    }

    private Behavior<CounterProtocol> onIncrementMatchingMessage(
            final CounterProtocol.IncrementMatchingMessage message) {
        this.matching++;
        if (viewer != null)
            viewer.tell(new SearchAnalyzeProtocol.UpdateGuiMessage(found, matching, analyzed));
        return this;
    }

    private Behavior<CounterProtocol> onIncrementAnalyzedMessage(
            final CounterProtocol.IncrementAnalyzedMessage message) {
        this.analyzed++;
        if (viewer != null) {
            viewer.tell(new SearchAnalyzeProtocol.UpdateGuiMessage(found, matching, analyzed));
            if (this.isMasterFinished && (this.found == this.analyzed))
                viewer.tell(new SearchAnalyzeProtocol.ResetGuiMessage());
        }
        if (isStopping()) {
            return Behaviors.stopped();
        }
        return this;
    }

    private boolean isStopping() {
        return (this.analyzed == this.found) && isMasterFinished;
    }

}
