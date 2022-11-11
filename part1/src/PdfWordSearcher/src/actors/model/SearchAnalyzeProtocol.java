package actors.model;

import akka.actor.typed.ActorRef;

public interface SearchAnalyzeProtocol {
    record BootMessage(ActorRef<CounterProtocol> counter)
            implements SearchAnalyzeProtocol{}

    record SearchMessage(String currentPath, ActorRef<CounterProtocol> counter)
            implements SearchAnalyzeProtocol{}


}
