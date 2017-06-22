package com.relecotech.androidsparsh.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.AttendancePagerAdapter;

import java.util.ArrayList;

/**
 * Created by amey on 10/16/2015.
 */
public class AttendanceTeacherFragment extends android.support.v4.app.Fragment {

//    private MobileServiceJsonTable mobileServiceJsonTable;
    private SessionManager sessionManager;
    private Cursor schoolClassCursorData;
    private DatabaseHandler databaseHandler;
    private View rootView;
    private TabLayout attendanceTabLayout;
    private ViewPager attendanceViewPager;
    private ArrayList<Fragment> fragmentsList;
    private ArrayList<String> fragmentsNameList;

    FragmentManager fragmentManager;
    private Fragment mFragment;
    String user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHandler = new DatabaseHandler(getActivity());
        schoolClassCursorData = databaseHandler.getTeacherClassDataByCursor();
        System.out.println("schoolClassCursorData : " + schoolClassCursorData);
        System.out.println("schoolClassCursorData.getCount() : " + schoolClassCursorData.getCount());

        //getting saved data to check today's attendance is taken or not
        sessionManager = new SessionManager(getActivity());
        System.out.println("markedDate : " + sessionManager.getSharedPrefItem(SessionManager.KEY_LATEST_ATTENDANCE_MARK_DATE));

//        //initializing student table object
//        mobileServiceJsonTable = MainActivity.mClient.getTable("student");

        fragmentsList = new ArrayList<>();
        fragmentsNameList = new ArrayList<>();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.attendance_teacher_view, container, false);
        // Inflate the layout for this fragment
        attendanceTabLayout = (TabLayout) rootView.findViewById(R.id.attendance_tab_layout);
        attendanceViewPager = (ViewPager) rootView.findViewById(R.id.attendance_view_pager);
        user = MainActivity.userRole;
        setUpViewPager(attendanceViewPager);

        attendanceTabLayout.setupWithViewPager(attendanceViewPager);
        return rootView;
    }

    private void setUpViewPager(ViewPager viewPager) {
        AttendancePagerAdapter adapter = new AttendancePagerAdapter(getFragmentManager());

        adapter.addFragment(new AttendanceClassFragment(), "Class");
        adapter.addFragment(new AttendanceStudentFragment(), "Student");

        viewPager.setAdapter(adapter);

    }


    @Override
    public void onResume() {

        super.onResume();

        /* Below statment for changing Action Bar Title */
        //((MainActivity) getActivity()).setActionBarTitle("Alert");

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {


                    mFragment = new DashboardTeacherFragment();
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();

                    return true;

                }

                return false;
            }
        });
    }
}
