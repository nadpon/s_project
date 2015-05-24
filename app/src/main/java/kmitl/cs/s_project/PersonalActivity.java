package kmitl.cs.s_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class PersonalActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal2);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        onBackPressed();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            setting();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setting() {
        Intent intent = new Intent(PersonalActivity.this,SettingPageActivity.class);
        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {
        GridView gridView;
        ImageView userImage;
        TextView userDisName;
        TextView email;
        RelativeLayout main;
        int uID;
        ProgressDialog pDialog;
        String js_result;
        InputStream is;
        JSONObject jsonObject;
        private CustomAdapterGridview mAdapter;

        public PlaceholderFragment() {
        }

        public class getUserInfo extends AsyncTask<Void, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                main.setVisibility(View.INVISIBLE);

                pDialog = new ProgressDialog(PersonalActivity.this);
                pDialog.setMessage(getResources().getString(R.string.please_wait));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("userID",String.valueOf(uID)));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/getUserInfo.php");
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    while ((line = reader.readLine()) != null){
                        sb.append(line+ "\n");
                    }
                    is.close();
                    js_result = sb.toString();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                try {
                    JSONArray jsonArray = new JSONArray(js_result);
                    for (int i=0;i<jsonArray.length();i++){
                        jsonObject = jsonArray.getJSONObject(i);

                        String displayImage = jsonObject.getString("displayImage");
                        Picasso.with(PersonalActivity.this)
                                .load("http://reportdatacenter.esy.es/process/userImage/"+displayImage)
                                .transform(new CircleTransform()).into(userImage);

                        userDisName.setText(jsonObject.getString("displayName"));

                        email.setText(jsonObject.getString("email"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(pDialog!=null)
                    pDialog.dismiss();

                main.setVisibility(View.VISIBLE);
                new getPostImage().execute();
            }
        }

        public class getPostImage extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String... params) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("userID",String.valueOf(uID)));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/getPostImage.php");
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

                showData(s);
            }

            private void showData(String jsonString) {
                Gson gson = new Gson();
                Blog_personal blogPersonal = gson.fromJson(jsonString,Blog_personal.class);

                if (blogPersonal.count==0){
                    gridView.setVisibility(View.INVISIBLE);
                }
                else {
                    List<Personal> datas = blogPersonal.getData();

                    mAdapter = new CustomAdapterGridview(PersonalActivity.this,datas);
                    //set grid view
                    gridView.setAdapter(mAdapter);
                }

            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_personal, container, false);

            //get value from intent
            uID = getIntent().getIntExtra("uId",0);

            //init
            userImage = (ImageView) rootView.findViewById(R.id.userImage);
            userDisName = (TextView) rootView.findViewById(R.id.userNameDisplay);
            email = (TextView) rootView.findViewById(R.id.email);
            gridView = (GridView) rootView.findViewById(R.id.gridview);
            main = (RelativeLayout) rootView.findViewById(R.id.main);

            // execute to get userInfo
            new getUserInfo().execute();

            return rootView;
        }
    }
}
