package threads.model;

public final class SharedData {

    private int foundPdf = 0;
    private int analyzedPdf = 0;
    private int matchingPdf = 0;
    private boolean masterRunning = true;
    private boolean searchPaused = false;
    private int closedWorkers = 0;

    public synchronized int getMatchingPdf() {
        return this.matchingPdf;
    }

    public synchronized void incrementOccurrences() {
        this.matchingPdf += 1;
    }

    public synchronized boolean isMasterRunning() {
        return this.masterRunning;
    }

    public synchronized void stopMaster() {
        this.masterRunning = false;
    }

    public synchronized int getClosedWorkers() {
        return this.closedWorkers;
    }

    public synchronized void incrementClosedWorkers() {
        this.closedWorkers += 1;
    }

    public synchronized void incrementFoundPdf() {
        this.foundPdf += 1;
    }

    public synchronized int getFoundPdf() {
        return this.foundPdf;
    }

    public synchronized int getAnalyzedPdf() {
        return this.analyzedPdf;
    }

    public synchronized void incrementAnalyzedPdf() {
        this.analyzedPdf += 1;
    }

    public synchronized boolean isSearchPaused() {
        return this.searchPaused;
    }

    public synchronized void resumeSearch() {
        this.searchPaused = false;
        notifyAll();
    }

    public synchronized void pauseSearch() {
        this.searchPaused = true;
    }

    public synchronized void checkPaused() {
        if(this.isSearchPaused()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
