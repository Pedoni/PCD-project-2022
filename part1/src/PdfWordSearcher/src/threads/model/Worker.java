package threads.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class Worker extends Thread {

    private final int id;
    private final SharedData sd;
    private final String word;

    public Worker(int id, SharedData sd, String word) {
        this.id = id;
        this.sd = sd;
        this.word = word;
    }

    public void run() {
        while(sd.isMasterRunning() || !sd.isQueueEmpty()){
            try {
                searchWordInPdf(sd);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void searchWordInPdf(SharedData sd) throws IOException {
        String currentPath = sd.pollFromQueue();
        if(currentPath != null){
            File file = new File(currentPath);
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            if(text.contains(this.word)) {
                sd.incrementOccurrences();
                System.out.println("TOTAL OCCURRENCES: " + sd.getMatchingPdf());
            }
            sd.incrementAnalyzedPdf();
            System.out.println("ANALYZED PDFs: " + sd.getAnalyzedPdf());
            document.close();
        }
    }


}
