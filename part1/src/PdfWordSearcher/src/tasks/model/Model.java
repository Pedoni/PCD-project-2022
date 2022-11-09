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

public class Model {

    private final String path;
    private final String word;
    private final SharedData sd;
    private final View view;

    public Model(
        String path,
        String word,
        SharedData sd,
        View view
    ) {
        this.path = path;
        this.word = word;
        this.sd = sd;
        this.view = view;
    }

    public void start() {

        final int nWorkers = sd.getWorkersNumber();
        final ExecutorService executor = Executors.newFixedThreadPool(nWorkers);
        final List<Future<Void>> futures = new ArrayList<>();

        final Thread master = new Thread(() -> {
            try (Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
                walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                    if (f.toString().endsWith("pdf")) {
                        sd.incrementFoundPdf();
                        futures.add(executor.submit(() -> {
                            sd.checkPaused();
                            try {
                                File file = new File(f.toString());
                                PDDocument document = PDDocument.load(file);
                                PDFTextStripper pdfStripper = new PDFTextStripper();
                                String text = pdfStripper.getText(document);
                                if(text.contains(this.word)) {
                                    sd.incrementOccurrences();
                                }
                                sd.incrementAnalyzedPdf();
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
                sd.closeAnalysis();
                executor.shutdown();
            } finally {
                executor.shutdownNow();
            }

            sd.stopMaster();
        });
        master.start();
    }
}
