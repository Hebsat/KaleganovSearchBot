package main.response;

public class ResponseStatistics {

    private TotalStatistics totalStatistics;

    private DetailedStatistics[] detailedStatistics;

    public ResponseStatistics(TotalStatistics totalStatistics, DetailedStatistics[] detailedStatistics) {
        this.totalStatistics = totalStatistics;
        this.detailedStatistics = detailedStatistics;
    }

    public TotalStatistics getTotalStatistics() {
        return totalStatistics;
    }

    public void setTotalStatistics(TotalStatistics totalStatistics) {
        this.totalStatistics = totalStatistics;
    }

    public DetailedStatistics[] getDetailedStatistics() {
        return detailedStatistics;
    }

    public void setDetailedStatistics(DetailedStatistics[] detailedStatistics) {
        this.detailedStatistics = detailedStatistics;
    }
}
