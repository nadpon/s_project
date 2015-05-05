package kmitl.cs.s_project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by nadpon on 19/1/2558.
 */
public class LoginActivity extends Fragment{
    Button loginButton;
    TextView signup,forgetPass;
    EditText emailTxt,passTxt;
    String email,password;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ProgressDialog pDialog;
    String js_result,js_result1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_main, container, false);

        //----------------Normal Login-------------------------------------
        loginButton = (Button) rootView.findViewById(R.id.loginButton);
        emailTxt = (EditText) rootView.findViewById(R.id.emailEditText);
        passTxt = (EditText) rootView.findViewById(R.id.passwordEditText);
        signup = (TextView) rootView.findViewById(R.id.signup);
        forgetPass = (TextView) rootView.findViewById(R.id.forgotpass);

        class Signin extends AsyncTask<Void, Void, Void>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(LoginActivity.this.getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                pDialog.setMessage("กรุณารอสักครู่ ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                //ตรวจสอบ email, password
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/checkLogin_email_password.php");
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    js_result = sb.toString();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                try {
                    JSONObject jObject = new JSONObject(js_result);
                    if (jObject.getString("status").equals("pass")){
                        //getUserID
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("email", email));
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/getUserID.php");
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                        HttpResponse response = httpClient.execute(httpPost);
                        HttpEntity entity = response.getEntity();
                        InputStream is = entity.getContent();
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        is.close();
                        js_result1 = sb.toString();
                        JSONArray jsonArray = new JSONArray(js_result1);
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jObject1 = jsonArray.getJSONObject(i);
                            String userID = String.valueOf(jObject1.getInt("userID"));
                            SharedPreferences sp = getActivity().getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("key_userID",userID);
                            editor.putString("key_login","yes");
                            editor.commit();
                            if(pDialog!=null)
                                pDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this.getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                    else {
                        if(pDialog!=null)
                            pDialog.dismiss();
                        Toast.makeText(LoginActivity.this.getActivity(), "กรุณากรอก อีเมล์ หรือ รหัสผ่าน ให้ถูกต้อง"
                                , Toast.LENGTH_LONG).show();
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

        //Click Login Button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(getActivity().getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    email = emailTxt.getText().toString();
                    password = passTxt.getText().toString();
                    if (email.equals("")&&password.equals("")){
                        Toast.makeText(LoginActivity.this.getActivity(),"กรุณากรอกข้อมูลให้ครบ"
                                ,Toast.LENGTH_LONG).show();
                    }
                    else {
                        new  Signin().execute();
                    }
                } else {
                    Toast.makeText(LoginActivity.this.getActivity(), getResources().getText(R.string.noInternetConnect)
                            , Toast.LENGTH_LONG).show();
                }
            }
        });

        //-------------------Click Signup Button-----------------------------
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(getActivity().getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent){
                    Intent intent = new Intent(LoginActivity.this.getActivity(),SignupActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginActivity.this.getActivity(), getResources().getText(R.string.noInternetConnect)
                            , Toast.LENGTH_LONG).show();
                }
            }
        });

        //---------------------Click Forgot Button----------------------------------
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(getActivity().getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent){
                    Intent intent = new Intent(LoginActivity.this.getActivity(),ForgetpassActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginActivity.this.getActivity(), getResources().getText(R.string.noInternetConnect)
                            , Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }
}

