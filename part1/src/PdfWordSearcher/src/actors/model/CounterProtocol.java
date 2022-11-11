package actors.model;

import akka.actor.typed.ActorRef;

public interface CounterProtocol {

    record FoundValueMessage(int value) implements CounterProtocol {}

    record IncrementFoundMessage() implements CounterProtocol {}

    record GetFoundMessage(ActorRef replyTo) implements CounterProtocol {}

    record MatchingValueMessage(int value) implements CounterProtocol {}

    record IncrementMatchingMessage() implements CounterProtocol {}

    record GetMatchingMessage(ActorRef replyTo) implements CounterProtocol {}

    record AnalyzedValueMessage(int value) implements CounterProtocol {}

    record IncrementAnalyzedMessage() implements CounterProtocol {}

    record GetAnalyzedMessage(ActorRef replyTo) implements CounterProtocol {}

}
