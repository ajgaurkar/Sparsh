package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.AssListData;
import com.relecotech.androidsparsh.fragments.AssignmentFragment;

import java.util.List;

/**
 * Created by amey on 10/16/2015.
 */
public class AssignListAdpater extends BaseAdapter {
    protected List<AssListData> listAssListDatas;
    Context context;
    String user = AssignmentFragment.loggedInUserForAssignmentListAdapter;
    LayoutInflater inflater;
    String scoreType;



    public AssignListAdpater(Context context, List<AssListData> listAssListDatas) {
        this.listAssListDatas = listAssListDatas;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return listAssListDatas.size();
    }

    public AssListData getItem(int position) {
        return listAssListDatas.get(position);
    }

    public long getItemId(int position) {
        return listAssListDatas.get(position).getDrawableId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (user.equals("Teacher")) {
        }
        if (user.equals("Student")) {
        }
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.assignment_list_item, parent, false);

            holder.txtSubject = (TextView) convertView.findViewById(R.id.txt_subject);
            holder.txtSubmittedBy = (TextView) convertView.findViewById(R.id.ass_Submited_By);
            holder.txtDuedate = (TextView) convertView.findViewById(R.id.txt_Submitdate);
            holder.txtissuedate = (TextView) convertView.findViewById(R.id.txt_idate);
            holder.txtassDescription = (TextView) convertView.findViewById(R.id.txt_assignmentDescript);

            holder.creditsStdntNumerator = (TextView) convertView.findViewById(R.id.creditsNumeratorTextView);
            holder.attachemntStatusIcon = (ImageView) convertView.findViewById(R.id.attachementStatusImgView);
            holder.creditsStdntDenominator = (TextView) convertView.findViewById(R.id.creditsDenominatorTextView);
            holder.teacherCreditNotation = (TextView) convertView.findViewById(R.id.teacher_credits_textView);
            holder.markText_view = (TextView) convertView.findViewById(R.id.markText_text_view);

            holder.teacherCreditlayout = (RelativeLayout) convertView.findViewById(R.id.teacher_credits_panel);
            holder.studentCreditlayout = (RelativeLayout) convertView.findViewById(R.id.student_credits_panel);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AssListData assListData = listAssListDatas.get(position);
        holder.txtSubject.setText(assListData.getSubject());
        holder.txtDuedate.setText(assListData.getDueDate());
        holder.txtissuedate.setText(assListData.getIssueDate());
        holder.txtSubmittedBy.setText("Submitted by : " + assListData.getSubmittedBy());
        holder.txtassDescription.setText(assListData.getDescription().replace("\\n", "\n").replace("\\",""));
        String scoreType = assListData.getScoreType();
        System.out.println("scoreType : " + scoreType);

        if (assListData.getAttachmentCount().toString().equals("0")) {
            holder.attachemntStatusIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.attachemntStatusIcon.setVisibility(View.VISIBLE);
        }

        if (user.equals("Teacher")) {
            holder.teacherCreditlayout.setVisibility(View.VISIBLE);
            holder.studentCreditlayout.setVisibility(View.INVISIBLE);
            holder.txtSubmittedBy.setVisibility(View.INVISIBLE);


            if (scoreType.equals("Grades")) {

                holder.teacherCreditNotation.setText("Grade");
                holder.teacherCreditNotation.setTextSize(12);
                holder.markText_view.setText("");

            } else if (scoreType.equals("Marks")) {

                holder.teacherCreditNotation.setText(assListData.getMaxCredits());
                holder.markText_view.setText("Marks");
                holder.markText_view.setTextSize(8);
            }
        }
        if (user.equals("Student")) {
            holder.studentCreditlayout.setVisibility(View.VISIBLE);
            holder.teacherCreditlayout.setVisibility(View.INVISIBLE);


            if (scoreType.equals("Grades")) {

                holder.creditsStdntNumerator.setText(assListData.getGradeEarned());
                holder.creditsStdntDenominator.setText("Grade");
                holder.creditsStdntDenominator.setTextSize(10);

            } else if (scoreType.equals("Marks")) {

                holder.creditsStdntNumerator.setText(assListData.getCreditsEarned());
                holder.creditsStdntDenominator.setText(assListData.getMaxCredits());
            }
        }



        return convertView;
    }


    private class ViewHolder {
        TextView txtSubject;
        TextView txtDuedate;
        TextView txtissuedate;
        TextView txtassDescription;
        TextView creditsStdntNumerator;
        TextView creditsStdntDenominator;
        TextView teacherCreditNotation;
        TextView markText_view;
        ImageView attachemntStatusIcon;
        TextView txtSubmittedBy;
        RelativeLayout teacherCreditlayout;
        RelativeLayout studentCreditlayout;

    }

}
