package kmitl.cs.s_project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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


public class PostInfoFullActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info_full);
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
        getMenuInflater().inflate(R.menu.menu_post_info_full, menu);
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
        ImageView userImage;
        TextView userNameDisplay;
        TextView postDate;
        TextView postName;
        TextView postCate;
        TextView postStatus;
        TextView postDetail;
        ImageView postImage;
        RatingBar ratingBar;
        LinearLayout commentButton;
        LinearLayout optionButton;
        ImageView map;
        LayerDrawable stars;
        RelativeLayout main;
        ProgressDialog pDialog;
        String postID;
        String js_result;
        InputStream is;
        JSONObject jsonObject;
        String pID;

        public PlaceholderFragment() {
        }

        public class getPostInfo extends AsyncTask<Void, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                main.setVisibility(View.INVISIBLE);

                pDialog = new ProgressDialog(PostInfoFullActivity.this);
                pDialog.setMessage(getResources().getString(R.string.please_wait));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("postID", postID));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/getPostFullInfo.php");
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

                        Picasso.with(PostInfoFullActivity.this)
                                .load("http://reportdatacenter.esy.es/process/userImage/"+displayImage)
                                .transform(new CircleTransform()).into(userImage);

                        userNameDisplay.setText(jsonObject.getString("displayName"));

                        postDate.setText(jsonObject.getString("postDate"));

                        postName.setText(jsonObject.getString("postName"));

                        postCate.setText(jsonObject.getString("cateName"));

                        if (Integer.parseInt(jsonObject.getString("statusID"))==1){
                            postStatus.setTextColor(PostInfoFullActivity.this.getResources().getColor(R.color.post_1_color));
                            postStatus.setText(jsonObject.getString("statusName"));
                        }
                        else if (Integer.parseInt(jsonObject.getString("statusID"))==2){
                            postStatus.setTextColor(PostInfoFullActivity.this.getResources().getColor(R.color.post_2_color));
                            postStatus.setText(jsonObject.getString("statusName"));
                        }
                        else if (Integer.parseInt(jsonObject.getString("statusID"))==3){
                            postStatus.setTextColor(PostInfoFullActivity.this.getResources().getColor(R.color.post_3_color));
                            postStatus.setText(jsonObject.getString("statusName"));
                        }
                        else if (Integer.parseInt(jsonObject.getString("statusID"))==4){
                            postStatus.setTextColor(PostInfoFullActivity.this.getResources().getColor(R.color.post_4_color));
                            postStatus.setText(jsonObject.getString("statusName"));
                        }
                        else if (Integer.parseInt(jsonObject.getString("statusID"))==5){
                            postStatus.setTextColor(PostInfoFullActivity.this.getResources().getColor(R.color.post_7_color));
                            postStatus.setText("เรื่องร้องเรียนซ้ำ");
                            commentButton.setVisibility(View.INVISIBLE);
                            optionButton.setVisibility(View.INVISIBLE);
                            ratingBar.setVisibility(View.INVISIBLE);
                        }
                        else {
                            postStatus.setTextColor(PostInfoFullActivity.this.getResources().getColor(R.color.post_8_color));
                            postStatus.setText("ไม่อยู่ในขอบเขต");
                            commentButton.setVisibility(View.INVISIBLE);
                            optionButton.setVisibility(View.INVISIBLE);
                            ratingBar.setVisibility(View.INVISIBLE);
                        }

                        postDetail.setText("รายละเอียด : "+jsonObject.getString("detail"));

                        String postIm = jsonObject.getString("postImage");

                        Picasso.with(PostInfoFullActivity.this)
                                .load("http://reportdatacenter.esy.es/process/postImage/" + postIm).into(postImage);

                        // draw rating bar
                        int nLike = jsonObject.getInt("nLike");

                        if (nLike<5){
                            ratingBar.setRating(0);
                        }
                        else if (nLike<10){
                            float a = (float) 0.5;
                            ratingBar.setRating(a);
                        }
                        else if (nLike<15){
                            ratingBar.setRating(1);
                        }
                        else if (nLike<20){
                            float a = (float) 1.5;
                            ratingBar.setRating(a);
                        }
                        else if (nLike<25){
                            ratingBar.setRating(2);
                        }
                        else if (nLike<30){
                            float a = (float) 2.5;
                            ratingBar.setRating(a);
                        }
                        else if (nLike<35){
                            ratingBar.setRating(3);
                        }
                        else if (nLike<40){
                            float a = (float) 3.5;
                            ratingBar.setRating(a);
                        }
                        else if (nLike<45){
                            ratingBar.setRating(4);
                        }
                        else if (nLike<50){
                            float a = (float) 4.5;
                            ratingBar.setRating(a);
                        }
                        else {
                            ratingBar.setRating(5);
                        }

                        // click map ImageView
                        map.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PostInfoFullActivity.this, PostMapActivity.class);
                                try {
                                    intent.putExtra("lat", String.valueOf(jsonObject.getDouble("gpsLatitude")));
                                    intent.putExtra("lng", String.valueOf(jsonObject.getDouble("gpsLongitude")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                            }
                        });

                        //click comment button
                        commentButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PostInfoFullActivity.this,CommentActivity.class);
                                try {
                                    intent.putExtra("pID",String.valueOf(jsonObject.getInt("postID")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                            }
                        });

                        //click option button
                        final String a = "เห็นด้วยกับเรื่องร้องเรียน";
                        final String b = "ติดตามเรื่องร้องเรียน";
                        final String[] choose = {a,b};

                        optionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostInfoFullActivity.this);
                                builder.setItems(choose, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (choose[which].equals(b)) {
                                            SharedPreferences sp = getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
                                            String uID = sp.getString("key_userID", "");
                                            int mID = Integer.parseInt(uID);
                                            int uId = 0;
                                            try {
                                                uId = jsonObject.getInt("userID");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            if (mID == uId) {
                                                Toast.makeText(PostInfoFullActivity.this, "ไม่สามารถติดตามเรื่องร้องเรียนของคุณเองได้"
                                                        , Toast.LENGTH_LONG).show();
                                            } else {
                                                try {
                                                    pID = String.valueOf(jsonObject.getInt("postID"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                new follow().execute();
                                            }
                                        } else {
                                            try {
                                                pID = String.valueOf(jsonObject.getInt("postID"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            new checkLike().execute();
                                        }
                                    }
                                });
                                builder.setNegativeButton(null, null);
                                builder.create();
                                builder.show();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(pDialog!=null)
                    pDialog.dismiss();

                main.setVisibility(View.VISIBLE);
            }
        }

        public class follow extends AsyncTask<Void, Void, String>{
            SharedPreferences sp = getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
            String uID = sp.getString("key_userID", "");

            @Override
            protected String doInBackground(Void... params) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("userID", uID));
                nameValuePairs.add(new BasicNameValuePair("postID",pID));

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/checkFollow.php");
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    HttpResponse response = httpClient.execute(httpPost);
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
                    JSONObject jObject = new JSONObject(js_result);
                    if (jObject.getString("status").equals("pass")){
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("userID", uID));
                        nameValuePairs.add(new BasicNameValuePair("postID",pID));
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/follow.php");
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                        httpClient.execute(httpPost);
                    }
                    else {
                        Toast.makeText(PostInfoFullActivity.this,"คุณได้ติดตามเรื่องร้องเรียนนี้ไปแล้ว"
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

        public class checkLike extends AsyncTask<Void, Void, String>{
            SharedPreferences sp = getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
            String uID = sp.getString("key_userID", "");

            @Override
            protected String doInBackground(Void... params) {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("userID", uID));
                nameValuePairs.add(new BasicNameValuePair("postID",pID));
                nameValuePairs.add(new BasicNameValuePair("actID","1"));

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/checkLike.php");
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    HttpResponse response = httpClient.execute(httpPost);
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
                    JSONObject jObject = new JSONObject(js_result);
                    if (jObject.getString("status").equals("pass")){
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("userID", uID));
                        nameValuePairs.add(new BasicNameValuePair("postID",pID));
                        nameValuePairs.add(new BasicNameValuePair("actID","1"));
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/like.php");
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                        httpClient.execute(httpPost);
                    }
                    else {
                        Toast.makeText(PostInfoFullActivity.this,"คุณเห็นด้วยกับเรื่องร้องเรียนนี้ไปแล้ว"
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.newfeed, container, false);
            //รับค่า intent
            postID = getIntent().getStringExtra("postID");

            //init
            userImage = (ImageView) rootView.findViewById(R.id.userImage);
            userNameDisplay = (TextView) rootView.findViewById(R.id.userNameDisplay);
            postDate = (TextView) rootView.findViewById(R.id.postDate);
            postName = (TextView) rootView.findViewById(R.id.postName);
            postCate = (TextView) rootView.findViewById(R.id.postCate);
            postStatus = (TextView) rootView.findViewById(R.id.postStatus);
            postDetail = (TextView) rootView.findViewById(R.id.postDetail);
            postImage = (ImageView) rootView.findViewById(R.id.postImage);
            ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
            commentButton = (LinearLayout) rootView.findViewById(R.id.commentButton);
            optionButton = (LinearLayout) rootView.findViewById(R.id.optionButton);
            map = (ImageView) rootView.findViewById(R.id.map);
            stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(this.getResources().getColor(R.color.post_3_color), PorterDuff.Mode.SRC_ATOP);
            main = (RelativeLayout) rootView.findViewById(R.id.main);

            new getPostInfo().execute();

            return rootView;
        }
    }
}
