package threads.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public final class Worker extends Thread {

    private final int id;
    private final SharedData sd;
    private final String word;

    public Worker(final int id, final SharedData sd, final String word) {
        this.id = id;
        this.sd = sd;
        this.word = word;
    }

    public void run() {
        while(sd.isMasterRunning() || !sd.isQueueEmpty()){
            sd.checkPaused();
            try {
                searchWordInPdf(sd);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        sd.incrementClosedWorkers();
    }

    private void searchWordInPdf(final SharedData sd) throws IOException {
        final String currentPath = sd.pollFromQueue();
        if(currentPath != null){
            final File file = new File(currentPath);
            final PDDocument document = PDDocument.load(file);
            final PDFTextStripper pdfStripper = new PDFTextStripper();
            final String text = pdfStripper.getText(document);
            if(text.contains(this.word)) {
                sd.incrementOccurrences();
            }
            sd.incrementAnalyzedPdf();
            document.close();
        }
    }


}
