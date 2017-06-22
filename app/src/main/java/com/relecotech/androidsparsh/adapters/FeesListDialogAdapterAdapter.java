package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.FeesDialogListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amey on 5/23/2016.
 */
public class FeesListDialogAdapterAdapter extends BaseAdapter {

    protected List<FeesDialogListData> feesDialogList;
    Context context;
    LayoutInflater inflater;

    public FeesListDialogAdapterAdapter(Context context, ArrayList<FeesDialogListData> feesDialogList) {
        this.feesDialogList = feesDialogList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return feesDialogList.size();
    }

    @Override
    public FeesDialogListData getItem(int position) {
        return feesDialogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.fees_dialog_list_item, parent, false);

            holder.txtDialog_Comment = (TextView) convertView.findViewById(R.id.fees_amount_comment_textView);
            holder.txtDialog_Amount_Split = (TextView) convertView.findViewById(R.id.fees_amount_split_textView);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FeesDialogListData feesDialogListData = feesDialogList.get(position);
        holder.txtDialog_Comment.setText(feesDialogListData.getAmount_comment());
        holder.txtDialog_Amount_Split.setText("Rs." + feesDialogListData.getAmount_split());

        return convertView;
    }

    private class ViewHolder {
        TextView txtDialog_Comment;
        TextView txtDialog_Amount_Split;

    }
}
