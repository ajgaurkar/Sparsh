package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.NoticeListData;
import com.relecotech.androidsparsh.fragments.Notifications;

import java.util.List;

/**
 * Created by amey on 10/16/2015.
 */
public class NotificationAdapterAdapter extends BaseAdapter {

    protected List<NoticeListData> noticeList;
    Context context;

    String user = Notifications.loggedInUserForNotifications;
    LayoutInflater inflater;

    public NotificationAdapterAdapter(Context context, List<NoticeListData> noticeList) {
        this.noticeList = noticeList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return noticeList.size();
    }

    public NoticeListData getItem(int position) {
        return noticeList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (user.equals("Teacher")) {

        }

        if (user.equals("Student")) {

        }
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.notification_list_item, parent, false);

            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_Date);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txt_Title);
            holder.txtBody = (TextView) convertView.findViewById(R.id.txt_Body);
            holder.txtSubmited_by = (TextView) convertView.findViewById(R.id.txt_Submited_By_or_to);
            holder.imageView_tag = (ImageView) convertView.findViewById(R.id.notifaction_tag_imageView);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        NoticeListData noticeListData = noticeList.get(position);
        holder.txtDate.setText(noticeListData.getNotifaction_Post_Date());
        holder.txtTitle.setText(noticeListData.getNotifaction_Tag());
        holder.txtBody.setText(noticeListData.getNotification_Message_Body());
        System.out.println("noticeListData.getSubmittedBy() Adapter Adapter  " + noticeListData.getNotifaction_SubmittedBy());
        holder.txtSubmited_by.setText("Submitted by : " + noticeListData.getNotifaction_SubmittedBy());

        if (noticeListData.getNotifaction_Tag().equals("Assignment")) {
            System.out.println("**************************************Assignment");
            holder.imageView_tag.setImageResource(R.drawable.assignment_notification_96);
        } else if (noticeListData.getNotifaction_Tag().equals("Leave")) {
            System.out.println("**************************************Leave");
            holder.imageView_tag.setImageResource(R.drawable.leave_notification_96);
        } else if (noticeListData.getNotifaction_Tag().equals("Notes")) {
            System.out.println("**************************************Notes");
            holder.imageView_tag.setImageResource(R.drawable.notes_notification_96);

        }
        return convertView;
    }

    private class ViewHolder {
        TextView txtDate;
        TextView txtTitle;
        TextView txtBody;
        TextView txtSubmited_by;
        ImageView imageView_tag;
    }

}
