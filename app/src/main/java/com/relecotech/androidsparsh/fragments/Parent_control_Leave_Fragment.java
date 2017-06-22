package com.relecotech.androidsparsh.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.activities.ApproveLeave;
import com.relecotech.androidsparsh.adapters.LeavesAdapterAdapter;
import com.relecotech.androidsparsh.controllers.LeavesListData;

import java.util.List;

/**
 * Created by amey on 8/6/2016.
 */
public class Parent_control_Leave_Fragment extends Fragment {
    ListView parentcontrolListView;
    private List<LeavesListData> getLeaveListData;
    private LeavesAdapterAdapter parentControlLeaveAdapter;
    private LeavesListData selectedLeavelistData;

    SessionManager sessionManager;
    private RelativeLayout leavesAccessRestrictorLayout;
    private TextView parentcontrolNoDataAvailable_Leave_TextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.parent_control_fragment, container, false);
        parentcontrolListView = (ListView) rootView.findViewById(R.id.parent_control_listView);
//        leavesAccessRestrictorLayout = (RelativeLayout) rootView.findViewById(R.id.leaves_access_restrictor_layout);
        parentcontrolNoDataAvailable_Leave_TextView = (TextView) rootView.findViewById(R.id.no_data_available_prent_control_textView);

        //  leavesAccessRestrictorLayout.setVisibility(View.INVISIBLE);
        Bundle getbundledata = this.getArguments();
        getLeaveListData = (List<LeavesListData>) getbundledata.getSerializable("LeaveList");

        sessionManager = new SessionManager(getActivity());
//        if (sessionManager.getSharedPrefItem(SessionManager.KEY_SCHOOL_CLASS_ID).equalsIgnoreCase("null")) {
//            leavesAccessRestrictorLayout.setVisibility(View.VISIBLE);
//        } else {
//            leavesAccessRestrictorLayout.setVisibility(View.INVISIBLE);

        try {

            if (!getLeaveListData.isEmpty() || getLeaveListData != null) {
                System.out.println("Value---------------------------" + getLeaveListData.size());
                parentControlLeaveAdapter = new LeavesAdapterAdapter(getActivity(), getLeaveListData);
                parentcontrolListView.setAdapter(parentControlLeaveAdapter);

            } else {
                System.out.println("Parents Zone Leave List  is NULLL");
                parentcontrolNoDataAvailable_Leave_TextView.setVisibility(View.VISIBLE);
                parentcontrolNoDataAvailable_Leave_TextView.setText(R.string.noDataAvailable);

            }
        } catch (Exception e) {
            System.out.println("getMassage---Leave Fragments----" + e.getMessage());
            parentcontrolNoDataAvailable_Leave_TextView.setVisibility(View.VISIBLE);
            parentcontrolNoDataAvailable_Leave_TextView.setText(R.string.noDataAvailable);

        }

        parentcontrolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedLeavelistData = getLeaveListData.get(position);
                Intent leaveApproveIntent = new Intent(getActivity(), ApproveLeave.class);
                Bundle leaveBundle = new Bundle();
                leaveBundle.putString("StudentName", selectedLeavelistData.getStudentName());
                leaveBundle.putString("StudentRollNo", selectedLeavelistData.getStudentRollNo());
                leaveBundle.putString("StudentId", selectedLeavelistData.getStudentId());
                leaveBundle.putString("Status", selectedLeavelistData.getLeaveStatus());
                leaveBundle.putString("StartDay", selectedLeavelistData.getLeaveStartDay());
                leaveBundle.putString("EndDay", selectedLeavelistData.getLeaveEndDay());
                leaveBundle.putString("Cause", selectedLeavelistData.getLeaveCause());
                leaveBundle.putString("PostDate", selectedLeavelistData.getLeavePostDate());
                leaveBundle.putString("Reply", selectedLeavelistData.getLeaveReply());
                leaveBundle.putInt("DaysCount", selectedLeavelistData.getLeaveDayCount());
                leaveBundle.putString("LeaveId", selectedLeavelistData.getLeaveId());
                leaveApproveIntent.putExtras(leaveBundle);
                startActivity(leaveApproveIntent);

            }
        });


        return rootView;
    }
}
