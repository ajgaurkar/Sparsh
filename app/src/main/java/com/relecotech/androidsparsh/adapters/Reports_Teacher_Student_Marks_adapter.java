package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.ReportsTeacherListOfStudentMarksData;

import java.util.List;

/**
 * Created by ajinkya on 10/29/2015.
 */
public class Reports_Teacher_Student_Marks_adapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<ReportsTeacherListOfStudentMarksData> reportsTeacherListOfStudentMarksDatas;

    public Reports_Teacher_Student_Marks_adapter(Context context, List<ReportsTeacherListOfStudentMarksData> reportsTeacherListOfStudentMarksDatas) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.reportsTeacherListOfStudentMarksDatas = reportsTeacherListOfStudentMarksDatas;
    }

    @Override
    public int getCount() {
        return reportsTeacherListOfStudentMarksDatas.size();
    }

    @Override
    public ReportsTeacherListOfStudentMarksData getItem(int position) {
        return reportsTeacherListOfStudentMarksDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return reportsTeacherListOfStudentMarksDatas.get(position).getNullId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = this.inflater.inflate(R.layout.reports_teacher_student_mark_list_item, parent, false);

            holder.nameTextView = (TextView) convertView.findViewById(R.id.markListStudentNameTextView);
            holder.rollNoTextView = (TextView) convertView.findViewById(R.id.markListStudentRollNoTextView);
            holder.scoreTextView = (TextView) convertView.findViewById(R.id.markListStudentScoreTextView);
//            holder.outOfTextView = (TextView) convertView.findViewById(R.id.markListStudentOutOfTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ReportsTeacherListOfStudentMarksData reportsTeacherListOfStudentMarksData = reportsTeacherListOfStudentMarksDatas.get(position);

        holder.rollNoTextView.setText("" + reportsTeacherListOfStudentMarksData.getRollNo());
        holder.nameTextView.setText("" + reportsTeacherListOfStudentMarksData.getStudentName());
        holder.scoreTextView.setText(reportsTeacherListOfStudentMarksData.getGrades());
//        holder.outOfTextView.setText(String.valueOf(reportsTeacherListOfStudentMarksData.getOutOff()));

        return convertView;
    }

    private class ViewHolder {
        TextView rollNoTextView;
        TextView nameTextView;
        TextView scoreTextView;
//        TextView outOfTextView;
    }
}
