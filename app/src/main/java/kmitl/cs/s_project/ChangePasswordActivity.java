package kmitl.cs.s_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ChangePasswordActivity extends ActionBarActivity {
    EditText oldPass;
    EditText newPass;
    EditText newPassAgain;
    Button changeBtn;
    InputStream is = null;
    String js_result = "";
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        oldPass = (EditText) findViewById(R.id.oldPass);
        newPass = (EditText) findViewById(R.id.newPass);
        newPassAgain = (EditText) findViewById(R.id.newPass2);
        changeBtn = (Button) findViewById(R.id.chageBtn);

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPass.getText().toString().equals("")||newPass.getText().toString().equals("")
                        ||newPassAgain.getText().toString().equals("")){
                    Toast.makeText(ChangePasswordActivity.this, ChangePasswordActivity.this.getResources().getText(R.string.f)
                            , Toast.LENGTH_LONG).show();
                }
                else if (oldPass.getText().toString()!=""&&newPass.getText().toString()!=""&&newPass.getText().length()<8
                        &&newPassAgain.getText().toString()!=""){
                    Toast.makeText(ChangePasswordActivity.this,ChangePasswordActivity.this.getResources().getText(R.string.d)
                            ,Toast.LENGTH_LONG).show();
                }
                else if (oldPass.getText().toString()!=""&&newPass.getText().toString()!=""&&newPass.getText().length()>=8
                        &&newPassAgain.getText().toString()!=""){
                    if (newPass.getText().toString().equals(newPassAgain.getText().toString())){
                        new changePassWord().execute();
                    }
                    else {
                        Toast.makeText(ChangePasswordActivity.this,ChangePasswordActivity.this.getResources().getText(R.string.e)
                                ,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    public class changePassWord extends AsyncTask<Void, Void, String> {
        SharedPreferences sp = getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        String uID = sp.getString("key_userID","");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ChangePasswordActivity.this);
            pDialog.setMessage(getResources().getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("userID", uID));
            nameValuePairs.add(new BasicNameValuePair("oldPass",oldPass.getText().toString()));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/checkOldPass.php");
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
                httpclient.execute(httppost);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //get value from json
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null){
                    sb.append(line+ "\n");
                }
                is.close();
                js_result = sb.toString();
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
                JSONObject jObject = new JSONObject(js_result);
                if (jObject.getString("status").equals("pass")){
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("userID", uID));
                    nameValuePairs.add(new BasicNameValuePair("newPass", newPass.getText().toString()));
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/chagePassword.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    httpClient.execute(httpPost);

                    if(pDialog!=null)
                        pDialog.dismiss();

                    Toast.makeText(ChangePasswordActivity.this,ChangePasswordActivity.this.getResources().getText(R.string.h)
                            ,Toast.LENGTH_LONG).show();

                    ChangePasswordActivity.this.finish();
                }
                else {
                    if(pDialog!=null)
                        pDialog.dismiss();

                    Toast.makeText(ChangePasswordActivity.this,ChangePasswordActivity.this.getResources().getText(R.string.g)
                            ,Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
