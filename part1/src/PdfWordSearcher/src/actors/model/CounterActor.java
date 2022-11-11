package actors.model;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class CounterActor extends AbstractBehavior<CounterProtocol> {

    private int found;
    private int matching;
    private int analyzed;

    public CounterActor(ActorContext<CounterProtocol> context) {
        super(context);
        found = 0;
        matching = 0;
        analyzed = 0;
    }

    @Override
    public Receive<CounterProtocol> createReceive() {
        return newReceiveBuilder()
                .onMessage(CounterProtocol.IncrementFoundMessage.class, this::onIncrementFoundMessage)
                .onMessage(CounterProtocol.GetFoundMessage.class, this::onGetFoundMessage)
                .onMessage(CounterProtocol.IncrementMatchingMessage.class, this::onIncrementMatchingMessage)
                .onMessage(CounterProtocol.GetMatchingMessage.class, this::onGetMatchingMessage)
                .onMessage(CounterProtocol.IncrementAnalyzedMessage.class, this::onIncrementAnalyzedMessage)
                .onMessage(CounterProtocol.GetAnalyzedMessage.class, this::onGetAnalyzedMessage)
                .build();
    }

    public static Behavior<CounterProtocol> create() {
        return Behaviors.setup(CounterActor::new);
    }

    private Behavior<CounterProtocol> onIncrementFoundMessage(
            CounterProtocol.IncrementFoundMessage message) {
        this.found++;
        System.out.println("FOUND: " + this.found);
        return this;
    }

    private Behavior<CounterProtocol> onGetFoundMessage(
            CounterProtocol.GetFoundMessage message) {
        //message.replyTo().tell(new SearchAnalyzeProtocol.FoundValueMessage(this.found));
        return this;
    }

    private Behavior<CounterProtocol> onIncrementMatchingMessage(
            CounterProtocol.IncrementMatchingMessage message) {
        this.matching++;
        System.out.println("MATCHING: " + this.matching);
        return this;
    }

    private Behavior<CounterProtocol> onGetMatchingMessage(
            CounterProtocol.GetMatchingMessage message) {

        return this;
    }

    private Behavior<CounterProtocol> onIncrementAnalyzedMessage(
            CounterProtocol.IncrementAnalyzedMessage message) {
        this.analyzed++;
        System.out.println("ANALYZED: " + this.analyzed);
        return this.analyzed == this.found ? Behaviors.stopped() : this;
    }

    private Behavior<CounterProtocol> onGetAnalyzedMessage(
            CounterProtocol.GetAnalyzedMessage message) {

        return this;
    }

}
