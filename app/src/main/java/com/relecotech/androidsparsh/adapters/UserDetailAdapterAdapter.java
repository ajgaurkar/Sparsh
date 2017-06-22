package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.UserDetailListData;

import java.util.List;

/**
 * Created by amey on 5/2/2016.
 */

public class UserDetailAdapterAdapter extends BaseAdapter {
    protected List<UserDetailListData> userDetailList;
    Context context;
    LayoutInflater inflater;

    public UserDetailAdapterAdapter(Context context, List<UserDetailListData> userDetailList) {
        this.userDetailList = userDetailList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return userDetailList.size();
    }

    public UserDetailListData getItem(int position) {
        return userDetailList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.user_detail_list_item, parent, false);

            holder.userDetailDescription = (TextView) convertView.findViewById(R.id.userDetailDescriptioneditTextView);
            holder.userDetailTitle = (TextView) convertView.findViewById(R.id.userdetailTitleTextView);
            holder.userDetailImageView = (ImageView) convertView.findViewById(R.id.userDetailImageView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserDetailListData userDetailListData = userDetailList.get(position);

        holder.userDetailDescription.setText(userDetailListData.getDescription());
        holder.userDetailTitle.setText(userDetailListData.getTitle());
        holder.userDetailImageView.setImageResource(userDetailListData.getTitleimage());

        return convertView;
    }

    private class ViewHolder {
        TextView userDetailDescription;
        TextView userDetailTitle;
        ImageView userDetailImageView;

    }

}
