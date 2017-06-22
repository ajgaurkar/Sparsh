package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.ReportListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajinkya on 10/20/2015.
 */
public class Report_subject_AdapterAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener {

    protected List<ReportListData> reportList;
    Context context;
    LayoutInflater inflater;

    public Report_subject_AdapterAdapter(Context context, List<ReportListData> subjectList) {
        this.reportList = subjectList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void updateResults(ArrayList<ReportListData> results) {
        reportList = results;
        //Triggers the list update
        notifyDataSetChanged();
    }


    public int getCount() {
        return reportList.size();
    }

    public ReportListData getItem(int position) {
        return reportList.get(position);
    }

    public long getItemId(int position) {
        return reportList.get(position).getNullId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.report_list_data, parent, false);


            holder.txtExam = (TextView) convertView.findViewById(R.id.exam);
            holder.txtMarks = (TextView) convertView.findViewById(R.id.marks);
            holder.txtGrades = (TextView) convertView.findViewById(R.id.grades);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        ReportListData reportListData = reportList.get(position);

        if (reportListData.isheader()) {
            holder.txtExam.setBackgroundColor(Color.parseColor("#90A4AE"));
            holder.txtMarks.setBackgroundColor(Color.parseColor("#90A4AE"));
            holder.txtGrades.setBackgroundColor(Color.parseColor("#90A4AE"));
            holder.txtExam.setTextColor(Color.WHITE);
            holder.txtMarks.setTextColor(Color.WHITE);
            holder.txtGrades.setTextColor(Color.WHITE);
        } else {
            holder.txtExam.setBackgroundColor(Color.WHITE);
            holder.txtMarks.setBackgroundColor(Color.WHITE);
            holder.txtGrades.setBackgroundColor(Color.WHITE);
            holder.txtExam.setTextColor(Color.parseColor("#414141"));
            holder.txtMarks.setTextColor(Color.parseColor("#414141"));
            holder.txtGrades.setTextColor(Color.parseColor("#414141"));
        }
        holder.txtExam.setText("" + reportListData.getSubject());
        holder.txtMarks.setText(reportListData.getMarks());
        holder.txtGrades.setText(reportListData.getGrades());

        return convertView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class ViewHolder {
        TextView txtExam;
        TextView txtMarks;
        TextView txtGrades;


    }

}
