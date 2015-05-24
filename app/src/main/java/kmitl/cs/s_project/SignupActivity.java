package kmitl.cs.s_project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends ActionBarActivity {
    EditText emailTxt,passTxt,fName,lName,address,tel;
    Spinner sexSpinner;
    ImageView userImage;
    Button signupButton;
    String email,password,fname,lname,sex;
    public static final int REQUEST_GALLERY = 1,REQUEST_CAMERA = 2;
    Uri imageUri;
    Uri selectedImageUri = null;
    InputStream is;
    Bitmap bitmap=null;
    String js_result;
    JSONObject jObject;
    ProgressDialog pDialog;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    private final String[] choose = {"ถ่ายภาพ","เลือกจากคลังรูปภาพ"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        public PlaceholderFragment() {
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            String filePath = null;

            //gallery
            if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK){
                selectedImageUri = data.getData();
            }

            //camera
            else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
                selectedImageUri = imageUri;
            }

            if(selectedImageUri != null){
                try {
                    // OI FILE Manager
                    String filemanagerstring = selectedImageUri.getPath();

                    // MEDIA GALLERY
                    String selectedImagePath = getPath(selectedImageUri);

                    if (selectedImagePath != null) {
                        filePath = selectedImagePath;
                    } else if (filemanagerstring != null) {
                        filePath = filemanagerstring;
                    } else {
                        Toast.makeText(getApplicationContext(), "Unknown path",
                                Toast.LENGTH_LONG).show();
                        Log.e("Bitmap", "Unknown path");
                    }

                    if (filePath != null) {
                        decodeFile(filePath);
                    } else {
                        bitmap = null;
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Internal error",
                            Toast.LENGTH_LONG).show();
                    Log.e(e.getClass().getName(), e.getMessage(), e);
                }
            }

        }

        private void decodeFile(String filePath) {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFile(filePath, o2);

            Picasso.with(getApplicationContext()).load(selectedImageUri).transform(new CircleTransform()).into(userImage);

        }

        private String getPath(Uri uri) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            if (cursor != null) {
                // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
                // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else
                return null;
        }

        //=====================================================================================================
        private class checkEmail extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(SignupActivity.this);
                pDialog.setMessage("กรุณารอสักครู่ ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
                nameValuePairs1.add(new BasicNameValuePair("email", email));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/checkEmail.php");
                try{
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs1,"UTF-8"));
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
                }catch(Exception e){
                    Log.v("log_tag", "Error in http connection " + e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    if (android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }

                    jObject = new JSONObject(js_result);
                    if (jObject.getString("status").equals("pass")){
                        if(pDialog!=null)
                            pDialog.dismiss();

                        new signup().execute();
                    }
                    else {
                        if(pDialog!=null)
                            pDialog.dismiss();

                        Toast.makeText(SignupActivity.this, "อีเมล์ได้มีการลงทะเบียนก่อนหน้านี้แล้ว", Toast.LENGTH_LONG).show();
                        passTxt.clearFocus();
                        fName.clearFocus();
                        lName.clearFocus();
                        emailTxt.setFocusable(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //=====================================================================================================
        public class signup extends AsyncTask<Void, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(SignupActivity.this);
                pDialog.setMessage("กรุณารอสักครู่ ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("isAdd", "true"));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                nameValuePairs.add(new BasicNameValuePair("fname", fname));
                nameValuePairs.add(new BasicNameValuePair("lname", lname));
                nameValuePairs.add(new BasicNameValuePair("sex", sex));
                nameValuePairs.add(new BasicNameValuePair("address",address.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("tel",tel.getText().toString()));

                ByteArrayOutputStream bao ;
                bao = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                byte [] ba = bao.toByteArray();
                String ba1 = Base64.encodeToString(ba,Base64.DEFAULT);
                nameValuePairs.add(new BasicNameValuePair("image",ba1));
                nameValuePairs.add(new BasicNameValuePair("cmd", System.currentTimeMillis() + ".jpg"));

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/signup.php");
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    httpClient.execute(httpPost);
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

                new getUserID().execute();
            }
        }

        //=====================================================================================================
        public class getUserID extends AsyncTask<Void, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(SignupActivity.this);
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
                HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/getUserID.php");
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
                        JSONObject jObject1 = jsonArray.getJSONObject(i);
                        String userID = String.valueOf(jObject1.getInt("userID"));
                        SharedPreferences sp = getActivity().getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("key_userID",userID);
                        editor.putString("key_login","yes");
                        editor.commit();

                        if(pDialog!=null)
                            pDialog.dismiss();

                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

            emailTxt = (EditText) rootView.findViewById(R.id.emailEditText);
            passTxt = (EditText) rootView.findViewById(R.id.passwordEditText);
            fName = (EditText) rootView.findViewById(R.id.fNameEditText);
            lName = (EditText) rootView.findViewById(R.id.lNameEditText);
            address = (EditText) rootView.findViewById(R.id.addressEditText);
            tel = (EditText) rootView.findViewById(R.id.telEditText);

            sexSpinner = (Spinner) rootView.findViewById(R.id.sex_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),R.array.sex, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sexSpinner.setAdapter(adapter);
            userImage = (ImageView) rootView.findViewById(R.id.userImage);
            signupButton = (Button) rootView.findViewById(R.id.signupButton);

            //รับรูป
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setTitle("กำหนดรูปภาพประจำตัว");
                    builder.setItems(choose,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //รับจาก gallery
                            if (choose[which]=="เลือกจากคลังรูปภาพ"){
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,"เลือกรูปภาพ"), REQUEST_GALLERY);
                            }
                            //รับจาก camera
                            else{
                                String timeStamp =
                                        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                String imageFileName = "IMG_" + timeStamp + ".jpg";
                                File f = new File(Environment.getExternalStorageDirectory()
                                        , "DCIM/Camera/" + imageFileName);
                                imageUri = Uri.fromFile(f);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent,  REQUEST_CAMERA);
                            }
                        }
                    });
                    builder.setNegativeButton(null, null);
                    builder.create();
                    builder.show();
                }
            });

            //กดปุ่มลงทะเบียน
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //รับค่า
                    email = emailTxt.getText().toString();
                    password = passTxt.getText().toString();
                    fname = fName.getText().toString();
                    lname = lName.getText().toString();
                    Object selectedItem = sexSpinner.getSelectedItem ( );
                    sex = selectedItem.toString();

                    cd = new ConnectionDetector(getActivity().getApplicationContext());
                    isInternetPresent = cd.isConnectingToInternet();

                    if (isInternetPresent){
                        if (email.equals("")||password.equals("")||fname.equals("")||lname.equals("")
                                ||address.getText().toString().equals("")||tel.getText().toString().equals("")){
                            Toast.makeText(SignupActivity.this.getApplicationContext()
                                    ,"กรุณากรอกข้อมูลให้ครบ",Toast.LENGTH_LONG).show();
                        }
                        else if (bitmap==null){
                            Toast.makeText(SignupActivity.this.getApplicationContext()
                                    ,"กรุณาเพิ่มรูปประจำตัว",Toast.LENGTH_LONG).show();
                        }
                        else {
                            if (isEmailValid(email)){
                                new checkEmail().execute();
                            }
                            else {
                                Toast.makeText(SignupActivity.this.getApplicationContext()
                                        ,"รูปแบบอีเมล์ไม่ถูกต้อง",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else {
                        Toast.makeText(SignupActivity.this, "กรุณาเชื่อมต่ออินเทอร์เน็ต", Toast.LENGTH_LONG).show();
                    }
                }
            });

            return rootView;
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
