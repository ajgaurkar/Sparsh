package com.relecotech.androidsparsh.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.relecotech.androidsparsh.MapsActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.adapters.BusTracker_Adapter;
import com.relecotech.androidsparsh.controllers.BusTrackerListData;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by yogesh on 08-Oct-16.
 */
public class Route_Tab_Fragment extends Fragment {

    private ListView busTrackeListView;
    private ArrayList<BusTrackerListData> busTrackerList;
    private BusTracker_Adapter busTrackerAdapter;
    private BusTrackerListData getRouteListData;
    private ArrayList<String> pickPointList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.bustracker_listview, container, false);

        busTrackeListView = (ListView) rootView.findViewById(R.id.busTrackerListView);
        Bundle getBundleData = this.getArguments();
        busTrackerList = new ArrayList<BusTrackerListData>();
        getRouteListData = (BusTrackerListData) getBundleData.getSerializable("RouteList");
        System.out.println("getRouteListData     " + getRouteListData);

        pickPointList = new ArrayList<>();
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabBusTrackerTab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putStringArrayListExtra("pickPoint",pickPointList);
                startActivity(intent);
            }
        });

//        pickPointList.add("21.143427, 79.080644");

        String schedule = getRouteListData.getSchedule();

        String dirStr = schedule.substring(schedule.indexOf("-") + 1, schedule.length());

        StringTokenizer st = new StringTokenizer(dirStr, "+");
        while (st.hasMoreTokens()) {

            //Got substring here e.g. 01:00 PM%GittiKhadan
            //'UP-01:00 PM%Nandanvan$01:30 PM%Reshimbagh'
            //'01:00 PM%Nandanvan$01:30 PM%Reshimbagh'
//            Up-09:00 AM%Medical Square$21.131334,79.097608+10:00 AM%Nandanwan$21.136166,79.119546+11:00 AM%Reshimbagh$21.137475,79.108479+

            String tmpStr = st.nextToken();
            System.out.println("tmpStr " + tmpStr);

            //Extract time & location from substring tmpStr
            String time = tmpStr.substring(tmpStr.indexOf("-") + 1, tmpStr.indexOf("%"));
            String stop = tmpStr.substring(tmpStr.indexOf("%") + 1,tmpStr.indexOf("$"));
            String stopPoints = tmpStr.substring(tmpStr.indexOf("$") + 1, tmpStr.length());

            pickPointList.add(stopPoints);
            System.out.println("time " + time);
            System.out.println("stop " + stop);
            busTrackerList.add(new BusTrackerListData(time, stop, null, null, null, null));
        }

//        busTrackerAdapter = new BusTracker_Adapter(getActivity(), busTrackerList);
//        busTrackeListView.setAdapter(busTrackerAdapter);

        try {

            if (!busTrackerList.isEmpty() || busTrackerList != null) {
                System.out.println("Value---------------------------" + busTrackerList.size());


                busTrackerAdapter = new BusTracker_Adapter(getActivity(), busTrackerList);
                busTrackeListView.setAdapter(busTrackerAdapter);

            } else {
                System.out.println("BusTrackerList  list is NULLL");
            }
        } catch (Exception e) {
            System.out.println("getMessage----" + e.getMessage());
        }

        return rootView;
    }
}