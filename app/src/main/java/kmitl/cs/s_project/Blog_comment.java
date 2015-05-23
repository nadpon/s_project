package kmitl.cs.s_project;

import java.util.List;

/**
 * Created by nadpon on 22/5/2558.
 */
public class Blog_comment {
    String status;
    int count;
    List<comment> data;

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

    public List<comment> getData() {
        return data;
    }

    public void setData(List<comment> data) {
        this.data = data;
    }
}
