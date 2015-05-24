package kmitl.cs.s_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by nadpon on 29/4/2558.
 */
public class CustomAdapterGridview extends BaseAdapter {
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;
    Activity mActivity;
    List<Personal> personals;
    Personal personal;

    public CustomAdapterGridview(Activity activity, List<Personal> data) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        personals = data;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return personals.size();
    }

    @Override
    public Object getItem(int position) {
        return personals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.post_image_gridview,parent,false);
            mViewHolder = new ViewHolder();
            mViewHolder.postImage = (ImageView) convertView.findViewById(R.id.postImage);
            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        personal = personals.get(position);

        int radius = 30;
        int stroke = 5;
        int margin = 5;

        Picasso.with(mActivity).load("http://reportdatacenter.esy.es/process/postImage/"+personal.getPostImage())
                .transform(new RoundedRectTransformation(radius, stroke, margin)).resize(200,200).into(mViewHolder.postImage);

        mViewHolder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int postID = personals.get(position).postID;

                Intent intent = new Intent(mActivity,PostInfoFullActivity.class);
                intent.putExtra("postID",String.valueOf(postID));
                mActivity.startActivity(intent);
            }
        });

        return convertView;
    }
    private static class ViewHolder{
        ImageView postImage;
    }
}
