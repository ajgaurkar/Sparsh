package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.ReportExamListData;

import java.util.List;

/**
 * Created by ajinkya on 10/20/2015.
 */
public class Report_student_exam_AdapterAdapter extends BaseAdapter {
    protected List<ReportExamListData> report_examList;
    Context context;
    boolean checkheader;
    LayoutInflater inflater;

    public Report_student_exam_AdapterAdapter(Context context, List<ReportExamListData> feesList) {
        this.report_examList = feesList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return report_examList.size();
    }

    public ReportExamListData getItem(int position) {
        return report_examList.get(position);
    }

    public long getItemId(int position) {
        return report_examList.get(position).getNullId();
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


        ReportExamListData reportListData = report_examList.get(position);
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
        holder.txtExam.setText("" + reportListData.getExam());
        holder.txtMarks.setText(reportListData.getMarks());
        holder.txtGrades.setText(reportListData.getGrades());

        return convertView;
    }

    private class ViewHolder {
        TextView txtExam;
        TextView txtMarks;
        TextView txtGrades;


    }
}
