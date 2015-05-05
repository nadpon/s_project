package kmitl.cs.s_project;

import java.util.List;

/**
 * Created by nadpon on 29/4/2558.
 */
public class Blog_personal {
    String status;
    int count;
    List<Personal> data;

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

    public List<Personal> getData() {
        return data;
    }

    public void setData(List<Personal> data) {
        this.data = data;
    }
}
