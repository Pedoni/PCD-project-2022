package tasks.model;

import tasks.view.View;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public final class Model {

    private final List<Future<Integer>> futures = new ArrayList<>();
    private final String path;
    private final String word;
    private final View view;
    private int foundPdf = 0;
    private int analyzedPdf = 0;
    private int matchingPdf = 0;
    private boolean isAnalysisClosed = false;
    private boolean paused = false;

    public Model(final String path, final String word, final View view) {
        this.path = path;
        this.word = word;
        this.view = view;
    }

    public void pauseSearch() {
        this.paused = true;

    }

    public void resumeSearch() {
        this.paused = false;
    }

    public void start() {
        final int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        final ExecutorService executor = Executors.newFixedThreadPool(nWorkers);
        new Thread(() -> {
            try (final Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
                walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                    while(paused) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (f.toString().endsWith("pdf")) {
                        foundPdf++;
                        futures.add(executor.submit(new WordSearchTask(f, word)));
                    }
                });
            } catch (IOException e){
                throw new RuntimeException(e);
            }
            try {
                futures.forEach(f -> {
                    try {
                        matchingPdf += f.get();
                        analyzedPdf++;
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
                this.isAnalysisClosed = true;
                executor.shutdown();
            } finally {
                executor.shutdownNow();
            }
        }).start();

        new Thread(() -> {
            while(!this.isAnalysisClosed) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                this.view.updateData(
                    foundPdf,
                    analyzedPdf,
                    matchingPdf
                );
            }
            this.view.resetState();
        }).start();
    }
}
