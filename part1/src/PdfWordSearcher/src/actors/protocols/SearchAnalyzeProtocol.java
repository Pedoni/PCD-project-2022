package actors.protocols;

import akka.actor.typed.ActorRef;

public interface SearchAnalyzeProtocol {
    record BootMessage(ActorRef<CounterProtocol> counter)
            implements SearchAnalyzeProtocol {}

    record SearchMessage(String currentPath, ActorRef<CounterProtocol> counter)
            implements SearchAnalyzeProtocol {}

    record UpdateGuiMessage(int found, int matching, int analyzed) implements SearchAnalyzeProtocol {}

    record ResetGuiMessage() implements SearchAnalyzeProtocol {}

}
