package threads;

import threads.model.Master;
import threads.model.SharedData;
import threads.model.Worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NoGuiMain {

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // System.out.println("Type the path to explore: ");
        // String path = reader.readLine();
        String path = "C:/Users/emala/Desktop/PCD";
        System.out.println("Type the word to search: ");
        String word = reader.readLine();

        SharedData sd = new SharedData();
        new Master(sd, path).start();
        final int cores = Runtime.getRuntime().availableProcessors();
        for(int i  = 0; i < cores; i++){
            new Worker(i, sd, word).start();
        }

    }

}