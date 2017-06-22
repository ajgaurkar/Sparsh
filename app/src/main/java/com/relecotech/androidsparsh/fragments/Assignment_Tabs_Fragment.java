package com.relecotech.androidsparsh.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.activities.AssignmentDetail;
import com.relecotech.androidsparsh.adapters.AssignListAdpater;
import com.relecotech.androidsparsh.controllers.AssListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amey on 8/20/2016.
 */
public class Assignment_Tabs_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private AssignmentFragment assignmentFragment = null;
    ListView assignment_ListView;
    private AssignListAdpater assignment_adapter;
    private List<AssListData> assignmentlist;
    private AssListData assignmentListData;
    private TextView assignmentNoDataAvailable_TextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public Assignment_Tabs_Fragment() {
        this.assignmentFragment = assignmentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.assignment_tabs_fragment, container, false);
        assignment_ListView = (ListView) rootView.findViewById(R.id.assignment_listView);
        assignmentNoDataAvailable_TextView = (TextView) rootView.findViewById(R.id.assignmentTabNoDataAvailable_textView);
        System.out.println("*************************Assignment_Tabs_Fragment*******************************");
        Bundle bundle = this.getArguments();
        assignmentlist = new ArrayList<>();
        assignmentlist = (List<AssListData>) bundle.getSerializable("assmntList");

        if (assignmentlist == null) {
            System.out.println("Assignment  list is NULL");
//            assignmentNoDataAvailable_TextView.setText(Html.fromHtml("<i><center><font color=\"#0d3e60\"> No data Available </font></center></i>"));
            assignmentNoDataAvailable_TextView.setText(R.string.noDataAvailable);

        } else {
            assignment_adapter = new AssignListAdpater(getActivity(), assignmentlist);
            assignment_ListView.setAdapter(assignment_adapter);
        }


        assignment_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                assignmentListData = assignmentlist.get(position);
                Intent assDetailIntent = new Intent(getActivity(), AssignmentDetail.class);
                Bundle intentBundle = new Bundle();
                intentBundle.putString("AssignId", assignmentListData.getAssId());
                intentBundle.putString("AssignStatus", assignmentListData.getAssStatus());
                intentBundle.putString("AssignAttachCount", assignmentListData.getAttachmentCount());
                intentBundle.putString("AssignMaxCredits", assignmentListData.getMaxCredits());
                System.out.println("before submitting assignmentPendingList 1---------------------- " + assignmentListData.getCreditsEarned());
                intentBundle.putString("AssignDescription", assignmentListData.getDescription());
                intentBundle.putString("AssignSubject", assignmentListData.getSubject());
                intentBundle.putString("AssignIssueDate", assignmentListData.getIssueDate());
                intentBundle.putString("AssignSubmittedBy", assignmentListData.getSubmittedBy());
                intentBundle.putString("AssignDivision", assignmentListData.getDivision());
                intentBundle.putString("AssignClassStd", assignmentListData.getClassStd());
                intentBundle.putString("AssignGradeEarned", assignmentListData.getGradeEarned());
                intentBundle.putString("AssignScoreType", assignmentListData.getScoreType());
                intentBundle.putString("AssignDueDate", assignmentListData.getDueDate());
                intentBundle.putString("AssignStatusComment", assignmentListData.getNote());
                assDetailIntent.putExtras(intentBundle);
                startActivity(assDetailIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onRefresh() {
//        assignmentFragment.reScheduleTimer();
    }
}
