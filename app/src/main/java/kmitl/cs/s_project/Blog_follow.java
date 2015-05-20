package kmitl.cs.s_project;

import java.util.List;

/**
 * Created by nadpon on 20/5/2558.
 */
public class Blog_follow {
    String status;
    int count;
    List<follow> data;

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

    public List<follow> getData() {
        return data;
    }

    public void setData(List<follow> data) {
        this.data = data;
    }
}
