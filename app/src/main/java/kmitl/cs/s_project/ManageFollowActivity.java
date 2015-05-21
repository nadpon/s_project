package kmitl.cs.s_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class ManageFollowActivity extends ActionBarActivity {
    ProgressDialog pDialog;
    String js_result;
    private ListView mListView;
    private CustomAdapterFollow mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_follow);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.listView);

        new getFollow().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_follow, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        onBackPressed();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class getFollow extends AsyncTask<String, Void, String>{
        SharedPreferences sp = getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        String uID = sp.getString("key_userID","");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ManageFollowActivity.this);
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
            HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/getFollow.php");
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

            showData(s);
        }
    }

    public void showData(String jsonString){
        Gson gson = new Gson();
        Blog_follow blog = gson.fromJson(jsonString, Blog_follow.class);

        if (blog.count==0){
            mListView.setVisibility(View.INVISIBLE);
            Toast.makeText(this,"ยังไม่ได้ติดตามเรื่องเรียนเลยนะ\nติดตามหน่อยจิ"
                    , Toast.LENGTH_LONG).show();
        }
        else {
            List<follow> datas = blog.getData();
            mAdapter = new CustomAdapterFollow(this,datas);
            mListView.setAdapter(mAdapter);

            mListView.setItemsCanFocus(true);
            mListView.setFocusable(false);
            mListView.setFocusableInTouchMode(false);
            mListView.setClickable(false);
        }
    }
}
