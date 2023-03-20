package actors.protocols;

public interface CounterProtocol {

    record IncrementFoundMessage() implements CounterProtocol {}

    record IncrementMatchingMessage() implements CounterProtocol {}

    record IncrementAnalyzedMessage() implements CounterProtocol {}

    record Finish() implements CounterProtocol {}

}
