package com.relecotech.androidsparsh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.relecotech.androidsparsh.activities.LoginActivity;

import java.util.HashMap;

/**
 * Created by ajinkya on 10/20/2015.
 */
public class SessionManager {

    // Sharedpref file name
    private static final String PREF_NAME = "appPrefrences";
    private DatabaseHandler databaseHandler;

    // All Shared Preferences Keys
    // (THAT ARE COMMON TO ALL 3 TYPES OF USERS)
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ROLE = "loggedInUserRole";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_MIDDLE_NAME = "middleName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_BLOOD_GROUP = "bloodGroup";
    public static final String KEY_PHONE = "phoneNo";
    public static final String KEY_DATE_OF_BIRTH = "dateOfBirth";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_SPECIAL_INFO = "specialInfo";
    public static final String KEY_PROFILE_PIC_URL = "profilePicURL";
    public static final String KEY_STATUS = "status";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_CLASS = "userClass";
    public static final String KEY_USER_DIVISON = "userDivision";
    public static final String KEY_USER_PROFILE_IMAGE_NAME = "userProfileImageName";

    //offline data jsons
    public static final String KEY_CLASS_SCHEDULE_JSON = "classScheduleJson";
    public static final String KEY_ALERT_JSON = "alertJson";
    public static final String KEY_ASSIGNMENT_JSON = "assignmentJson";
    public static final String KEY_FEES_JSON = "feesJson";
    public static final String KEY_PARENTZONE_JSON = "parentZoneJson";
    public static final String KEY_BUSSCHEDULE_JSON = "busScheduleJson";


    //offline dashboard data json's
    public static final String KEY_CALENDAR_JSON = "calendarJson";
    public static final String KEY_DASHBOARD_EVENTS_JSON = "dashboardEventsJson";
    public static final String KEY_DASHBOARD_FEES_JSON = "dashboardFeesJson";
    public static final String KEY_DASHBOARD_ALERT_JSON = "dashboardAlertJson";
    public static final String KEY_DASHBOARD_ATTENDANCE_JSON = "dashboardAttendanceJson";
    public static final String KEY_DASHBOARD_PARENTZONE_JSON = "dashboardParentZoneJson";

    //Shared Preferences Keys
    //(THAT ARE UNIQUE TO STUDENT AND TEACHER)
    public static final String KEY_SCHOOL_CLASS_ID = "schoolClassId";

    //Shared Preferences Keys
    //(THAT ARE UNIQUE TO STUDENT)
    public static final String KEY_STUDENT_ID = "studentId";
    public static final String KEY_ENROLLMENT_NO = "enrollmentNo";
    public static final String KEY_ROLL_NO = "rollNo";
    public static final String KEY_HOUSE_COLOR = "houseColor";
    public static final String KEY_ILLNESS = "illness";
    public static final String KEY_EMERGENCY_CONTACT = "emergencyContact";
    public static final String KEY_USER_PIN = "userPin";
    public static final String KEY_MOTHER_NAME = "motherName";
    public static final String KEY_PRIMARY_TEACHER_NAME = "primaryTeacherName";
    public static final String KEY_BUS_ID = "busId";
    public static final String KEY_BUS_PICKPOINT = "busPickPoint";

    //Shared Preferences Keys
    //(THAT ARE UNIQUE TO TEACHER)
    public static final String KEY_TEACHER_REG_ID = "teacherRegId";
    public static final String KEY_QUALIFICATION = "qualification";
    public static final String KEY_SPECIALITY = "speciality";
    public static final String KEY_MARITAL_STATUS = "maritalStatus";
    public static final String KEY_TEACHER_RECORD_ID = "teacherrecord";

    //ProfilePic
    public static final String KEY_USER_PROFILE = "userProfile";


    public static final String KEY_NOTIFICATION_SWITCH = "notificationSwitch";
    public static final String KEY_REMINDER = "reminder";


    //Shared Preferences Keys
    //(THAT ARE UNIQUE TO ADMIN)
    public static final String KEY_ADMIN_ID = "adminId";

    //Miscellaneous Shared Preferences Keys
    public static final String KEY_LATEST_ATTENDANCE_MARK_DATE = "latestAttendanceDate";

    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    private HashMap<String, String> user;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        databaseHandler = new DatabaseHandler(context);
    }

    /**
     * Create login session
     */
    public void createLoginSession(String firstname, String email, String lastname, String password, String role
            , String middleName, String gender, String bloodGrp, String DOB, String address
            , String profilepicURL, String speciality, String phone, String marital_status
            , String user_id, String schoolClassId, String teacherRegid, String qualification
            , String specialInfo, String student_id, String enrollmentNo, String rollNo
            , String houseColor, String illness, String emergencyContact, String admin_id, String teacherRecordid, String teacherName, String primaryClass, String primaryDivison, String userPin, String mother_name, String busId, String busPickPoint) {

        Log.d("USER ROLE SESSION MANGR", "" + role);

        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_FIRST_NAME, firstname);
        // Storing email in pref
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_LAST_NAME, lastname);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_MIDDLE_NAME, middleName);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_BLOOD_GROUP, bloodGrp);
        editor.putString(KEY_DATE_OF_BIRTH, DOB);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_PROFILE_PIC_URL, profilepicURL);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_USER_ID, user_id);
        editor.putString(KEY_USER_CLASS, primaryClass);
        editor.putString(KEY_USER_DIVISON, primaryDivison);
        editor.putString(KEY_USER_PIN, userPin);
        editor.putString(KEY_SPECIAL_INFO, specialInfo);
        editor.putString(KEY_TEACHER_RECORD_ID, teacherRecordid);
        editor.putString(KEY_NOTIFICATION_SWITCH, "true");
        editor.putString(KEY_BUS_ID, busId);
        editor.putString(KEY_BUS_PICKPOINT, busPickPoint);


        System.out.println("teacherRecordid Seesion manger @@@@@@ " + teacherRecordid);
        System.out.println("primaryClass Seesion manger @@@@@@ " + primaryClass);
        System.out.println("primaryDivison Seesion manger @@@@@@ " + primaryDivison);
        System.out.println("specialInfo Seesion manger @@@@@@ " + specialInfo);
        try {
            if (specialInfo == null) {
                System.out.println("1 specialInfo Seesion manger @@@@@@ " + specialInfo);
            }
        } catch (Exception e) {

            System.out.println("1 Exception");
            e.printStackTrace();
        }
        try {
            if (specialInfo.equals("null")) {
                System.out.println(" 2 specialInfo Seesion manger @@@@@@ " + specialInfo);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        try {
            if (specialInfo.equals(null)) {
                System.out.println(" 3 specialInfo Seesion manger @@@@@@ " + specialInfo);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        if (role.equals("Teacher")) {
            editor.putString(KEY_TEACHER_REG_ID, teacherRegid);
            editor.putString(KEY_QUALIFICATION, qualification);
            editor.putString(KEY_SPECIALITY, speciality);
            editor.putString(KEY_MARITAL_STATUS, marital_status);
            editor.putString(KEY_TEACHER_RECORD_ID, teacherRecordid);
            editor.putString(KEY_SCHOOL_CLASS_ID, schoolClassId);
            //latest attendance date has to be null initially
            //only used by teacher
            editor.putString(KEY_LATEST_ATTENDANCE_MARK_DATE, null);

            editor.putString(KEY_REMINDER, "18:00");

            System.out.println("SESSION MANAGER qualification " + qualification);
            System.out.println("SESSION MANAGER speciality " + speciality);
            System.out.println("SESSION MANAGER marital_status " + marital_status);

        }
        if (role.equals("Student")) {
            editor.putString(KEY_SCHOOL_CLASS_ID, schoolClassId);

            editor.putString(KEY_STUDENT_ID, student_id);
            editor.putString(KEY_ENROLLMENT_NO, enrollmentNo);
            editor.putString(KEY_ROLL_NO, rollNo);
            editor.putString(KEY_HOUSE_COLOR, houseColor);
            editor.putString(KEY_ILLNESS, illness);
            editor.putString(KEY_EMERGENCY_CONTACT, emergencyContact);
            editor.putString(KEY_MOTHER_NAME, mother_name);
            editor.putString(KEY_PRIMARY_TEACHER_NAME, teacherName);
            editor.putString(KEY_REMINDER, "18:00");
            System.out.println("SESSION MANAGER KEY_MOTHER_NAME " + mother_name);

        }
        if (role.equals("Admin")) {
            editor.putString(KEY_ADMIN_ID, admin_id);
        }

        // commit changes
        editor.commit();
    }


    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }

    /**
     * Get stored session data
     */

    public HashMap<String, String> getUserDetails() {
        //HashMap<String, String> user = new HashMap<String, String>();
        user = new HashMap<String, String>();

        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, null));
        user.put(KEY_LAST_NAME, pref.getString(KEY_LAST_NAME, null));
        user.put(KEY_MIDDLE_NAME, pref.getString(KEY_MIDDLE_NAME, null));
        user.put(KEY_USER_ROLE, pref.getString(KEY_USER_ROLE, null));

        user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));
        user.put(KEY_BLOOD_GROUP, pref.getString(KEY_BLOOD_GROUP, null));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));
        user.put(KEY_DATE_OF_BIRTH, pref.getString(KEY_DATE_OF_BIRTH, null));
        user.put(KEY_ADDRESS, pref.getString(KEY_ADDRESS, null));
        user.put(KEY_SPECIAL_INFO, pref.getString(KEY_SPECIAL_INFO, null));
        user.put(KEY_PROFILE_PIC_URL, pref.getString(KEY_PROFILE_PIC_URL, null));
        user.put(KEY_STATUS, pref.getString(KEY_STATUS, null));
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_USER_PIN, pref.getString(KEY_USER_PIN, null));

        //Shared Preferences Keys
        //(THAT ARE UNIQUE TO STUDENT AND TEACHER)
        user.put(KEY_SCHOOL_CLASS_ID, pref.getString(KEY_SCHOOL_CLASS_ID, null));
        user.put(KEY_USER_DIVISON, pref.getString(KEY_USER_DIVISON, null));
        user.put(KEY_USER_CLASS, pref.getString(KEY_USER_CLASS, null));
        user.put(KEY_LATEST_ATTENDANCE_MARK_DATE, pref.getString(KEY_LATEST_ATTENDANCE_MARK_DATE, null));
        user.put(KEY_TEACHER_RECORD_ID, pref.getString(KEY_TEACHER_RECORD_ID, null));
        user.put(KEY_BUS_ID, pref.getString(KEY_BUS_ID, null));
        user.put(KEY_BUS_PICKPOINT, pref.getString(KEY_BUS_PICKPOINT, null));

        //Shared Preferences Keys
        //(THAT ARE UNIQUE TO STUDENT)
        user.put(KEY_STUDENT_ID, pref.getString(KEY_STUDENT_ID, null));
        user.put(KEY_ENROLLMENT_NO, pref.getString(KEY_ENROLLMENT_NO, null));
        user.put(KEY_ROLL_NO, pref.getString(KEY_ROLL_NO, null));
        user.put(KEY_HOUSE_COLOR, pref.getString(KEY_HOUSE_COLOR, null));
        user.put(KEY_ILLNESS, pref.getString(KEY_ILLNESS, null));
        user.put(KEY_EMERGENCY_CONTACT, pref.getString(KEY_EMERGENCY_CONTACT, null));
        user.put(KEY_MOTHER_NAME, pref.getString(KEY_MOTHER_NAME, null));
        user.put(KEY_PRIMARY_TEACHER_NAME, pref.getString(KEY_PRIMARY_TEACHER_NAME, null));


        //Shared Preferences Keys
        //(THAT ARE UNIQUE TO TEACHER)
        user.put(KEY_TEACHER_REG_ID, pref.getString(KEY_TEACHER_REG_ID, null));
        user.put(KEY_QUALIFICATION, pref.getString(KEY_QUALIFICATION, null));
        user.put(KEY_SPECIALITY, pref.getString(KEY_SPECIALITY, null));
        user.put(KEY_MARITAL_STATUS, pref.getString(KEY_MARITAL_STATUS, null));
        user.put(KEY_TEACHER_RECORD_ID, pref.getString(KEY_TEACHER_RECORD_ID, null));


        //Shared Preferences Keys
        //(THAT ARE UNIQUE TO ADMIN)
        user.put(KEY_ADMIN_ID, pref.getString(KEY_ADMIN_ID, null));

        // return user
        return user;
    }


    //gives single value from shared pref
    public String getSharedPrefItem(String dataKEY) {

        return pref.getString(dataKEY, null);
    }

    //Inserts single value into shared pref
    public void setSharedPrefItem(String dataKEY, String dataValue) {
        editor.putString(dataKEY, dataValue);
        editor.commit();
    }


    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        user.clear();
        // appDatabase.delete("Teacher_Class_Table", null, null);
        Log.d("logoutUser", " databaseHandler ");
        databaseHandler.deleteAllTables();
        Log.d("logoutUser", "logoutUser call");
        Log.d("user map", "user map" + user);


        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
