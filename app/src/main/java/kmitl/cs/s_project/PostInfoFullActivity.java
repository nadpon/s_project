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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_info_full, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
        ImageView userImage;
        TextView userNameDisplay;
        TextView postDate;
        TextView postName;
        TextView postCate;
        TextView postStatus;
        TextView postDetail;
        ImageView postImage;
        TextView nLikeTxt;
        TextView nShareTxt;
        Button likeButton;
        Button commentButton;
        Button shareButton;
        ImageView arrowDown;
        ProgressDialog pDialog;
        String postID;
        String js_result;
        InputStream is;
        JSONObject jsonObject;

        public PlaceholderFragment() {
        }

        public class getPostInfo extends AsyncTask<Void, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
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
                        }
                        else {
                            postStatus.setTextColor(PostInfoFullActivity.this.getResources().getColor(R.color.post_8_color));
                            postStatus.setText("ไม่อยู่ในขอบเขต");
                        }

                        postDetail.setText("รายละเอียด : "+jsonObject.getString("detail"));

                        String postIm = jsonObject.getString("postImage");

                        Picasso.with(PostInfoFullActivity.this)
                                .load("http://reportdatacenter.esy.es/process/postImage/" + postIm).into(postImage);

                        nLikeTxt.setText(String.valueOf(jsonObject.getInt("nLike")));
                        nShareTxt.setText(String.valueOf(jsonObject.getInt("nShare")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(pDialog!=null)
                    pDialog.dismiss();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_post_info_full, container, false);
            //รับค่า intent
            postID = getIntent().getStringExtra("postID");

            //init
            userImage = (ImageView) rootView.findViewById(R.id.userImage);
            userNameDisplay = (TextView) rootView.findViewById(R.id.userNameDisplay);
            userNameDisplay = (TextView) rootView.findViewById(R.id.userNameDisplay);
            postDate = (TextView) rootView.findViewById(R.id.postDate);
            postName = (TextView) rootView.findViewById(R.id.postName);
            postCate = (TextView) rootView.findViewById(R.id.postCate);
            postStatus = (TextView) rootView.findViewById(R.id.postStatus);
            postDetail = (TextView) rootView.findViewById(R.id.postDetail);
            postImage = (ImageView) rootView.findViewById(R.id.postImage);
            likeButton = (Button) rootView.findViewById(R.id.likeButton);
            commentButton = (Button) rootView.findViewById(R.id.commentButton);
            shareButton = (Button) rootView.findViewById(R.id.shareButton);
            arrowDown = (ImageView) rootView.findViewById(R.id.arrowDown);
            nLikeTxt = (TextView) rootView.findViewById(R.id.nLikeTxt);
            nShareTxt = (TextView) rootView.findViewById(R.id.nShareTxt);

            new getPostInfo().execute();

            return rootView;
        }
    }
}
