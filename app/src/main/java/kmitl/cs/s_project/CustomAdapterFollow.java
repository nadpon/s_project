package kmitl.cs.s_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadpon on 20/5/2558.
 */
public class CustomAdapterFollow extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<follow> nFeed;
    private ViewHolder mViewHolder;
    Activity mActivity;
    follow feed;
    InputStream is = null;
    String js_result = "";
    String fID;

    public CustomAdapterFollow(Activity activity, List<follow> data){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.follow, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.postName = (TextView) convertView.findViewById(R.id.postNameTxt);
            mViewHolder.deleteBtn = (ImageView) convertView.findViewById(R.id.deleteBtn);

            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        feed = nFeed.get(position);

        mViewHolder.postName.setText(feed.postName);

        mViewHolder.postName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,PostInfoFullActivity.class);
                intent.putExtra("postID",String.valueOf(nFeed.get(position).postID));
                mActivity.startActivity(intent);
            }
        });

        mViewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fID = String.valueOf(nFeed.get(position).followID);
                new deleteFollow().execute();

                nFeed.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private static class ViewHolder{
        TextView postName;
        ImageView deleteBtn;
    }

    public class deleteFollow extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("followID", fID));
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://reportdatacenter.esy.es/deleteFollow.php");
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
    }
}
