package events.model;

import events.controller.FlowController;
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
    private final FlowController fc;
    private final String path;
    private final String word;

    public AnalyzerAgent(final FlowController fc, String path, String word) {
        this.fc = fc;
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

        eb.consumer("next", handler -> {
            final Path p = walkStream.next();
            if (p.toFile().isFile() && p.toString().endsWith(".pdf")) {
                System.out.println("INIZIO");
                eb.send("found", true);
                vertx.executeBlocking(future -> {
                    try {
                        this.fc.checkPaused();
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
                }, res -> System.out.println("Completata"));
            }
            if (!fc.isSearchPaused())
                eb.send("next", true);
            if (!walkStream.hasNext()) {
                eb.send("masterfinished", true);
                this.vertx.undeploy(this.deploymentID());
            }

        });

        /*
        final EventBus eb = getVertx().eventBus();
        try (final Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                this.fc.checkPaused();
                if (f.toString().endsWith("pdf")) {
                    eb.send("found", true);
                    vertx.executeBlocking(future -> {
                        try {
                            this.fc.checkPaused();
                            final File file = new File(f.toString());
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
                    }, res -> System.out.println("Completata"));
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        eb.send("masterfinished", true);
        try {
            this.vertx.undeploy(this.deploymentID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

         */
    }

}
