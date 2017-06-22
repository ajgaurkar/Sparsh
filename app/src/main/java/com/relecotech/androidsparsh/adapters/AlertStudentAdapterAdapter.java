package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.AlertStudentListData;

import java.util.List;

/**
 * Created by amey on 3/4/2016.
 */
public class AlertStudentAdapterAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    List<AlertStudentListData> spinnerStudentLists;

    public AlertStudentAdapterAdapter(Context context, List<AlertStudentListData> spinnerStudentLists) {
        this.spinnerStudentLists = spinnerStudentLists;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return spinnerStudentLists.size();
    }

    @Override
    public AlertStudentListData getItem(int position) {

        return spinnerStudentLists.get(position);
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
            convertView = this.inflater.inflate(R.layout.spinner_dropdown_item_for_custom_adapter, parent, false);
            holder.teacherNameTextView = (TextView) convertView.findViewById(R.id.text_drop_down);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AlertStudentListData spinnerStudentListObj = spinnerStudentLists.get(position);

        holder.teacherNameTextView.setText(spinnerStudentListObj.getFullname());

        return convertView;

    }


    private class ViewHolder {

        private TextView teacherNameTextView;

    }

}
