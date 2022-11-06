package iterative;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {

    public static int occurrences = 0;

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Type the word to search: ");
        String word = reader.readLine();
        // System.out.println("Type the path to explore: ");
        // String path = reader.readLine();
        String path = "C:/Users/emala/Desktop/PCD";

        try (Stream<Path> walkStream = Files.walk(Paths.get(path))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                if (f.toString().endsWith("pdf")) {
                    try {
                        searchWordInPdf(f.toString(), word);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            System.out.println("Total number of pdf containing word: " + occurrences);
        }
    }

    private static void searchWordInPdf(String path, String word) throws IOException {
        File file = new File(path);
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        if(text.contains(word)){
            occurrences += 1;
            System.out.println("TOTAL OCCURRENCES: " + occurrences);
        }
        document.close();
    }
}