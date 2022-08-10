package main.response;

public class ResponseStatistics {

    private TotalStatistics total;

    private DetailedStatistics[] detailed;

    public ResponseStatistics(TotalStatistics totalStatistics, DetailedStatistics[] detailedStatistics) {
        this.total = totalStatistics;
        this.detailed = detailedStatistics;
    }

    public TotalStatistics getTotal() {
        return total;
    }

    public void setTotal(TotalStatistics total) {
        this.total = total;
    }

    public DetailedStatistics[] getDetailed() {
        return detailed;
    }

    public void setDetailed(DetailedStatistics[] detailed) {
        this.detailed = detailed;
    }
}
