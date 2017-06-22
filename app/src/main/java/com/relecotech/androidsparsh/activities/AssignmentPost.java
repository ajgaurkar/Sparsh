package com.relecotech.androidsparsh.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh.AlertDialogManager;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.azurecontroller.Assignment;
import com.relecotech.androidsparsh.azurecontroller.Assignment_Attachment;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by amey on 10/16/2015.
 */
public class AssignmentPost extends AppCompatActivity {

    private static final int PICKFILE_RESULT_CODE = 2;
    private static final int DATE_DIALOG_ID = 999;
    static Bitmap photo;
    static Bitmap attachlistdata, bm;
    private final int RESULT_LOAD_IMAGE = 1;
    private final int CAMERA_REQUEST = 0;

    public Uri mPhotoFileUri = null;
    public Uri selectedImage = null;
    public Uri fileData = null;
    public File mPhotoFile = null;

    long submitDate, postingDate;
    Date azuresubmitDate, azurepostingDate;
    EditText descriptionEditText;
    Spinner classSpinner, divisionSpinner, scoreTypeSpinner, subjectSpinner;
    Button submitButton;
    ArrayAdapter<String> adapter;
    String encodedLowerToBase64;
    HashMap<String, String> attachmap;
    ArrayAdapter<String> attachAdapter;
    ArrayList<String> attachList;
    ListView attachListView;
    FilePickerClass filePicker;
    AttachmentListPopulate attachListPopulate;
    int index;
    File finalFile;
    String path;
    String keyValue;
    Cursor cursor;
    int idx;
    int oldMapSize = 0, newMapSize = 0;
    ProgressDialog progress;
    AlertDialog alertDialog;
    int itemPosition;
    Snackbar snackbar;
    TextView snackbartextView;
    View snackbarView;
    View view;
    private DatePicker datePicker;
    private java.util.Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private ImageView imgConfirm;
    private ProgressDialog assignmentrogressdialog;
    AlertDialogManager alert = new AlertDialogManager();
    private Intent cameraIntent;
    static final int REQUEST_TAKE_PHOTO = 3;
    String attachedFilePath;
    private JsonObject jsonObjectForAddAssignment;
    private Timestamp postingDateForAzure;
    public String MAP_FILE_NAME_KEY;
    String classString;
    String divString;
    String schoolClassIdString;
    String creditString = "0";
    String subString;
    String desString;
    String timestamp;
    String attachmentUniqueIdentifier;
    public int noOfAttachments = 0;
    private int attachmentLoopCounter = 0;
    private int copyCountValue = 1;
    private boolean addAssignmentExceptionFlag = false;
    private String ATTACHMENT_RESOURCE_NAME;
    HashMap<String, String> condtionHashMap;
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=sparshdevmobileapp;AccountKey=JCqZZj5ZhHmCkaQWgDolvX+UW6OmJTfEVb922NhCKnKnWAddpYL8gYIdj2MoHCx1HWyamP++GDy0E54PbYKVsg==;EndpointSuffix=core.windows.net";
    private Uri imagePathToSendAzureStorage;


    private long resourceNameIdentifier;
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            showDate(arg1, arg2, arg3);
        }
    };
    private ConnectionDetector connectionDetector;
    private boolean checkConnection;
    private JsonObject jsonToFetchAssignmentPostSpinnerdata;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    private MobileServiceTable<Assignment_Attachment> mAssignment_Attachment;
    MobileServiceTable<Assignment> mAssignment;
    Assignment assignmentItem;
    private boolean assignmentStatusFlag;
    SQLiteDatabase appDatabase;
    private Cursor resultSet;
    private TextView creditCountText;
    private String scoreTypeString;
    private DatabaseHandler databaseHandler;
    private File myDir;
    private File file;
    private String imageFileName;

    private String attachedFilePath_From_SaveImage;
    private DiscreteSeekBar creditSeekBar;
    private Calendar calender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_post);

        creditCountText = (TextView) findViewById(R.id.creditsCountTextView);
        creditCountText.setVisibility(View.GONE);

        creditSeekBar = (DiscreteSeekBar) findViewById(R.id.creditsSeekBar);
        creditSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                System.out.println("seekValue : " + value);
                creditCountText.setText("" + value);
                creditString = String.valueOf(value);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        checkConnection = connectionDetector.isConnectingToInternet();
        sessionManager = new SessionManager(getApplicationContext());
        jsonToFetchAssignmentPostSpinnerdata = new JsonObject();
        appDatabase = openOrCreateDatabase("SCHOOL_ONLINE_GLOBAL_DATABASE", MODE_PRIVATE, null);
        userDetails = sessionManager.getUserDetails();


        classSpinner = (Spinner) findViewById(R.id.spinnerAssignmentClass);
        divisionSpinner = (Spinner) findViewById(R.id.spinnerAssignmentDivision);
        scoreTypeSpinner = (Spinner) findViewById(R.id.spinnerAssignmentScoreType);
        subjectSpinner = (Spinner) findViewById(R.id.spinnerAssignmentSubject);
        descriptionEditText = (EditText) findViewById(R.id.assignmentTextDescription);
        dateView = (TextView) findViewById(R.id.spinnerAssignmentDueDate);
        attachListView = (ListView) findViewById(R.id.listViewAttachment);

//        submitButton = (Button) findViewById(R.id.buttonSubmit);
//
//        imgConfirm = (ImageView) findViewById(R.id.imageView6);
        view = findViewById(R.id.view20);
        attachList = new ArrayList<String>();
        attachmap = new HashMap<String, String>();
        condtionHashMap = new HashMap<>();
        try {
            // unknown null pointer exception occurs on selecting doc file attachment from file storage
            // (only on file part. not on camera img or gallery pick img part)
            // need to fix it
            mAssignment_Attachment = MainActivity.mClient.getTable(Assignment_Attachment.class);
            mAssignment = MainActivity.mClient.getTable(Assignment.class);
            assignmentItem = new Assignment();
            Log.d("MainActmClient.getTable", "TRY block");

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MainActmClient.getTable", "" + e);
        }
        calendar = java.util.Calendar.getInstance();
        filePicker = new FilePickerClass();
        attachListPopulate = new AttachmentListPopulate();
        attachAdapter = new ArrayAdapter<String>(this, R.layout.my_list, attachList);

        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH);
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        azurepostingDate = new Date(calendar.getTimeInMillis());
        System.out.println("azurepostingDate------------------- " + azurepostingDate);
        assignmentrogressdialog = new ProgressDialog(this);

        //setting values to spinners
        databaseHandler = new DatabaseHandler(AssignmentPost.this);
        resultSet = databaseHandler.getTeacherClassDataByCursor();


        resultSet = databaseHandler.getTeacherClassDataByCursor();
        resultSet.moveToFirst();
        List<String> classlist = new ArrayList<>();
        for (int i = 0; i < resultSet.getCount(); i++) {
            if (!classlist.contains(resultSet.getString(resultSet.getColumnIndex("class")))) {
                classlist.add(resultSet.getString(resultSet.getColumnIndex("class")));
            }
            resultSet.moveToNext();
        }


        classlist.add("[ Class ]");
        System.out.println("class list : " + classlist);

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, classlist);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        classSpinner.setAdapter(adapter);
        classSpinner.setSelection(adapter.getCount() - 1);

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classString = classSpinner.getSelectedItem().toString();
                condtionHashMap.put("class", classString);
                resultSet = databaseHandler.getTeacherClassData(condtionHashMap);
                System.out.println("@@@@@@@@@@ result count size " + resultSet.getCount());
                resultSet.moveToFirst();

                List<String> divlist = new ArrayList<>();
                for (int i = 0; i < resultSet.getCount(); i++) {
                    if (!divlist.contains(resultSet.getString(3))) {
                        divlist.add(resultSet.getString(3));
                    }
                    resultSet.moveToNext();
                }

                divlist.add("[ Division ]");
                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, divlist);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                divisionSpinner.setAdapter(adapter);
                divisionSpinner.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                divString = divisionSpinner.getSelectedItem().toString();

                condtionHashMap.put("class", classString);
                condtionHashMap.put("division", divString);
                resultSet = databaseHandler.getTeacherClassData(condtionHashMap);

                resultSet.moveToFirst();
                System.out.println("Inside division spinner");
                List<String> subjectlist = new ArrayList<>();
                for (int i = 0; i < resultSet.getCount(); i++) {
                    if (!subjectlist.contains(resultSet.getString(4))) {
                        subjectlist.add(resultSet.getString(4));
                    }
                    resultSet.moveToNext();

                }


                subjectlist.add("[ Subject ]");
                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, subjectlist);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                subjectSpinner.setAdapter(adapter);
                subjectSpinner.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subString = subjectSpinner.getSelectedItem().toString();
                condtionHashMap.put("class", classString);
                condtionHashMap.put("division", divString);
                condtionHashMap.put("subject", subString);

                // resultSet = appDatabase.rawQuery("select schoolClassId from Teacher_Class_Table where class = '" + classString + "' and division = '" + divString + "' and subject = '" + subString + "'", null);
                resultSet = databaseHandler.getTeacherClassData(condtionHashMap);
                resultSet.moveToFirst();
                System.out.println("Inside subject spinner");
                for (int i = 0; i < resultSet.getCount(); i++) {
                    schoolClassIdString = resultSet.getString(resultSet.getColumnIndex("schoolClassId"));

                }
                System.out.println("schoolClassId " + schoolClassIdString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> scoreTypelist = new ArrayList<>();
        scoreTypelist.add("Marks");
        scoreTypelist.add("Grades");
        scoreTypelist.add("[ Score type ]");
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, scoreTypelist);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        scoreTypeSpinner.setAdapter(adapter);
        scoreTypeSpinner.setSelection(adapter.getCount() - 1);
        scoreTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scoreTypeString = scoreTypeSpinner.getSelectedItem().toString();
                System.out.println("Inside score type spinner");
                if (scoreTypeString.equals("Marks")) {
                    revealCreditSeekBar();
                    creditCountText.setVisibility(View.VISIBLE);

                } else if (scoreTypeString.equals("Grades")) {
                    hideCreditSeekBar();
                    creditCountText.setVisibility(View.GONE);
                } else if (scoreTypeString.equals("[ Score type ]")) {
                    hideCreditSeekBar();
                    creditCountText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        attachListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item index
                itemPosition = position;
                Log.d("item position", "" + itemPosition);
                alertDialog = new AlertDialog.Builder(AssignmentPost.this).create();
                alertDialog.setMessage("Select action..");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        keyValue = (String) attachListView.getItemAtPosition(itemPosition);
                        attachList.remove(keyValue);
                        oldMapSize = attachmap.size();
                        System.out.println("oldMapSize-----------" + oldMapSize);
                        System.out.println("oldMap-----------" + attachmap);
                        attachmap.remove(keyValue);
                        newMapSize = attachmap.size();
                        System.out.println("newMapSize----------" + newMapSize);
                        System.out.println("newMap-----------" + attachmap);
                        attachListView.setAdapter(attachAdapter);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });
    }

    private void hideCreditSeekBar() {
        // previously visible view

        // get the center for the clipping circle
        int cx = creditSeekBar.getWidth() / 2;
        int cy = creditSeekBar.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(creditSeekBar, cx, cy, initialRadius, 0);

            // make the view invisible when the animation is done

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    creditSeekBar.setVisibility(View.GONE);

                }
            });

            // start the animation
            anim.start();
        }//chnge by Yogesh
        else {
            creditSeekBar.setVisibility(View.GONE);
        }
    }

    private void revealCreditSeekBar() {
        // previously invisible view
        //        View myView = findViewById(R.id.my_view);

        // get the center for the clipping circle
        int cx = creditSeekBar.getWidth() / 2;
        int cy = creditSeekBar.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(creditSeekBar, cx, cy, 0, finalRadius);
        }

        // make the view visible and start the animation
        creditSeekBar.setVisibility(View.VISIBLE);
        if (anim != null) {
            anim.start();
        }
    }


    public void opencamera() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.d("version ", "kitkat");
            takePicture();
        } else {
            Log.d("version ", "lower");
            Intent opencamIntent = new Intent(getApplicationContext(), PickCameraImage.class);
            startActivity(opencamIntent);
        }
    }

    public void pickFileFromFileSystem() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("*/*");
        intent.setType("application/pdf");
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    public void browseImage() {
        Intent browseIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(browseIntent, RESULT_LOAD_IMAGE);
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

    public void takePicture() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                mPhotoFile = createImageFile();
                System.out.println("File path checking  >>>>>>>>" + mPhotoFile);


            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println("IOException--------" + ex.getMessage());
            }
            // Continue only if the File was successfully created

            if (mPhotoFile != null) {
                mPhotoFileUri = Uri.fromFile(mPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoFileUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.d("Photo size after intent", "" + mPhotoFile.length());

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_TAKE_PHOTO:

                try {

                    attachedFilePath = mPhotoFileUri.getPath();
                    System.out.println("1-------CompresstionOfImage--------Call");
                    attachedFilePath_From_SaveImage = CompresstionOfImage(attachedFilePath);
                    System.out.println("2-------getCompressed image from attachedFilePath_From_SaveImage   " + attachedFilePath_From_SaveImage);

                    //logic to get file name
                    MAP_FILE_NAME_KEY = attachedFilePath_From_SaveImage;
                    index = MAP_FILE_NAME_KEY.lastIndexOf('/');
                    MAP_FILE_NAME_KEY = MAP_FILE_NAME_KEY.substring(index + 1);

                    //check if only file name and memory allocation done not the camera data is stored.
                    // i.e. camera opened but closed w/o capturing
                    // if so goto else part
                    System.out.println(" 3----------Length of Photo ----" + mPhotoFile.length());
                    if (mPhotoFile.length() > 0) {
                        attachmap.put(MAP_FILE_NAME_KEY, attachedFilePath_From_SaveImage);
                        attachList.add(0, MAP_FILE_NAME_KEY);
                        attachListView.setAdapter(attachAdapter);
                    } else {
                        System.out.println("--Length of Photo is Zero--");
                    }
                    System.out.println("4--------------Attachmap---------------" + attachmap);
                    System.out.println("5------AttachedFilePath--" + attachedFilePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("image not capture from camera");
                }

                break;
            case RESULT_LOAD_IMAGE:

                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                    try {

                        selectedImage = data.getData();
                        Log.d("RESULT_LOAD_IMAGE", "selectedImage     " + selectedImage);
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        System.out.println("RESULT_LOAD_IMAGE cursor : " + cursor + " " + cursor.getCount());
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        attachedFilePath = cursor.getString(columnIndex);
                        attachedFilePath_From_SaveImage = CompresstionOfImage(attachedFilePath);
                        cursor.close();
                        MAP_FILE_NAME_KEY = attachedFilePath_From_SaveImage;
                        index = MAP_FILE_NAME_KEY.lastIndexOf('/');
                        MAP_FILE_NAME_KEY = MAP_FILE_NAME_KEY.substring(index + 1);

                        //check if file name key is already present or not
                        //if present then append "copyCountValue" to file name
                        if (attachmap.containsKey(MAP_FILE_NAME_KEY)) {
                            Log.d("file already", "exist");
                            index = MAP_FILE_NAME_KEY.lastIndexOf('.');
                            MAP_FILE_NAME_KEY = MAP_FILE_NAME_KEY.substring(0, index) + "(" + copyCountValue + ")" + MAP_FILE_NAME_KEY.substring(index, MAP_FILE_NAME_KEY.length());
                            copyCountValue++;
                            // Log.d("MAP_KEY after duplicate", "" + MAP_FILE_NAME_KEY);
                        }
                        attachmap.put(MAP_FILE_NAME_KEY, attachedFilePath_From_SaveImage);
                        attachList.add(0, MAP_FILE_NAME_KEY);
                        attachListView.setAdapter(attachAdapter);
                        Log.d("attachmap", "" + attachmap);
                        System.out.println("attachedFilePath  RESULT_LOAD_IMAGE," + attachedFilePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("gallary attachement not listed ");
                    }

                }
                break;

            case PICKFILE_RESULT_CODE:

                if (resultCode == RESULT_OK) {
                    try {
                        System.out.println("before getting file url-----------");
                        fileData = data.getData();
                        System.out.println("after getting file url" + fileData);

                        attachedFilePath = fileData.getPath();
                        System.out.println("attachedFilePath ---- " + attachedFilePath);
                        MAP_FILE_NAME_KEY = attachedFilePath;
                        index = MAP_FILE_NAME_KEY.lastIndexOf('/');
                        MAP_FILE_NAME_KEY = MAP_FILE_NAME_KEY.substring(index + 1);

                        //check if file name key is already present or not
                        //if present then append "(1)" to file name
                        if (attachmap.containsKey(MAP_FILE_NAME_KEY)) {
                            Log.d("file already", "exist");
                            index = MAP_FILE_NAME_KEY.lastIndexOf('.');
                            MAP_FILE_NAME_KEY = MAP_FILE_NAME_KEY.substring(0, index) + "(" + copyCountValue + ")" + MAP_FILE_NAME_KEY.substring(index, MAP_FILE_NAME_KEY.length());
                            copyCountValue++;
                        }
                        attachmap.put(MAP_FILE_NAME_KEY, attachedFilePath);
                        System.out.println("attachmap-----PICKFILE_RESULT_CODE----" + PICKFILE_RESULT_CODE);
                        attachList.add(0, MAP_FILE_NAME_KEY);
                        attachListView.setAdapter(attachAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("file attachement not listed ");
                    }
                }
                break;

        }
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

    public void submit() {

        subString = subjectSpinner.getSelectedItem().toString();
        desString = descriptionEditText.getText().toString();
        postingDateForAzure = new Timestamp(postingDate);
        System.out.println(dateView.getText().toString());
        if (classString != "[ Class ]") {

            if (divString != "[ Division ]") {

                if (subString != "[ Subject ]") {

                    if (!dateView.getText().toString().equals("[ Due date ]")) {

                        if (scoreTypeString != "[ Score type ]") {

                            if (desString.trim().length() > 0) {

                                timestamp = new SimpleDateFormat(("yyyyMMdd_HHmmss"), Locale.getDefault()).format(new Date());
                                attachmentUniqueIdentifier = classString + divString + subString + timestamp;

                                assignmentrogressdialog.setCancelable(false);
                                assignmentrogressdialog.show();

                                if (attachmap.size() > 0) {
                                    noOfAttachments = attachmap.size() - 1;
                                    Log.d("ATTACHMENT(S) PRESENT", "" + noOfAttachments);
                                    uploadAttachment();
                                } else {
                                    Log.d("NO ATTACHMENT", "PRESENT");
                                    addAssignmentToAzure();
                                }
                            } else {
                                Snackbar.make(view, "Provide some description", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(view, "Select Score type", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(view, "Select Due Date", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(view, "Select Subject", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(view, "Select Division", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(view, "Select Class", Snackbar.LENGTH_SHORT).show();
        }

    }

    public void uploadAttachment() {

        System.out.println("HANDLER STARTED");
        Log.d("UPLOADPHOTO()", "METHOD STARTED");
        Log.d("attachmap", "" + attachmap);
        Log.d("noOfAttachments", "" + noOfAttachments);

        //progress dialog status for assmnt upload
        assignmentrogressdialog.setMessage("Uploading " + (++attachmentLoopCounter) + " of " + (++noOfAttachments) + " attachment(s)");
        attachmentLoopCounter--;
        noOfAttachments--;

        MAP_FILE_NAME_KEY = attachList.get(attachmentLoopCounter);
        System.out.println("MAP_FILE_NAME_KEY--------" + MAP_FILE_NAME_KEY);

        final String DATA_FOR_UPLOAD = attachmap.get(MAP_FILE_NAME_KEY);
        System.out.println("DATA_FOR_UPLOAD---------" + DATA_FOR_UPLOAD);
        final String concate = "file://" + DATA_FOR_UPLOAD;
        System.out.println("concate---------" + concate);
        final Uri sendAttachmentToAzure = Uri.parse(concate);
        System.out.println("myUri---------" + sendAttachmentToAzure);
        if (MainActivity.mClient == null) {
            return;
        }
        //sending meta-data to azure to get SASqueryString
        // Create a new item
        final Assignment_Attachment item = new Assignment_Attachment();
        item.setAttachementIdentifier(attachmentUniqueIdentifier);
        item.setFilename(MAP_FILE_NAME_KEY);
        item.setContainerName("sparshtrailtestrbscontainer");

        //process to append unique iden (current millisecond) in resource name
        index = MAP_FILE_NAME_KEY.lastIndexOf('.');
        resourceNameIdentifier = calendar.getTimeInMillis();
        ATTACHMENT_RESOURCE_NAME = MAP_FILE_NAME_KEY.substring(0, index) + "_" + resourceNameIdentifier + MAP_FILE_NAME_KEY.substring(index, MAP_FILE_NAME_KEY.length());
        item.setResourceName(ATTACHMENT_RESOURCE_NAME);

        // Use a unigue GUID to avoid collisions.
        // UUID uuid = UUID.randomUUID();
        // String uuidInString = uuid.toString();

        // Send the item to be inserted. When blob properties are set this
        // generates a SAS in the response.
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Assignment_Attachment entity = addItemInTable(item);
                    System.out.println("Send Attachment To Azure-------------" + sendAttachmentToAzure);
                    final InputStream imageStream = getContentResolver().openInputStream(sendAttachmentToAzure);
                    final int imageLength = imageStream.available();
                    CloudBlobContainer container = getContainer();
                    System.out.println("ImageManager : UploadImage : container---------- " + container);

                    CloudBlockBlob imageBlob = container.getBlockBlobReference(MAP_FILE_NAME_KEY);
                    imageBlob.upload(imageStream, imageLength);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error");
                    Log.d("catch error in ", "Asyn taskk");
                    e.printStackTrace();
                    assignmentrogressdialog.dismiss();
                    addAssignmentExceptionFlag = true;

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                //condition to check, if there is some problem uploading attachments
                if (addAssignmentExceptionFlag) {
                    new AlertDialog.Builder(AssignmentPost.this)
                            .setMessage(R.string.check_network)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            }).setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addAssignmentExceptionFlag = false;
//                            attachmentLoopCounter = 0;
                            assignmentrogressdialog.show();
                            uploadAttachment();
                        }
                    }).setCancelable(false).create().show();
                } else {
                    //if attachments uploaded successfully, then add text data


                    if (attachmentLoopCounter == noOfAttachments) {
                        Log.d("addAssignmentToAzure", "calling");
                        System.out.println("ASSMNT TEXT ADDITION AFTER IMAGE UPLOAD");
                        addAssignmentToAzure();
                    } else {
                        attachmentLoopCounter++;
                        Log.d("attachment loop", "continued...FILE NO " + (attachmentLoopCounter));
                        uploadAttachment();
                    }
                }
                addAssignmentExceptionFlag = false;
            }

        };

        runAsyncTask(task);
    }

    public Assignment_Attachment addItemInTable(Assignment_Attachment item) throws ExecutionException, InterruptedException {
        Assignment_Attachment attachmentEntity = mAssignment_Attachment.insert(item).get();
        System.out.println("attachmentEntity--------------" + attachmentEntity);
        return attachmentEntity;
    }

    /**
     * Run an ASync task on the corresponding executor
     *
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.d("AsyncTask", "if..calling");
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Log.d("AsyncTask", "else..calling");
            return task.execute();
        }
    }

    private void addAssignmentToAzure() {

        System.out.println("HANDLER STARTED");

        jsonObjectForAddAssignment = new JsonObject();
        System.out.println("setters of assmnt class called");

        //progress dialog status for assmnt upload
        assignmentrogressdialog.setMessage("Uploading assignment");
        attachmentLoopCounter--;
        noOfAttachments--;


        calender = java.util.Calendar.getInstance();
        postingDate = calender.getTimeInMillis();

        // Yogesh changes for invoke api
        jsonObjectForAddAssignment.addProperty("Assignment_class", classString);
        jsonObjectForAddAssignment.addProperty("Attachement_identifier", attachmentUniqueIdentifier);
        jsonObjectForAddAssignment.addProperty("Assignment_description", desString);
        jsonObjectForAddAssignment.addProperty("Assignment_subject", subString);
        jsonObjectForAddAssignment.addProperty("Assignment_credit", Integer.parseInt(creditString));
        jsonObjectForAddAssignment.addProperty("Assignment_div", divString);
        jsonObjectForAddAssignment.addProperty("Assignment_submitted_by", userDetails.get(SessionManager.KEY_TEACHER_REG_ID));
        jsonObjectForAddAssignment.addProperty("Attachment_count", attachmap.size());
        jsonObjectForAddAssignment.addProperty("Active", true);
        jsonObjectForAddAssignment.addProperty("School_class_id", schoolClassIdString);
        jsonObjectForAddAssignment.addProperty("Assignment_dauedate", submitDate);
        System.out.println("Assignment_dauedate" + submitDate);
        jsonObjectForAddAssignment.addProperty("Assignment_postdate", postingDate);
        System.out.println("Assignment_postdate" + postingDate);
        jsonObjectForAddAssignment.addProperty("Score_type", scoreTypeString);


        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("addAssignmentApi", jsonObjectForAddAssignment);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("exception    " + exception);
                assignmentrogressdialog.dismiss();
                new AlertDialog.Builder(AssignmentPost.this)
                        .setMessage(R.string.check_network)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        }).setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        assignmentrogressdialog.show();
                        addAssignmentToAzure();
                    }
                }).create().show();
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Assignment Post API   response    " + response);

                if (response.toString().equals("true")) {
                    assignmentrogressdialog.dismiss();
                    new AlertDialog.Builder(AssignmentPost.this)
                            .setMessage("Assignment submitted ..")
                            .setCancelable(false)
                            .setNegativeButton("Add more", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // finish current activity..
                                    AssignmentPost assignmentPost = new AssignmentPost();
                                    assignmentPost.finish();

                                    // start same activity..
                                    Intent addAssignmentintent = new Intent(getApplicationContext(), AssignmentPost.class);
                                    startActivity(addAssignmentintent);
                                    finish();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    attachmentLoopCounter = 0;
                                    onBackPressed();

                                }
                            }).create().show();

                } else {
                    assignmentrogressdialog.dismiss();
                    new AlertDialog.Builder(AssignmentPost.this)
                            .setMessage(R.string.check_network)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            }).setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            assignmentrogressdialog.show();
                            addAssignmentToAzure();
                        }
                    }).create().show();
                }
            }
        });


    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(DATE_DIALOG_ID);

    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                DatePickerDialog _date = new DatePickerDialog(this, myDateListener, year, month,
                        day) {
                    @Override
                    public void onDateChanged(DatePicker view, int Dyear, int DmonthOfYear, int DdayOfMonth) {
                        if (Dyear < year)
                            view.updateDate(year, month, day);

                        if (DmonthOfYear < month && Dyear == year)
                            view.updateDate(year, month, day);

                        if (DdayOfMonth < day && Dyear == year && DmonthOfYear == month)
                            view.updateDate(year, month, day);

                    }
                };
                return _date;
        }
        return null;
    }

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
        System.out.println("year------------" + year + "month-------------" + month + "day------------------" + day);
        calendar.set(year, month, day);
//        calendar.set(Calendar.HOUR_OF_DAY, 5);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        submitDate = calendar.getTimeInMillis();
        System.out.println("submitDate------------" + submitDate);
        azuresubmitDate = new Date(submitDate);


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
        myDir = new File(root + "/Sparsh/Assignment_Post");
        System.out.println("myDir" + myDir);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        imageFileName = "Sparsh-Assignment-" + n + ".jpg";
        file = new File(myDir, imageFileName);
        System.out.println("myDir : " + file);
        System.out.println("_File_Name : " + imageFileName);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alert_menu, menu);
        return true;
    }

//    public void showPopup() {
//        View menuItemView = findViewById(R.id.action_send);
//        PopupMenu popup = new PopupMenu(AssignmentPost.this, menuItemView);
//        MenuInflater inflate = popup.getMenuInflater();
//        inflate.inflate(R.menu.popup, popup.getMenu());
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_camera:
//                        opencamera();
////                        if (attachedFilePath_From_SaveImage == null) {
////                            System.out.println("action_camera :::: attachedFilePath_From_SaveImage is null");
////                            opencamera();
////
////                        } else {
////
////                            Toast.makeText(AssignmentPost.this, "Only one attachment attached", Toast.LENGTH_LONG).show();
////                        }
//                        break;
//                    case R.id.action_gallery:
//                        browseImage();
////                        if (attachedFilePath_From_SaveImage == null) {
////                            System.out.println("action_gallery :::: attachedFilePath_From_SaveImage is null");
////                            browseImage();
////                        } else {
////                            Toast.makeText(AssignmentPost.this, "Only one attachment attached", Toast.LENGTH_LONG).show();
////                        }
//                        break;
//                    case R.id.action_document:
////                        if (attachedFilePath_From_SaveImage == null) {
////                            System.out.println("action_document :::: attachedFilePath_From_SaveImage is null");
////                            pickFileFromFileSystem();
////                        } else {
////                            Toast.makeText(AssignmentPost.this, "Only one attachment attached", Toast.LENGTH_LONG).show();
////                        }
//                        pickFileFromFileSystem();
//                        break;
//                }
//                return false;
//            }
//        });
//        popup.show();
//
//    }


    public void showPopup() {
        View menuItemView = findViewById(R.id.action_attachment);
        PopupMenu popup = new PopupMenu(AssignmentPost.this, menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_camera:
                        if (PermissionChecker.checkSelfPermission(AssignmentPost.this, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
                            /*
                                    Camera Permission Granted.
                                    Open Camera for Assignment Attachement.
                             */
                            System.out.println(" INSIDE opencamera ");
                            opencamera();

                        } else {
                            System.out.println(" INSIDE open camera permission ");
                            AlertDialog.Builder checkPermissionDialog = new AlertDialog.Builder(AssignmentPost.this);
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
                        if (PermissionChecker.checkSelfPermission(AssignmentPost.this, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(AssignmentPost.this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
//                        if (PermissionChecker.checkSelfPermission(AssignmentPost.this, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
//                            if (PermissionChecker.checkSelfPermission(AssignmentPost.this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {


                            /*
                                   Gallery Permission Granted.
                                    Browse image from Gallery for Assignment Attachement.
                             */
                                browseImage();
//                            } else {
//                                AlertDialog.Builder checkPermissionDialog = new AlertDialog.Builder(AssignmentPost.this);
//                                checkPermissionDialog.setMessage("Allow Sparsh App to access Gallery");
//                                checkPermissionDialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        Intent checkPermissionIntent = new Intent();
//                                        checkPermissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                        checkPermissionIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                                        checkPermissionIntent.setData(Uri.parse("package:" + getPackageName()));
//                                        checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                        checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                                        startActivity(checkPermissionIntent);
//                                    }
//                                });
//                                checkPermissionDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                    }
//                                });
//                                checkPermissionDialog.show();
//                            }


                        } else {
                            AlertDialog.Builder checkPermissionDialog = new AlertDialog.Builder(AssignmentPost.this);
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

                        if (PermissionChecker.checkSelfPermission(AssignmentPost.this, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(AssignmentPost.this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
//                            if (PermissionChecker.checkSelfPermission(AssignmentPost.this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {


                            /*
                                   Read Write file Expoler Permission Granted.
                                    open file expoler pick documents etc. for Assignment Attachement.
                             */

                                pickFileFromFileSystem();
//                            } else {
//                                AlertDialog.Builder checkPermissionDialog = new AlertDialog.Builder(AssignmentPost.this);
//                                checkPermissionDialog.setMessage("Allow Sparsh App to access File Explorer");
//                                checkPermissionDialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        Intent checkPermissionIntent = new Intent();
//                                        checkPermissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                        checkPermissionIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                                        checkPermissionIntent.setData(Uri.parse("package:" + getPackageName()));
//                                        checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                        checkPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                                        startActivity(checkPermissionIntent);
//                                    }
//                                });
//                                checkPermissionDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                    }
//                                });
//                                checkPermissionDialog.show();
//                            }


                        } else {
                            AlertDialog.Builder checkPermissionDialog = new AlertDialog.Builder(AssignmentPost.this);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_send:
                submit();
                return true;
            case R.id.action_attachment:
                if (attachmap.size() == 0) {
                    System.out.println(" INSIDE action_attachment ");
                    showPopup();
                } else {
                    Toast.makeText(AssignmentPost.this, "Only one attachment allowed !", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

