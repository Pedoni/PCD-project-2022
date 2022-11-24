package events.model;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public final class AnalyzerAgent extends AbstractVerticle {

    private Iterator<Path> walkStream;
    private final String path;
    private final String word;
    private boolean searchPaused = false;

    public AnalyzerAgent(String path, String word) {
        this.path = path;
        this.word = word;
    }

    @Override
    public void start() {
        final EventBus eb = getVertx().eventBus();
        try {
            walkStream = Files.walk(Paths.get(path)).iterator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        eb.consumer("pause", handler -> this.searchPaused = true);
        eb.consumer("resume", handler -> this.searchPaused = false);
        eb.consumer("next", handler -> {
            if (!walkStream.hasNext()) {
                eb.send("masterfinished", true);
                this.vertx.undeploy(this.deploymentID());
            } else {
                final Path p = walkStream.next();
                if (p.toFile().isFile() && p.toString().endsWith(".pdf")) {
                    eb.send("found", true);
                    this.scanPdf(eb, p);
                } else {
                    eb.send("next", true);
                }
            }
        });
    }

    private void scanPdf(final EventBus eb, final Path p) {
        vertx.executeBlocking(future -> {
            //System.out.println("Thread: " + Thread.currentThread().getName());
            try {
                final File file = new File(p.toString());
                final PDDocument document = PDDocument.load(file);
                final AccessPermission ap = document.getCurrentAccessPermission();
                if (!ap.canExtractContent())
                    throw new IOException("You do not have permission to extract text");
                final PDFTextStripper pdfStripper = new PDFTextStripper();
                final String text = pdfStripper.getText(document);
                if (text.contains(word))
                    eb.publish("matching", true);
                eb.publish("analyzed", true);
                document.close();
                future.complete();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, res -> {
            if (!searchPaused)
                eb.send("next", true);
        });
    }

}
