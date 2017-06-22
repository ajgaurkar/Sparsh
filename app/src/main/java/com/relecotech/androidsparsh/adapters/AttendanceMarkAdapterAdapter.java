package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.AttendanceMarkListData;

import java.util.List;

/**
 * Created by amey on 10/16/2015.
 */
public class AttendanceMarkAdapterAdapter extends BaseAdapter {
    protected List<AttendanceMarkListData> attendancemarkList;
    Context context;
    LayoutInflater inflater;

    public AttendanceMarkAdapterAdapter(Context context, List<AttendanceMarkListData> attendancemarkList) {
        this.attendancemarkList = attendancemarkList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return attendancemarkList.size();
    }

    public AttendanceMarkListData getItem(int position) {
        return attendancemarkList.get(position);
    }

    public long getItemId(int i) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.attendance_mark_list_item, parent, false);

            holder.attendanceRollNoTextView = (TextView) convertView.findViewById(R.id.attendanceRollNoTextView);
            holder.studentNameTextView = (TextView) convertView.findViewById(R.id.attendanceStdNameTextView);
            holder.presentStatusTextView = (TextView) convertView.findViewById(R.id.attendanceStatusTextView);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AttendanceMarkListData attendanceMarkListData = attendancemarkList.get(position);
        Log.d("AttendanceAdptrAdptrnme", attendanceMarkListData.getFullName());
        Log.d("AttendanceAdptrRoll", attendanceMarkListData.getRollNo());
        Log.d("AttendanceAdptrStatus", attendanceMarkListData.getPresentStatus());

        holder.studentNameTextView.setText(attendanceMarkListData.getFullName());
        holder.attendanceRollNoTextView.setText("" + attendanceMarkListData.getRollNo());

        switch (attendanceMarkListData.getPresentStatus()) {

            case "P":
                holder.presentStatusTextView.setText(attendanceMarkListData.getPresentStatus());
                holder.presentStatusTextView.setBackgroundResource(R.drawable.green_circle_bg);
                break;
            case "A":
                holder.presentStatusTextView.setText(attendanceMarkListData.getPresentStatus());
                holder.presentStatusTextView.setBackgroundResource(R.drawable.red_circe_bg);
                break;
            case "U":
                holder.presentStatusTextView.setText("");
                holder.presentStatusTextView.setBackgroundResource(R.drawable.grey_empty_attendance_circle_bg);
                break;

        }

        return convertView;
    }

    private class ViewHolder {
        TextView attendanceRollNoTextView;
        TextView studentNameTextView;
        TextView presentStatusTextView;
    }

}
