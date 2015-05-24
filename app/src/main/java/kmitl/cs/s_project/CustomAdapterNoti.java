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
import java.util.List;

/**
 * Created by nadpon on 21/5/2558.
 */
public class CustomAdapterNoti extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Noti> nFeed;
    private ViewHolder mViewHolder;
    Activity mActivity;
    Noti feed;

    public CustomAdapterNoti(Activity activity, List<Noti> data){
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
            convertView = mInflater.inflate(R.layout.notification,parent,false);
            mViewHolder = new ViewHolder();

            mViewHolder.postImage = (ImageView) convertView.findViewById(R.id.postImage);
            mViewHolder.postName = (TextView) convertView.findViewById(R.id.postName);
            mViewHolder.status = (TextView) convertView.findViewById(R.id.statusName);
            mViewHolder.date = (TextView) convertView.findViewById(R.id.date);

            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        feed = nFeed.get(position);

        Picasso.with(mActivity).load("http://reportdatacenter.esy.es/process/postImage/"+feed.getPostImage())
                .resize(600,600).into(mViewHolder.postImage);

        mViewHolder.postName.setText(nFeed.get(position).postName);

        if (feed.getStatusID()==1){
            mViewHolder.status.setTextColor(mActivity.getResources().getColor(R.color.post_1_color));
        }
        else if (feed.getStatusID()==2){
            mViewHolder.status.setTextColor(mActivity.getResources().getColor(R.color.post_2_color));
        }
        else if (feed.getStatusID()==3){
            mViewHolder.status.setTextColor(mActivity.getResources().getColor(R.color.post_3_color));
        }
        else {
            mViewHolder.status.setTextColor(mActivity.getResources().getColor(R.color.post_4_color));
        }

        mViewHolder.status.setText("อัพเดท : "+feed.statusName);

        mViewHolder.date.setText(nFeed.get(position).updateDate);

        //click post image
        mViewHolder.postImage.setTag(position);
        mViewHolder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,PostInfoFullActivity.class);
                intent.putExtra("postID",String.valueOf(nFeed.get(position).postID));
                mActivity.startActivity(intent);
            }
        });

        return convertView;
    }

    private static class ViewHolder{
        ImageView postImage;
        TextView postName;
        TextView status;
        TextView date;
    }
}
