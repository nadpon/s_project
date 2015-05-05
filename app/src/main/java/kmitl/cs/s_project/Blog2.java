package kmitl.cs.s_project;

import java.util.List;

/**
 * Created by nadpon on 5/5/2558.
 */
public class Blog2 {
    String status;
    int count;
    List<HotIssue> data;

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

    public List<HotIssue> getData() {
        return data;
    }

    public void setData(List<HotIssue> data) {
        this.data = data;
    }
}
