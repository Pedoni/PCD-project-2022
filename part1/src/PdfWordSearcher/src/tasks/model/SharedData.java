package tasks.model;

public final class SharedData {

    private boolean masterRunning = true;
    private boolean searchPaused = false;
    private boolean isAnalysisClosed = false;

    public synchronized void stopMaster() {
        this.masterRunning = false;
    }

    public synchronized boolean isAnalysisClosed() {
        return this.isAnalysisClosed;
    }

    public synchronized void closeAnalysis() {
        this.isAnalysisClosed = true;
    }

    public synchronized boolean isSearchPaused() {
        return this.searchPaused;
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

    public synchronized void resumeSearch() {
        this.searchPaused = false;
        this.notifyAll();
    }

    public synchronized void pauseSearch() {
        this.searchPaused = true;
    }
}
