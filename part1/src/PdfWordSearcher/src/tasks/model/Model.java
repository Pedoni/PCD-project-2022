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

    public Model(
        final String path,
        final String word,
        final SharedData sd
    ) {
        this.path = path;
        this.word = word;
        this.sd = sd;
    }

    public void start() {
        final int nWorkers = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(nWorkers);
        final List<Future<Void>> futures = new ArrayList<>();

        final Thread master = new Thread(() -> {
            try (final Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
                walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                    if (f.toString().endsWith("pdf")) {
                        this.sd.incrementFoundPdf();
                        futures.add(executor.submit(() -> {
                            this.sd.checkPaused();
                            try {
                                final File file = new File(f.toString());
                                final PDDocument document = PDDocument.load(file);
                                final PDFTextStripper pdfStripper = new PDFTextStripper();
                                final String text = pdfStripper.getText(document);
                                if(text.contains(this.word)) {
                                    this.sd.incrementOccurrences();
                                }
                                this.sd.incrementAnalyzedPdf();
                                document.close();
                            } catch(Exception e) {
                                throw new RuntimeException(e);
                            }
                            return null;
                        }));
                    }
                });
            } catch (IOException e){
                throw new RuntimeException(e);
            }
            System.out.println("Master finished");
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
        });
        master.start();
    }
}
