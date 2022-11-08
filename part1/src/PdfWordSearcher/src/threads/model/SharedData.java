package threads.model;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedData {

    private int foundPdf = 0;
    private int analyzedPdf = 0;
    private int matchingPdf = 0;
    private final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    private boolean masterRunning = true;
    private boolean searchPaused = false;
    private final int nWorkers = Runtime.getRuntime().availableProcessors();
    private int closedWorkers = 0;


    public synchronized int getMatchingPdf() {
        return this.matchingPdf;
    }

    public synchronized void incrementOccurrences() {
        this.matchingPdf += 1;
    }

    public synchronized void addToQueue(String path) {
        this.queue.add(path);
    }

    public synchronized String pollFromQueue() {
        if(this.isSearchPaused()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return this.queue.poll();
    }

    public synchronized boolean isMasterRunning() {
        return this.masterRunning;
    }

    public synchronized void stopMaster() {
        this.masterRunning = false;
    }

    public synchronized boolean isQueueEmpty() {
        return this.queue.isEmpty();
    }

    public synchronized int getWorkersNumber() {
        return this.nWorkers;
    }

    public synchronized boolean areAllWorkersFinished() {
        return this.closedWorkers == this.nWorkers;
    }

    public synchronized void incrementClosedWorkers() {
        this.closedWorkers += 1;
    }

    public synchronized void incrementFoundPdf() {
        if(this.isSearchPaused()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.foundPdf += 1;
    }

    public synchronized int getFoundPdf() {
        return this.foundPdf;
    }

    public synchronized int getAnalyzedPdf() {
        return analyzedPdf;
    }

    public synchronized void incrementAnalyzedPdf() {
        this.analyzedPdf += 1;
    }

    public synchronized boolean isSearchPaused() {
        return searchPaused;
    }

    public synchronized void resumeSearch() {
        this.searchPaused = false;
        notifyAll();
    }

    public synchronized void pauseSearch() {
        this.searchPaused = true;
    }
}
