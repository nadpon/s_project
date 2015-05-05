package kmitl.cs.s_project;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import android.os.Handler;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * Created by nadpon on 6/4/2558.
 */
public class NewFeedActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ProgressDialog pDialog;
    String js_result;
    private ListView mListView;
    private CustomAdapter mAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    public class getNewFeed extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewFeedActivity.this.getActivity());
            pDialog.setMessage(getResources().getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/getNewFeed.php");
            try {
                HttpResponse response = httpclient.execute(httppost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line+ "\n");
                    }
                    inputStream.close();
                    js_result = sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return js_result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(pDialog!=null)
                pDialog.dismiss();

            SharedPreferences sp = getActivity().getSharedPreferences("prefs_newFeed", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("load", "yes");
            editor.putString("result",js_result);
            editor.commit();

            showData(s);
        }
    }

    public void showData(String jsonString) {
        Gson gson = new Gson();
        Blog blog = gson.fromJson(jsonString, Blog.class);

        List<NewFeed> datas = blog.getData();

        mAdapter = new CustomAdapter(this.getActivity(),datas);
        mListView.setAdapter(mAdapter);

        mListView.setItemsCanFocus(true);
        mListView.setFocusable(false);
        mListView.setFocusableInTouchMode(false);
        mListView.setClickable(false);
    }

    public void onRefresh(){
        cd = new ConnectionDetector(getActivity().getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent){
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    new getNewFeed().execute();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 2000);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    Toast.makeText(NewFeedActivity.this.getActivity(), getResources().getText(R.string.noInternetConnect)
                            , Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 3000);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_newfeed, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(NewFeedActivity.this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright);

        mListView = (ListView) rootView.findViewById(R.id.listView);

        cd = new ConnectionDetector(getActivity().getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent){
                SharedPreferences sp = getActivity().getSharedPreferences("prefs_newFeed", Context.MODE_PRIVATE);
                String load = sp.getString("load", "");
                String result = sp.getString("result","");
                if (load.equals("yes")){
                    showData(result);
                }
                else {
                    new getNewFeed().execute();
                }
            }
            else {
                final Dialog dialog = new Dialog(this.getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.lost_internet_dialog);
                dialog.setCancelable(false);
                dialog.show();
                Button ok = (Button) dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        getActivity().recreate();
                    }
                });
            }

        return rootView;
    }
}
