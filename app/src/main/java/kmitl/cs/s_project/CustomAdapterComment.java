package kmitl.cs.s_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.List;

/**
 * Created by nadpon on 22/5/2558.
 */
public class CustomAdapterComment extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<comment> nFeed;
    private ViewHolder mViewHolder;
    Activity mActivity;
    comment feed;
    InputStream is = null;
    String js_result = "";

    public CustomAdapterComment(Activity activity, List<comment> data){
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
            convertView = mInflater.inflate(R.layout.comment,parent,false);
            mViewHolder = new ViewHolder();
            mViewHolder.userImage = (ImageView) convertView.findViewById(R.id.userImage);
            mViewHolder.commentTxt = (TextView) convertView.findViewById(R.id.commentTxt);
            mViewHolder.date = (TextView) convertView.findViewById(R.id.date);

            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        feed = nFeed.get(position);

        // load userImage
        Picasso.with(mActivity).load("http://reportdatacenter.esy.es/process/userImage/"+feed.getDisplayImage())
                .transform(new CircleTransform()).into(mViewHolder.userImage);

        mViewHolder.commentTxt.setText(feed.comment);

        mViewHolder.date.setText(feed.commentDate);

        mViewHolder.userImage.setTag(position);
        mViewHolder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int uId = nFeed.get(position).userID;

                Intent intent = new Intent(mActivity,PersonalActivity.class);
                intent.putExtra("uId",uId);
                mActivity.startActivity(intent);
            }
        });

        return convertView;
    }

    public static class ViewHolder{
        ImageView userImage;
        TextView commentTxt;
        TextView date;
    }
}
