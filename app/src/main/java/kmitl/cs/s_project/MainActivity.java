package kmitl.cs.s_project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    private static final int ACTION_BUTTON_SHOW_DELAY_MS = 200;
    ViewPager viewPager;
    TabsPagerAdapter mAdapter;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Is Login Already
        SharedPreferences sp = getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        String login = sp.getString("key_login","");

        if (login.equals("yes")){

            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent){
                ViewAllPost viewAllPost = new ViewAllPost();
                android.support.v4.app.FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
                fragmenttransaction.add(android.R.id.content, viewAllPost);
                fragmenttransaction.commit();
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                getSupportActionBar().setLogo(R.mipmap.ic_launcher);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                // Initilization
                viewPager = (ViewPager) findViewById(R.id.pager);
                mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

                viewPager.setAdapter(mAdapter);
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
                // Adding Tabs
                getSupportActionBar().addTab(getSupportActionBar().newTab().setText(R.string.newfeed).setTabListener(this));
                getSupportActionBar().addTab(getSupportActionBar().newTab().setText(R.string.hotissue).setTabListener(this));
                getSupportActionBar().addTab(getSupportActionBar().newTab().setText(R.string.noti).setTabListener(this));

                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        // on changing the page
                        // make respected tab selected
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
            else {
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.lost_internet_dialog);
                dialog.setCancelable(false);

                Button ok = (Button) dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.this.recreate();
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        }
        else {
            LoginActivity loginactivity = new LoginActivity();
            android.support.v4.app.FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
            fragmenttransaction.add(android.R.id.content, loginactivity);
            fragmenttransaction.commit();
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        }
    }

    // Click FAB Button
    public void onActionButtonClick(View v) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        }, ACTION_BUTTON_SHOW_DELAY_MS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_all_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout){
            logOut();
            return true;
        }

        if (id == R.id.action_me){
            go_to_personal();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void go_to_personal() {
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent){
            SharedPreferences sp = getSharedPreferences("prefs_user",MODE_PRIVATE);
            String userID = sp.getString("key_userID", "");

            Intent intent = new Intent(this,PersonalActivity.class);
            intent.putExtra("uId",Integer.parseInt(userID));
            startActivity(intent);
        }
        else {
            Toast.makeText(MainActivity.this, getResources().getText(R.string.noInternetConnect)
                    , Toast.LENGTH_LONG).show();
        }

    }

    private void logOut() {
        SharedPreferences sp = getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("key_userID", "");
        editor.putString("key_login","no");
        editor.commit();
        LoginActivity loginactivity = new LoginActivity();
        android.support.v4.app.FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
        fragmenttransaction.add(android.R.id.content, loginactivity);
        fragmenttransaction.commit();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    // Tabs
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        SharedPreferences sp = getSharedPreferences("prefs_newFeed", Context.MODE_PRIVATE);
        String result = sp.getString("result","");
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("load", "yes");
        editor.putString("result", result);
        editor.commit();

        SharedPreferences sp1 = getSharedPreferences("prefs_hotIssue", Context.MODE_PRIVATE);
        String result1 = sp1.getString("result","");
        SharedPreferences.Editor editor1 = sp1.edit();
        editor.putString("load", "yes");
        editor.putString("result", result1);
        editor1.commit();
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    // closeApp
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences("prefs_newFeed", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("load", "");
        editor.putString("result", "");
        editor.commit();
    }
}
