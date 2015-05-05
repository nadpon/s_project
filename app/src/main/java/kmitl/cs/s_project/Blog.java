package kmitl.cs.s_project;

import java.util.List;

/**
 * Created by nadpon on 8/4/2558.
 */
public class Blog {
    String status;
    int count;
    List<NewFeed> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<NewFeed> getData() {
        return data;
    }

    public void setData(List<NewFeed> data) {
        this.data = data;
    }
}
