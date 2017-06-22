package com.relecotech.androidsparsh.activities;

/**
 * Created by ajinkya on 10/20/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.relecotech.androidsparsh.AlertDialogManager;
import com.relecotech.androidsparsh.AppNotificationHandler;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.NotificationSettings;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.RegistrationIntentService;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.azurecontroller.Users;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by amey on 10/19/2015.
 */
public class LoginActivity extends Activity {

    // login button
    public static String firstnameresponse, lastnamerespnse, emailidresponse;
    // Email, password edittext
    EditText txtEmail, txtPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    TextView forgotPasswordTextView;

    private String role;
    private String username;
    private String password;
    Button btnLogin;
    ProgressBar pBar;
    String loginUrl = "https://relecoschool-developer-edition.ap2.force.com/services/apexrest/login?login=";
    String preparedUrl;

    // All values for user profile
    String middleName;
    String gender;
    String bloodGroup;
    String phone;
    String dateOfBirth;
    String address;
    String specialInfo;
    String profilePicURL;
    String status;
    String user_id;
    String school_class_id;
    String student_id;
    String enrollmentNo;
    String rollNo;
    String houseColor;
    String illness;
    String emergencyContact;
    String teacherRegid;
    String qualification;
    String speciality;
    String maritalStatus;
    String admin_id;
    SQLiteDatabase sqLiteDatabase;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    // Session Manager Class
    SessionManager session;
    ConnectionDetector connectionDetector;
    Intent validuserintent;
    View view;
    public static ProgressDialog loginprogressdialog;
    private int activeStatusCheck;
    private String userPassword;
    public static String userPin;
    private String userRole;
    private MobileServiceTable<Users> usersMobileServiceTable;
    JsonObject jsonObjectgetting_login_credential;
    JsonObject jsonObjectforgetting_teaching_data;
    private boolean checkstudent;
    private Spinner roleSelector;
    List<String> roleList;
    ArrayAdapter<String> roleAdapter;
    //    SQLiteDatabase appDatabase;
    String sqlite_schoolClassId;
    String sqlite_class;
    String sqlite_division;
    String sqlite_subject;
    private String teacherRecordid;
    private String primaryClass;
    private String primaryDivison;

    //    private String SENDER_ID = "858460695555";
//    private GoogleCloudMessaging gcm;
//    private NotificationHub hub;
//    private String HubName = "schoolonlinemobservicehub";
//
//    private String HubEndpoint = null;
//    private String HubSasKeyName = null;
//    private String HubSasKeyValue = null;
//    private String HubListenConnectionString = "Endpoint=sb://schoolonlinemobservicehub-ns.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=e60igQl0gV0QMEKnFR1UoloYwB6pUv3o6BABlDXBWew=";
    private DatabaseHandler databaseHandler;
    private Handler handler;
    private Timer timer;
    private TimeOutTimerClass timeOutTimerClass;
    long TIMEOUT_TIME = 30000;
    private String mother_name;
    private String primary_teacher_First_name;
    private String primary_teacher_Last_name;
    private String primary_teacher_Recored_id;

    CloudBlobContainer container;
    String directory;
    private String fileName = "School_On_Image-3939_1467107941280.jpg";
    private String profile_pic_name;
    private String busId;
    private String busPickPoint;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Email, Password input text
        txtEmail = (EditText) findViewById(R.id.emailEditText);
        txtPassword = (EditText) findViewById(R.id.passwordEditText);
        forgotPasswordTextView = (TextView) findViewById(R.id.forgotClickText);
        roleSelector = (Spinner) findViewById(R.id.userRoleSpinner);
        view = findViewById(R.id.rootview); // this is used for snack bar,
        loginprogressdialog = new ProgressDialog(this);
        roleList = new ArrayList<>();
        Snackbar snackbar = Snackbar.make(view, "User Login Status" + session.isLoggedIn(), Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        //snackbar.show();
        //Azure

//        usersMobileServiceTable = MainActivity.mClient.getTable(Users.class);

        //Azure Login button
        btnLogin = (Button) findViewById(R.id.loginButton);

        //populate roles in picklist
        roleList.add("Student");
        roleList.add("Teacher");
        roleList.add("Administrator");
        roleAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.login_spinner_item, roleList);
        roleAdapter.setDropDownViewResource(R.layout.login_spinner_dropdown_item);
        roleSelector.setAdapter(roleAdapter);

        timeOutTimerClass = new TimeOutTimerClass();
        handler = new Handler();
        timer = new Timer();

        //mainActivity = this;
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, AppNotificationHandler.class);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Get username, password from EditText
                username = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                role = roleSelector.getSelectedItem().toString();
                preparedUrl = loginUrl + username + ";" + password;

                // Check if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0) {

                    if (username.matches(emailPattern)) {

                        //temporary password check mimic
                        if (password.length() > 3) {
                            // Creating user login session
                            if (connectionDetector.isConnectingToInternet()) {

                                jsonObjectgetting_login_credential = new JsonObject();
                                jsonObjectgetting_login_credential.addProperty("userEmail", username);
                                jsonObjectgetting_login_credential.addProperty("userPassword", password);
                                jsonObjectgetting_login_credential.addProperty("userRole", role);

                                Log.d("username", "jsonObject  " + username);
                                Log.d("password", "jsonObject  " + password);

                                loginprogressdialog.setMessage("Authenticating...");
                                loginprogressdialog.setCancelable(false);
                                loginprogressdialog.show();
                                System.out.println("1  .Progress Dialog Start");
                                onExecutionStart();

                            } else {
                                Snackbar.make(view, "No Internet Connection !", Snackbar.LENGTH_SHORT)
                                        .setAction("Turn On", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent i = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                                                startActivity(i);
                                            }
                                        }).show();
                            }
                        } else {
                            //passwqord length too short
                            txtPassword.setError("Invalid password");
                        }
                    } else {
                        // invalid email id
                        txtEmail.setError("Invalid Email");
                    }
                } else {

                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    Snackbar snackbar1 = Snackbar.make(view, "Fields are empty", Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar1.getView();
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar1.show();
                    // alert.showAlertDialog(LoginActivity.this, "Login failed..", "Fields are empty", false);
                }

            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder forgotPswdDialog = new AlertDialog.Builder(LoginActivity.this);
                forgotPswdDialog.setMessage("Please Contact Admin Department.");
                forgotPswdDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                forgotPswdDialog.create().show();
            }
        });
    }


    private void onExecutionStart() {
        initiateLogin();
        timer = new Timer("LoginTimer", true);
        timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("1.    onExecutionStart");
    }

    private void reScheduleTimer() {
        timer = new Timer("LoginTimer", true);
        timeOutTimerClass = new TimeOutTimerClass();
        initiateLogin();
        loginprogressdialog.setMessage("Authenticating...");
        loginprogressdialog.setCancelable(false);
        loginprogressdialog.show();
        timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("2.   reScheduleTimer");
    }


    private void initiateLogin() {

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("loginApi", jsonObjectgetting_login_credential);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("response    " + response);

                try {

                    if (response.equals("")) {
                        timeOutTimerClass.check = false;
                        System.out.println("Continue Execution");
                    } else {
                        timeOutTimerClass.check = true;
                        System.out.println("Terminate Execution");
                    }
                    String jsonToString = response.toString();
                    System.out.println("jsonToString" + jsonToString);
                    if (jsonToString.equals("false")) {
                        Log.d("user role mismatch", "user role mismatch");
                        loginprogressdialog.dismiss();
                        timeOutTimerClass.cancel();
                                /*
                                    method is used to remove all cancelled tasks from this timer's task queue.
                                 */
                        timer.purge();
                        timeOutTimerClass = new TimeOutTimerClass();
                        timer = new Timer();


                        AlertDialog.Builder invalidcredentialsBuilder = new AlertDialog.Builder(LoginActivity.this);
                        invalidcredentialsBuilder.setTitle("School On");
                        invalidcredentialsBuilder.setMessage("The username,password and userrole you have entered is incorrect");
                        invalidcredentialsBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtEmail.setText("");
                                txtPassword.setText("");
                            }
                        });
                        invalidcredentialsBuilder.setCancelable(false);
                        invalidcredentialsBuilder.create().show();


                    } else {

                        try {
                            JsonArray jsonArray = response.getAsJsonArray();
                            Log.d("jsonArray.size", "jsonArray.size " + jsonArray.size());
                            System.out.println(" jsonArray.size " + jsonArray.size());
                            if ((jsonArray.size()) == 0) {
                                loginprogressdialog.dismiss();

                                AlertDialog.Builder invalidcredentialsBuilder = new AlertDialog.Builder(LoginActivity.this);
                                invalidcredentialsBuilder.setTitle("School On");
                                invalidcredentialsBuilder.setMessage("The username,password and userrole you have entered is incorrect");
                                invalidcredentialsBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        txtEmail.setText("");
                                        txtPassword.setText("");
                                    }
                                });
                                invalidcredentialsBuilder.setCancelable(false);
                                invalidcredentialsBuilder.create().show();
                                System.out.println(" Invalid credentials");

                                /*  method is used to remove all cancelled tasks from this timer's task queue.  */
                                timer.purge();
                                timeOutTimerClass = new TimeOutTimerClass();
                                timer = new Timer();

                            } else {
                                JsonObject jsonObjectForProcessing = jsonArray.get(0).getAsJsonObject();

                                userRole = jsonObjectForProcessing.get("userRole").toString().replace("\"", "");
                                emailidresponse = jsonObjectForProcessing.get("userEmail").toString().replace("\"", "");
                                userPassword = jsonObjectForProcessing.get("userPassword").toString().replace("\"", "");
                                userPin = jsonObjectForProcessing.get("userPin").toString().replace("\"", "");
                                activeStatusCheck = Integer.parseInt(jsonObjectForProcessing.get("active").toString());
                                firstnameresponse = jsonObjectForProcessing.get("firstName").toString().replace("\"", "");
                                lastnamerespnse = jsonObjectForProcessing.get("lastName").toString().replace("\"", "");
                                middleName = jsonObjectForProcessing.get("middleName").toString().replace("\"", "");
                                gender = jsonObjectForProcessing.get("gender").toString().replace("\"", "");
                                bloodGroup = jsonObjectForProcessing.get("bloodGrp").toString().replace("\"", "");
                                phone = jsonObjectForProcessing.get("phone").toString().replace("\"", "");
                                dateOfBirth = jsonObjectForProcessing.get("dateOfBirth").toString().replace("\"", "");
                                address = jsonObjectForProcessing.get("address").toString().replace("\"", "");
                                profilePicURL = jsonObjectForProcessing.get("profilePicURL").toString().replace("\"", "");
                                user_id = jsonObjectForProcessing.get("Users_id").toString().replace("\"", "");
                                primaryClass = jsonObjectForProcessing.get("class").toString().replace("\"", "");
                                primaryDivison = jsonObjectForProcessing.get("division").toString().replace("\"", "");
                                busId = jsonObjectForProcessing.get("bus_id").toString().replace("\"", "");
                                busPickPoint = jsonObjectForProcessing.get("busPickUpPoint").toString().replace("\"", "");

                                System.out.println("primaryClass @@@@@@ " + primaryClass);
                                System.out.println("primaryDivison @@@@@@ " + primaryDivison);
                                System.out.println("userRole @@@@@@ --------------------------------------" + userRole);


//

                                if (userRole.equals("Student")) {

                                    enrollmentNo = jsonObjectForProcessing.get("studentEnrollmentNo").toString().replace("\"", "");
                                    rollNo = jsonObjectForProcessing.get("rollNo").toString().replace("\"", "");
                                    houseColor = jsonObjectForProcessing.get("houseColor").toString().replace("\"", "");
                                    illness = jsonObjectForProcessing.get("illness").toString().replace("\"", "");
                                    emergencyContact = jsonObjectForProcessing.get("emergencyContact").toString().replace("\"", "");
                                    school_class_id = jsonObjectForProcessing.get("School_Class_id").toString().replace("\"", "");
                                    specialInfo = jsonObjectForProcessing.get("specialInfo").toString().replace("\"", "");
                                    student_id = jsonObjectForProcessing.get("id").toString().replace("\"", "");
                                    mother_name = jsonObjectForProcessing.get("mothersName").toString().replace("\"", "");
                                    teacherRecordid = jsonObjectForProcessing.get("Teacher_id").toString().replace("\"", "");
                                    primary_teacher_First_name = jsonObjectForProcessing.get("teacher_firstName").toString().replace("\"", "");
                                    primary_teacher_Last_name = jsonObjectForProcessing.get("teacher_lastName").toString().replace("\"", "");

                                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!   " + mother_name);

                                }
                                if (userRole.equals("Teacher")) {

                                    teacherRegid = jsonObjectForProcessing.get("teacherRegid").toString().replace("\"", "");

                                    qualification = jsonObjectForProcessing.get("qualification").toString().replace("\"", "");
                                    speciality = jsonObjectForProcessing.get("speciality").toString().replace("\"", "");
                                    maritalStatus = jsonObjectForProcessing.get("maritalStatus").toString().replace("\"", "");
                                    teacherRecordid = jsonObjectForProcessing.get("id").toString().replace("\"", "");
                                    specialInfo = jsonObjectForProcessing.get("specialInfo").toString().replace("\"", "");

                                    school_class_id = jsonObjectForProcessing.get("school_class_id").toString().replace("\"", "");
                                }
                                if (userRole.equals("Admin")) {
                                    admin_id = jsonObjectForProcessing.get("id").toString().replace("\"", "");
                                }

                                session.createLoginSession(firstnameresponse, emailidresponse, lastnamerespnse, userPassword,
                                        userRole, middleName, gender, bloodGroup, dateOfBirth, address, profilePicURL,
                                        speciality, phone, maritalStatus, user_id, school_class_id, teacherRegid,
                                        qualification, specialInfo, student_id, enrollmentNo, rollNo, houseColor,
                                        illness, emergencyContact, admin_id, teacherRecordid, primary_teacher_First_name + " " + primary_teacher_Last_name, primaryClass, primaryDivison, userPin, mother_name, busId, busPickPoint);

                                jsonObjectforgetting_teaching_data = new JsonObject();
                                jsonObjectforgetting_teaching_data.addProperty("teacher_id", teacherRecordid);

                                    /*
                                          Fetching Teaher data  as well download profile pic
                                     */
                                teachingdata();
                                //downloadProfilePic();
                                /*********/

                                Log.d("teachingData(); ", "after Calling");


                                if (activeStatusCheck == 1) {
                                    loginprogressdialog.dismiss();

//                                        create sqlite data after login
                                    if (userRole.equals("Student")) {

                                    }
                                    if (userRole.equals("Teacher")) {


                                    }
                                    if (userRole.equals("Admin")) {

                                    }
                                        /*  Registering  app for Notifiaciton in Azure Notifiaction Hub */
                                    // new RgisterForNotifaction().execute();
                                    registerWithNotificationHubs();

                                    System.out.println("DatabaseHandler STARTED");
                                    databaseHandler = new DatabaseHandler(LoginActivity.this);
                                    System.out.println("DatabaseHandler FINISHED");
                                    System.out.println("add data FINISHED");


                                    validuserintent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(validuserintent);

                                    finish();
                                } else {
                                    loginprogressdialog.dismiss();
                                    alert.showAlertDialog(LoginActivity.this, "Login failed..", "Account deactive", false);
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                } catch (Exception e) {
                    System.out.println("JsonArray Exception * * * * * * * * * * * * * * * *" + response);
                }
            }
        });


//            MainActivity.mClient.invokeApi("loginapi", jsonObjectgetting_login_credential, new ApiJsonOperationCallback() {
//                    @Override
//                    public void onCompleted(JsonElement jsonObject, Exception exception, ServiceFilterResponse response) {
//                        Log.d("jsonObject response", "::" + jsonObject);
//
//                      /*check if json response is false
//                      * here json false = role specified by user during login and role corresponding to user credentials are diffrent
//                        */
//                        try {
//
//                            if (jsonObject.equals(null)) {
//                                timeOutTimerClass.check = false;
//                                System.out.println("Continue Execution");
//                            } else {
//                                timeOutTimerClass.check = true;
//                                System.out.println("Terminate Execution");
//                            }
//                            String jsonToString = jsonObject.toString();
//                            System.out.println("jsonToString" + jsonToString);
//                            if (jsonToString.equals("false")) {
//                                Log.d("user role mismatch", "user role mismatch");
//                                loginprogressdialog.dismiss();
//                                timeOutTimerClass.cancel();
//                                /*
//                                    method is used to remove all cancelled tasks from this timer's task queue.
//                                 */
//                                timer.purge();
//                                timeOutTimerClass = new TimeOutTimerClass();
//                                timer = new Timer();
//
//
//                                AlertDialog.Builder invalidcredentialsBuilder = new AlertDialog.Builder(LoginActivity.this);
//                                invalidcredentialsBuilder.setTitle("School On");
//                                invalidcredentialsBuilder.setMessage("The username,password and userrole you have entered is incorrect");
//                                invalidcredentialsBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        txtEmail.setText("");
//                                        txtPassword.setText("");
//                                    }
//                                });
//                                invalidcredentialsBuilder.setCancelable(false);
//                                invalidcredentialsBuilder.create().show();
//
//
//                            } else {
//
//                                try {
//                                    JsonArray jsonArray = jsonObject.getAsJsonArray();
//                                    Log.d("jsonArray.size", "jsonArray.size " + jsonArray.size());
//                                    System.out.println(" jsonArray.size " + jsonArray.size());
//                                    if ((jsonArray.size()) == 0) {
//                                        loginprogressdialog.dismiss();
//
//                                        AlertDialog.Builder invalidcredentialsBuilder = new AlertDialog.Builder(LoginActivity.this);
//                                        invalidcredentialsBuilder.setTitle("School On");
//                                        invalidcredentialsBuilder.setMessage("The username,password and userrole you have entered is incorrect");
//                                        invalidcredentialsBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                txtEmail.setText("");
//                                                txtPassword.setText("");
//                                            }
//                                        });
//                                        invalidcredentialsBuilder.setCancelable(false);
//                                        invalidcredentialsBuilder.create().show();
//                                        Log.d("Invalid credentials", "invalid credentials");
//                                        System.out.println(" Invalid credentials" );
//
//                                          /*
//                                    method is used to remove all cancelled tasks from this timer's task queue.
//                                 */
//                                        timer.purge();
//                                        timeOutTimerClass = new TimeOutTimerClass();
//                                        timer = new Timer();
//
//                                    } else {
//                                        JsonObject jsonObjectForProcessing = jsonArray.get(0).getAsJsonObject();
//
//                                        userRole = jsonObjectForProcessing.get("userRole").toString().replace("\"", "");
//                                        emailidresponse = jsonObjectForProcessing.get("email").toString().replace("\"", "");
//                                        userPassword = jsonObjectForProcessing.get("userPassword").toString().replace("\"", "");
//                                        userPin = jsonObjectForProcessing.get("userPin").toString().replace("\"", "");
//                                        activeStatusCheck = Integer.parseInt(jsonObjectForProcessing.get("active").toString());
//                                        firstnameresponse = jsonObjectForProcessing.get("firstName").toString().replace("\"", "");
//                                        lastnamerespnse = jsonObjectForProcessing.get("lastName").toString().replace("\"", "");
//                                        middleName = jsonObjectForProcessing.get("middleName").toString().replace("\"", "");
//                                        gender = jsonObjectForProcessing.get("gender").toString().replace("\"", "");
//                                        bloodGroup = jsonObjectForProcessing.get("bloodGrp").toString().replace("\"", "");
//                                        phone = jsonObjectForProcessing.get("phone").toString().replace("\"", "");
//                                        dateOfBirth = jsonObjectForProcessing.get("dateOfBirth").toString().replace("\"", "");
//                                        address = jsonObjectForProcessing.get("address").toString().replace("\"", "");
//                                        profilePicURL = jsonObjectForProcessing.get("profilePicURL").toString().replace("\"", "");
//                                        user_id = jsonObjectForProcessing.get("Users_id").toString().replace("\"", "");
//                                        primaryClass = jsonObjectForProcessing.get("class").toString().replace("\"", "");
//                                        primaryDivison = jsonObjectForProcessing.get("division").toString().replace("\"", "");
//                                        System.out.println("primaryClass @@@@@@ " + primaryClass);
//                                        System.out.println("primaryDivison @@@@@@ " + primaryDivison);
//                                        System.out.println("userRole @@@@@@ --------------------------------------" + userRole);
//
//
////
//
//                                        if (userRole.equals("Student")) {
//
//                                            enrollmentNo = jsonObjectForProcessing.get("studentEnrollmentNo").toString().replace("\"", "");
//                                            rollNo = jsonObjectForProcessing.get("rollNo").toString().replace("\"", "");
//                                            houseColor = jsonObjectForProcessing.get("houseColor").toString().replace("\"", "");
//                                            illness = jsonObjectForProcessing.get("illness").toString().replace("\"", "");
//                                            emergencyContact = jsonObjectForProcessing.get("emergencyContact").toString().replace("\"", "");
//                                            school_class_id = jsonObjectForProcessing.get("School_Class_id").toString().replace("\"", "");
//                                            specialInfo = jsonObjectForProcessing.get("specialInfo").toString().replace("\"", "");
//                                            student_id = jsonObjectForProcessing.get("id").toString().replace("\"", "");
//                                            mother_name = jsonObjectForProcessing.get("mothersName").toString().replace("\"", "");
//                                            teacherRecordid = jsonObjectForProcessing.get("Teacher_id").toString().replace("\"", "");
//                                            primary_teacher_First_name = jsonObjectForProcessing.get("teacher_firstName").toString().replace("\"", "");
//                                            primary_teacher_Last_name = jsonObjectForProcessing.get("teacher_lastName").toString().replace("\"", "");
//
//                                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!   " + mother_name);
//
//                                        }
//                                        if (userRole.equals("Teacher")) {
//
//                                            teacherRegid = jsonObjectForProcessing.get("teacherRegid").toString().replace("\"", "");
//
//                                            qualification = jsonObjectForProcessing.get("qualification").toString().replace("\"", "");
//                                            speciality = jsonObjectForProcessing.get("speciality").toString().replace("\"", "");
//                                            maritalStatus = jsonObjectForProcessing.get("maritalStatus").toString().replace("\"", "");
//                                            teacherRecordid = jsonObjectForProcessing.get("id").toString().replace("\"", "");
//                                            specialInfo = jsonObjectForProcessing.get("specialInfo").toString().replace("\"", "");
//
//
//
//
//                                            /*
//                                            Cloumn25 because teacher login process does not return school class id
//                                             with "schoolClassId" variable name
//                                            instead returns it with name "column25"
//                                            check it later some day
//                                            */
//                                            school_class_id = jsonObjectForProcessing.get("school_class_id").toString().replace("\"", "");
//                                        }
//                                        if (userRole.equals("Admin")) {
//                                            admin_id = jsonObjectForProcessing.get("id").toString().replace("\"", "");
//
////                                            admin_id = admin_id.substring(1, admin_id.length() - 1);
//                                        }
//
////                                        Log.d("String", "emailidresponse  " + emailidresponse);
////                                        Log.d("String", "userRole  " + userRole);
////                                        Log.d("activeStatusCheck", "activeStatusCheck  " + "" + activeStatusCheck);
////                                        Log.d("String", "userPassword  " + userPassword);
////                                        Log.d("String", "userPin  " + userPin);
////                                        Log.d("firstnameresponse", "firstnameresponse  " + firstnameresponse);
////                                        Log.d("lastnamerespnse", "lastnamerespnse  " + lastnamerespnse);
////                                        Log.d("teacherRecordid", "teacherRecordid  " + teacherRecordid);
////                                        Log.d("teacherPrimaryClass", "teacherPrimaryClass  " + primaryClass);
////                                        Log.d("teacherPrimaryDivison", "teacherPrimaryDivison  " + primaryDivison);
////
////                                        Log.d("Log ", "::2");
////
////                                        System.out.println("1.KEY_FIRST_NAME " + firstnameresponse);
////                                        System.out.println("2.KEY_LAST_NAME " + lastnamerespnse);
////                                        System.out.println("3.KEY_MIDDLE_NAME " + middleName);
////                                        System.out.println("4.KEY_PHONE " + phone);
////                                        System.out.println("5.KEY_ROLL_NO " + rollNo);
////                                        System.out.println("6.KEY_SPECIAL_INFO " + specialInfo);
////                                        System.out.println("7.KEY_GENDER " + gender);
////                                        System.out.println("8.KEY_BLOOD_GROUP " + bloodGroup);
////                                        System.out.println("9.KEY_ADDRESS " + address);
////                                        System.out.println("10.KEY_DATE_OF_BIRTH " + dateOfBirth);
////                                        System.out.println("11.KEY_SPECIAL_ILLNESS " + illness);
////                                        System.out.println("12.KEY_EMERGENCY_CONTACT " + emergencyContact);
//
//
//                                        session.createLoginSession(firstnameresponse, emailidresponse, lastnamerespnse, userPassword,
//                                                userRole, middleName, gender, bloodGroup, dateOfBirth, address, profilePicURL,
//                                                speciality, phone, maritalStatus, user_id, school_class_id, teacherRegid,
//                                                qualification, specialInfo, student_id, enrollmentNo, rollNo, houseColor,
//                                                illness, emergencyContact, admin_id, teacherRecordid, primary_teacher_First_name + " " + primary_teacher_Last_name, primaryClass, primaryDivison, userPin, mother_name);
//
//                                        jsonObjectforgetting_teaching_data = new JsonObject();
//                                        jsonObjectforgetting_teaching_data.addProperty("teacher_id", teacherRecordid);
//
//                                    /*
//                                          Fetching Teaher data  as well download profile pic
//                                     */
//                                        teachingdata();
//                                        //downloadProfilePic();
//                                        /*********/
//
//                                        Log.d("teachingdata(); ", "after Calling");
//
//
//                                        if (activeStatusCheck == 1) {
//                                            loginprogressdialog.dismiss();
//
////                                        create sqlite data after login
//                                            if (userRole.equals("Student")) {
//
//                                            }
//                                            if (userRole.equals("Teacher")) {
//
//
//                                            }
//                                            if (userRole.equals("Admin")) {
//
//                                            }
//                                        /*
//                                                Registering  app for Notifiaciton in Azure Notifiaction Hub
//                                         */
//                                            new RgisterForNotifaction().execute();
//
//
//                                            System.out.println("DatabaseHandler STARTED");
//                                            databaseHandler = new DatabaseHandler(LoginActivity.this);
//
//                                            System.out.println("DatabaseHandler FINISHED");
////                                        dataBaseHelper.addNotificationToDatabase(noticeListData);
//                                            System.out.println("add data FINISHED");
//
//
//                                            validuserintent = new Intent(getApplicationContext(), MainActivity.class);
//                                            startActivity(validuserintent);
//
//                                            finish();
//                                        } else {
//                                            loginprogressdialog.dismiss();
//                                            alert.showAlertDialog(LoginActivity.this, "Login failed..", "Account deactive", false);
//                                        }
//                                    }
//
//                                } catch (Exception e) {
//
//                                }
//                            }
//                        } catch (Exception e) {
////                            loginprogressdialog.dismiss();
//                            System.out.println("JsonArray Exception * * * * * * * * * * * * * * * *" + jsonObject);
//                        }
//                    }
//
//
//
    }


    public void registerWithNotificationHubs() {
        // Start IntentService to register this application with FCM.
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
      /*  if (checkPlayServices()) {
        }*/
    }

//    public class RgisterForNotifaction extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            try {
//                NotificationsManager.handleNotifications(getApplicationContext(), SENDER_ID, AppNotificationHandler.class);
//                Log.d("Gcm object intial", "1 Login");
//                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//                Log.d("Gcm object intial", "2 Login");
//                hub = new NotificationHub(HubName, HubListenConnectionString, getApplicationContext());
//                Log.d("Gcm object intial", "3 Login");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//    }

    private void teachingdata() {

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchTeachingData", jsonObjectforgetting_teaching_data);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" fetchteachingdata Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("fetchteachingdata response    " + response);

                JsonArray jsonArray = response.getAsJsonArray();
                System.out.println("jsonArray.size----------------------------------------" + jsonArray.size());

                for (int insertdata = 0; insertdata < jsonArray.size(); insertdata++) {

                    //old database (Working). Should be deleted later
                    JsonObject jsonObjectForProcessing = jsonArray.get(insertdata).getAsJsonObject();
                    sqlite_schoolClassId = jsonObjectForProcessing.get("id").toString();
                    sqlite_class = jsonObjectForProcessing.get("class").toString();
                    sqlite_division = jsonObjectForProcessing.get("division").toString();
                    sqlite_subject = jsonObjectForProcessing.get("subject").toString();
                    //sqlite_sec_teacher_id = jsonObjectForProcessing.get("subject").toString();

                    sqlite_schoolClassId = sqlite_schoolClassId.substring(1, sqlite_schoolClassId.length() - 1);
                    sqlite_class = sqlite_class.substring(1, sqlite_class.length() - 1);
                    sqlite_division = sqlite_division.substring(1, sqlite_division.length() - 1);
                    sqlite_subject = sqlite_subject.substring(1, sqlite_subject.length() - 1);

                    System.out.println(" sqlite_schoolClassId " + sqlite_schoolClassId);
                    System.out.println(" sqlite_class " + sqlite_class);
                    System.out.println(" sqlite_division " + sqlite_division);
                    System.out.println(" sqlite_subject " + sqlite_subject);

                    //appDatabase.execSQL("INSERT INTO Teacher_Class_Table(schoolClassId,class,division,subject) values('" + sqlite_schoolClassId + "','" + sqlite_class + "','" + sqlite_division + "','" + sqlite_subject + "')");
                    System.out.println("data inserted in table  " + insertdata);

                    //New database record entry. delete old one once all linking is done by new one.
                    databaseHandler.addTeacherClassRecordToDatabase(sqlite_schoolClassId, sqlite_class, sqlite_division, sqlite_subject);


                }

            }
        });
    }

    //
//    private void teachingdata() {
//
//        MainActivity.mClient.invokeApi("fetchteachingdata", jsonObjectforgetting_teaching_data, new ApiJsonOperationCallback() {
//            @Override
//            public void onCompleted(JsonElement jsonObject, Exception exception, ServiceFilterResponse response) {
//                System.out.println("fetchteachingdata::---------------------------------" + jsonObject);
//                JsonArray jsonArray = jsonObject.getAsJsonArray();
//                System.out.println("jsonArray.size----------------------------------------" + jsonArray.size());
//                //appDatabase.execSQL("CREATE TABLE IF NOT EXISTS Teacher_Class_Table(id VARCHAR,schoolClassId VARCHAR,class VARCHAR,division VARCHAR,subject VARCHAR);");
//                Log.d("table created ", " :");
//                for (int insertdata = 0; insertdata < jsonArray.size(); insertdata++) {
//
//                    //old database (Working). Should be deleted later
//                    JsonObject jsonObjectForProcessing = jsonArray.get(insertdata).getAsJsonObject();
//                    sqlite_schoolClassId = jsonObjectForProcessing.get("id").toString();
//                    sqlite_class = jsonObjectForProcessing.get("class").toString();
//                    sqlite_division = jsonObjectForProcessing.get("division").toString();
//                    sqlite_subject = jsonObjectForProcessing.get("subject").toString();
//
//                    sqlite_schoolClassId = sqlite_schoolClassId.substring(1, sqlite_schoolClassId.length() - 1);
//                    sqlite_class = sqlite_class.substring(1, sqlite_class.length() - 1);
//                    sqlite_division = sqlite_division.substring(1, sqlite_division.length() - 1);
//                    sqlite_subject = sqlite_subject.substring(1, sqlite_subject.length() - 1);
//
//                    Log.d("sqlite_schoolClassId", "::" + sqlite_schoolClassId);
//                    Log.d("sqlite_class", "::" + sqlite_class);
//                    Log.d("sqlite_division", "::" + sqlite_division);
//                    Log.d("sqlite_subject", "::" + sqlite_subject);
//
//                    //appDatabase.execSQL("INSERT INTO Teacher_Class_Table(schoolClassId,class,division,subject) values('" + sqlite_schoolClassId + "','" + sqlite_class + "','" + sqlite_division + "','" + sqlite_subject + "')");
//                    Log.d("data inserted in table ", " :" + insertdata);
//
//                    //New database record entry. delete old one once all linking is done by new one.
//                    databaseHandler.addTeacherClassRecordToDatabase(sqlite_schoolClassId, sqlite_class, sqlite_division, sqlite_subject);
//
//
//                }
//
//
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit ?")
                .setMessage("Are you sure you want to exit ?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentfinish = new Intent(Intent.ACTION_MAIN);
                        intentfinish.addCategory(Intent.CATEGORY_HOME);
                        intentfinish.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentfinish);
                        finish();
                        System.exit(1);
                    }
                })
                .create().show();

    }

    public class TimeOutTimerClass extends TimerTask {
        Boolean check = false;

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!check) {
                        timer.cancel();
                        loginprogressdialog.dismiss();
                        try {
                            new AlertDialog.Builder(LoginActivity.this).setMessage(R.string.check_network)
                                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            reScheduleTimer();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            System.out.println("Cancel Button  pressed");
                                            timeOutTimerClass.cancel();
                                            /*
                                            method is used to remove all cancelled tasks from this timer's task queue.
                                             */
                                            timer.purge();


                                            timeOutTimerClass = new TimeOutTimerClass();
                                            timer = new Timer();

                                        }
                                    }).setCancelable(false).create().show();
                        } catch (Exception e) {
                              /*
                                    it may produce null pointer exception on creation of alert dialog object
                             */
                            System.out.println("Exception Handle for Alert Dialog");
                        }

                    } else {
                          /*
                              this line of code is used when everything goes normal .
                              and cancel the timer.
                         */
                        timer.cancel();
                    }

                }
            });

        }
    }

    public void downloadProfilePic() {
        try {

            final String storageConnectionString = "DefaultEndpointsProtocol=http;" + "AccountName=sodataattachment;" + "AccountKey=FMYWSHReV9q+Wsll+yAza4LDk/HS9+t75+ZAJGQC/ammjJhz3XimQV6xGBAs4ojX+TFGtfNzRpZ8ZxRhkqQ+Rg==";

            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            // Get a reference to a container.
            // The container name must be lower case
            container = blobClient.getContainerReference("schoolonlineblobattachment");
            if (container.listBlobs() != null) {
                Log.d("in if ", "list conatin value");
                new downloadingAttachment().execute();
            } else {
                Log.d("in else ", "list does not any contain value ");
            }

        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    public class downloadingAttachment extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("in if ", "container.listBlobs();" + container.listBlobs());
            Iterable<ListBlobItem> valuesofConatainer = container.listBlobs();
            Log.d("in if ", "valuesofConatainer=" + valuesofConatainer);
            Log.d("in if ", "valuesofConatainer=" + valuesofConatainer.iterator().toString());

            for (ListBlobItem blobItem : container.listBlobs(fileName)) {
                // If the item is a blob, not a virtual directory.
                if (blobItem instanceof CloudBlob) {
                    // Download the item and save it to a file with the same name.
                    CloudBlob blob = (CloudBlob) blobItem;
                    try {
                        blob.download(new FileOutputStream(directory + "/" + blob.getName()));
                        // blob.download(new FileOutputStream(dirname + blob.getStreamWriteSizeInBytes()));
                        System.out.println("blob name***************** ASync Taskkkkkkkk" + blob.getName());
                        profile_pic_name = blob.getName();

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            session.setSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME, profile_pic_name);
            //  pofile_pic_filePath = "file://" + directory + "/" + profile_pic_name;
            // System.out.println("pofile_pic_filePath" + pofile_pic_filePath);
        }
    }


}
