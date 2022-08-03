package main.response;

public class ResponseSearchObject {

    private boolean result;
    private int count;
    private SearchData[] searchData;

    public ResponseSearchObject(boolean result, int count, SearchData[] searchData) {
        this.result = result;
        this.count = count;
        this.searchData = searchData;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public SearchData[] getSearchData() {
        return searchData;
    }

    public void setSearchData(SearchData[] searchData) {
        this.searchData = searchData;
    }
}
