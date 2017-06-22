package com.relecotech.androidsparsh.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.AlertStudentAdapterAdapter;
import com.relecotech.androidsparsh.azurecontroller.Alert;
import com.relecotech.androidsparsh.azurecontroller.Assignment_Attachment;
import com.relecotech.androidsparsh.controllers.AlertStudentListData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.relecotech.androidsparsh.activities.AssignmentPost.storageConnectionString;


/**
 * Created by amey on 10/15/2015.
 */
public class AlertPost extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int REQUEST_TAKE_PHOTO_FROM_CAMERA = 1;
    private static final int REQUEST_PICK_IMAGE_FROM_GALLERY = 2;
    private static final int REQUEST_PICK_FILE_FROM_FILESYSTEM = 3;
    private String ATTACHEMENT_RESOURCE_NAME;
    private ProgressDialog alertProgressDialog;
    Date alertPostingDate;
    EditText alertDescriptionEditText;
    Spinner alertClassSpinner, alertDivisionSpinner, alertTagSpinner, alertStudentSpinner, alertPrioritySpinner;
    ImageView alertAttachmentImageView;
    ArrayAdapter<String> adapterClass;
    ArrayAdapter<String> adapterDivision;
    ArrayAdapter<String> adapterTag;
    ArrayAdapter<String> adapterAlertPriority;
    EditText alerTtitleEditText;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    MobileServiceTable<Assignment_Attachment> mAssignment_Attachment;
    MobileServiceTable<Alert> mAlert;
    Alert alertItem;
    private JsonObject jsonObjectForAddAlert;
    SessionManager manager;
    static int testvarible;
    private boolean alertStatus;
    private SQLiteDatabase appDatabase;
    private DatabaseHandler databaseHandler;
    private Cursor resultSet;
    private String alertClassString;
    private MobileServiceJsonTable mobileServiceJsonTable;
    private String student_Id;
    private String student_firstName;
    private String student_lastName;
    private String student_fullName;
    private String studentID;
    private String divString;
    private String KEY_SCHOOL_CLASS_ID;
    private String imageFileName;
    private String attachedFilePath_From_SaveImage;
    private String attachedFilePath;
    private String ATTACHED_FILE_MAP_KEY_NAME;
    private String attachedFileName;
    private String timestamp;
    private String attachmentUniqueIdentifier;
    private List<String> checkingDuplicateStudentID;
    private List<String> alertDdivlist;
    private List<String> alertClasslist;
    private List<String> alertTaglist;
    private List<String> alertPrioritylist;
    private ArrayList<AlertStudentListData> studentSpinnerList;
    private AlertStudentListData spinnerStudentListData;
    private AlertStudentAdapterAdapter studentAdapter;

    private int check = 0;
    private HashMap<String, String> userDetails;
    private HashMap<String, String> condtionHashMap;
    private Map<String, String> alert_Attached_Map;
    private TextView alertAttachmentTextView;
    private File mPhotoFile;
    private Uri mPhotoFileUri;
    private View snackBarView;
    private File myDir;
    private File file;
    private Uri selectedImage;
    private Uri selectedFile;
    private AlertDialog.Builder confirmAlertDialog;

    private boolean addAlertExceptionFlag;
    private JsonObject jsonObjectAddAlertParameters;
    private Uri imagePathToSendAzureStorage;
    private String tagSpinnerSelectedItem;


    @Override

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertClassSpinner = (Spinner) findViewById(R.id.spinnerAlertClass);
        alertDivisionSpinner = (Spinner) findViewById(R.id.spinnerAlertDivision);
        alertTagSpinner = (Spinner) findViewById(R.id.spinnerAlertTag);
        alertStudentSpinner = (Spinner) findViewById(R.id.spinnerAlertStudent);
        alertDescriptionEditText = (EditText) findViewById(R.id.alertTextDescription);
        alerTtitleEditText = (EditText) findViewById(R.id.alertTitletextView);
        alertAttachmentTextView = (TextView) findViewById(R.id.alertAttachmentTextView);
        alertPrioritySpinner = (Spinner) findViewById(R.id.spinnerAlertPriority);
        alertAttachmentImageView = (ImageView) findViewById(R.id.attachmentImageView);
        System.out.println("alertClassSpinner_________________________" + alertClassSpinner);
        System.out.println("alertClassSpinner_________________________" + alertClassSpinner);
        System.out.println("alertDivisionSpinner______________________" + alertDivisionSpinner);
        System.out.println("alertTagSpinner___________________________" + alertTagSpinner);
        System.out.println("alertStudentSpinner_______________________" + alertStudentSpinner);


        jsonObjectAddAlertParameters = new JsonObject();
        snackBarView = (View) findViewById(R.id.view24);
        appDatabase = openOrCreateDatabase("SCHOOL_ONLINE_GLOBAL_DATABASE", MODE_PRIVATE, null);
        databaseHandler = new DatabaseHandler(AlertPost.this);
        condtionHashMap = new HashMap<>();
        alert_Attached_Map = new HashMap<>();
        alertProgressDialog = new ProgressDialog(AlertPost.this);

        //Session and Hashmap intialization.
        manager = new SessionManager(this);
        userDetails = manager.getUserDetails();
        mobileServiceJsonTable = MainActivity.mClient.getTable("student");
        studentSpinnerList = new ArrayList<>();
        checkingDuplicateStudentID = new ArrayList<>();
        alertDdivlist = new ArrayList<>();
        alertClasslist = new ArrayList<>();
        calendar = Calendar.getInstance();


        try {
            mAssignment_Attachment = MainActivity.mClient.getTable(Assignment_Attachment.class);
            mAlert = MainActivity.mClient.getTable(Alert.class);
            alertItem = new Alert();
            Log.d("MainActmClient.getTable", "TRY block");

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MainActmClient.getTable", "" + e);
        }

        //alertPostingDate = new Date(calendar.getTimeInMillis());
        System.out.println("alertPostingDate" + alertPostingDate);

        resultSet = databaseHandler.getTeacherClassDataByCursor();

        resultSet.moveToFirst();
        for (int i = 0; i < resultSet.getCount(); i++) {
            if (!alertClasslist.contains(resultSet.getString(2))) {
                alertClasslist.add(resultSet.getString(2));
            }
            resultSet.moveToNext();
        }
        alertClasslist.add("[ Class ]");

        //studentSpinnerList.add("[ Select Student]");

        System.out.println("classlist : " + alertClasslist);

        adapterClass = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, alertClasslist);
        adapterClass.setDropDownViewResource(R.layout.spinner_dropdown_item);
        alertClassSpinner.setAdapter(adapterClass);
        alertClassSpinner.setSelection(adapterClass.getCount() - 1);

        alertDdivlist.add("[ Division ]");
        adapterDivision = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, alertDdivlist);
        adapterDivision.setDropDownViewResource(R.layout.spinner_dropdown_item);
        alertDivisionSpinner.setAdapter(adapterDivision);
        alertDivisionSpinner.setSelection(adapterDivision.getCount() - 1);

        studentSpinnerList.add(new AlertStudentListData("[ Select Student ]", "0"));
        studentAdapter = new AlertStudentAdapterAdapter(getApplicationContext(), studentSpinnerList);
        alertStudentSpinner.setAdapter(studentAdapter);
        alertStudentSpinner.setSelection(studentAdapter.getCount() - 1);


        alertClassSpinner.setOnItemSelectedListener(AlertPost.this);
        alertDivisionSpinner.setOnItemSelectedListener(AlertPost.this);

        alertTaglist = new ArrayList<>();
        alertTaglist.add("Assignment");
        alertTaglist.add("Attendance");
        alertTaglist.add("Reports");
        alertTaglist.add("Fees");
        alertTaglist.add("Complaint");
        alertTaglist.add("Others");
        alertTaglist.add("Student Unwell");
        alertTaglist.add("[ Alert Category ]");

        alertPrioritylist = new ArrayList<>();
        alertPrioritylist.add("Normal");
        alertPrioritylist.add("Urgent");
        alertPrioritylist.add("[ Priority ]");

        adapterTag = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, alertTaglist);
        adapterTag.setDropDownViewResource(R.layout.spinner_dropdown_item);
        alertTagSpinner.setAdapter(adapterTag);
        alertTagSpinner.setSelection(adapterTag.getCount() - 1);


        adapterAlertPriority = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, alertPrioritylist);
        adapterAlertPriority.setDropDownViewResource(R.layout.spinner_dropdown_item);
        alertPrioritySpinner.setAdapter(adapterAlertPriority);
        alertPrioritySpinner.setSelection(adapterAlertPriority.getCount() - 1);


        alertAttachmentTextView.setVisibility(View.GONE);
        alertAttachmentImageView.setVisibility(View.GONE);

        alertAttachmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAlertDialog = new AlertDialog.Builder(AlertPost.this);
                confirmAlertDialog.setTitle("Do you want to Delete ?");
                confirmAlertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attachedFilePath_From_SaveImage = null;
                        alertAttachmentTextView.setText("");
                        alertAttachmentTextView.setVisibility(View.GONE);
                        alertAttachmentImageView.setVisibility(View.GONE);
                        alert_Attached_Map.clear();
                    }

                });
                confirmAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                confirmAlertDialog.create().show();
            }
        });

        alertTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tagSpinnerSelectedItem = (String) parent.getItemAtPosition(position);
                System.out.println("tagSpinnerSelectedItem--------" + tagSpinnerSelectedItem);
                if (!tagSpinnerSelectedItem.equals("[ Alert Category ]")) {
                    if (!tagSpinnerSelectedItem.equals("Student Unwell") && !tagSpinnerSelectedItem.equals("Complaint")) {
                        System.out.println("Yes Sick not selected ");
                        alertPrioritySpinner.setSelection(alertPrioritylist.indexOf("Normal"));
                        alerTtitleEditText.setText("");
                        alertDescriptionEditText.setText("");
                        alerTtitleEditText.setHint("Title");
                        alertDescriptionEditText.setHint("Add Description here");
                    }
                    else {
                        if (tagSpinnerSelectedItem.contains("Complaint")){
                            alertPrioritySpinner.setSelection(alertPrioritylist.indexOf("Urgent"));
                            alerTtitleEditText.setText("Complaint Note");
                            alertDescriptionEditText.setText("Hello Parents, Please take note of below : ");
                        }
                        if (tagSpinnerSelectedItem.contains("Student Unwell")) {
                            System.out.println("Yes Sick selected ");
                            alertPrioritySpinner.setSelection(alertPrioritylist.indexOf("Urgent"));
                            alerTtitleEditText.setText("Student Unwell - Urgent");
                            alertDescriptionEditText.setText("Hello Parents, Your child is not well. Please report to school.");
                        }
                    }
                } else {
                    alertPrioritySpinner.setSelection(alertPrioritylist.indexOf("[ Priority ]"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long id) {
        Spinner spinner = (Spinner) parent;
        check = check + 1;
        /*
        Avoid onItemSelected calls when oncreate is invoke
         */
        if (check > 1) {
            if (spinner.getId() == R.id.spinnerAlertClass) {
                System.out.println("spinnerAlertClass   spinnerAlertClass");
                alertClassString = alertClassSpinner.getSelectedItem().toString();
                Log.d("alertClassString", "alertClassString" + alertClassString);
                condtionHashMap.put("class", alertClassString);
                // resultSet = appDatabase.rawQuery("select * from Teacher_Class_Table where class = '" + alertClassString + "'", null);
                resultSet = databaseHandler.getTeacherClassData(condtionHashMap);
                Log.d("resultSet", "aaaaa   " + resultSet.getCount());
                resultSet.moveToFirst();
                alertDdivlist.clear();
                for (int i = 0; i < resultSet.getCount(); i++) {
                    if (!alertDdivlist.contains(resultSet.getString(3))) {
                        alertDdivlist.add(resultSet.getString(3));
                    }
                    resultSet.moveToNext();

                }
                alertDdivlist.add("All");
                alertDdivlist.add("[ Division ]");
                adapterDivision = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, alertDdivlist);
                adapterDivision.setDropDownViewResource(R.layout.spinner_dropdown_item);
                alertDivisionSpinner.setAdapter(adapterDivision);
                alertDivisionSpinner.setSelection(adapterDivision.getCount() - 1);


            } else if (spinner.getId() == R.id.spinnerAlertDivision) {
                if (check > 1) {
                    System.out.println("spinnerAlertDivision   spinnerAlertDivision");
                    divString = alertDivisionSpinner.getSelectedItem().toString();
                    condtionHashMap.put("class", alertClassString);
                    condtionHashMap.put("division", divString);
                    resultSet = databaseHandler.getTeacherClassData(condtionHashMap);
                    // resultSet = appDatabase.rawQuery("select * from Teacher_Class_Table where class = '" + alertClassString + "' and division = '" + divString + "'", null);
                    resultSet.moveToFirst();
                    System.out.println("selection getCount getCount   " + resultSet.getCount());
                    studentSpinnerList.clear();
                    for (int i = 0; i < resultSet.getCount(); i++) {
                        System.out.println("Assignment post getCount getCount   " + resultSet.getString(1));
                        KEY_SCHOOL_CLASS_ID = resultSet.getString(1);
                        System.out.println("KEY_SCHOOL_CLASS_ID post   " + KEY_SCHOOL_CLASS_ID);
                    }
                    resultSet.moveToNext();

                    System.out.println("KEY_SCHOOL_CLASS_ID" + KEY_SCHOOL_CLASS_ID);
                    System.out.println("divString " + divString);

                    if (!divString.equals("[ Division ]")) {
                        System.out.println("in if condition of division string checking");

                        if (divString.equals("All")) {
                            System.out.println("in if condtion  All");
                            studentSpinnerList.clear();
                            studentSpinnerList.add(new AlertStudentListData("All", "All"));
                            studentSpinnerList.add(0, new AlertStudentListData("[ Select Student ]", "0"));
                            studentAdapter = new AlertStudentAdapterAdapter(this, studentSpinnerList);
                            alertStudentSpinner.setAdapter(studentAdapter);
                            try {
                                alertStudentSpinner.setSelection(alertStudentSpinner.getCount() - 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            spinnerStudentListData = studentSpinnerList.get(pos);
                            studentID = spinnerStudentListData.getId();
                        } else {

                            new FetchingStudent().execute();

                        }
                    } else {
                        System.out.println("Select divison");
                    }

                }

            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void submitAlert() {
        try {

            String AlertClassString = alertClassSpinner.getSelectedItem().toString();
            System.out.println("ALert class string ********* " + AlertClassString);

            String AlertDivString = alertDivisionSpinner.getSelectedItem().toString();
            System.out.println("AlertDivString *********** " + AlertDivString);

            String AlertTagString = alertTagSpinner.getSelectedItem().toString();
            System.out.println("AlertTagString ***************" + AlertTagString);


            AlertStudentListData AlertStudentListData_Object = (AlertStudentListData) alertStudentSpinner.getSelectedItem();
            String AlertStudentNameString = "";
            if (AlertStudentListData_Object != null) {
                AlertStudentNameString = AlertStudentListData_Object.getFullname();
                System.out.println("AlertStudentListData_Object " + AlertStudentListData_Object);
                System.out.println("AlertStudentNameString " + AlertStudentNameString);
            } else {
                System.out.println("AlertStudentListData_Object " + AlertStudentListData_Object);
                System.out.println("AlertStudentListData_Object  Null");
            }


            String AlertDesText = alertDescriptionEditText.getText().toString();
            System.out.println("AlertDesText **********" + AlertDesText);
            String AlertTitletext = alerTtitleEditText.getText().toString();

            //*****************************************************************************************


            //*****************************************************************************************
            if (AlertClassString != "[ Class ]") {

                if (AlertDivString != "[ Division ]") {

                    if (AlertStudentNameString != "[ Select Student ]") {

                        if (AlertTagString != "[ Alert Category ]") {

                            if (AlertTitletext.length() > 0) {

                                if (AlertDesText.length() > 0) {

                                    if (AlertTitletext.length() < 100) {
                                        timestamp = new SimpleDateFormat(("yyyyMMdd_HHmmss"), Locale.getDefault()).format(new Date());
                                        attachmentUniqueIdentifier = alertClassString + divString + alertTagSpinner.getSelectedItem().toString() + timestamp;
                                        System.out.println("Attachement identifer for alert *************************" + attachmentUniqueIdentifier);
                                        alertProgressDialog.setCancelable(false);
                                        alertProgressDialog.show();
                                        if (alert_Attached_Map.size() > 0) {
                                            /*
                                            Uploading Images , Documents,Photos to Azure Storage..
                                             */
                                            upload_Alert_Attachement();
                                        } else {
                                            /*
                                            Send Text data to alert tabele.
                                             */
                                            addAlertToAzure();
                                        }


                                    } else {
                                        Snackbar.make(snackBarView, "Title Too Long", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(snackBarView, "Add Description", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(snackBarView, "Add Title", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(snackBarView, "Select Category", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(snackBarView, "Select Student Name", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(snackBarView, "Select Division", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(snackBarView, "Select class", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            System.out.println("Error  " + e.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alert_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_send:
                submitAlert();
                return true;
            case R.id.action_attachment:
                if (attachedFilePath_From_SaveImage == null) {

                    showPopup();

                } else {

                    Toast.makeText(AlertPost.this, "Only one attachment allowed !", Toast.LENGTH_LONG).show();
                }
//                showPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void showPopup() {
        View menuItemView = findViewById(R.id.action_send);
        PopupMenu popup = new PopupMenu(AlertPost.this, menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_camera:
                        if (PermissionChecker.checkSelfPermission(AlertPost.this, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {

                            if (PermissionChecker.checkSelfPermission(AlertPost.this, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(AlertPost.this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                                /*  Camera Permission Granted.
                                    Open Camera for Alert Attachment */
                                System.out.println(" INSIDE opencamera ");
                                openCamera();
                            } else {
                                AlertDialog.Builder checkPermissionDialog = new AlertDialog.Builder(AlertPost.this);
                                checkPermissionDialog.setMessage("Allow Sparsh App to access Storage");
                                checkPermissionDialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent checkPermissionIntent = new Intent();
                                        checkPermissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        checkPermissionIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                        checkPermissionIntent.setData(Uri.parse("package:" + getPackageName()));
                                        checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                        startActivity(checkPermissionIntent);
                                    }
                                });
                                checkPermissionDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                checkPermissionDialog.show();
                            }

                        } else {
                            System.out.println(" INSIDE open camera permission ");
                            AlertDialog.Builder checkPermissionDialog = new AlertDialog.Builder(AlertPost.this);
                            checkPermissionDialog.setMessage("Allow Sparsh App to access Camera");
                            checkPermissionDialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent checkPermissionIntent = new Intent();
                                    checkPermissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    checkPermissionIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                    checkPermissionIntent.setData(Uri.parse("package:" + getPackageName()));
                                    checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(checkPermissionIntent);
                                }
                            });
                            checkPermissionDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            checkPermissionDialog.show();
                        }
                        break;
                    case R.id.action_gallery:
                        if (PermissionChecker.checkSelfPermission(AlertPost.this, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(AlertPost.this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {

                            /*  Gallery Permission Granted.
                                Browse image from Gallery for Assignment Attachement.
                             */
                            browseImage();

                        } else {
                            AlertDialog.Builder checkPermissionDialog = new AlertDialog.Builder(AlertPost.this);
                            checkPermissionDialog.setMessage("Allow Sparsh App to access Gallery");
                            checkPermissionDialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent checkPermissionIntent = new Intent();
                                    checkPermissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    checkPermissionIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                    checkPermissionIntent.setData(Uri.parse("package:" + getPackageName()));
                                    checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(checkPermissionIntent);
                                }
                            });
                            checkPermissionDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            checkPermissionDialog.show();
                        }


                        break;
                    case R.id.action_document:

                        if (PermissionChecker.checkSelfPermission(AlertPost.this, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(AlertPost.this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {

                            /*  Read Write file Explorer Permission Granted.
                                open file Explorer pick documents etc. for Alert  Attachment.
                             */

                            pickFileFromFileSystem();

                        } else {
                            AlertDialog.Builder checkPermissionDialog = new AlertDialog.Builder(AlertPost.this);
                            checkPermissionDialog.setMessage("Allow Sparsh App to access File Explorer");
                            checkPermissionDialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent checkPermissionIntent = new Intent();
                                    checkPermissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    checkPermissionIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                    checkPermissionIntent.setData(Uri.parse("package:" + getPackageName()));
                                    checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(checkPermissionIntent);
                                }
                            });
                            checkPermissionDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            checkPermissionDialog.show();
                        }


                        break;
                }
                return false;
            }
        });
        popup.show();

    }

    private static CloudBlobContainer getContainer() throws Exception {
        // Retrieve storage account from connection-string.

        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

        // Create the blob client.
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
        // Get a reference to a container.
        // The container name must be lower case
        CloudBlobContainer container = blobClient.getContainerReference("sparshdevmobileapp");

        return container;
    }

    private void openCamera() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.d("version ", "kitkat");

            takePicture();
        } else {
            Log.d("version ", "lower");
            Intent opencamIntent = new Intent(getApplicationContext(), PickCameraImage.class);
            startActivity(opencamIntent);
        }
    }

    private void takePicture() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                mPhotoFile = createImageFile();
                System.out.println("File path checking  >>>>>>>>" + mPhotoFile);


            } catch (Exception ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created

            if (mPhotoFile != null) {
                mPhotoFileUri = Uri.fromFile(mPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoFileUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_FROM_CAMERA);
                Log.d("Photo size after intent", "" + mPhotoFile.length());

            }
        }
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private void browseImage() {
        Intent browseIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(browseIntent, REQUEST_PICK_IMAGE_FROM_GALLERY);
    }

    private void pickFileFromFileSystem() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //  intent.setType("*/*");
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_PICK_FILE_FROM_FILESYSTEM);
    }

    public void addAlertToAzure() {

        alertProgressDialog.setMessage("Sending Alert");
        jsonObjectAddAlertParameters.addProperty("Category", alertTagSpinner.getSelectedItem().toString());
        jsonObjectAddAlertParameters.addProperty("Alert_class", alertClassSpinner.getSelectedItem().toString());
        jsonObjectAddAlertParameters.addProperty("Divison", alertDivisionSpinner.getSelectedItem().toString());
        jsonObjectAddAlertParameters.addProperty("Alert_description", alertDescriptionEditText.getText().toString());
        jsonObjectAddAlertParameters.addProperty("Title", alerTtitleEditText.getText().toString());
        jsonObjectAddAlertParameters.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));
        jsonObjectAddAlertParameters.addProperty("Attachment_identifier", attachmentUniqueIdentifier);
        jsonObjectAddAlertParameters.addProperty("Active", true);
        jsonObjectAddAlertParameters.addProperty("Postdate", calendar.getTimeInMillis());
        jsonObjectAddAlertParameters.addProperty("Student_id", studentID);
        jsonObjectAddAlertParameters.addProperty("Attachment_count", alert_Attached_Map.size());
        jsonObjectAddAlertParameters.addProperty("Alert_priority", alertPrioritySpinner.getSelectedItem().toString());


        System.out.println(" Alert_class   " + alertClassSpinner.getSelectedItem().toString());
        System.out.println(" KEY_STUDENT_ID   " + studentID);
        System.out.println(" Teacher_id" + userDetails.get(SessionManager.KEY_TEACHER_RECORD_ID));

        AddAlert();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO_FROM_CAMERA:

                try {
                    attachedFilePath = mPhotoFileUri.getPath();
                    attachedFilePath_From_SaveImage = CompresstionOfImage(attachedFilePath);
                    System.out.println("attachedFilePath " + attachedFilePath);
                    System.out.println("attachedFilePath_From_SaveImage  " + attachedFilePath_From_SaveImage);
                    attachedFileName = attachedFilePath_From_SaveImage.substring(attachedFilePath_From_SaveImage.lastIndexOf('/') + 1);
                    ATTACHED_FILE_MAP_KEY_NAME = attachedFileName;
                    alert_Attached_Map.put(ATTACHED_FILE_MAP_KEY_NAME, attachedFilePath_From_SaveImage);
                    System.out.println("File name _____________ " + attachedFileName);
                    alertAttachmentTextView.setText(attachedFileName);
                    alertAttachmentTextView.setVisibility(View.VISIBLE);
                    alertAttachmentImageView.setVisibility(View.VISIBLE);

                    System.out.println("alert_Attached_Map ::::::::::::::: TAKE_PHOTO_FROM_CAMERA" + alert_Attached_Map);
                } catch (Exception e) {
                    System.out.println("Caught error " + e.getMessage());

                }
                break;
            case REQUEST_PICK_IMAGE_FROM_GALLERY:
                if (requestCode == REQUEST_PICK_IMAGE_FROM_GALLERY && resultCode == RESULT_OK && null != data) {
                    try {
                        selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        attachedFilePath = cursor.getString(columnIndex);
                        attachedFilePath_From_SaveImage = CompresstionOfImage(attachedFilePath);
                        System.out.println("picturePath  " + attachedFilePath_From_SaveImage);
                        cursor.close();
                        attachedFileName = attachedFilePath_From_SaveImage.substring(attachedFilePath_From_SaveImage.lastIndexOf('/') + 1);
                        ATTACHED_FILE_MAP_KEY_NAME = attachedFileName;
                        alert_Attached_Map.put(ATTACHED_FILE_MAP_KEY_NAME, attachedFilePath_From_SaveImage);
                        System.out.println("File name _____________ " + attachedFileName);
                        alertAttachmentTextView.setText(attachedFileName);
                        alertAttachmentTextView.setVisibility(View.VISIBLE);
                        alertAttachmentImageView.setVisibility(View.VISIBLE);


                        System.out.println("alert_Attached_Map ::::::::::::::: PICK_IMAGE_FROM_GALLERY" + alert_Attached_Map);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Exception  _____________" + e.getMessage());
                    }
                }

                break;
            case REQUEST_PICK_FILE_FROM_FILESYSTEM:


                if (requestCode == REQUEST_PICK_FILE_FROM_FILESYSTEM && resultCode == RESULT_OK && null != data) {
                    try {
                        System.out.println("try block");
                        selectedFile = data.getData();
                        System.out.println("selectedFile " + selectedFile);
                        attachedFilePath_From_SaveImage = selectedFile.getPath();
                        attachedFileName = attachedFilePath_From_SaveImage.substring(attachedFilePath_From_SaveImage.lastIndexOf('/') + 1);
                        ATTACHED_FILE_MAP_KEY_NAME = attachedFileName;
                        alert_Attached_Map.put(ATTACHED_FILE_MAP_KEY_NAME, attachedFilePath_From_SaveImage);
                        System.out.println("File name _____________ " + attachedFileName);
                        alertAttachmentTextView.setText(attachedFileName);
                        alertAttachmentTextView.setVisibility(View.VISIBLE);
                        alertAttachmentImageView.setVisibility(View.VISIBLE);

                        System.out.println("alert_Attached_Map ::::::::::::::: PICK_FILE_FROM_FILESYSTEM " + alert_Attached_Map);
                    } catch (Exception e) {
                        System.out.println("Caught Exception EEEEEEEEEE");
                    }
                }
                break;
        }
    }


    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.d("AsyncTask", "if..calling");
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Log.d("AsyncTask", "else..calling");
            return task.execute();
        }
    }


    private void AddAlert() {

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("addAlertApi", jsonObjectAddAlertParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Alert Post exception    " + exception);
                alertProgressDialog.dismiss();
                new AlertDialog.Builder(AlertPost.this).setMessage(R.string.check_network)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        })
                        .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertProgressDialog.show();
                                addAlertToAzure();
                            }
                        }).create().show();

            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("Alert POst API   response    " + response);

                if (response.toString().equals("true")) {
                    alertProgressDialog.dismiss();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AlertPost.this);
                    alertDialogBuilder.setMessage("Alert Successfully Submitted");
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Add more.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AlertPost alertPost = new AlertPost();
                            alertPost.finish();

                            Intent alertPostIntent = new Intent(getApplicationContext(), AlertPost.class);
                            startActivity(alertPostIntent);
                            finish();

                        }
                    });
                    alertDialogBuilder.create().show();

                } else {
                    alertProgressDialog.dismiss();
                    new AlertDialog.Builder(AlertPost.this).setMessage(R.string.check_network)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            })
                            .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertProgressDialog.show();
                                    addAlertToAzure();
                                }
                            }).create().show();
                }
            }
        });
    }

    public class FetchingStudent extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                System.out.println("Doing Background.......");
                JsonElement jsonElement = mobileServiceJsonTable.where().select("id", "rollNo", "firstName", "middleName", "lastName").field("School_Class_id").eq(KEY_SCHOOL_CLASS_ID).execute().get();
                System.out.println("jsonElement" + jsonElement);
                JsonArray getJsonListResponse = jsonElement.getAsJsonArray();
                System.out.println("getJsonListResponse.size()" + getJsonListResponse.size());
                try {
                    if (getJsonListResponse.size() == 0) {
                        System.out.println("json not recived");
                        studentSpinnerList.clear();
                    }
                    for (int loop = 0; loop < getJsonListResponse.size(); loop++) {

                        JsonObject jsonObjectForIteration = getJsonListResponse.get(loop).getAsJsonObject();
                        student_Id = jsonObjectForIteration.get("id").toString().replace("\"", "");
                        student_firstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                        student_lastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                        student_fullName = student_firstName + " " + student_lastName;
                        System.out.println("student_Id" + student_Id);
                        System.out.println("student_count" + loop);
                        System.out.println("student_fullName" + student_fullName);

                        if (!checkingDuplicateStudentID.contains(student_Id)) {
                            System.out.println("checkingDuplicateStudentID list after");
                            spinnerStudentListData = new AlertStudentListData(student_fullName, student_Id);
                            studentSpinnerList.add(spinnerStudentListData);
                            checkingDuplicateStudentID.add(student_Id);
                            System.out.println("checkingDuplicateStudentID list after");
                        } else {
                            System.out.println("checkingDuplicateStudentID else   ..check");
                        }
                    }
                    System.out.println("studentSpinnerList  " + studentSpinnerList.size());
                } catch (Exception e) {

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            studentSpinnerList.add(new AlertStudentListData("ALL", "All"));
            studentSpinnerList.add(new AlertStudentListData("[ Select Student ]", "0"));
            //studentAdapter = new AlertStudentAdapterAdapter(getApplicationContext(), studentSpinnerList);
            //alertStudentSpinner.setAdapter(studentAdapter);
            studentAdapter.notifyDataSetChanged();
            alertStudentSpinner.setSelection(studentAdapter.getCount() - 1);
            alertStudentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("onItemSelected", "onItemSelected");
                    spinnerStudentListData = studentSpinnerList.get(position);
                    studentID = spinnerStudentListData.getId();
                    Log.d("list id", "list id" + spinnerStudentListData.getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
    }

    public String CompresstionOfImage(String selectedImagePath) {
        //************************compress logic start****************
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 500;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) {
            scale *= 2;
            System.out.println("Scaling start@@@@@@@@@@@");
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        System.out.println("selectedImagePath" + selectedImagePath);
        Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
        System.out.println("selectedImagefile bitmap" + bm);

        return SaveImage(bm);

//************************compress logic end****************
    }

    private String SaveImage(Bitmap finalBitmap) {
        String filePath = null;

        String root = Environment.getExternalStorageDirectory().toString();
        myDir = new File(root + "/Sparsh/Alert_Attachment");
        System.out.println("myDir" + myDir);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        imageFileName = "Sparsh-Alert-" + n + ".jpg";
        System.out.println("imageFileName alert " + imageFileName);
        file = new File(myDir, imageFileName);
        System.out.println("myDir : " + file);
        System.out.println("baby_Profile_File_Name : " + imageFileName);

        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            System.out.println("out" + out);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

//            imagePathToSendAzureStorage = Uri.fromFile(file);
//            filePath = imagePathToSendAzureStorage.getPath();

            Uri imagePath = Uri.fromFile(file);
            filePath = imagePath.getPath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    private void upload_Alert_Attachement() {

        System.out.println("Upload_ALert_Attachment_Start");
        alertProgressDialog.setMessage("Uploading Attachment");
        final String DATA_FOR_UPLOAD = alert_Attached_Map.get(ATTACHED_FILE_MAP_KEY_NAME);
        System.out.println("Retrive Data from map######################################## " + DATA_FOR_UPLOAD);

        final String concate = "file://" + DATA_FOR_UPLOAD;
        System.out.println("concate---------" + concate);
        final Uri sendAttachmentToAzure = Uri.parse(concate);

        final Assignment_Attachment item = new Assignment_Attachment();
        item.setAttachementIdentifier(attachmentUniqueIdentifier);
        item.setFilename(ATTACHED_FILE_MAP_KEY_NAME);
        item.setContainerName("sparshtrailtestrbscontainer");

        int index = ATTACHED_FILE_MAP_KEY_NAME.lastIndexOf('.');
        long resourceNameIdentifier = calendar.getTimeInMillis();
        ATTACHEMENT_RESOURCE_NAME = ATTACHED_FILE_MAP_KEY_NAME.substring(0, index) + "_" + resourceNameIdentifier + ATTACHED_FILE_MAP_KEY_NAME.substring(index, ATTACHED_FILE_MAP_KEY_NAME.length());
        System.out.println("ATTACHMENT_RESOURCE_NAME==================" + ATTACHEMENT_RESOURCE_NAME);
        item.setResourceName(ATTACHEMENT_RESOURCE_NAME);
        System.out.println("ATTACHMENT_RESOURCE_NAME  ++++++++++++++++++++++++++++++++++++ " + ATTACHEMENT_RESOURCE_NAME);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Assignment_Attachment entity = addItemInTable(item);
                    Log.d("sasquerystring", "sasquerystring" + entity.getSasQueryString());


                    final InputStream imageStream = getContentResolver().openInputStream(sendAttachmentToAzure);
                    final int imageLength = imageStream.available();
                    CloudBlobContainer container = getContainer();
                    System.out.println("ImageManager : UploadImage : container---------- " + container);

                    CloudBlockBlob imageBlob = container.getBlockBlobReference(ATTACHED_FILE_MAP_KEY_NAME);
                    imageBlob.upload(imageStream, imageLength);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error");
                    Log.d("catch error in ", "Async taskk");
                    e.printStackTrace();
                    alertProgressDialog.dismiss();
                    addAlertExceptionFlag = true;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                //condition to check, if there is some problem uploading attachments
                if (addAlertExceptionFlag) {
                    new AlertDialog.Builder(AlertPost.this)
                            .setMessage(R.string.check_network)
                            .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            }).setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addAlertExceptionFlag = false;
                            alertProgressDialog.show();
                            upload_Alert_Attachement();
                        }
                    }).setCancelable(false).create().show();
                } else {
                    //if attachments uploaded successfully, then add text data
                    addAlertToAzure();
                }
                addAlertExceptionFlag = false;
            }

        };
        runAsyncTask(task);
    }

    private Assignment_Attachment addItemInTable(Assignment_Attachment item) throws ExecutionException, InterruptedException {
        Assignment_Attachment attachmentEntity = mAssignment_Attachment.insert(item).get();
        return attachmentEntity;
    }
}