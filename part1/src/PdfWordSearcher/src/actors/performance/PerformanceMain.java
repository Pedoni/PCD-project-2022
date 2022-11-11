package actors.performance;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

public class PerformanceMain {

    public static void main(String[] args) throws IOException {

        final ActorSystem<PerformanceProtocol> performer =
                ActorSystem.create(PerformanceActor.create(), "performer");

        performer.tell(new PerformanceProtocol.PerformMessage());

    }
}
