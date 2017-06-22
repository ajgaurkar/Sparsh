package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.AlertListData;
import com.relecotech.androidsparsh.fragments.AlertFragment;

import java.util.List;


public class AlertAdapterAdapter extends BaseAdapter {

    protected List<AlertListData> alertList;
    Context context;
    String user = AlertFragment.loggedInUserForAlertListAdapter;
    LayoutInflater inflater;

    public AlertAdapterAdapter(Context context, List<AlertListData> alertList) {
        this.alertList = alertList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return alertList.size();
    }

    public AlertListData getItem(int position) {
        return alertList.get(position);
    }

    public long getItemId(int position) {
        return alertList.get(position).getNullId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;


        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.alert_list_item, parent, false);

            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_Date);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txt_Title);
            holder.txtBody = (TextView) convertView.findViewById(R.id.txt_Body);
            holder.txtSubmited_by_or_to = (TextView) convertView.findViewById(R.id.txt_Submited_By_or_to);
            holder.imageViewAttachment = (ImageView) convertView.findViewById(R.id.attachementStatusImgView);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AlertListData alertListData = alertList.get(position);
        holder.txtDate.setText(alertListData.getIssueDate());
        holder.txtTitle.setText(alertListData.getTitle().replace("\\n", "\n"));
        holder.txtBody.setText(alertListData.getBody().replace("\\n", "\n"));

        System.out.println("Others-------------alertListData.getTag()" + alertListData.getAlert_priority());

        if (alertListData.getAlert_priority().contains("Urgent")) {
            System.out.println("Others--------------yes got others Alert");
//            holder.txtDate.setBackgroundResource(R.drawable.alert_emergency_date_bg);
            holder.txtDate.setBackgroundResource(R.drawable.assignment_date_bg);
            holder.txtTitle.setBackgroundResource(R.drawable.alert_emergency_title_bg_red);
        } else {
            System.out.println("Others--------------not in otherss");
            holder.txtDate.setBackgroundResource(R.drawable.assignment_date_bg);
            holder.txtTitle.setBackgroundResource(R.drawable.ass_title_bg_blue);
        }


        if (alertListData.getAttachmentCount().toString().equals("0") || alertListData.getAttachmentCount().toString().equals("null")) {
            holder.imageViewAttachment.setVisibility(View.INVISIBLE);
        } else {
            holder.imageViewAttachment.setVisibility(View.VISIBLE);
        }

        if (user.equals("Student")) {
            holder.txtSubmited_by_or_to.setText("Submitted by : " + alertListData.getSubmitted_By_to());
        }
        if (user.equals("Teacher")) {
            if (alertListData.getAlertDivision().equals("All")) {
                holder.txtSubmited_by_or_to.setText("Submitted to class " + alertListData.getAlertClass() + " - All divisions");
                System.out.println("Submitted to class " + alertListData.getAlertClass() + " - All divisions");
            } else if (alertListData.getAlertStudId().equals("All")) {
                holder.txtSubmited_by_or_to.setText("Submitted to class " + alertListData.getAlertClass() + " " + alertListData.getAlertDivision() + " - All students");
                System.out.println("Submitted to class " + alertListData.getAlertClass() + " " + alertListData.getAlertDivision() + " - All students");

            } else {
                holder.txtSubmited_by_or_to.setText("Submitted to " + alertListData.getAlertClass() + " " + alertListData.getAlertDivision() + " - " + alertListData.getAlertStudent());
                System.out.println("Submitted to " + alertListData.getAlertClass() + " " + alertListData.getAlertDivision() + " - " + alertListData.getAlertStudent());

            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView txtDate;
        TextView txtTitle;
        TextView txtBody;
        TextView txtSubmited_by_or_to;
        ImageView imageViewAttachment;
    }

}