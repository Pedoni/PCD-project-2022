package reactive;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import reactive.model.AnalyzerAgent;
import reactive.model.SharedData;

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

        Future<String> analyzer = vertx.deployVerticle(new AnalyzerAgent(sd, path, word));

    }

}
