package kmitl.cs.s_project;

import java.util.List;

/**
 * Created by nadpon on 21/5/2558.
 */
public class Blog_noti {
    String status;
    int count;
    List<Noti> data;

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

    public List<Noti> getData() {
        return data;
    }

    public void setData(List<Noti> data) {
        this.data = data;
    }
}
