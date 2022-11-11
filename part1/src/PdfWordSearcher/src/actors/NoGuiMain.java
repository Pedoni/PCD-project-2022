package actors;

import actors.model.*;
import akka.actor.typed.ActorSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NoGuiMain {

    public static void main(String[] args) throws IOException {

        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // System.out.println("Type the path to explore: ");
        // String path = reader.readLine();
        Data.path = "C:/Users/emala/Desktop/PCD";
        System.out.println("Type the word to search: ");
        Data.word = reader.readLine();

        final ActorSystem<SearchAnalyzeProtocol> analyzer =
                ActorSystem.create(AnalyzerActor.create(), "analyzer");

        final ActorSystem<CounterProtocol> counter =
                ActorSystem.create(CounterActor.create(), "counter");

        analyzer.tell(new SearchAnalyzeProtocol.BootMessage(counter));
    }

}
