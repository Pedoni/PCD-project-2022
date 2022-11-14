package threads.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public final class Worker extends Thread {

    private final SharedData sd;
    private final String word;

    public Worker(final SharedData sd, final String word) {
        this.sd = sd;
        this.word = word;
    }

    public void run() {
        while(this.sd.isMasterRunning() || !this.sd.isQueueEmpty()){
            this.sd.checkPaused();
            try {
                this.searchWordInPdf();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.sd.incrementClosedWorkers();
    }

    private void searchWordInPdf() throws IOException {
        final String currentPath = this.sd.pollFromQueue();
        if(currentPath != null){
            final File file = new File(currentPath);
            final PDDocument document = PDDocument.load(file);
            final PDFTextStripper pdfStripper = new PDFTextStripper();
            final String text = pdfStripper.getText(document);
            if(text.contains(this.word))
                this.sd.incrementOccurrences();
            this.sd.incrementAnalyzedPdf();
            document.close();
        }
    }

}
