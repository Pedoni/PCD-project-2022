package threads.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public final class Worker extends Thread {

    private final SharedData sd;
    private final String word;
    private final BlockingQueue<String> queue;

    public Worker(final SharedData sd, final String word, final BlockingQueue<String> queue) {
        this.sd = sd;
        this.word = word;
        this.queue = queue;
    }

    public void run() {
        while(this.sd.isMasterRunning() || !this.queue.isEmpty()){
            this.sd.checkPaused();
            try {
                final String currentPath = this.queue.take();
                final File file = new File(currentPath);
                final PDDocument document = PDDocument.load(file);
                final PDFTextStripper pdfStripper = new PDFTextStripper();
                final String text = pdfStripper.getText(document);
                if(text.contains(this.word))
                    this.sd.incrementOccurrences();
                this.sd.incrementAnalyzedPdf();
                document.close();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.sd.incrementClosedWorkers();
    }

}
