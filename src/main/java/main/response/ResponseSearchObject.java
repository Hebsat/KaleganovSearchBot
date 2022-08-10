package main.response;

public class ResponseSearchObject {

    private boolean result;
    private int count;
    private SearchData[] data;

    public ResponseSearchObject(boolean result, int count, SearchData[] searchData) {
        this.result = result;
        this.count = count;
        this.data = searchData;
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

    public SearchData[] getData() {
        return data;
    }

    public void setData(SearchData[] data) {
        this.data = data;
    }
}
