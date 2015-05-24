package kmitl.cs.s_project;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by nadpon on 6/4/2558.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    public static final String ARGS_POSITION = "name";

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // NewFeed fragment activity
                return new NewFeedActivity();
            case 1:
                // HotIssue fragment activity
                return new HotIssueActivity();
            case 2:
                // Notification fragment activity
                return new NotiActivity();
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
}
