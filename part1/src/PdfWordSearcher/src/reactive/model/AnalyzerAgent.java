package reactive.model;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class AnalyzerAgent {

    private final String path;
    private final String word;
    private final Flowable<String> source;
    private final Flowable<Boolean> analyzed;
    private boolean paused;

    public AnalyzerAgent(final String path, final String word) {
        this.path = path;
        this.word = word;
        this.source = this.genHotStream();
        this.analyzed = source.map(this::searchInPdf);
        this.paused = false;
    }

    private Flowable<String> genHotStream() {
        final Flowable<String> source = Flowable.create(emitter -> new Thread(() -> {
            try (final Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
                walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                    while (paused) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (f.toString().endsWith("pdf")) {
                        emitter.onNext(f.toString());
                    }
                });
                emitter.onComplete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start(), BackpressureStrategy.BUFFER);
        final ConnectableFlowable<String> hotObservable = source.publish();
        return hotObservable.autoConnect();
    }

    private boolean searchInPdf(final String v) {
        try {
            if(v != null) {
                final File file = new File(v);
                final PDDocument document = PDDocument.load(file);
                final PDFTextStripper pdfStripper = new PDFTextStripper();
                final String text = pdfStripper.getText(document);
                document.close();
                return text.contains(this.word);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public Flowable<String> getMasterStream() {
        return this.source;
    }

    public Flowable<Boolean> getWorkerStream() {
        return this.analyzed;
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
    }
}
