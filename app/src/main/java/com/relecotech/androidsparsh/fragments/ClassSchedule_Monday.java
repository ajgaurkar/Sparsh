package com.relecotech.androidsparsh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.Class_Schedule_AdapterAdapter;
import com.relecotech.androidsparsh.controllers.StudentTimeTableListData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amey on 6/10/2016.
 */
public class ClassSchedule_Monday extends Fragment {
    private Map<Integer, StudentTimeTableListData> slotWiseMap;
    private long timeDifference;
    private Date start_time;
    private Date end_time;
    private String subject;
    private String subject_teacher_name;
    private String slot_type;
    private String userRole;
    private String teacher_class_division;
    //    ArrayList<StudentTimeTableListData> studentTimeTableList;
    ArrayList<StudentTimeTableListData> studentTimeTableList;
    ListView timeTableListView;
    Class_Schedule_AdapterAdapter class_schedule_adapterAdapter;
    private TextView noDataAvailableTextView;
    private ArrayAdapter<String> stringArrayAdapter;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private static long DaysDifference;

    private Map map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentTimeTableList = new ArrayList<>();
        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.class_schedule_timetable_fragment, container, false);

        timeTableListView = (ListView) rootView.findViewById(R.id.time_table_listView);
        noDataAvailableTextView = (TextView) rootView.findViewById(R.id.no_data_available_text_view);
        timeTableListView = (ListView) rootView.findViewById(R.id.time_table_listView);
        noDataAvailableTextView = (TextView) rootView.findViewById(R.id.no_data_available_text_view);
        studentTimeTableList.clear();
        slotWiseMap = Class_ScheduleFragment.studentTimeTableHashMap.get("Monday");
        System.out.println("slotWiseMap  Monday " + slotWiseMap);


        if (slotWiseMap != null) {
            noDataAvailableTextView.setVisibility(View.INVISIBLE);


            if (userRole.equals("Student")) {
                try {
                    System.out.println("userRole       :: " + "Student");
                    for (Map.Entry<Integer, StudentTimeTableListData> entrys : slotWiseMap.entrySet()) {
                        System.out.println("Slot No        :: " + entrys.getKey());
                        System.out.println("Start Time     :: " + entrys.getValue().getSlot_StartTime());
                        System.out.println("End Time       :: " + entrys.getValue().getSlot_EndTime());
                        System.out.println("Subject        :: " + entrys.getValue().getSlot_Subject());
                        System.out.println("teacher name        :: " + entrys.getValue().getSubject_teacher_name());
                        System.out.println("slot type        :: " + entrys.getValue().getSlot_type());


                        start_time = entrys.getValue().getSlot_StartTime();
                        end_time = entrys.getValue().getSlot_EndTime();
                        subject = entrys.getValue().getSlot_Subject();
                        subject_teacher_name = entrys.getValue().getSubject_teacher_name();
                        slot_type = entrys.getValue().getSlot_type();
                        teacher_class_division = null;


                        studentTimeTableList.add(new StudentTimeTableListData(start_time, end_time, subject, subject_teacher_name, slot_type, teacher_class_division, false));

                    }
                } catch (Exception e) {
                    System.out.println("Exception---------" + e.getMessage());
                }
            }
            if (userRole.equals("Teacher")) {
                System.out.println("userRole       :: " + "Teacher");


                try {
                    Map.Entry<Integer, StudentTimeTableListData> entry = slotWiseMap.entrySet().iterator().next();
                    Integer key = entry.getKey();

                    System.out.println("Monday First Key___________" + key);


                    Date prev_endTime = slotWiseMap.get(key).getSlot_StartTime();
                    System.out.println("prev_endTime 1 **********************************" + prev_endTime);


                    for (Map.Entry<Integer, StudentTimeTableListData> entrys : slotWiseMap.entrySet()) {

                        start_time = entrys.getValue().getSlot_StartTime();
                        end_time = entrys.getValue().getSlot_EndTime();
                        subject = entrys.getValue().getSlot_Subject();
                        subject_teacher_name = null;
                        slot_type = entrys.getValue().getSlot_type();
                        teacher_class_division = entrys.getValue().getTeacher_class_division();

                        System.out.println("Data to be send to date difffenece method is " + start_time + "" +
                                "::" + prev_endTime);
                        timeDifference = dateDifference(start_time, prev_endTime);
                        System.out.println("timeDifference  ######################### " + timeDifference);
                        prev_endTime = end_time;
                        System.out.println("prev_endTime 2 ***********************************" + prev_endTime);
                        if (timeDifference > 10) {

                            studentTimeTableList.add(new StudentTimeTableListData(null, null, null, null, "Free", null, false));
                        }
                        studentTimeTableList.add(new StudentTimeTableListData(start_time, end_time, subject, subject_teacher_name, slot_type, teacher_class_division, false));

                    }
                } catch (Exception e) {
                    System.out.println("Exception---------" + e.getMessage());
                }
            }
        } else {

            noDataAvailableTextView.setVisibility(View.VISIBLE);
        }
        class_schedule_adapterAdapter = new Class_Schedule_AdapterAdapter(getActivity(), studentTimeTableList);
        timeTableListView.setAdapter(class_schedule_adapterAdapter);


        timeTableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StudentTimeTableListData getlistObject = studentTimeTableList.get(position);
                Boolean getStatus = getlistObject.getExpandable_list_status();
                System.out.println("Expandable list status" + getStatus);
                if (getStatus == true) {
                    getlistObject.setExpandable_list_status(false);
                    System.out.println("***************************");
                } else if (getStatus == false) {
                    getlistObject.setExpandable_list_status(true);
                    System.out.println("###########################");
                    studentTimeTableList.set(position, getlistObject);

                }


            }
        });
        return rootView;
    }

    private static long dateDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = startDate.getTime() - endDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);

        return different / 60000;
    }
}
