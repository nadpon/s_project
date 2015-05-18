package kmitl.cs.s_project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

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


public class EditMyInfoActivity extends ActionBarActivity {
    EditText fName,lName,address,tel,email;
    Spinner sexSpinner;
    ImageView userImage;
    Button saveButton;
    ArrayAdapter<CharSequence> adapter;
    Bitmap bitmap=null;
    LinearLayout main;
    ProgressDialog pDialog;
    InputStream is = null;
    String js_result = "";
    JSONObject jsonObject;
    public static final int REQUEST_GALLERY = 1,REQUEST_CAMERA = 2;
    private final String[] choose = {"ถ่ายภาพ","เลือกจากคลังรูปภาพ"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_info);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        main = (LinearLayout) findViewById(R.id.main);
        fName = (EditText) findViewById(R.id.fNameEditText);
        lName = (EditText) findViewById(R.id.lNameEditText);
        address = (EditText) findViewById(R.id.addressEditText);
        tel = (EditText) findViewById(R.id.telEditText);
        email = (EditText) findViewById(R.id.emailEditText);
        userImage = (ImageView) findViewById(R.id.userImage);
        saveButton = (Button) findViewById(R.id.saveButton);

        sexSpinner = (Spinner) findViewById(R.id.sex_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(adapter);

        new getMyInfo().execute();
        // ==========================================================================================================
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ทำตรงนี้ ต่อไป
                // update ข้อมูล
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_my_info, menu);
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

    public class getMyInfo extends AsyncTask<Void, Void, String>{
        SharedPreferences sp = getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        String uID = sp.getString("key_userID","");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            main.setVisibility(View.INVISIBLE);
            pDialog = new ProgressDialog(EditMyInfoActivity.this);
            pDialog.setMessage(getResources().getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("userID",uID));
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/getMyInfo.php");
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

                    fName.setText(jsonObject.getString("fname"));
                    lName.setText(jsonObject.getString("lname"));
                    address.setText(jsonObject.getString("address"));
                    tel.setText(jsonObject.getString("tel"));
                    email.setText(jsonObject.getString("email"));

                    String sex = jsonObject.getString("sex");
                    if (sex.equals("ชาย")){
                        sexSpinner.setSelection(1);
                    }
                    else {
                        sexSpinner.setSelection(0);
                    }

                    String displayImage = jsonObject.getString("displayImage");
                    Picasso.with(EditMyInfoActivity.this)
                            .load("http://reportdatacenter.esy.es/process/userImage/" + displayImage)
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
}