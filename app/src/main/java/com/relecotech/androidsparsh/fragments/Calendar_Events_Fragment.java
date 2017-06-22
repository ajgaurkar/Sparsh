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

import com.google.gson.JsonArray;
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
public class Calendar_Events_Fragment extends Fragment {

    private JsonArray calendarEnrtiesJsonArray;
    List<CalendarFragmentListData> calendarEventList;


    private ListView eventListView;
    private TextView noDataAvailableTextView;

    public Calendar_Events_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting data from calendar main fragment.
//        calendarEnrtiesJ////
        //getting data from calendar main fragment.
        try {
            calendarEnrtiesJsonArray = Calendar_Fragment.calendar_results_json_element.getAsJsonArray();
            calendarEventList = new ArrayList<>();
        } catch (NullPointerException nullException) {
            nullException.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_bottom_fragment, container, false);

        eventListView = (ListView) rootView.findViewById(R.id.calendar_bottom_fragment_list_view);
        noDataAvailableTextView = (TextView) rootView.findViewById(R.id.no_data_available_text_view);

        populateList();

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CalendarFragmentListData selectedCalendarFragmentListData = calendarEventList.get(position);

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

        Iterator dateEntryMapIterator = null;

        Map<String, List<CalendarFragmentListData>> dateEntryMapForIteration;
        Map<Integer, Map<String, List<CalendarFragmentListData>>> monthEntriesMapForIteration;
        monthEntriesMapForIteration = Calendar_Fragment.combinedEntryMap.get("Event");
        List calendarEntryListForIteration;
        System.out.println("populateList monthEntriesMapForIteration value : " + monthEntriesMapForIteration);

        try {
            dateEntryMapForIteration = monthEntriesMapForIteration.get(Calendar_Fragment.actualCurrentMonthIndex);

            //if (dateEntryMapForIteration.isEmpty()) {
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


                        calendarEventList.add(new CalendarFragmentListData(listLoopObj.getCalendarTitle(),
                                listLoopObj.getCalendarBody(), listLoopObj.getCalendarDate(), listLoopObj.getCalendarTime(), "Event"));

                    }
                }

                CalendarEntryListAdapter adapter = new CalendarEntryListAdapter(getActivity(), calendarEventList);
                eventListView.setAdapter(adapter);

            }
        } catch (Exception nullException) {
            nullException.printStackTrace();
        }

    }


}
