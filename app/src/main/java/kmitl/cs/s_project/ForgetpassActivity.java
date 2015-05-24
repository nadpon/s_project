package kmitl.cs.s_project;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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


public class ForgetpassActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpass);
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
        getMenuInflater().inflate(R.menu.menu_forgetpass, menu);
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
    @SuppressLint("ValidFragment")
    public class PlaceholderFragment extends Fragment {
        ConnectionDetector cd;
        Boolean isInternetPresent = false;
        String email;
        InputStream is = null;
        String js_result = "";
        ProgressDialog pDialog;
        EditText emailEditText;
        Button nextButton;

        public PlaceholderFragment() {
        }

        private class fetchUser extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                pDialog = new ProgressDialog(ForgetpassActivity.this);
                pDialog.setMessage("กรุณารอสักครู่ ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                //ส่งค่า email ไปตรวจ
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", email));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/checkEmail2.php");
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
                //รับค่า json
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
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                try {
                    JSONObject jObject = new JSONObject(js_result);
                    if (jObject.getString("status").equals("pass")){
                        if(pDialog!=null)
                            pDialog.dismiss();
                        Intent intent = new Intent(ForgetpassActivity.this, GetNewPassword.class);
                        intent.putExtra("email",email);
                        startActivity(intent);
                    }
                    else {
                        if(pDialog!=null)
                            pDialog.dismiss();
                        Toast toast = Toast.makeText(ForgetpassActivity.this.getApplicationContext()
                                ,"ไม่พบอีเมล์"+"\n"+"กรุณากรอกใหม่อีกครั้ง",Toast.LENGTH_LONG);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_forgetpass, container, false);

            emailEditText = (EditText) rootView.findViewById(R.id.emailEditText);
            nextButton = (Button) rootView.findViewById(R.id.nextButton);

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    email = emailEditText.getText().toString();

                    cd = new ConnectionDetector(getActivity().getApplicationContext());
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent){
                        if (email.equals("")){
                            Toast toast = Toast.makeText(ForgetpassActivity.this.getApplicationContext(),"กรุณากรอกที่อยู่อีเมล์"
                                    ,Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else {
                            new fetchUser().execute();
                        }
                    }
                    else {
                        Toast.makeText(ForgetpassActivity.this, "กรุณาเชื่อมต่ออินเทอร์เน็ต", Toast.LENGTH_LONG).show();
                    }
                }
            });

            return rootView;
        }
    }
}
