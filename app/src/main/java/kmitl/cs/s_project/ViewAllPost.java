package kmitl.cs.s_project;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.software.shell.fab.ActionButton;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by nadpon on 6/4/2558.
 */
public class ViewAllPost extends Fragment{
    ActionButton actionButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_all_post, container, false);
        //-----Floating Action Button
        actionButton = (ActionButton) rootView.findViewById(R.id.action_button);

        return rootView;
    }
}