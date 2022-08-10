package main.response;

public class FinalResponseStatistics {

    private boolean result;
    private ResponseStatistics statistics;

    public FinalResponseStatistics(boolean result, ResponseStatistics statistics) {
        this.result = result;
        this.statistics = statistics;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ResponseStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(ResponseStatistics statistics) {
        this.statistics = statistics;
    }
}
