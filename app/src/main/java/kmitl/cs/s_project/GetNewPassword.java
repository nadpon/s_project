package kmitl.cs.s_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class GetNewPassword extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_new_password);
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
        getMenuInflater().inflate(R.menu.menu_get_new_password, menu);
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
        Button getNewPassBtn;
        TextView userNameDisplay;
        ImageView userImage;
        RelativeLayout main;
        ProgressDialog pDialog;
        String email;
        InputStream is;
        String js_result;
        JSONObject jsonObject;

        public PlaceholderFragment() {
        }

        private class fetchUser extends AsyncTask<Void, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                main.setVisibility(View.INVISIBLE);

                pDialog = new ProgressDialog(GetNewPassword.this);
                pDialog.setMessage("กรุณารอสักครู่ ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", email));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/getUserForgotPass.php");
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

                        String displayName = jsonObject.getString("displayName");
                        String displayImage = jsonObject.getString("displayImage");
                        userNameDisplay.setText(displayName);
                        userNameDisplay.setVisibility(View.VISIBLE);
                        Picasso.with(GetNewPassword.this)
                                .load("http://reportdatacenter.esy.es/process/userImage/"+displayImage)
                                .transform(new CircleTransform()).into(userImage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(pDialog!=null)
                    pDialog.dismiss();

                main.setVisibility(View.VISIBLE);
            }
        }

        private class sendPassword extends AsyncTask<Void, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(GetNewPassword.this);
                pDialog.setMessage("กรุณารอสักครู่ ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", email));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/sendPass.php");
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
                if(pDialog!=null)
                    pDialog.dismiss();

                Intent intent = new Intent(GetNewPassword.this,MainActivity.class);
                startActivity(intent);

                Toast.makeText(GetNewPassword.this,"รหัสผ่านถูกส่งไปที่อีเมล์ของคุณเรียบร้อย",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_get_new_password, container, false);

            getNewPassBtn = (Button) rootView.findViewById(R.id.getNewPassButton);
            userNameDisplay = (TextView) rootView.findViewById(R.id.userNameDisplay);
            userImage = (ImageView) rootView.findViewById(R.id.userImage);
            main = (RelativeLayout) rootView.findViewById(R.id.main);

            email = getIntent().getStringExtra("email");

            new fetchUser().execute();

            getNewPassBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new sendPassword().execute();
                }
            });

            return rootView;
        }
    }
}
