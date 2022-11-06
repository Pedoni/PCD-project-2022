package tasks;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class SearchPdfTask implements Callable<Void> {

    private final SharedData sd;
    private final String word;

    public SearchPdfTask(SharedData sd, String word) {
        this.sd = sd;
        this.word = word;
    }

    @Override
    public Void call() {
        while(this.sd.isMasterRunning() || !this.sd.isQueueEmpty()){
            try {
                searchWordInPdf(this.sd);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
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
