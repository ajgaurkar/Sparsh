package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.AssApproveListData;

import java.util.List;

/**
 * Created by amey on 10/16/2015.
 */
public class AssApproveAdapterAdapter extends BaseAdapter {
    protected List<AssApproveListData> assApproveList;
    Context context;
    LayoutInflater inflater;

    public AssApproveAdapterAdapter(Context context, List<AssApproveListData> assApproveList) {
        this.assApproveList = assApproveList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return assApproveList.size();
    }

    public AssApproveListData getItem(int position) {
        return assApproveList.get(position);
    }

    public long getItemId(int position) {
        return assApproveList.get(position).getNullId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.ass_approve_list_item, parent, false);

            holder.rollNoTextView = (TextView) convertView.findViewById(R.id.approvalRollNoTextView);
            holder.NameTextView = (TextView) convertView.findViewById(R.id.approvalNameTextView);
            holder.statusTextView = (TextView) convertView.findViewById(R.id.approvalStatusTextView);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AssApproveListData assApproveListData = assApproveList.get(position);
        holder.NameTextView.setText(assApproveListData.getfName() + " " + assApproveListData.getlName());
        holder.rollNoTextView.setText("" + assApproveListData.getRollNo());
//        holder.statusTextView.setText(assApproveListData.getStatus());
        switch (assApproveListData.getStatus()) {

            case "Approved":
                holder.statusTextView.setBackgroundResource(R.drawable.green_circle_bg);
                holder.statusTextView.setText("A");
                break;

            case "Overdue":
                holder.statusTextView.setBackgroundResource(R.drawable.orange_circe_bg);
                holder.statusTextView.setText("O");
                break;

            case "Re-Submit":
                holder.statusTextView.setBackgroundResource(R.drawable.red_circe_bg);
                holder.statusTextView.setText("R");
                break;

            case "Pending":
                holder.statusTextView.setBackgroundResource(R.drawable.blue_circle_bg);
                holder.statusTextView.setText("P");
                break;

        }

        return convertView;
    }

    private class ViewHolder {
        TextView rollNoTextView;
        TextView NameTextView;
        TextView statusTextView;
    }

}
