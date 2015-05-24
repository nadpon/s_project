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
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PostActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
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
        getMenuInflater().inflate(R.menu.menu_post, menu);
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
        ImageView postImage;
        EditText postName;
        EditText postDetail;
        Spinner cateSpinner;
        Button sendButton;
        View locateLayout;
        String cate;
        String lat, lng;
        public static final int REQUEST_GALLERY = 1,REQUEST_CAMERA = 2;
        Uri imageUri;
        Uri selectedImageUri = null;
        InputStream is;
        Bitmap bitmap=null;
        ProgressDialog pDialog;
        private final String[] choose = {"ถ่ายภาพ","เลือกจากคลังรูปภาพ"};
        String userID;

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

            postImage.setImageBitmap(bitmap);

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

        //===================================================================================================================
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        public class post extends AsyncTask<Void, Void, Void>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = new ProgressDialog(PostActivity.this);
                pDialog.setMessage("กรุณารอสักครู่ ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                nameValuePairs.add(new BasicNameValuePair("isAdd", "true"));
                nameValuePairs.add(new BasicNameValuePair("postName",postName.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair(" postDetail",postDetail.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("userID",userID));
                Object selectedItem = cateSpinner.getSelectedItem();
                if (selectedItem.toString().equals("จุดเสี่ยงอาชญากรรม")){
                    cate = "1";
                }
                else if (selectedItem.toString().equals("ชำรุด ทรุดโทรม")){
                    cate = "2";
                }
                else if (selectedItem.toString().equals("คมนาคม")){
                    cate = "3";
                }
                else {
                    cate = "4";
                }
                nameValuePairs.add(new BasicNameValuePair("cate",cate));
                nameValuePairs.add(new BasicNameValuePair("lat",lat));
                nameValuePairs.add(new BasicNameValuePair("lng",lng));

                ByteArrayOutputStream bao ;
                bao = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                byte [] ba = bao.toByteArray();
                String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                nameValuePairs.add(new BasicNameValuePair("image",ba1));
                nameValuePairs.add(new BasicNameValuePair("cmd",System.currentTimeMillis() + ".jpg"));

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/post.php");
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
                if(pDialog!=null)
                    pDialog.dismiss();
                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(intent);
                SharedPreferences sp = getSharedPreferences("prefs_newFeed", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("load", "");
                editor.putString("result", "");
                editor.commit();
                finish();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_post, container, false);
            //init
            postImage = (ImageView) rootView.findViewById(R.id.postImage);
            postName = (EditText) rootView.findViewById(R.id.postName);
            postDetail = (EditText) rootView.findViewById(R.id.postDetail);
            sendButton = (Button) rootView.findViewById(R.id.sendButton);
            cateSpinner = (Spinner) rootView.findViewById(R.id.cate_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.cate,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cateSpinner.setAdapter(adapter);

            SharedPreferences sp = getSharedPreferences("prefs_user",MODE_PRIVATE);
            userID = sp.getString("key_userID","");

            //รับค่าจาก map
            lat = getActivity().getIntent().getStringExtra("lat");
            lng = getActivity().getIntent().getStringExtra("lng");

            //----------รับรูปตอนเริ่ม Activity------------------
            AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
            builder.setCancelable(false);
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

            //----------รับรูปจาก ImageView-----------------
            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                    builder.setCancelable(true);
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

            //--------------click sendButton--------------------------
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bitmap==null){
                        Toast.makeText(getApplicationContext(), "กรุณาเพิ่มรูปภาพ",
                                Toast.LENGTH_LONG).show();
                    }
                    else if (postName.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "กรุณาเพิ่มชื่อเรื่อง",
                                Toast.LENGTH_LONG).show();
                        postDetail.clearFocus();
                        postName.setFocusable(true);
                    }
                    else if (postDetail.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "กรุณาเพิ่มรายละเอียด",
                                Toast.LENGTH_LONG).show();
                        postName.clearFocus();
                        postDetail.setFocusable(true);
                    }
                    else if (lat.equals("")){
                        Toast.makeText(getApplicationContext(), "กรุณาเพิ่มตำแหน่ง",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        new post().execute();
                    }
                }
            });

            return rootView;
        }
    }
}
