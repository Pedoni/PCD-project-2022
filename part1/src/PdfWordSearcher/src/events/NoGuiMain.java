package events;

import events.model.AnalyzerAgent;
import events.model.SearcherAgent;
import events.model.SharedData;
import io.vertx.core.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NoGuiMain {

    public static void main(String[] args) throws IOException {

        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // System.out.println("Type the path to explore: ");
        // String path = reader.readLine();
        final String path = "C:/Users/emala/Desktop/PCD";
        System.out.println("Type the word to search: ");
        final String word = reader.readLine();

        final SharedData sd = new SharedData();
        final Vertx vertx = Vertx.vertx();

        Future<String> analyzer = vertx.deployVerticle(new AnalyzerAgent(sd, path));
        Future<String> searcher = vertx.deployVerticle(new SearcherAgent(sd, word));

    }

}
