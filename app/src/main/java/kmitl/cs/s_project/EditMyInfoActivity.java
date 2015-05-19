package kmitl.cs.s_project;

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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class EditMyInfoActivity extends ActionBarActivity {
    EditText fName,lName,address,tel,email;
    Spinner sexSpinner;
    ImageView userImage;
    Button saveButton;
    ArrayAdapter<CharSequence> adapter;
    Bitmap bitmap=null;
    Uri imageUri;
    Uri selectedImageUri = null;
    LinearLayout main;
    ProgressDialog pDialog;
    InputStream is = null;
    String js_result = "";
    JSONObject jsonObject;
    public static final int REQUEST_GALLERY = 1,REQUEST_CAMERA = 2;
    private final String[] choose = {"ถ่ายภาพ","เลือกจากคลังรูปภาพ"};
    String displayImage;

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

        //==========================================================================================================
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditMyInfoActivity.this);
                builder.setTitle("กำหนดรูปภาพประจำตัว");
                builder.setItems(choose,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //รับจาก gallery
                        if (choose[which].equals("เลือกจากคลังรูปภาพ")){
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

        // ==========================================================================================================
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update ข้อมูล
                new uploadInfo().execute();
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

    //======================================================================================================
    public class uploadInfo extends AsyncTask<Void, Void, Void>{
        SharedPreferences sp = getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        String uID = sp.getString("key_userID","");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditMyInfoActivity.this);
            pDialog.setMessage(getResources().getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (bitmap==null){
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("userID",uID));
                nameValuePairs.add(new BasicNameValuePair("fName",fName.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("lName",lName.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("address",address.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("tel",tel.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("email",email.getText().toString()));
                Object selectedItem = sexSpinner.getSelectedItem();
                String sex = selectedItem.toString();
                nameValuePairs.add(new BasicNameValuePair("sex",sex));
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/upDateInfo.php");
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
            }
            else {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("userID",uID));
                nameValuePairs.add(new BasicNameValuePair("fName",fName.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("lName",lName.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("address",address.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("tel",tel.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("email",email.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("displayImage",displayImage));

                Object selectedItem = sexSpinner.getSelectedItem();
                String sex = selectedItem.toString();
                nameValuePairs.add(new BasicNameValuePair("sex",sex));

                ByteArrayOutputStream bao ;
                bao = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                byte [] ba = bao.toByteArray();
                String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                nameValuePairs.add(new BasicNameValuePair("image",ba1));
                nameValuePairs.add(new BasicNameValuePair("cmd",System.currentTimeMillis() + ".jpg"));

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/upDateInfoWithImage.php");
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
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(pDialog!=null)
                pDialog.dismiss();

            Toast.makeText(EditMyInfoActivity.this,"แก้ไขข้อมูลส่วนตัวสำเร็จ"
                    ,Toast.LENGTH_LONG).show();

            EditMyInfoActivity.this.finish();
        }
    }

    //===================================================================================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    //=======================================================================================================
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
                        sexSpinner.setSelection(0);
                    }
                    else {
                        sexSpinner.setSelection(1);
                    }

                    displayImage = jsonObject.getString("displayImage");
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