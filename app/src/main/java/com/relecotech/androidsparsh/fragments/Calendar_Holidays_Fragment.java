package com.relecotech.androidsparsh.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class Calendar_Holidays_Fragment extends Fragment {

    List<CalendarFragmentListData> calendarHolidaysList;

    private ListView holidaysListView;
    private TextView noDataAvailableTextView;

    public Calendar_Holidays_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calendarHolidaysList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.calendar_bottom_fragment, container, false);

        holidaysListView = (ListView) rootView.findViewById(R.id.calendar_bottom_fragment_list_view);
        noDataAvailableTextView = (TextView) rootView.findViewById(R.id.no_data_available_text_view);

        populateList();

        holidaysListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CalendarFragmentListData selectedCalendarFragmentListData = calendarHolidaysList.get(position);

                //method to check if cal bocyy is null or not
                //if null then dont show null, instead show blank ("")
                String calBody = selectedCalendarFragmentListData.getCalendarBody();
                if (calBody == null) {
                    calBody = "";
                }

                AlertDialog.Builder calendarAlertDialog = new AlertDialog.Builder(getActivity());
                calendarAlertDialog.setTitle(selectedCalendarFragmentListData.getCalendarDate());
                calendarAlertDialog.setMessage(Html.fromHtml("<b>" + selectedCalendarFragmentListData.getCalendarTitle() + "</b><br><br>" + calBody));
                calendarAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                calendarAlertDialog.create().show();
            }
        });

        return rootView;
    }

    private void populateList() {
        Map<String, List<CalendarFragmentListData>> dateEntryMapForIteration;
        Map<Integer, Map<String, List<CalendarFragmentListData>>> monthEntriesMapForIteration;
        List calendarEntryListForIteration;

        Iterator dateEntryMapIterator = null;

        monthEntriesMapForIteration = Calendar_Fragment.combinedEntryMap.get("Holiday");
//        System.out.println("populateList monthEntriesMapForIteration value : " + monthEntriesMapForIteration);

        try {
//            System.out.println("Calendar_Fragment.actualCurrentMonthIndex : " + Calendar_Fragment.actualCurrentMonthIndex);

//            System.out.println("populateList monthEntriesMapForIteration BEFORE----- : " + monthEntriesMapForIteration);
            dateEntryMapForIteration = monthEntriesMapForIteration.get(Calendar_Fragment.actualCurrentMonthIndex);
//            System.out.println("populateList monthEntriesMapForIteration AFTER----- : " + monthEntriesMapForIteration);

//            System.out.println("populateList dateEntryMap value : " + dateEntryMapForIteration);
            if (dateEntryMapForIteration.isEmpty()) {
                noDataAvailableTextView.setVisibility(View.VISIBLE);
            } else {

                noDataAvailableTextView.setVisibility(View.INVISIBLE);
                dateEntryMapIterator = dateEntryMapForIteration.entrySet().iterator();

//                System.out.println("populateList dateEntryMapIterator value : " + dateEntryMapIterator);
                while (dateEntryMapIterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) dateEntryMapIterator.next();
//                    System.out.println("populateList combinedMapIterator : -------------------------------------------------------------------------------------------------------------------------");
                    calendarEntryListForIteration = (ArrayList) pair.getValue();

                    for (int i = 0; i < calendarEntryListForIteration.size(); i++) {
                        CalendarFragmentListData listLoopObj = (CalendarFragmentListData) calendarEntryListForIteration.get(i);

                        calendarHolidaysList.add(new CalendarFragmentListData(listLoopObj.getCalendarTitle(),
                                listLoopObj.getCalendarBody(), listLoopObj.getCalendarDate(),
                                null, "Holiday"));

//                        System.out.println("populateList listLoopObj.getCalendarDate():  " + listLoopObj.getCalendarDate());
                    }
                }
                CalendarEntryListAdapter adapter = new CalendarEntryListAdapter(getActivity(), calendarHolidaysList);
                holidaysListView.setAdapter(adapter);
            }
        } catch (Exception nullException) {
            nullException.printStackTrace();
        }
    }

}

