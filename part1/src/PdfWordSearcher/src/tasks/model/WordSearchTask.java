package tasks.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class WordSearchTask implements Callable<Integer> {

    private final SharedData sd;
    private final Path f;
    private final String word;

    public WordSearchTask(SharedData sd, Path f, String word) {
        this.sd = sd;
        this.f = f;
        this.word = word;
    }

    @Override
    public Integer call() {
        int count = 0;
        this.sd.checkPaused();
        try {
            final File file = new File(f.toString());
            final PDDocument document = PDDocument.load(file);
            final PDFTextStripper pdfStripper = new PDFTextStripper();
            final String text = pdfStripper.getText(document);
            if (text.contains(this.word)) {
                //this.sd.incrementOccurrences();
                count++;
            }
            //this.sd.incrementAnalyzedPdf();
            document.close();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return count;
    }
}
