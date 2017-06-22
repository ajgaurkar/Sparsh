package com.relecotech.androidsparsh.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.FeesPagerAdapter;
import com.relecotech.androidsparsh.controllers.FeesDialogListData;
import com.relecotech.androidsparsh.controllers.FeesListdata;

import org.json.JSONArray;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;

/**
 * Created by amey on 10/16/2015.
 */
public class FeesFragment extends android.support.v4.app.Fragment {

    int installment = 1, extrainstallment = 1;
    private ProgressBar feesProgressbar;
    private String status;
    private JSONArray feeArray;
    private ProgressDialog feesprogressDialog;
    private MobileServiceJsonTable feesMobileServiceJsonTable;
    private JsonElement feesJsonElement;
    private SessionManager mainManager;
    private HashMap<String, String> userDetails;
    private boolean checkConnection;
    private ConnectionDetector connectionDetector;
    private Fragment mFragment;
    private FragmentManager fragmentManager;
    private String fee_Status;
    private String paid_Date;
    private String fee_Amount;
    private String due_Date;
    private String fee_Type;
    private String fee_Comment;
    private String fee_Period;
    private String amount_Split1;
    private String amount_Split2;
    private String amount_Split3;
    private String amount_Split4;
    private String amount_Split5;
    private String amount_Split6;
    private String fee_extra_Events;
    private String due_date;

    ListView fees_Academics_ListView;
    ListView fees_Extracurricular_ListView;
    List<FeesListdata> fees_ArrayList;
    FeesListdata feesListdata;
    ArrayList<FeesDialogListData> fees_Dialog_List;
    private String amount_split_comment_1;
    private String amount_split_comment_2;
    private String amount_split_comment_3;
    private String amount_split_comment_4;
    private String amount_split_comment_5;
    private String amount_split_comment_6;
    private ArrayList<String> fragmentsNameList;
    private ArrayList<Fragment> fragmentsList;
    private ViewPager fees_pager;
    private int currentTab;
    private TabLayout fees_tabLayout;
    private Map<String, List<FeesListdata>> feesList_Map;
    private JsonObject jsonObjectFeeFragParameters;
    private TextView extra_curr_totalFeesTv;
    private TextView academics_totalFeesTv;
    private TextView dur_FeesTv;
    private Handler fee_Handler;
    Timer fee_Timer;
    private boolean flagRefresh = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();
//        feesMobileServiceJsonTable = MainActivity.mClient.getTable("fees_status");
        mainManager = new SessionManager(getActivity());
        userDetails = mainManager.getUserDetails();
        fee_Timer = new Timer();
        fee_Handler = new Handler();
        fees_ArrayList = new ArrayList<>();
        fees_Dialog_List = new ArrayList<>();
        fragmentsList = new ArrayList<>();
        fragmentsNameList = new ArrayList<>();
        fragmentsNameList.add("Academic");
        fragmentsNameList.add("Extracurricular");
        feesList_Map = new HashMap();


        setHasOptionsMenu(true);

        feesprogressDialog = new ProgressDialog(getActivity());
        feesprogressDialog.setCancelable(false);
        feesprogressDialog.setMessage(getActivity().getString(R.string.loading));
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        System.out.println("Post  After Initialization");
        View rootView = inflater.inflate(R.layout.fees, container, false);
        fees_tabLayout = (TabLayout) rootView.findViewById(R.id.fees_tab_layout);
        fees_pager = (ViewPager) rootView.findViewById(R.id.fees_view_pager);
        System.out.println("fees_pager----------------------------- " + fees_pager);
//        fees_Academics_ListView = (ListView) rootView.findViewById(R.id.academicslistview);
//        fees_Extracurricular_ListView = (ListView) rootView.findViewById(R.id.extracurricularlistview);
//
//        academics_totalFeesTv = (TextView) rootView.findViewById(R.id.acdamics_total_fee_textView);
//        extra_curr_totalFeesTv = (TextView) rootView.findViewById(R.id.extra_curr_total_fee_textView);
        dur_FeesTv = (TextView) rootView.findViewById(R.id.dur_textView);

        if (mainManager.getSharedPrefItem(SessionManager.KEY_FEES_JSON) == null) {
            if (connectionDetector.isConnectingToInternet()) {

                //Calling Fetch Assignment Async Task with TimeoutConnection handling thread.
                onExecutionStart();
            } else {
                Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        } else {

            System.out.println("mainManager.getSharedPrefItem(SessionManager.KEY_FEES_JSON) " + mainManager.getSharedPrefItem(SessionManager.KEY_FEES_JSON));
            feesprogressDialog.show();
            parseFeesJSON(new JsonParser().parse((mainManager.getSharedPrefItem(SessionManager.KEY_FEES_JSON))));

        }

        return rootView;
    }

    private void CallingFeesApi() {

        jsonObjectFeeFragParameters = new JsonObject();
        jsonObjectFeeFragParameters.addProperty("studentId", userDetails.get(SessionManager.KEY_STUDENT_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchFeesStatus", jsonObjectFeeFragParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Fees Fragment exception    " + exception);
                feesprogressDialog.dismiss();
                new android.app.AlertDialog.Builder(getActivity()).setMessage(R.string.check_network)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onResume();
                                feesprogressDialog.dismiss();

                            }
                        })
                        .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //feesprogressDialog.show();
                                reScheduleTimer();
                            }
                        }).create().show();
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" FEES FRAGMENT  API   response    " + response);

                mainManager.setSharedPrefItem(SessionManager.KEY_FEES_JSON, String.valueOf(response));
                parseFeesJSON(response);
            }
        });
    }

    private void parseFeesJSON(JsonElement response) {

        JsonArray feesJsonArray = response.getAsJsonArray();

        try {
            if ((feesJsonArray.size()) == 0) {
                System.out.println(" NO Fees  RECEIVED");

            }
            System.out.println(" feesJsonArray.size() " + feesJsonArray.size());
            for (int loopI = 0; loopI < feesJsonArray.size(); loopI++) {
                System.out.println(" INSIDE LOOP OF FEES JSON PARSING ");
                JsonObject jsonObjectForIteration = feesJsonArray.get(loopI).getAsJsonObject();
                fee_Status = jsonObjectForIteration.get("fee_status").toString().replace("\"", "");
                paid_Date = jsonObjectForIteration.get("paid_date").toString().replace("\"", "");
                fee_Amount = jsonObjectForIteration.get("amount").toString().replace("\"", "");
                due_Date = jsonObjectForIteration.get("due_date").toString().replace("\"", "");
                fee_Type = jsonObjectForIteration.get("fee_type").toString().replace("\"", "");
                System.out.println(" fee_Type " + fee_Type);
                fee_Comment = jsonObjectForIteration.get("fee_status_comment").toString().replace("\"", "");
                fee_Period = jsonObjectForIteration.get("period").toString().replace("\"", "");
                fee_extra_Events = jsonObjectForIteration.get("fee_comment").toString().replace("\"", "");

                amount_Split1 = jsonObjectForIteration.get("amount_split_1").toString().replace("\"", "");
                amount_Split2 = jsonObjectForIteration.get("amount_split_2").toString().replace("\"", "");
                amount_Split3 = jsonObjectForIteration.get("amount_split_3").toString().replace("\"", "");
                amount_Split4 = jsonObjectForIteration.get("amount_split_4").toString().replace("\"", "");
                amount_Split5 = jsonObjectForIteration.get("amount_split_5").toString().replace("\"", "");
                amount_Split6 = jsonObjectForIteration.get("amount_split_6").toString().replace("\"", "");

                amount_split_comment_1 = jsonObjectForIteration.get("amount_split_comment_1").toString().replace("\"", "");
                amount_split_comment_2 = jsonObjectForIteration.get("amount_split_comment_2").toString().replace("\"", "");
                amount_split_comment_3 = jsonObjectForIteration.get("amount_split_comment_3").toString().replace("\"", "");
                amount_split_comment_4 = jsonObjectForIteration.get("amount_split_comment_4").toString().replace("\"", "");
                amount_split_comment_5 = jsonObjectForIteration.get("amount_split_comment_5").toString().replace("\"", "");
                amount_split_comment_6 = jsonObjectForIteration.get("amount_split_comment_6").toString().replace("\"", "");

                due_Date = DateFormat(due_Date);
                paid_Date = DateFormat(paid_Date);

                if (fee_Type.equals("Academic")) {

                    if (feesList_Map.containsKey("Academic")) {
                        System.out.println("Academic--------------------creation of list");
                        fees_ArrayList = feesList_Map.get("Academic");
                        feesListdata = new FeesListdata(installment, fee_Period, due_Date, fee_Amount, fee_Status, amount_Split1, amount_Split2, amount_Split3, amount_Split4, amount_Split5, amount_Split6, fee_Type, fee_Comment, paid_Date, amount_split_comment_1, amount_split_comment_2, amount_split_comment_3, amount_split_comment_4, amount_split_comment_5, amount_split_comment_6);
                        fees_ArrayList.add(feesListdata);
                        installment++;
                        feesList_Map.put("Academic", fees_ArrayList);
                        System.out.println("fees_ArrayList-----Academic----------fees_ArrayList" + fees_ArrayList);
                    } else {
                        fees_ArrayList = new ArrayList<>();
                        feesListdata = new FeesListdata(installment, fee_Period, due_Date, fee_Amount, fee_Status, amount_Split1, amount_Split2, amount_Split3, amount_Split4, amount_Split5, amount_Split6, fee_Type, fee_Comment, paid_Date, amount_split_comment_1, amount_split_comment_2, amount_split_comment_3, amount_split_comment_4, amount_split_comment_5, amount_split_comment_6);
                        fees_ArrayList.add(feesListdata);
                        installment++;
                        feesList_Map.put("Academic", fees_ArrayList);
                    }

                } else if (fee_Type.equals("Extracurricular")) {

                    System.out.println("Inside Extracurricular");

                    if (feesList_Map.containsKey("Extracurricular")) {
                        System.out.println("Extracurricular--------------------creation of list");
                        fees_ArrayList = feesList_Map.get("Extracurricular");
                        feesListdata = new FeesListdata(extrainstallment, fee_Period, due_Date, fee_Amount, fee_Status, amount_Split1, amount_Split2, amount_Split3, amount_Split4, amount_Split5, amount_Split6, fee_Type, fee_extra_Events, paid_Date, amount_split_comment_1, amount_split_comment_2, amount_split_comment_3, amount_split_comment_4, amount_split_comment_5, amount_split_comment_6);
                        fees_ArrayList.add(feesListdata);
                        extrainstallment++;
                        feesList_Map.put("Extracurricular", fees_ArrayList);
                        System.out.println("fees_ArrayList-----Extracurricular----------fees_ArrayList" + fees_ArrayList);
                    } else {
                        System.out.println("Extracurricular- else-------------------creation of list");
                        fees_ArrayList = new ArrayList<>();
                        feesListdata = new FeesListdata(extrainstallment, fee_Period, due_Date, fee_Amount, fee_Status, amount_Split1, amount_Split2, amount_Split3, amount_Split4, amount_Split5, amount_Split6, fee_Type, fee_extra_Events, paid_Date, amount_split_comment_1, amount_split_comment_2, amount_split_comment_3, amount_split_comment_4, amount_split_comment_5, amount_split_comment_6);
                        fees_ArrayList.add(feesListdata);
                        extrainstallment++;
                        feesList_Map.put("Extracurricular", fees_ArrayList);
                        System.out.println("fees_ArrayList----- else Extracurricular----------fees_ArrayList" + fees_ArrayList);
                    }

                }
            }
            setFragment();
            setTabs();
            flagRefresh = true;
            feesprogressDialog.dismiss();
        } catch (Exception e) {
            System.out.println("exception in Fees fetch");
            e.printStackTrace();

        }
    }

    private void setFragment() {
        System.out.println("fragmentsNameList " + fragmentsNameList);
        for (int i = 0; i < fragmentsNameList.size(); i++) {
            System.out.println(" inside loop fragmentsNameList " + fragmentsNameList);
            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putSerializable("feesList", (Serializable) feesList_Map.get(fragmentsNameList.get(i)));
            Fees_Tabs_Fragment fees_tabs_fragment = new Fees_Tabs_Fragment();
            fees_tabs_fragment.setArguments(fragmentBundle);
            fragmentsList.add(fees_tabs_fragment);
            System.out.println("fragmentsList Size-----------" + fragmentsList.size());
        }
    }

    private void setTabs() {
        try {
            FragmentManager manager = getFragmentManager();
            System.out.println("fragmentsList---- " + fragmentsList);
            System.out.println("fragmentsNameList---- " + fragmentsNameList);
            FeesPagerAdapter adapter = new FeesPagerAdapter(manager, fragmentsList, fragmentsNameList);
            fees_pager.setAdapter(adapter);

            fees_tabLayout.setupWithViewPager(fees_pager);

            /*
                Yogesh Written Code..
                Fees Tabs Headings Tags Change on Refresh.

            */
            System.out.println(" fees_tabLayout.getSelectedTabPosition() " + fees_tabLayout.getSelectedTabPosition());

            if (fees_tabLayout.getSelectedTabPosition() == 0) {
                dur_FeesTv.setText("Duration");
            }
            if (fees_tabLayout.getSelectedTabPosition() == 1) {
                dur_FeesTv.setText("Events");
            }

            fees_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(fees_tabLayout) {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    System.out.println(" position " + position);
                    if (position == 1) {
                        dur_FeesTv.setText("Events");

                    } else if (position == 0) {

                        dur_FeesTv.setText("Duration");

                    } else {

                        dur_FeesTv.setText("Duration");
                    }
                }
            });
            fees_tabLayout.setTabsFromPagerAdapter(adapter);
            System.out.println("currentTab : " + currentTab);
            fees_pager.setCurrentItem(currentTab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        /* Below statment for changing Action Bar Title */
        ((MainActivity) getActivity()).setActionBarTitle("Fees");


        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // handle back buttson
                    mFragment = new DashboardStudentFragment();
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).addToBackStack(null).commit();

                    return true;
                }
                return false;
            }
        });
    }


    @Nullable
    private String DateFormat(String due_date) {

        if (!due_date.equals("null")) {
            String string_date = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat target_date_format = new SimpleDateFormat("d MMM yy ", Locale.getDefault());
            try {
                Date temp_date = dateFormat.parse(due_date);
                string_date = target_date_format.format(temp_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return string_date;
        } else {
            return null;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        connectionDetector = new ConnectionDetector(getActivity());
        checkConnection = connectionDetector.isConnectingToInternet();
        if (checkConnection) {
            if (id == R.id.refresh_dashboard) {

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
        }else {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }


    public void onExecutionStart() {
        feesprogressDialog.show();
        CallingFeesApi();
    }

    public void reScheduleTimer() {
        feesprogressDialog.show();
        fragmentsList.clear();
        fragmentsNameList.clear();
        feesList_Map.clear();
        installment = 1;
        extrainstallment = 1;
        fragmentsList = new ArrayList<>();
        fragmentsNameList = new ArrayList<>();
        fragmentsNameList.add("Academic");
        fragmentsNameList.add("Extracurricular");
        CallingFeesApi();
    }

}