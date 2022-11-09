package reactive.model;

public class SharedData {

    private int foundPdf = 0;
    private int analyzedPdf = 0;
    private int matchingPdf = 0;
    private boolean masterRunning = true;
    private boolean searchPaused = false;
    private boolean isAnalysisClosed = false;

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

    public synchronized void incrementFoundPdf() {
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

    public synchronized boolean isAnalysisClosed() {
        return this.isAnalysisClosed;
    }

    public synchronized void closeAnalysis() {
        this.isAnalysisClosed = true;
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

    public synchronized boolean isSearchPaused() {
        return searchPaused;
    }

    public synchronized void resumeSearch() {
        this.searchPaused = false;
        this.notifyAll();
    }

    public synchronized void pauseSearch() {
        this.searchPaused = true;
    }
}
