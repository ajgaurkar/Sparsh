package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.LeavesListData;
import com.relecotech.androidsparsh.fragments.Parent_control;

import java.util.List;

/**
 * Created by amey on 10/16/2015.
 */
public class LeavesAdapterAdapter extends BaseAdapter {

    String user = Parent_control.loggedInUserForParentControl;
    Context context;

    LayoutInflater inflater;

    List<LeavesListData> leavesList;

    public LeavesAdapterAdapter(Context context, List<LeavesListData> leavesList) {
        this.context = context;
        this.leavesList = leavesList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return leavesList.size();
    }

    @Override
    public LeavesListData getItem(int position) {
        return leavesList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {

            holder = new ViewHolder();
            view = this.inflater.inflate(R.layout.leaves_list_item, viewGroup, false);

            holder.dateTextView = (TextView) view.findViewById(R.id.leavesDateTextView);
            holder.statusTextView = (TextView) view.findViewById(R.id.leavesStatusTextView);
            holder.titleTextView = (TextView) view.findViewById(R.id.leavesTitleTextView);
            holder.fromTextView = (TextView) view.findViewById(R.id.leavesperiodTextView);
            holder.toTextView = (TextView) view.findViewById(R.id.leavesToTextView);
            holder.studentNameTextView = (TextView) view.findViewById(R.id.studentNameTextView);
            holder.partition = view.findViewById(R.id.namePartition);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        LeavesListData leavesListData = leavesList.get(i);

        holder.dateTextView.setText(leavesListData.getLeavePostDate());
        holder.titleTextView.setText(leavesListData.getLeaveCause().replace("\\n","\n"));
//        holder.statusTextView.setTextColor(Color.parseColor("#F06103"));

        if (user.equals("Teacher")) {
            holder.studentNameTextView.setText(leavesListData.getStudentName());
        }
        if (user.equals("Student")) {
            holder.studentNameTextView.setText("");
            holder.partition.setVisibility(View.INVISIBLE);
        }

        holder.statusTextView.setText(leavesListData.getLeaveStatus());

        if (leavesListData.getLeaveStatus().equals("Approved")) {
            holder.statusTextView.setTextColor(Color.parseColor("#009688"));
        }
        if (leavesListData.getLeaveStatus().equals("Pending")) {
            holder.statusTextView.setTextColor(Color.parseColor("#8E8E8E"));
        }
        if (leavesListData.getLeaveStatus().equals("Denied")) {
            holder.statusTextView.setTextColor(Color.RED);
        }
        holder.fromTextView.setText(leavesListData.getLeaveStartDay() + "  To  " + leavesListData.getLeaveEndDay());
        //holder.toTextView.setText(leavesListData.getLeaveEndDay());

        return view;
    }

    private class ViewHolder {

        private TextView dateTextView;
        private TextView titleTextView;
        private TextView statusTextView;
        private TextView fromTextView;
        private TextView toTextView;
        private View partition;
        private TextView studentNameTextView;

    }
}

