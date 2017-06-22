package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.RewardsRatingListData;

import java.util.List;

/**
 * Created by amey on 10/16/2015.
 */
public class RewardsRatingAdapterAdapter extends BaseAdapter {

    protected List<RewardsRatingListData> ratingSportsList;
    Context context;
    LayoutInflater inflater;



    public RewardsRatingAdapterAdapter(Context context, List<RewardsRatingListData> ratingSportsList) {
        this.ratingSportsList = ratingSportsList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return ratingSportsList.size();
    }

    public RewardsRatingListData getItem(int position) {
        return ratingSportsList.get(position);
    }

    public long getItemId(int position) {
        return ratingSportsList.get(position).getNullId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.rewards_list_item, parent, false);

            holder.eventTitle = (TextView) convertView.findViewById(R.id.txt_event_Title);
            holder.eventdate = (TextView) convertView.findViewById(R.id.txt_event_date);
            holder.eventRating = (RatingBar) convertView.findViewById(R.id.rewards_rating_bar);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RewardsRatingListData rewardsRatingListData = ratingSportsList.get(position);
        holder.eventTitle.setText(rewardsRatingListData.getRewardsEventTitle());
        holder.eventdate.setText("" + rewardsRatingListData.getRewardsEventDate());
        holder.eventRating.setRating(rewardsRatingListData.getRewardsRating());

        return convertView;
    }

    private class ViewHolder {
        TextView eventTitle;
        TextView eventdate;
        RatingBar eventRating;
    }

}
