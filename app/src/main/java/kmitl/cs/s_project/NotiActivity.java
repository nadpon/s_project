package kmitl.cs.s_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by nadpon on 6/4/2558.
 */
public class NotiActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ProgressDialog pDialog;
    String js_result;
    private ListView mListView;
    private CustomAdapterNoti mAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noti, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(NotiActivity.this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright);

        mListView = (ListView) rootView.findViewById(R.id.listView);

        /*SharedPreferences sp = getActivity().getSharedPreferences("prefs_noti", Context.MODE_PRIVATE);
        String load = sp.getString("load", "");
        String result = sp.getString("result","");
        if (load.equals("yes")){
            showData(result);
        }
        else {
            new getNoti().execute();
        }*/
        new getNoti().execute();

        return rootView;
    }

    @Override
    public void onRefresh() {
        cd = new ConnectionDetector(getActivity().getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent){
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    new getNoti().execute();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 2000);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    Toast.makeText(NotiActivity.this.getActivity(), getResources().getText(R.string.noInternetConnect)
                            , Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 3000);
        }
    }

    public class getNoti extends AsyncTask<String, Void, String>{
        SharedPreferences sp = NotiActivity.this.getActivity().getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        String uID = sp.getString("key_userID","");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NotiActivity.this.getActivity());
            pDialog.setMessage(getResources().getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("userID",String.valueOf(uID)));
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/getNoti.php");
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                HttpResponse response = httpclient.execute(httppost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200){
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

            SharedPreferences sp = getActivity().getSharedPreferences("prefs_noti", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("load", "yes");
            editor.putString("result", js_result);
            editor.commit();

            showData(s);
        }
    }
    public void showData(String jsonString){
        Gson gson = new Gson();
        Blog_noti blog = gson.fromJson(jsonString, Blog_noti.class);

        if (blog.count==0){
            swipeRefreshLayout.setVisibility(View.INVISIBLE);
            Toast.makeText(NotiActivity.this.getActivity(),"ยังไม่มีการแจ้งเตื่อนตอนนี้"
                    , Toast.LENGTH_LONG).show();
        }
        else {
            List<Noti> datas = blog.getData();

            mAdapter = new CustomAdapterNoti(this.getActivity(),datas);
            mListView.setAdapter(mAdapter);

            mListView.setItemsCanFocus(true);
            mListView.setFocusable(false);
            mListView.setFocusableInTouchMode(false);
            mListView.setClickable(false);
        }
    }
}
