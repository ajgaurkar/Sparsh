package com.relecotech.androidsparsh;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.relecotech.androidsparsh.activities.EditUserDetails;
import com.relecotech.androidsparsh.fragments.AlertFragment;
import com.relecotech.androidsparsh.fragments.AssignmentFragment;
import com.relecotech.androidsparsh.fragments.AttendanceTeacherFragment;
import com.relecotech.androidsparsh.fragments.Attendance_Student_Fragment;
import com.relecotech.androidsparsh.fragments.BusTrackerFragment;
import com.relecotech.androidsparsh.fragments.Calendar_Fragment;
import com.relecotech.androidsparsh.fragments.Class_ScheduleFragment;
import com.relecotech.androidsparsh.fragments.DashboardStudentFragment;
import com.relecotech.androidsparsh.fragments.DashboardTeacherFragment;
import com.relecotech.androidsparsh.fragments.FeesFragment;
import com.relecotech.androidsparsh.fragments.Parent_control;
import com.relecotech.androidsparsh.fragments.Reports_StudentFragment;
import com.relecotech.androidsparsh.fragments.Reports_TeacherFragment;
import com.relecotech.androidsparsh.fragments.SettingFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static SQLiteDatabase schoolOnlineDatabase;

    public static String userfirstname;
    public static String userlastname;
    public static String username;
    long back_pressed;
    public static String teacherRegId;
    public static String studentId;
    public static String schoolClassId;
    private CircleImageView circularImageView;
    static ImageView schoolLogo;
    List<String> accountSelectionList;
    ArrayAdapter<String> stringArrayAdapter;
    DrawerLayout drawer;
    View viewForSnack;
    public static String userRole;
    private SessionManager mainManager;
    private String useremail;
    /*
     * Mobile Service Client reference
     */
    public static MobileServiceClient mClient;

    static DatabaseHandler databaseHandler;
    private String uristring = "/storage/emulated/0/Sparsh/Profile_Pic/";
    private TextView appUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewForSnack = findViewById(R.id.mainViewForSnack);
        setSupportActionBar(toolbar);
        accountSelectionList = new ArrayList<>();
        appUserName = (TextView) findViewById(R.id.userNameSpinner);
        circularImageView = (CircleImageView) findViewById(R.id.circularuserimage);
        schoolLogo = (ImageView) findViewById(R.id.schoolLogoImageView);

        databaseHandler = new DatabaseHandler(this);

        mainManager = new SessionManager(getApplicationContext());

        schoolOnlineDatabase = openOrCreateDatabase("SCHOOL_ONLINE_GLOBAL_DATABASE", MODE_PRIVATE, null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //*******************User LoginActivity START******************************************//
        mainManager = new SessionManager(getApplicationContext());
        try {
            mainManager.checkLogin();
            Boolean checkLoginStatus = mainManager.isLoggedIn();
            System.out.println("Login Status------------" + checkLoginStatus);

        } catch (Exception mainEx) {
            mainEx.printStackTrace();
            Log.d("Login Activity Error", "" + mainEx);
        }

        // Azure database registration;
        try {
//            mClient = new MobileServiceClient("http://schoolontrailtest.azurewebsites.net", this);
//            mClient = new MobileServiceClient("http://sparshmobileappdevelopment.azurewebsites.net", this);
            mClient = new MobileServiceClient("http://sparshdevmobileapp.azurewebsites.net",this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            HashMap<String, String> user = mainManager.getUserDetails();

            userfirstname = user.get(SessionManager.KEY_FIRST_NAME);
            userlastname = user.get(SessionManager.KEY_LAST_NAME);
            useremail = user.get(SessionManager.KEY_EMAIL);
            userRole = user.get(SessionManager.KEY_USER_ROLE);
            studentId = user.get(SessionManager.KEY_STUDENT_ID);
            teacherRegId = user.get(SessionManager.KEY_TEACHER_RECORD_ID);
            schoolClassId = user.get(SessionManager.KEY_SCHOOL_CLASS_ID);

            Log.d("USER ROLE MAP DATA", "" + user);
            Log.d("USER ROLE MAINACTIVITY", "" + userRole);

            username = userfirstname + " " + userlastname;
            Log.d("username for nav header", username);

        /*   Download User Profile and profile in navigation drawer.  */

            if (!(userRole == null)) {
                if (userRole.equals("Teacher")) {
                    appUserName.setText(username);
                }
                if (userRole.equals("Student")) {
                    appUserName.setText(username);

                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.switch_user_simple_spinner_item, accountSelectionList);
        stringArrayAdapter.setDropDownViewResource(R.layout.switch_user_spinner_dropdown_item);
//        System.out.println("accountSelectionSpinner " + accountSelectionSpinner);
//        accountSelectionSpinner.setAdapter(stringArrayAdapter);

        //********USER PROFILE SELECTOR FOR STUDENT VIEW ON NAVIGATION DRAWER************************
//        accountSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String s = (String) accountSelectionSpinner.getItemAtPosition(position);
//                Log.d("selected Item ", "" + s);
//                switch (s) {
//                    case "Neha Patil":
//                        circularImageView.setImageResource(R.drawable.app_girl_1);
//                        Log.d("circular image", "section 1");
//                        break;
//
//                    case "Ajinkya Gaurkar":
//                        circularImageView.setImageResource(R.drawable.app_boy_1);
//                        Log.d("circular image", "section 2");
//                        break;
//                }
//            }
//            //****************************************************************************************
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        //*******************User LoginActivity END*****************************************//

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProfileIntent = new Intent(getApplicationContext(), EditUserDetails.class);
                startActivity(userProfileIntent);
            }
        });


        if (!(userRole == null)) {
            displayFragment(R.id.nav_dashboard);
            if (userRole.equals("Teacher")) {
                MenuItem item = navigationView.getMenu().findItem(R.id.nav_fees);
                item.setVisible(false);
            }
//            if (userRole.equals("Student")) {
//                MenuItem item = navigationView.getMenu().findItem(R.id.nav_forums);
//                item.setVisible(false);
//            }
        }


    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "Press Once again to exit", Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displayFragment(item.getItemId());
        return true;
    }


    private void displayFragment(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if (!(userRole == null)) {

            switch (viewId) {
                case R.id.nav_dashboard:
                    if (userRole.equals("Teacher")) {
                        Log.d("login_user_role", userRole);
                        fragment = new DashboardTeacherFragment();
                    }
                    if (userRole.equals("Student")) {
                        Log.d("login_user_role", userRole);
                        fragment = new DashboardStudentFragment();
                    }
                    title = "Dashboard";
                    break;

                case R.id.nav_attendance:
                    if (userRole.equals("Teacher")) {
                        Log.d("login_user_role", userRole);
                        fragment = new AttendanceTeacherFragment();
                    }
                    if (userRole.equals("Student")) {
                        Log.d("TeacherFragment();", "fragment = new  Attendance_Student_Fragment();");
                        fragment = new Attendance_Student_Fragment();
                    }
                    title = "Attendance";
                    break;
                case R.id.nav_alert:
                    fragment = new AlertFragment();
                    title = "Alerts";
                    break;

                case R.id.nav_reports:
                    if (userRole.equals("Teacher")) {
                        Log.d("login_user_role", userRole);
                        fragment = new Reports_TeacherFragment();
                    }
                    if (userRole.equals("Student")) {
                        Log.d("TeacherFragment();", "fragment = new TeacherReportsFragment();");
                        fragment = new Reports_StudentFragment();
                    }
                    title = "Reports";
                    break;
                case R.id.nav_assignment:
                    fragment = new AssignmentFragment();
                    title = "Assignment";
                    break;
                case R.id.nav_fees:
                    fragment = new FeesFragment();
                    title = "Fees";
                    break;
                case R.id.nav_parent_zone:
                    fragment = new Parent_control();
                    title = "Parent Zone";
                    break;
                case R.id.nav_settings:
                    fragment = new SettingFragment();
                    title = "Settings";
                    break;
                case R.id.nav_logout:

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.openDrawer(Gravity.LEFT);
                        }
                    });
                    alertDialogBuilder.setMessage("Confirm Logout ?");
                    alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mainManager.logoutUser();
                        }
                    });
                    alertDialogBuilder.create().show();
                    break;

                case R.id.nav_bus_route:
                    fragment = new BusTrackerFragment();
                    title = "Bus Tracker";
                    break;
                case R.id.nav_class_schedule:
                    fragment = new Class_ScheduleFragment();
                    title = "Class Schedule";
                    break;
                case R.id.nav_calendar:
                    fragment = new Calendar_Fragment();
                    title = "Calendar";
                    break;
            }
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    public void setActionBarTitle(String title) {
        //getSupportActionBar().setTitle(title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        setProfilePic();
        System.out.println("On Resume........");
    }

    void setProfilePic() {
        if (mainManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME) != null) {

            String profile_file_name = mainManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME);

            String string_file_path = uristring + profile_file_name;
            System.out.print(string_file_path);
            System.out.print(profile_file_name);
            File file_path = new File(string_file_path);
            Bitmap myBitmap = BitmapFactory.decodeFile(file_path.getAbsolutePath());
            circularImageView.setImageBitmap(myBitmap);
        } else {
            System.out.println("Set Profile Not set we are in els of SetProfile");
        }
    }

}
