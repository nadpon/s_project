package kmitl.cs.s_project;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.logging.Log;

import java.util.List;

/**
 * Created by nadpon on 8/4/2558.
 */
public class CustomAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<NewFeed> nFeed;
    private ViewHolder mViewHolder;
    Activity mActivity;
    NewFeed feed;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    public CustomAdapter(Activity activity, List<NewFeed> data){
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
            mViewHolder.likeButton = (Button) convertView.findViewById(R.id.likeButton);
            mViewHolder.commentButton = (Button) convertView.findViewById(R.id.commentButton);
            mViewHolder.shareButton = (Button) convertView.findViewById(R.id.shareButton);
            mViewHolder.arrowDown = (ImageView) convertView.findViewById(R.id.arrowDown);
            mViewHolder.nLikeTxt = (TextView) convertView.findViewById(R.id.nLikeTxt);
            mViewHolder.nShareTxt = (TextView) convertView.findViewById(R.id.nShareTxt);

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

        cd = new ConnectionDetector(mActivity.getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // load postImage
        Picasso.with(mActivity).load("http://reportdatacenter.esy.es/process/postImage/"+feed.getPostImage())
                .into(mViewHolder.postImage);

        mViewHolder.nLikeTxt.setText(String.valueOf(feed.nLike));
        mViewHolder.nShareTxt.setText(String.valueOf(feed.nShare));

        // like Button
        if (mViewHolder!=null){
            mViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pos = nFeed.get(position).postName;
                    int p = nFeed.get(position).postID;
                /*if (p==feed.postID){
                    Toast.makeText(mActivity,pos,Toast.LENGTH_LONG).show();
                }*/
                if (p==p){
                    Toast.makeText(mActivity,pos,Toast.LENGTH_LONG).show();
                    mViewHolder.likeButton.setTag(position);
                    mViewHolder.likeButton.setTextColor(mActivity.getResources().getColor(R.color.post_5_color));
                }
                    //Toast.makeText(mActivity,pos,Toast.LENGTH_LONG).show();
                    //mViewHolder.likeButton.setTextColor(mActivity.getResources().getColor(R.color.post_5_color));
                }
            });
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

        final String a = mActivity.getResources().getString(R.string.b);
        final String b = mActivity.getResources().getString(R.string.c);
        final String[] choose = {a,b};

        mViewHolder.arrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setItems(choose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (choose[which].equals(a)){

                        }
                        else {
                            Intent intent = new Intent(mActivity,PostMapActivity.class);
                            intent.putExtra("lat",String.valueOf(nFeed.get(position).gpsLatitude));
                            intent.putExtra("lng",String.valueOf(nFeed.get(position).gpsLongitude));
                            mActivity.startActivity(intent);
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
        TextView nLikeTxt;
        TextView nShareTxt;
        Button likeButton;
        Button commentButton;
        Button shareButton;
        ImageView arrowDown;
    }
}
