package actors.actors;

import actors.performance.PerformanceActor;
import actors.performance.PerformanceProtocol;
import actors.protocols.CounterProtocol;
import actors.protocols.SearchAnalyzeProtocol;
import akka.actor.Kill;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class CounterActor extends AbstractBehavior<CounterProtocol> {

    private static ActorRef<SearchAnalyzeProtocol> viewer;
    private static ActorRef<PerformanceProtocol> performer;
    private int found;
    private int matching;
    private int analyzed;
    private boolean isMasterFinished;

    public CounterActor(ActorContext<CounterProtocol> context) {
        super(context);
        found = 0;
        matching = 0;
        analyzed = 0;
        isMasterFinished = false;
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

    public static Behavior<CounterProtocol> create(
            ActorRef<SearchAnalyzeProtocol> viewer,
            ActorRef<PerformanceProtocol> performer
    ) {
        if (viewer != null)
            CounterActor.viewer = viewer;
        if (performer != null)
            CounterActor.performer = performer;
        return Behaviors.setup(CounterActor::new);
    }

    private Behavior<CounterProtocol> onFinish(CounterProtocol.Finish message) {
        this.isMasterFinished = true;
        return this;
    }

    private Behavior<CounterProtocol> onIncrementFoundMessage(
            CounterProtocol.IncrementFoundMessage message) {
        this.found++;
        if (viewer != null)
            viewer.tell(new SearchAnalyzeProtocol.UpdateGuiMessage(found, matching, analyzed));
        return this;
    }

    private Behavior<CounterProtocol> onIncrementMatchingMessage(
            CounterProtocol.IncrementMatchingMessage message) {
        this.matching++;
        if (viewer != null)
            viewer.tell(new SearchAnalyzeProtocol.UpdateGuiMessage(found, matching, analyzed));
        return this;
    }

    private Behavior<CounterProtocol> onIncrementAnalyzedMessage(
            CounterProtocol.IncrementAnalyzedMessage message) {
        this.analyzed++;
        if (viewer != null)
            viewer.tell(new SearchAnalyzeProtocol.UpdateGuiMessage(found, matching, analyzed));
        System.out.println(
                "ANALYZED: " + this.analyzed +
                "\n, FOUND: " + this.found +
                "\n, MATCHING: " + this.matching +
                "\n"
        );
        if (isStopping()) {
            performer.tell(new PerformanceProtocol.KillMessage());
            return Behaviors.stopped();
        }
        return this;
        //return isStopping() ? Behaviors.stopped() : this;
    }

    private boolean isStopping() {
        return (this.analyzed == this.found) && isMasterFinished;
    }

}
