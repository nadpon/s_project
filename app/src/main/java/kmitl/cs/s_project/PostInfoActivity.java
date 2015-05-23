package kmitl.cs.s_project;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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


public class PostInfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);
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
        getMenuInflater().inflate(R.menu.menu_post_info, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {
        String postName;
        ProgressDialog pDialog;
        String js_result;
        InputStream is;
        JSONObject jsonObject;
        ImageView userIm;
        TextView userDisplayName;
        TextView date;
        TextView postN;
        TextView cateName;
        TextView status;
        TextView detail;
        ImageView postIm;
        RelativeLayout main;

        public PlaceholderFragment() {
        }

        public class postInfo extends AsyncTask<Void, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                main.setVisibility(View.INVISIBLE);
                pDialog = new ProgressDialog(PostInfoActivity.this);
                pDialog.setMessage(getResources().getString(R.string.please_wait));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("postName",postName));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/getPostInfo.php");
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

                        Picasso.with(PostInfoActivity.this)
                                .load("http://reportdatacenter.esy.es/process/userImage/"+displayImage)
                                .transform(new CircleTransform()).into(userIm);

                        userDisplayName.setText(jsonObject.getString("displayName"));

                        date.setText(jsonObject.getString("postDate"));

                        postN.setText(jsonObject.getString("postName"));
                        cateName.setText(jsonObject.getString("cateName"));
                        status.setText(jsonObject.getString("statusName"));
                        detail.setText("รายละเอียด : " + jsonObject.getString("detail"));

                        String postImage = jsonObject.getString("postImage");

                        Picasso.with(PostInfoActivity.this)
                                .load("http://reportdatacenter.esy.es/process/postImage/" + postImage).into(postIm);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(pDialog!=null)
                    pDialog.dismiss();
                main.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_post_info, container, false);
            //รับค่า
            postName = getActivity().getIntent().getStringExtra("postName");

            //init
            userIm = (ImageView) rootView.findViewById(R.id.userImage);
            userDisplayName = (TextView) rootView.findViewById(R.id.userNameDisplay);
            date = (TextView) rootView.findViewById(R.id.postDate);
            postN = (TextView) rootView.findViewById(R.id.postName);
            cateName = (TextView) rootView.findViewById(R.id.postCate);
            status = (TextView) rootView.findViewById(R.id.postStatus);
            detail = (TextView) rootView.findViewById(R.id.postDetail);
            postIm = (ImageView) rootView.findViewById(R.id.postImage);
            main = (RelativeLayout) rootView.findViewById(R.id.main);

            if (postName!=null){
                new postInfo().execute();
            }
            else {
                PostInfoActivity.this.finish();
            }

            return rootView;
        }
    }
}
