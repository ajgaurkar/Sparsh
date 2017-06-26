package com.relecotech.androidsparsh.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.MapsActivity;
import com.relecotech.androidsparsh.R;
//import com.relecotech.sparshalpha.MapsActivity;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.BusTrackerPagerAdapter;
import com.relecotech.androidsparsh.controllers.BusTrackerListData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by amey on 2/27/2016.
 */

public class BusTrackerFragment extends android.support.v4.app.Fragment {
    String userRole;
    private ConnectionDetector connectionDetector;
    private Fragment mFragment;
    private FragmentManager fragmentManager;
    public ViewPager pager;
    private TabLayout busTracker_tabLayout;
    private ArrayList<Fragment> fragmentsList;
    private ArrayList<String> fragmentsNameList;
    private ArrayList<BusTrackerListData> busTrackerListDataArrayList;
    private int currentTab = 0;
    private MobileServiceJsonTable mobileServiceJsonTable;
    private boolean checkConnection;
    private JsonArray getJsonBusListResponse;
    private String bus_route;
    private String bus_no;
    private String route_no;
    private String schedule;
    private String direction;
    private Map<String, BusTrackerListData> BusHashmapData;
    private String time, stop;
    public static Map<String, BusTrackerListData> RouteUp;
    public static Map<String, BusTrackerListData> RouteDown;
    public String currentSwitchDirection = "UP";
    private ProgressDialog progressDialog;
    TimeOutTimerClass timeOutTimerClass;
    Timer busTrack_Timer;
    long TIMEOUT_TIME = 25000;
    Handler busTrack_Handler;
    private SessionManager sessionManager;
    HashMap<String, String> userDetails;
    private TextView timeTv, stopTv;
    private TextView noDataAvailable_TextView;
    private boolean flagRefresh = true;
    JsonElement jsonElement;
    private String scheduleDown, scheduleUp;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentsList = new ArrayList<>();
        fragmentsNameList = new ArrayList<>();
        BusHashmapData = new TreeMap<>();

        RouteUp = new HashMap<>();
        RouteDown = new HashMap<>();

        busTrack_Handler = new Handler();
        timeOutTimerClass = new TimeOutTimerClass();
        busTrack_Timer = new Timer();

        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();

        sessionManager = new SessionManager(getActivity());
        userDetails = sessionManager.getUserDetails();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getActivity().getString(R.string.loading));

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bustracker, container, false);
        userRole =userDetails.get(SessionManager.KEY_USER_ROLE);
        setHasOptionsMenu(true);
        busTrackerListDataArrayList = new ArrayList<>();

        noDataAvailable_TextView = (TextView) rootView.findViewById(R.id.busTrackNoDataAvailable_textView);
        noDataAvailable_TextView.setVisibility(View.INVISIBLE);

        pager = (ViewPager) rootView.findViewById(R.id.busTracker_view_pager);
        busTracker_tabLayout = (TabLayout) rootView.findViewById(R.id.busTracker_tab_layout);

        timeTv = (TextView) rootView.findViewById(R.id.textViewTime);
        stopTv = (TextView) rootView.findViewById(R.id.textViewStop);

        timeTv.setVisibility(View.INVISIBLE);
        stopTv.setVisibility(View.INVISIBLE);


        fab = (FloatingActionButton) rootView.findViewById(R.id.fabBusTracker);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        });

//        busTracker_tabLayout.addTab(busTracker_tabLayout.newTab().setText("Route 1"), 0);
//        busTracker_tabLayout.addTab(busTracker_tabLayout.newTab().setText("Route 2"), 1);
//        busTracker_tabLayout.addTab(busTracker_tabLayout.newTab().setText("Route 3"), 2);

        busTracker_tabLayout.addTab(busTracker_tabLayout.newTab());

//        mobileServiceJsonTable = MainActivity.mClient.getTable("Bus_schedule");

        String busId = userDetails.get(SessionManager.KEY_BUS_ID);
        System.out.println(" Bus Id " + busId);
        if (busId == null) {
            System.out.println(" INSIDE If  Bus Id " + busId);
            noDataAvailable_TextView.setVisibility(View.VISIBLE);
            noDataAvailable_TextView.setText("Not Opted for Bus Service!");
        } else {
            noDataAvailable_TextView.setVisibility(View.INVISIBLE);
            if (sessionManager.getSharedPrefItem(SessionManager.KEY_BUSSCHEDULE_JSON) == null) {

                if (connectionDetector.isConnectingToInternet()) {
                    onExecutionStart();


                } else {
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
                }
            } else {

                parseBusScheduleJSON(new JsonParser().parse(sessionManager.getSharedPrefItem(SessionManager.KEY_BUSSCHEDULE_JSON)));

            }
        }


        return rootView;
    }


    public void setTab() {
        timeTv.setVisibility(View.VISIBLE);
        stopTv.setVisibility(View.VISIBLE);
        FragmentManager manager = getFragmentManager();
        BusTrackerPagerAdapter adapter = new BusTrackerPagerAdapter(manager, fragmentsList, fragmentsNameList);
        pager.setAdapter(adapter);

        busTracker_tabLayout.setupWithViewPager(pager);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(busTracker_tabLayout));
        busTracker_tabLayout.setTabsFromPagerAdapter(adapter);
        pager.setCurrentItem(currentTab);
    }


    public void onExecutionStart() {

        new BusSchedule().execute();
        busTrack_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
    }


    public void reScheduleTimer() {

        System.out.println("reScheduleTimer called");
        busTrack_Timer = new Timer("busTrackTimer", true);
        timeOutTimerClass = new TimeOutTimerClass();
        new BusSchedule().execute();
        busTrack_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
    }

    @Override
    public void onResume() {

        super.onResume();
        /* Below statement for changing Action Bar Title */
        ((MainActivity) getActivity()).setActionBarTitle("Bus Tracker");
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // handle back button
                    if (userRole.equals("Teacher")) {
                        mFragment = new DashboardTeacherFragment();
                        fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();

                    }
                    if (userRole.equals("Student")) {
                        mFragment = new DashboardStudentFragment();
                        fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();

                    }
                    return true;

                }

                return false;
            }
        });
    }

    private class BusSchedule extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                getBusScheduleData();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }
    }

    private void getBusScheduleData() {

        JsonObject busScheduleItem = new JsonObject();
        busScheduleItem.addProperty("BusId", userDetails.get(SessionManager.KEY_BUS_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();

        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("getBusSchedule", busScheduleItem);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" getBusSchedule exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" getBusSchedule  response    " + response);
                try {
                    sessionManager.setSharedPrefItem(SessionManager.KEY_BUSSCHEDULE_JSON, response.toString());
                    parseBusScheduleJSON(response);

                } catch (Exception e) {
                    System.out.println("Post Execute Catch Error");
                    timeOutTimerClass.check = false;
                }


            }
        });
    }

// Old table call to azure chnged because of functionality to specific busId
//    private class BusSchedule extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                jsonElement = mobileServiceJsonTable.execute().get();
//                sessionManager.setSharedPrefItem(SessionManager.KEY_BUSTRACKER_JSON, String.valueOf(jsonElement));
//                return null;
//
//            } catch (MobileServiceException | InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            parseBusTrackerJSON(jsonElement);
//
//        }
//    }

    private void parseBusScheduleJSON(JsonElement busScheduleResponse) {

        try {
            System.out.println("busTrackerResponse " + busScheduleResponse);
            if (busScheduleResponse == null) {
                timeOutTimerClass.check = false;
                System.out.println("BusTracker jsonElement null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            } else {
                System.out.println("BusTracker jsonElement not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                timeOutTimerClass.check = true;
            }
            getJsonBusListResponse = busScheduleResponse.getAsJsonArray();

            if (getJsonBusListResponse.size() == 0) {
                System.out.println("data not received");
                timeTv.setVisibility(View.INVISIBLE);
                stopTv.setVisibility(View.INVISIBLE);
                noDataAvailable_TextView.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                progressDialog.dismiss();

            } else {
                fab.setVisibility(View.INVISIBLE);
                System.out.println("getJsonListResponse.size()" + getJsonBusListResponse.size());
                for (int loop = 0; loop < getJsonBusListResponse.size(); loop++) {

                    timeTv.setVisibility(View.VISIBLE);
                    stopTv.setVisibility(View.VISIBLE);

                    JsonObject jsonObjectForIteration = getJsonBusListResponse.get(loop).getAsJsonObject();

                    bus_route = jsonObjectForIteration.get("route").toString().replace("\"", "");
                    bus_no = jsonObjectForIteration.get("vehicle_no").toString().replace("\"", "");
                    route_no = jsonObjectForIteration.get("route_no").toString().replace("\"", "");
//                schedule = jsonObjectForIteration.get("schedule").toString().replace("\"", "");
                    scheduleUp = jsonObjectForIteration.get("schedule_up").toString().replace("\"", "");
                    scheduleDown = jsonObjectForIteration.get("schedule_down").toString().replace("\"", "");
//                direction = jsonObjectForIteration.get("direction").toString().replace("\"", "");

                    if (scheduleUp.contains("Up")) {

                        RouteUp.put(route_no, new BusTrackerListData(time, stop, "Up", bus_route, bus_no, route_no, scheduleUp));
                    }
                    if (scheduleDown.contains("Down")) {

                        RouteDown.put(route_no, new BusTrackerListData(time, stop, "Down", bus_route, bus_no, route_no, scheduleDown));
                    }

                }
                flagRefresh = true;
                if (currentSwitchDirection.equals("Up")) {
                    System.out.println("inside onPostExecute SWAP IF UP");
                    setRouteUpFragment();

                } else {
                    System.out.println("inside onPostExecute SWAP IF DOWN");
                    setRouteDownFragment();
                }
                System.out.println("Before Set tab");
                currentTab = pager.getCurrentItem();
                setTab();
                progressDialog.dismiss();
            }

        } catch (Exception e) {
            System.out.println("Bus Tracker Fragment " + e.getMessage());
        }
    }

    private void setRouteUpFragment() {

        fragmentsList.clear();
        fragmentsNameList.clear();
        for (Map.Entry<String, BusTrackerListData> entry : RouteUp.entrySet()) {
            String key = entry.getKey();
            BusTrackerListData Value = entry.getValue();

            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putSerializable("RouteList", (Serializable) Value);
            Route_Tab_Fragment busFragment = new Route_Tab_Fragment();
            busFragment.setArguments(fragmentBundle);

            // do stuff
            fragmentsNameList.add(0, key);
            fragmentsList.add(0, busFragment);
        }
    }

    private void setRouteDownFragment() {

        fragmentsList.clear();
        fragmentsNameList.clear();
        for (Map.Entry<String, BusTrackerListData> entry : RouteDown.entrySet()) {
            String key = entry.getKey();
            BusTrackerListData Value = entry.getValue();

            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putSerializable("RouteList", (Serializable) Value);
            Route_Tab_Fragment busFragment = new Route_Tab_Fragment();
            busFragment.setArguments(fragmentBundle);
            // do stuff
            fragmentsNameList.add(0, key);
            fragmentsList.add(0, busFragment);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bus_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.swap) {
            if (currentSwitchDirection.equals("Up")) {
                System.out.println("Inside Option SWAP IF UP");
                setRouteUpFragment();
                currentSwitchDirection = "Down";

            } else {
                System.out.println("inside Option SWAP IF DOWN");
                setRouteDownFragment();
                currentSwitchDirection = "Up";
            }

            currentTab = pager.getCurrentItem();
            setTab();
        }

        if (id == R.id.refresh_dashboard) {
            if (checkConnection) {
                String busId = userDetails.get(SessionManager.KEY_BUS_ID);
                System.out.println(" Bus Id " + busId);
                if (busId == null) {
                    System.out.println(" INSIDE If  Bus Id " + busId);
                    noDataAvailable_TextView.setVisibility(View.VISIBLE);
                    noDataAvailable_TextView.setText("Not Opted for Bus Service!");
                } else {
                    noDataAvailable_TextView.setVisibility(View.INVISIBLE);
                    if (flagRefresh == true) {

                        System.out.println(" INSIDE IF onOptionsItemSelected");
                        reScheduleTimer();
                        System.out.println("if  flagRefresh " + flagRefresh);
                        flagRefresh = false;

                    } else {
                        System.out.println(" INSIDE Else onOptionsItemSelected");
                        System.out.println(" else flagRefresh " + flagRefresh);
                    }
                }

            } else {
                Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public class TimeOutTimerClass extends TimerTask {
        Boolean check = false;

        @Override
        public void run() {
            busTrack_Handler.post(new Runnable() {
                @Override
                public void run() {
                    busTrack_Handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!check) {
                                System.out.println("!check " + !check);
                                busTrack_Timer.cancel();
                                progressDialog.dismiss();
                                System.out.println("Timer Canceled");
                                try {

                                    new android.app.AlertDialog.Builder(getActivity()).setCancelable(false)
                                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    reScheduleTimer();

                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                            /*   method is used to remove all cancelled tasks from this timer's task queue.*/
                                                    busTrack_Timer.purge();


                                                }
                                            }).setMessage(R.string.check_network).create().show();

                                } catch (Exception e) {
                                         /*
                                          it may produce null pointer exception on creation of alert dialog object
                                         */
                                    System.out.println("Exception Handle for Alert Dialog");
                                }
                            } else {
                              /* this line of code is used when everything goes normal . and cancel the timer. */
                                busTrack_Timer.cancel();
                            }
                        }
                    });
                }
            });

        }
    }
}
