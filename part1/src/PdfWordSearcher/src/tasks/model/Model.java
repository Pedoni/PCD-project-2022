package tasks.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import tasks.view.View;

import java.io.File;
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

    private final String path;
    private final String word;
    private final SharedData sd;
    private final View view;

    public Model(final String path, final String word, final SharedData sd, final View view) {
        this.path = path;
        this.word = word;
        this.sd = sd;
        this.view = view;
    }

    public void start() {
        final int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        final ExecutorService executor = Executors.newFixedThreadPool(nWorkers);
        final List<Future<Void>> futures = new ArrayList<>();

        new Thread(() -> {
            try (final Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
                walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                    if (f.toString().endsWith("pdf")) {
                        this.sd.incrementFoundPdf();
                        futures.add(executor.submit(new WordSearchTask(sd, f, word)));
                    }
                });
            } catch (IOException e){
                throw new RuntimeException(e);
            }
            try {
                futures.forEach(f -> {
                    try {
                        f.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
                this.sd.closeAnalysis();
                executor.shutdown();
            } finally {
                executor.shutdownNow();
            }
            this.sd.stopMaster();
        }).start();

        new Thread(() -> {
            while(!this.sd.isAnalysisClosed()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                this.view.updateData(
                    this.sd.getFoundPdf(),
                    this.sd.getAnalyzedPdf(),
                    this.sd.getMatchingPdf()
                );
            }
            this.view.resetState();
        }).start();
    }
}
