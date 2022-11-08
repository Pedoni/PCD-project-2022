package tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class NoGuiMain {

    public static void main(String[] args) throws IOException {

        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // System.out.println("Type the path to explore: ");
        // String path = reader.readLine();
        final String path = "C:/Users/emala/Desktop/PCD";
        System.out.println("Type the word to search: ");
        final String word = reader.readLine();

        final SharedData sd = new SharedData();

        final AnalyzePdfTask analyze = new AnalyzePdfTask(sd, path);
        final SearchPdfTask search = new SearchPdfTask(sd, word);

        final int cores = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(cores);

        final List<Future<Void>> futures = new ArrayList<>();

        futures.add(executor.submit(analyze));
        Stream.iterate(0, n -> n + 1)
                .limit(cores)
                .forEach(x -> futures.add(executor.submit(search)));

        futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            executor.shutdown();
        } finally {
            executor.shutdownNow();
        }
    }

}
