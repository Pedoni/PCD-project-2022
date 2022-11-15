package reactive.model;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.vertx.core.AbstractVerticle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import reactive.controller.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class AnalyzerAgent {

    private final SharedData sd;
    private final String path;
    private final String word;

    public AnalyzerAgent(final SharedData sd, final String path, final String word) {
        this.sd = sd;
        this.path = path;
        this.word = word;
    }

    public void start() {
        final Flowable<String> source = this.genHotStream();
        source.onBackpressureDrop(v -> System.out.println("DROPPING: " + v))
                .observeOn(Schedulers.computation())
                .subscribe(this::searchInPdf, error -> System.out.println("ERROR: " + error));
        this.sd.stopMaster();
    }

    private Flowable<String> genHotStream() {
        final Flowable<String> source = Flowable.create(emitter -> new Thread(() -> {
            try (final Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
                walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                    this.sd.checkPaused();
                    if (f.toString().endsWith("pdf")) {
                        this.sd.incrementFoundPdf();
                        emitter.onNext(f.toString());
                    }
                });
                emitter.onComplete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start(), BackpressureStrategy.LATEST);
        final ConnectableFlowable<String> hotObservable = source.publish();
        hotObservable.connect();
        return hotObservable;
    }

    private void searchInPdf(final String v) {
        try {
            this.sd.checkPaused();
            if(v != null){
                final File file = new File(v);
                final PDDocument document = PDDocument.load(file);
                final PDFTextStripper pdfStripper = new PDFTextStripper();
                final String text = pdfStripper.getText(document);
                if(text.contains(this.word)) {
                    this.sd.incrementOccurrences();
                    System.out.println("TOTAL OCCURRENCES: " + this.sd.getMatchingPdf());
                }
                this.sd.incrementAnalyzedPdf();
                System.out.println("ANALYZED PDFs: " + this.sd.getAnalyzedPdf());
                document.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
