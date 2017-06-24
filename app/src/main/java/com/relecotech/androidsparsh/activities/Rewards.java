package com.relecotech.androidsparsh.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.RewardsRatingAdapterAdapter;
import com.relecotech.androidsparsh.controllers.RewardsRatingListData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by amey on 10/16/2015.
 */
public class Rewards extends AppCompatActivity {

    TextView extraCurriTextBtn, sportsTextBtn, academicsTextBtn;
    Boolean extraCurriBtnStatus = true, sportsBtnStatus = false, academicsBtnStatus = false;
    ListView rewardsListView;
    RewardsRatingListData rewardsRatingListData;
    RewardsRatingAdapterAdapter rewardsRatingAdapterAdapter;
    String userRole;
    private TextView studentNameTextView;
    private RelativeLayout teacherRewardslayoutForStudentSelection;
    private Spinner classSpinner;
    private Spinner divisionSpinner;
    private Spinner studentNameSpinner;
    private Button rewardsViewBtn;
    private String rewardsSelectedClass;
    private String rewardsSelectedDivision;
    private String rewardsSelectedStudent;
    private Snackbar snackbar;
    private TextView snackbarTextView;
    private View snackbarView;
    private View view;
    private MobileServiceJsonTable rewardJsonDataTable;
    private MobileServiceJsonTable rewardStudentJsonDataTable;
    private JsonElement rewardJsonElement;
    private JsonObject jsonObjectForIteration;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private HashMap<String, List<RewardsRatingListData>> rewardsRatingListDataHashMap;
    private String rewardsType;
    private String rewardsTitle;
    private String rewardsDescription;
    private String rewardsStar;
    private String rewardsDate;
    private List<RewardsRatingListData> academicsRewardsRatingListData;
    private List<RewardsRatingListData> sportRewardsRatingListData;
    private List<RewardsRatingListData> extraCurricularRewardsRatingListData;
    private int rewardsStarInteger;
    private Set<String> keysOfMap;
    private String currentTab = "ExtraCurricular";
    private DatabaseHandler databaseHandler;
    private Cursor schoolClassData;
    public static String rewardsDivision, rewardsClass;
    private String schoolClassID;
    private List<String> studentNamelist;
    private List<String> studentIdList;
    private List<String> classlist;
    private List<String> divlist;
    private ArrayAdapter<String> adapterStudentName;
    private String queryConditionParam;
    private ProgressDialog rewardsProgressDialog;
    private Handler rewardsHandler;
    private TimeOutTimerClass timeOutTimerClass;
    private Timer rewards_Timer;
    private RewardDataAsynTask rewardDataAsynTask;
    long TIMEOUT_TIME = 25000;
    private TextView noDataTextView;
    private HashMap<String, String> conditionHashMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewards_student);

        /*
        Action Bar BackPressed....
        */
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException nullPoint) {
            nullPoint.printStackTrace();
        }
        extraCurriTextBtn = (TextView) findViewById(R.id.extracurricularTextView);
        sportsTextBtn = (TextView) findViewById(R.id.sportsTextView);
        academicsTextBtn = (TextView) findViewById(R.id.academicsTextView);
        rewardsListView = (ListView) findViewById(R.id.rewardslistView);
        studentNameTextView = (TextView) findViewById(R.id.rewardsStudentName);

        noDataTextView = (TextView) findViewById(R.id.noDataAvailabletextView);
        noDataTextView.setVisibility(View.INVISIBLE);

        teacherRewardslayoutForStudentSelection = (RelativeLayout) findViewById(R.id.teacherRewardsLayout);
        classSpinner = (Spinner) findViewById(R.id.rewardsClassSpinner);
        divisionSpinner = (Spinner) findViewById(R.id.rewardsDivisionSpinner);
        studentNameSpinner = (Spinner) findViewById(R.id.rewardsStudentNamespinner);
        rewardsViewBtn = (Button) findViewById(R.id.rewardsViewButton);
        view = findViewById(R.id.rewardsSnackView);
        academicsRewardsRatingListData = new ArrayList<>();
        sportRewardsRatingListData = new ArrayList<>();
        extraCurricularRewardsRatingListData = new ArrayList<>();

        userRole = MainActivity.userRole;
        Log.d("login_user_role", userRole);
        rewardJsonDataTable = MainActivity.mClient.getTable("rewards");
        rewardStudentJsonDataTable = MainActivity.mClient.getTable("student");
        sessionManager = new SessionManager(Rewards.this);
        userDetails = sessionManager.getUserDetails();
        rewardsRatingListDataHashMap = new HashMap<>();
        databaseHandler = new DatabaseHandler(Rewards.this);
        schoolClassData = databaseHandler.getTeacherClassDataByCursor();
        rewardsProgressDialog = new ProgressDialog(Rewards.this);
        timeOutTimerClass = new TimeOutTimerClass();
        rewards_Timer = new Timer();
        rewardDataAsynTask = new RewardDataAsynTask();
        rewardsHandler = new Handler();
        conditionHashMap = new HashMap<>();

        if (userRole.equals("Student")) {
            teacherRewardslayoutForStudentSelection.setVisibility(View.GONE);
            System.out.println("Welcome to Student View");
            studentNameTextView.setText(MainActivity.username);
            String studentID = userDetails.get(SessionManager.KEY_STUDENT_ID);
             /*
             Fetch rewards Data Task calling
             */
            fetchRewardsData(studentID);
        }

        if (userRole.equals("Teacher")) {
            teacherRewardslayoutForStudentSelection.setVisibility(View.VISIBLE);
            System.out.println("Welcome  to Teacher View");

            classlist = new ArrayList<>();
            divlist = new ArrayList<>();
            studentIdList = new ArrayList<>();
            studentNamelist = new ArrayList<>();

            try {
                //Setup data to class Spinner
                schoolClassData.moveToFirst();
                do {
                    if (!classlist.contains(schoolClassData.getString(2))) {
                        classlist.add(schoolClassData.getString(2));
                    }
                } while (schoolClassData.moveToNext());
            } catch (Exception e) {
                System.out.println("Rewards Exception " + e.getMessage());
            }

            classlist.add("[ Class ]");
            ArrayAdapter adapterClass = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, classlist);
            adapterClass.setDropDownViewResource(R.layout.spinner_dropdown_item);
            classSpinner.setAdapter(adapterClass);
            classSpinner.setSelection(adapterClass.getCount() - 1);

             /*
            Setup data to Student Name Spinner.
             */
            studentNamelist.add("[ Select Student ]");
            studentIdList.add("DummyId");
            adapterStudentName = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, studentNamelist);
            adapterStudentName.setDropDownViewResource(R.layout.spinner_dropdown_item);
            studentNameSpinner.setAdapter(adapterStudentName);
            studentNameSpinner.setSelection(adapterStudentName.getCount() - 1);


            classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    rewardsClass = classSpinner.getSelectedItem().toString();
                    conditionHashMap.put("class", rewardsClass);
                    schoolClassData = databaseHandler.getTeacherClassData(conditionHashMap);
                    schoolClassData.moveToFirst();
                    divlist.clear();
                    for (int i = 0; i < schoolClassData.getCount(); i++) {
                        if (!divlist.contains(schoolClassData.getString(3))) {
                            divlist.add(schoolClassData.getString(3));
                        }
                        schoolClassData.moveToNext();
                    }
                    divlist.add("[ Division ]");
                    System.out.println("Division list  classSpinner" + divlist);
//                    schoolClassData.moveToFirst();
//                    do {
//                        if (schoolClassData.getString(2).equals(rewardsClass)) {
//                            if (!divlist.contains(schoolClassData.getString(3))) {
//                                divlist.add(schoolClassData.getString(3));
//                            }
//                        }
//                    } while (schoolClassData.moveToNext());
//                    divlist.add("[ Division ]");
                    ArrayAdapter adapterDivision = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, divlist);
                    adapterDivision.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    divisionSpinner.setAdapter(adapterDivision);
                    divisionSpinner.setSelection(adapterDivision.getCount() - 1);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    rewardsDivision = divisionSpinner.getSelectedItem().toString();
                    System.out.println("Divisoin list  divisionSpinner" + divlist);
                    System.out.println("Cusrsor data count" + schoolClassData.getCount());
                    schoolClassData.moveToFirst();

                    for (int loopk = 0; loopk < schoolClassData.getCount(); loopk++) {

                        if (schoolClassData.getString(2).equals(rewardsClass) && schoolClassData.getString(3).equals(rewardsDivision)) {
                            schoolClassID = schoolClassData.getString(1);
                            System.out.println("School class id" + schoolClassData.getString(1));
                            System.out.println("Sucessssssssssssssssssssssssss");
                        }
                    }
                    schoolClassData.moveToNext();
                    if (!rewardsDivision.equals("[ Division ]")) {
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ in if condition");
                        /*
                        Fetching Student name . Task Calli
                         */
//                        new FetchStudentData().execute();
                        FetchStudentList();

                    } else {
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ in else condition");
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            rewardsViewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rewardsSelectedClass = classSpinner.getSelectedItem().toString();
                    rewardsSelectedDivision = divisionSpinner.getSelectedItem().toString();
                    rewardsSelectedStudent = studentNameSpinner.getSelectedItem().toString();
                    String selectedStudentID = studentIdList.get(studentNameSpinner.getSelectedItemPosition());
                    if (rewardsSelectedClass != "[ Class ]") {
                        if (rewardsSelectedDivision != "[ Division ]") {
                            if (rewardsSelectedStudent != "[ Select Student ]") {

                                teacherRewardslayoutForStudentSelection.setVisibility(View.INVISIBLE);
                                studentNameTextView.setText(rewardsSelectedStudent);
                                System.out.println("Student  Name " + studentNameSpinner.getSelectedItem().toString());
                                System.out.println("selectedStudentID " + selectedStudentID);
                                /*
                               Fetech rewards Data Task calling
                                 */
                                fetchRewardsData(selectedStudentID);


                            } else {
                                snackbar = Snackbar.make(view, "Select Student", Snackbar.LENGTH_SHORT);
                                snackbarView = snackbar.getView();
                                snackbarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                                snackbarTextView.setTextColor(Color.WHITE);
                                snackbar.show();
                            }
                        } else {
                            snackbar = Snackbar.make(view, "Select Division", Snackbar.LENGTH_SHORT);
                            snackbarView = snackbar.getView();
                            snackbarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                            snackbarTextView.setTextColor(Color.WHITE);
                            snackbar.show();
                        }
                    } else {
                        snackbar = Snackbar.make(view, "Select Class", Snackbar.LENGTH_SHORT);
                        snackbarView = snackbar.getView();
                        snackbarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        snackbarTextView.setTextColor(Color.WHITE);
                        snackbar.show();
                    }
                }
            });

            //If Condtion Closing Brace
        }

        rewardsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentTab.equals("ExtraCurricular")) {
                    RewardsRatingListData selectedRewardsRatingListData = extraCurricularRewardsRatingListData.get(i);
                    showrewardDetails(selectedRewardsRatingListData);
                } else if (currentTab.equals("Sports")) {
                    RewardsRatingListData selectedRewardsRatingListData = sportRewardsRatingListData.get(i);
                    showrewardDetails(selectedRewardsRatingListData);
                } else if (currentTab.equals("Academics")) {
                    RewardsRatingListData selectedRewardsRatingListData = academicsRewardsRatingListData.get(i);
                    showrewardDetails(selectedRewardsRatingListData);
                }

            }
        });


        extraCurriTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (extraCurriBtnStatus == false) {
                    extraCurriBtnStatus = true;
                    currentTab = "ExtraCurricular";
                    sportsBtnStatus = false;
                    academicsBtnStatus = false;

                    academicsTextBtn.setTextColor(Color.parseColor("#2880B8"));
                    academicsTextBtn.setBackgroundResource(R.drawable.rewards_panel_left_clear);

                    extraCurriTextBtn.setTextColor(Color.WHITE);
                    extraCurriTextBtn.setBackgroundResource(R.drawable.rewards_panel_center_filled);

                    sportsTextBtn.setTextColor(Color.parseColor("#2880B8"));
                    sportsTextBtn.setBackgroundResource(R.drawable.rewards_panel_right_clear);

                     /*
                     Showing ExtraCarricularList Data
                     */
                    if (extraCurricularRewardsRatingListData == null || extraCurricularRewardsRatingListData.isEmpty()) {
                        System.out.println(" INSIDE IF EXTRa");
                        // noDataTextView.setText("No Data Available");
                        noDataTextView.setVisibility(View.VISIBLE);
                        rewardsListView.setVisibility(View.INVISIBLE);
                    } else {
                        noDataTextView.setVisibility(View.INVISIBLE);
                        rewardsListView.setVisibility(View.VISIBLE);

                        System.out.println("extraCurricularRewardsRatingListData" + extraCurricularRewardsRatingListData);
                        rewardsRatingAdapterAdapter = new RewardsRatingAdapterAdapter(Rewards.this, extraCurricularRewardsRatingListData);
                        rewardsListView.setAdapter(rewardsRatingAdapterAdapter);
                    }


                }
            }
        });
        sportsTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sportsBtnStatus == false) {
                    sportsBtnStatus = true;
                    currentTab = "Sports";

                    extraCurriBtnStatus = false;
                    academicsBtnStatus = false;

                    academicsTextBtn.setTextColor(Color.parseColor("#2880B8"));
                    academicsTextBtn.setBackgroundResource(R.drawable.rewards_panel_left_clear);

                    extraCurriTextBtn.setTextColor(Color.parseColor("#2880B8"));
                    extraCurriTextBtn.setBackgroundResource(R.drawable.rewards_panel_center_clear);

                    sportsTextBtn.setTextColor(Color.WHITE);
                    sportsTextBtn.setBackgroundResource(R.drawable.rewards_panel_right_filled);

                    /*
                     Showing Sportlist Data
                     */
                    if (sportRewardsRatingListData == null || sportRewardsRatingListData.isEmpty()) {
                        // noDataTextView.setText("No Data Available");

                        noDataTextView.setVisibility(View.VISIBLE);
                        rewardsListView.setVisibility(View.INVISIBLE);
                    } else {
                        noDataTextView.setVisibility(View.INVISIBLE);
                        rewardsListView.setVisibility(View.VISIBLE);
                        System.out.println("sportRewardsRatingListData" + sportRewardsRatingListData);
                        rewardsRatingAdapterAdapter = new RewardsRatingAdapterAdapter(Rewards.this, sportRewardsRatingListData);
                        rewardsListView.setAdapter(rewardsRatingAdapterAdapter);
                    }


                }
            }
        });
        academicsTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (academicsBtnStatus == false) {
                    currentTab = "Academics";
                    academicsBtnStatus = true;

                    sportsBtnStatus = false;
                    extraCurriBtnStatus = false;

                    academicsTextBtn.setTextColor(Color.WHITE);
                    academicsTextBtn.setBackgroundResource(R.drawable.rewards_panel_left_filled);

                    extraCurriTextBtn.setTextColor(Color.parseColor("#2880B8"));
                    extraCurriTextBtn.setBackgroundResource(R.drawable.rewards_panel_center_clear);

                    sportsTextBtn.setTextColor(Color.parseColor("#2880B8"));
                    sportsTextBtn.setBackgroundResource(R.drawable.rewards_panel_right_clear);

                      /*
                     Showing Acdemicslist Data
                     */
                    if (academicsRewardsRatingListData == null || academicsRewardsRatingListData.isEmpty()) {
                        noDataTextView.setVisibility(View.VISIBLE);
                        rewardsListView.setVisibility(View.INVISIBLE);
                    } else {
                        noDataTextView.setVisibility(View.INVISIBLE);
                        rewardsListView.setVisibility(View.VISIBLE);
                        System.out.println("academicsRewardsRatingListData" + academicsRewardsRatingListData);
                        rewardsRatingAdapterAdapter = new RewardsRatingAdapterAdapter(Rewards.this, academicsRewardsRatingListData);
                        rewardsListView.setAdapter(rewardsRatingAdapterAdapter);
                    }


                }
            }
        });
    }

    public void fetchRewardsData(String parameter) {
        queryConditionParam = parameter;
        rewardDataAsynTask = new RewardDataAsynTask();
        rewardDataAsynTask.execute();
        rewards_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
    }

    private void reScheduleTimer() {
        rewards_Timer = new Timer("alertTimer", true);
        timeOutTimerClass = new TimeOutTimerClass();
        rewardDataAsynTask = new RewardDataAsynTask();
        rewardDataAsynTask.execute();
        rewards_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("2.   reScheduleTimer");
    }

    // method to set initial data in list for default selected extra-curricular tab
    private void setInitialListdata() {
        if (extraCurricularRewardsRatingListData.isEmpty()) {
            noDataTextView.setVisibility(View.VISIBLE);
            rewardsListView.setVisibility(View.INVISIBLE);
        } else {
            noDataTextView.setVisibility(View.INVISIBLE);
            rewardsListView.setVisibility(View.VISIBLE);
            rewardsRatingAdapterAdapter = new RewardsRatingAdapterAdapter(Rewards.this, extraCurricularRewardsRatingListData);
            rewardsListView.setAdapter(rewardsRatingAdapterAdapter);
        }

    }


    /*
    Action Bar BackPressed....

     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showrewardDetails(RewardsRatingListData selectedRewardsRatingListData) {

        LayoutInflater rewardsDialogInflater = this.getLayoutInflater();
        View view = rewardsDialogInflater.inflate(R.layout.rewards_custom_dialoge, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(view);
        alertDialog.setTitle(selectedRewardsRatingListData.getRewardsEventTitle());

        TextView rewardsDialogDate = (TextView) view.findViewById(R.id.rewardsDialogdate);
        TextView rewardsDialogDescription = (TextView) view.findViewById(R.id.rewardsDialogDescription);
        RatingBar rewardsDialogRatingBar = (RatingBar) view.findViewById(R.id.rewardsDialogRatingBar);

        rewardsDialogDate.setText(selectedRewardsRatingListData.getRewardsEventDate());
        rewardsDialogDescription.setText(selectedRewardsRatingListData.getRewardsEventDescription());
        rewardsDialogRatingBar.setRating(selectedRewardsRatingListData.getRewardsRating());

        alertDialog.setPositiveButton("Done", null);
        alertDialog.create();
        alertDialog.show();


    }


    public class RewardDataAsynTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rewardsProgressDialog.setMessage(getString(R.string.loading));
            rewardsProgressDialog.setCancelable(false);
            rewardsProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                rewardJsonElement = rewardJsonDataTable.where().field("Student_id").eq(queryConditionParam).execute().get();
                System.out.println("rewardJsonElement response " + rewardJsonElement);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            try {
                JsonArray rewardsJsonArray = rewardJsonElement.getAsJsonArray();

                if (!rewardsJsonArray.equals(null)) {
                    timeOutTimerClass.check = true;

                    for (int loopi = 0; loopi < rewardsJsonArray.size(); loopi++) {
                        jsonObjectForIteration = rewardsJsonArray.get(loopi).getAsJsonObject();
                        System.out.println("title" + jsonObjectForIteration.get("title").toString().replace("\"", ""));
                        System.out.println("reward_date" + jsonObjectForIteration.get("reward_date").toString().replace("\"", ""));
                        System.out.println("reward_description" + jsonObjectForIteration.get("reward_description").toString().replace("\"", ""));
                        System.out.println("stars" + jsonObjectForIteration.get("stars").toString().replace("\"", ""));
                        System.out.println("reward_type" + jsonObjectForIteration.get("reward_type").toString().replace("\"", ""));

                        rewardsType = jsonObjectForIteration.get("reward_type").toString().replace("\"", "");
                        rewardsTitle = jsonObjectForIteration.get("title").toString().replace("\"", "");
                        rewardsDescription = jsonObjectForIteration.get("reward_description").toString().replace("\"", "");
                        rewardsStar = jsonObjectForIteration.get("stars").toString().replace("\"", "");
                        rewardsDate = jsonObjectForIteration.get("reward_date").toString().replace("\"", "");

                        System.out.println(" Before Reward Star Parse to Int");
                        if (rewardsStar.equals("null")) {
                            rewardsStarInteger = 0;
                        } else {
                            rewardsStarInteger = Integer.parseInt(rewardsStar);
                        }

                        System.out.println(" After Parsing data ");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                        System.out.println(" After Parsing data simpleDateFormat " + simpleDateFormat);
                        SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy");
                        System.out.println(" After Parsing data targetDateFormat" + targetDateFormat);
                        targetDateFormat.setTimeZone(TimeZone.getDefault());
                        try {
                            Date daterewards = simpleDateFormat.parse(rewardsDate);
                            rewardsDate = targetDateFormat.format(daterewards);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rewardsType.equals("Sports")) {
                            rewardsRatingListData = new RewardsRatingListData(rewardsTitle, rewardsType, rewardsStarInteger, rewardsDescription, rewardsDate);
                            sportRewardsRatingListData.add(rewardsRatingListData);
                        } else if (rewardsType.equals("Academics")) {
                            rewardsRatingListData = new RewardsRatingListData(rewardsTitle, rewardsType, rewardsStarInteger, rewardsDescription, rewardsDate);
                            academicsRewardsRatingListData.add(rewardsRatingListData);
                        } else if (rewardsType.equals("ExtraCurricular")) {
                            rewardsRatingListData = new RewardsRatingListData(rewardsTitle, rewardsType, rewardsStarInteger, rewardsDescription, rewardsDate);
                            extraCurricularRewardsRatingListData.add(rewardsRatingListData);
                        }

                    }
                    rewardsRatingListDataHashMap.put("Sports", sportRewardsRatingListData);
                    rewardsRatingListDataHashMap.put("Academics", academicsRewardsRatingListData);
                    rewardsRatingListDataHashMap.put("ExtraCurricular", extraCurricularRewardsRatingListData);

                    System.out.println(" KEY SPORT" + sportRewardsRatingListData.size());
                    System.out.println(" KEY ACADEMICS" + academicsRewardsRatingListData.size());
                    System.out.println(" KEY EXTRA" + extraCurricularRewardsRatingListData.size());
                } else {
                    timeOutTimerClass.check = false;
                    System.out.println("************ Json Array is null ****************");
                }
            } catch (Exception e) {
                System.out.println("Exception Json array null");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*
            method to set initial data in list for
            default selected extra-curricular tab
            */
            if (timeOutTimerClass.check) {
                rewardsProgressDialog.cancel();
            }
            setInitialListdata();
            System.out.println("Current Tab" + currentTab);

        }
    }


    private void FetchStudentList() {

        JsonObject jsonObjectForStudentList = new JsonObject();

        // Yogesh changes for invoke api
        jsonObjectForStudentList.addProperty("SchoolClassId", schoolClassID);
        jsonObjectForStudentList.addProperty("UserRole", userDetails.get(SessionManager.KEY_USER_ROLE));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("FetchStudentListApi", jsonObjectForStudentList);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Fetch Student List Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Fetch Student List  API   response    " + response);

                StudentListJsonParse(response);
            }
        });
    }

    private void StudentListJsonParse(JsonElement jsonElement) {

        try {
            // JsonElement jsonResponse = rewardStudentJsonDataTable.parameter("SchoolClassId", schoolClassID).execute().get();
            System.out.println("jsonResponse" + jsonElement);
            JsonArray jsonResponseArray = jsonElement.getAsJsonArray();
            if (!jsonResponseArray.equals(null)) {

                studentNamelist.clear();
                studentIdList.clear();

                for (int loopj = 0; loopj < jsonResponseArray.size(); loopj++) {
                    jsonObjectForIteration = jsonResponseArray.get(loopj).getAsJsonObject();
                    String student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    String student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    String student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    String student_fullName = student_firstName + " " + student_lastName;
                    System.out.println("student_Id" + student_Id);
                    System.out.println("student_count" + loopj);
                    System.out.println("student_fullName" + student_fullName);
                        /*
                        two lists, one to maintain name and other for id.
                        we could have instead used custom adapter with list<object>
                        but that results in addition of list data class and writting custom adapter
                        so to avoid that 2 lists are used for trial. this logic may sound bad but easy to write and deal with
                        */
                    studentNamelist.add(student_fullName);
                    studentIdList.add(student_Id);


                }
                System.out.println("Studentlist Size" + studentNamelist.size());
                System.out.println("Studentlist Size" + studentIdList.size());
            }


            studentNamelist.add("[ Select Student ]");
            studentIdList.add("DummyId");
            adapterStudentName.notifyDataSetChanged();
            studentNameSpinner.setSelection(adapterStudentName.getCount() - 1);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//    private class FetchStudentData extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                JsonElement jsonResponse = rewardStudentJsonDataTable.parameter("SchoolClassId", schoolClassID).execute().get();
//                System.out.println("jsonResponse" + jsonResponse);
//                JsonArray jsonResponseArray = jsonResponse.getAsJsonArray();
//                if (!jsonResponseArray.equals(null)) {
//
//                    studentNamelist.clear();
//                    studentIdList.clear();
//
//                    for (int loopj = 0; loopj < jsonResponseArray.size(); loopj++) {
//                        jsonObjectForIteration = jsonResponseArray.get(loopj).getAsJsonObject();
//                        String student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
//                        String student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
//                        String student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
//                        String student_fullName = student_firstName + " " + student_lastName;
//                        System.out.println("student_Id" + student_Id);
//                        System.out.println("student_count" + loopj);
//                        System.out.println("student_fullName" + student_fullName);
//                            /*
//                            two lists, one to maintain name and other for id.
//                            we could have instead used custom adapter with list<object>
//                            but that results in addition of list data class and writting custom adapter
//                            so to avoid that 2 lists are used for trial. this logic may sound bad but easy to write and deal with
//                            */
//                        studentNamelist.add(student_fullName);
//                        studentIdList.add(student_Id);
//
//
//                    }
//                    System.out.println("Studentlist Size" + studentNamelist.size());
//                    System.out.println("Studentlist Size" + studentIdList.size());
//                }
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            studentNamelist.add("[ Select Student ]");
//            studentIdList.add("DummyId");
//            adapterStudentName.notifyDataSetChanged();
//            studentNameSpinner.setSelection(adapterStudentName.getCount() - 1);
//        }
//    }


    public class TimeOutTimerClass extends TimerTask {
        Boolean check = false;

        @Override
        public void run() {
            rewardsHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!check) {
                        rewards_Timer.cancel();
                        rewardDataAsynTask.cancel(true);
                        rewardsProgressDialog.dismiss();
                        try {
                            AlertDialog.Builder rewardAlertDialogBuilder = new AlertDialog.Builder(Rewards.this);
                            rewardAlertDialogBuilder.setCancelable(false);
                            rewardAlertDialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reScheduleTimer();
                                }
                            });
                            rewardAlertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            });
                            rewardAlertDialogBuilder.setMessage(R.string.check_network);
                            rewardAlertDialogBuilder.create().show();
                        } catch (Exception e) {
                            System.out.println("Catch Alert Dialog error");
                        }

                    }

                }
            });
        }
    }

}