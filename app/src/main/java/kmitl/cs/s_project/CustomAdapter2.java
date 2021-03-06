package kmitl.cs.s_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadpon on 5/5/2558.
 */
public class CustomAdapter2 extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<HotIssue> nFeed;
    private ViewHolder mViewHolder;
    Activity mActivity;
    HotIssue feed;
    InputStream is = null;
    String js_result = "";
    String pID;

    public CustomAdapter2(Activity activity, List<HotIssue> data){
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        nFeed = data;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return nFeed.size();
    }

    @Override
    public Object getItem(int position) {
        return nFeed.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.newfeed,parent,false);
            mViewHolder = new ViewHolder();
            mViewHolder.userImage = (ImageView) convertView.findViewById(R.id.userImage);
            mViewHolder.userNameDisplay = (TextView) convertView.findViewById(R.id.userNameDisplay);
            mViewHolder.postDate = (TextView) convertView.findViewById(R.id.postDate);
            mViewHolder.postName = (TextView) convertView.findViewById(R.id.postName);
            mViewHolder.postCate = (TextView) convertView.findViewById(R.id.postCate);
            mViewHolder.postStatus = (TextView) convertView.findViewById(R.id.postStatus);
            mViewHolder.postDetail = (TextView) convertView.findViewById(R.id.postDetail);
            mViewHolder.postImage = (ImageView) convertView.findViewById(R.id.postImage);
            mViewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            mViewHolder.commentButton = (LinearLayout) convertView.findViewById(R.id.commentButton);
            mViewHolder.optionButton = (LinearLayout) convertView.findViewById(R.id.optionButton);
            mViewHolder.map = (ImageView) convertView.findViewById(R.id.map);
            mViewHolder.stars = (LayerDrawable) mViewHolder.ratingBar.getProgressDrawable();
            mViewHolder.stars.getDrawable(2).setColorFilter(mActivity.getResources().getColor(R.color.post_3_color), PorterDuff.Mode.SRC_ATOP);

            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        feed = nFeed.get(position);

        // load userImage
        Picasso.with(mActivity).load("http://reportdatacenter.esy.es/process/userImage/"+feed.getDisplayImage())
                .transform(new CircleTransform()).into(mViewHolder.userImage);

        mViewHolder.userNameDisplay.setText(feed.displayName);
        mViewHolder.postDate.setText(feed.postDate);
        mViewHolder.postName.setText(feed.postName);
        mViewHolder.postCate.setText(feed.cateName);

        if (feed.getStatusID()==1){
            mViewHolder.postStatus.setTextColor(mActivity.getResources().getColor(R.color.post_1_color));
        }
        else if (feed.getStatusID()==2){
            mViewHolder.postStatus.setTextColor(mActivity.getResources().getColor(R.color.post_2_color));
        }
        else if (feed.getStatusID()==3){
            mViewHolder.postStatus.setTextColor(mActivity.getResources().getColor(R.color.post_3_color));
        }
        else {
            mViewHolder.postStatus.setTextColor(mActivity.getResources().getColor(R.color.post_4_color));
        }

        mViewHolder.postStatus.setText(feed.statusName);
        mViewHolder.postDetail.setText(mActivity.getResources().getText(R.string.dtail) + " " + feed.detail);

        // load postImage
        Picasso.with(mActivity).load("http://reportdatacenter.esy.es/process/postImage/"+feed.getPostImage())
                .into(mViewHolder.postImage);

        // draw rating bar
        if (nFeed.get(position).nLike<5){
            mViewHolder.ratingBar.setRating(0);
        }
        else if (nFeed.get(position).nLike<10){
            float a = (float) 0.5;
            mViewHolder.ratingBar.setRating(a);
        }
        else if (nFeed.get(position).nLike<15){
            mViewHolder.ratingBar.setRating(1);
        }
        else if (nFeed.get(position).nLike<20){
            float a = (float) 1.5;
            mViewHolder.ratingBar.setRating(a);
        }
        else if (nFeed.get(position).nLike<25){
            mViewHolder.ratingBar.setRating(2);
        }
        else if (nFeed.get(position).nLike<30){
            float a = (float) 2.5;
            mViewHolder.ratingBar.setRating(a);
        }
        else if (nFeed.get(position).nLike<35){
            mViewHolder.ratingBar.setRating(3);
        }
        else if (nFeed.get(position).nLike<40){
            float a = (float) 3.5;
            mViewHolder.ratingBar.setRating(a);
        }
        else if (nFeed.get(position).nLike<45){
            mViewHolder.ratingBar.setRating(4);
        }
        else if (nFeed.get(position).nLike<50){
            float a = (float) 4.5;
            mViewHolder.ratingBar.setRating(a);
        }
        else {
            mViewHolder.ratingBar.setRating(5);
        }

        // Intent to Personal Activity
        mViewHolder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int uId = nFeed.get(position).userID;

                Intent intent = new Intent(mActivity,PersonalActivity.class);
                intent.putExtra("uId",uId);
                mActivity.startActivity(intent);
            }
        });

        // click map ImageView
        mViewHolder.map.setTag(position);
        mViewHolder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,PostMapActivity.class);
                intent.putExtra("lat",String.valueOf(nFeed.get(position).gpsLatitude));
                intent.putExtra("lng",String.valueOf(nFeed.get(position).gpsLongitude));
                mActivity.startActivity(intent);
            }
        });

        //click comment button
        mViewHolder.commentButton.setTag(position);
        mViewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,CommentActivity.class);
                intent.putExtra("pID",String.valueOf(nFeed.get(position).postID));
                mActivity.startActivity(intent);
            }
        });

        //click option button
        final String a = "เห็นด้วยกับเรื่องร้องเรียน";
        final String b = "ติดตามเรื่องร้องเรียน";
        final String[] choose = {a,b};

        mViewHolder.optionButton.setTag(position);
        mViewHolder.optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setItems(choose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (choose[which].equals(b)) {
                            SharedPreferences sp = mActivity.getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
                            String uID = sp.getString("key_userID", "");
                            int mID = Integer.parseInt(uID);
                            final int uId = nFeed.get(position).userID;

                            if (mID == uId) {
                                Toast.makeText(mActivity, "ไม่สามารถติดตามเรื่องร้องเรียนของคุณเองได้"
                                        , Toast.LENGTH_LONG).show();
                                notifyDataSetChanged();
                            } else {
                                pID = String.valueOf(nFeed.get(position).postID);
                                new follow().execute();
                            }
                        } else {
                            pID = String.valueOf(nFeed.get(position).postID);
                            new checkLike().execute();
                        }
                    }
                });
                builder.setNegativeButton(null, null);
                builder.create();
                builder.show();
            }
        });

        return convertView;
    }

    private static class ViewHolder{
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
    }

    public class follow extends AsyncTask<Void, Void, String> {
        SharedPreferences sp = mActivity.getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
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
                    Toast.makeText(mActivity,"คุณได้ติดตามเรื่องร้องเรียนนี้ไปแล้ว"
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
        SharedPreferences sp = mActivity.getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
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
                    Toast.makeText(mActivity,"คุณเห็นด้วยกับเรื่องร้องเรียนนี้ไปแล้ว"
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
