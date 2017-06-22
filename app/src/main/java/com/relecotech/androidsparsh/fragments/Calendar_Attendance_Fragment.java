package com.relecotech.androidsparsh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.adapters.CalendarEntryListAdapter;
import com.relecotech.androidsparsh.controllers.CalendarFragmentListData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by amey on 4/5/2016.
 */
public class Calendar_Attendance_Fragment extends Fragment {


    private List<CalendarFragmentListData> calendarAttendanceList;
    private ListView holidayListView;
    private TextView noDataAvailableTextView;

    public Calendar_Attendance_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting data from calendar main fragment.
        calendarAttendanceList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.calendar_bottom_fragment, container, false);

        holidayListView = (ListView) rootView.findViewById(R.id.calendar_bottom_fragment_list_view);
        noDataAvailableTextView = (TextView) rootView.findViewById(R.id.no_data_available_text_view);
        populateList();

        return rootView;
    }

    private void populateList() {
        Map<String, List<CalendarFragmentListData>> dateEntryMapForIteration;
        Map<Integer, Map<String, List<CalendarFragmentListData>>> monthEntriesMapForIteration;
        List calendarEntryListForIteration;

        Iterator dateEntryMapIterator = null;

        monthEntriesMapForIteration = Calendar_Fragment.combinedEntryMap.get("Attendance");

        try {
            dateEntryMapForIteration = monthEntriesMapForIteration.get(Calendar_Fragment.actualCurrentMonthIndex);

            if (dateEntryMapForIteration == null) {
                noDataAvailableTextView.setVisibility(View.VISIBLE);
            } else {
                noDataAvailableTextView.setVisibility(View.INVISIBLE);

                dateEntryMapIterator = dateEntryMapForIteration.entrySet().iterator();

                while (dateEntryMapIterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) dateEntryMapIterator.next();
                    calendarEntryListForIteration = (ArrayList) pair.getValue();

                    for (int i = 0; i < calendarEntryListForIteration.size(); i++) {
                        CalendarFragmentListData listLoopObj = (CalendarFragmentListData) calendarEntryListForIteration.get(i);

                        calendarAttendanceList.add(new CalendarFragmentListData("Absent",
                                null, listLoopObj.getCalendarDate(),
                                null, "Attendance"));

                    }
                }
                CalendarEntryListAdapter adapter = new CalendarEntryListAdapter(getActivity(), calendarAttendanceList);
                holidayListView.setAdapter(adapter);

            }
        } catch (Exception nullException) {
            nullException.printStackTrace();
        }
    }

}
