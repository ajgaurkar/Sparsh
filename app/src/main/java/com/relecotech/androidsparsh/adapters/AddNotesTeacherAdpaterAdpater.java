package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.AddNotesTeacherListData;

import java.util.List;

/**
 * Created by amey on 2/17/2016.
 */
public class AddNotesTeacherAdpaterAdpater extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    List<AddNotesTeacherListData> spinnerTeacherLists;

    public AddNotesTeacherAdpaterAdpater(Context context, List<AddNotesTeacherListData> spinnerTeacherLists) {
        this.spinnerTeacherLists = spinnerTeacherLists;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return spinnerTeacherLists.size();
    }

    @Override
    public AddNotesTeacherListData getItem(int position) {

        return spinnerTeacherLists.get(position);
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

        AddNotesTeacherListData spinnerTeacherListObj = spinnerTeacherLists.get(position);

        holder.teacherNameTextView.setText(spinnerTeacherListObj.getMessage());

        return convertView;

    }


    private class ViewHolder {

        private TextView teacherNameTextView;

    }

}
