package com.relecotech.androidsparsh.activities;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.AssignmentAttachmentAdapter;
import com.relecotech.androidsparsh.controllers.AssignmentAttachmentListData;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.relecotech.androidsparsh.activities.AssignmentPost.storageConnectionString;

/**
 * Created by amey on 10/16/2015.
 */
public class AssignmentDetail extends AppCompatActivity {

    static String divisionForAssApproval, classForAssapproval, assId;
    static String scoreTypeForAssApproval;
    String assignAttachCount;
    static int maxCreditsForAssApproval;
    TextView subjectText, classtext, postedOnText, dueDateText, submittedByText, attachmentDividerText, descriptiontext;
    ListView attachList;
    ProgressBar downloadProgressBar;
    String assignmentId;
    ImageView downloadButton;
    TextView downloadingText;
    //Button approveBtn;
    private FloatingActionButton approveBtn;
    String userRole;
    private Dialog about_dlg;
    private Spinner spsrc;
    private Spinner spdest;
    private String attachmentItemname;
    private String attachment_Item_name;
    private Intent intent;
    ArrayList<String> attachmentList;
    ArrayList<String> attachmentPrefixesList;
    ArrayAdapter<String> attachAdapter;

    ArrayList<AssignmentAttachmentListData> assignmentAttachmentList;

    private ConnectionDetector connectionDetector;
    private boolean checkConnection;
    private JsonObject jsonObjectToFetchAttachmentMetadata;
    Cursor attachmentCursor;
    SQLiteDatabase sqlDatabase = MainActivity.schoolOnlineDatabase;
    private String directory;
    private CloudBlobContainer container;
    private String fileNameToDownload;
    private String attachmentFileName;
    private String attachmentFileURI;
    private String attachmentPrefix;
    private int downloadCount = 0;
    Boolean downloadCompleteStatus = false;
    private String filePath;
    private TextView submittedByTextNotation;
    private Bundle getBundleData;
    private RelativeLayout assmntDetailLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private TextView commentText;
    private String assignmentStatusComment;
    private CardView assignmentCommentPanel;
    private CardView assignmentAttachmentPanel;
    private AssignmentAttachmentAdapter assignmentAttachmentAdapter;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        checkConnection = connectionDetector.isConnectingToInternet();
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        Log.d("login_user_role", userRole);


        if (checkConnection) {

        } else {
            Toast.makeText(getApplicationContext(), "No internet connection..!", Toast.LENGTH_LONG).show();
        }

        File dir = new File(Environment.getExternalStorageDirectory(), "/Sparsh/Assignment_Download");
        directory = dir.getPath();
        System.out.println("Directory created " + directory);

        try {
            System.out.println("directory creation block");
            if (dir.mkdirs()) {
                System.out.println("Directory  created");
            } else {
                System.out.println("Directory not created");
            }
        } catch (Exception e) {
            System.out.println("directory creation EXCEPTIION");
            e.printStackTrace();
        }

        //Initializing all the components
        assmntDetailLayout = (RelativeLayout) findViewById(R.id.assignmentDetailLayout);
//        subjectText = (TextView) findViewById(R.id.subjectTextView);
//        classtext = (TextView) findViewById(R.id.classTextView);
        postedOnText = (TextView) findViewById(R.id.postedOnTextView);
        dueDateText = (TextView) findViewById(R.id.dueDateTextView);
        submittedByTextNotation = (TextView) findViewById(R.id.submittedByText);
        submittedByText = (TextView) findViewById(R.id.submittedByTextView);
//        attachmentDividerText = (TextView) findViewById(R.id.attachmentsDividerTextView);
        descriptiontext = (TextView) findViewById(R.id.descriptionTextView);
        descriptiontext.setMovementMethod(new ScrollingMovementMethod());// to make assignment description scrollable
        attachList = (ListView) findViewById(R.id.attachmentListView);
        downloadButton = (ImageView) findViewById(R.id.downloadAttachmentButton);
        downloadButton.setVisibility(View.INVISIBLE);
        downloadingText = (TextView) findViewById(R.id.downloadingTtextView);
        commentText = (TextView) findViewById(R.id.commentTextView);
        assignmentCommentPanel = (CardView) findViewById(R.id.assignment_detail_comment_panel_cv);
        assignmentAttachmentPanel = (CardView) findViewById(R.id.assignment_detail_attachment_panel_cv);
        downloadProgressBar = (ProgressBar) findViewById(R.id.attachDownloadProgressBar);
        downloadProgressBar.setVisibility(View.INVISIBLE);
        approveBtn = (FloatingActionButton) findViewById(R.id.assignmentApproveButton);

        getBundleData = getIntent().getExtras();
        divisionForAssApproval = getBundleData.getString("AssignDivision");
        classForAssapproval = getBundleData.getString("AssignClassStd");
        maxCreditsForAssApproval = Integer.parseInt(getBundleData.getString("AssignMaxCredits"));
        scoreTypeForAssApproval = getBundleData.getString("AssignScoreType");
        assignAttachCount = getBundleData.getString("AssignAttachCount");
        assId = getBundleData.getString("AssignId");


        attachmentList = new ArrayList<String>();
        attachmentPrefixesList = new ArrayList<String>();
        attachAdapter = new ArrayAdapter<String>(this, R.layout.attachment_list_item, attachmentPrefixesList);
        assignmentAttachmentList = new ArrayList<>();

        //initialize json object to send and fetch attachment list
        jsonObjectToFetchAttachmentMetadata = new JsonObject();
        jsonObjectToFetchAttachmentMetadata.addProperty("ASSIGNMENT_ID", assId);
        System.out.println("jsonObjectToFetchAttachmentMetadata : " + jsonObjectToFetchAttachmentMetadata);

        sqlDatabase.execSQL("CREATE TABLE IF NOT EXISTS ATTACHMENT_METADATA(EVENT_ID VARCHAR,FILE_NAME VARCHAR,IMG_URI VARCHAR);");

        System.out.println("assId----------------assId" + assId);
        attachmentCursor = sqlDatabase.rawQuery("SELECT * FROM ATTACHMENT_METADATA WHERE EVENT_ID ='" + assId + "';", null);

        System.out.println("attachmentCursor----------------attachmentCursor" + attachmentCursor);
        System.out.println("attachmentCursor----------------assignAttachCount" + assignAttachCount);

        if (assignAttachCount.equals("0")) {
            assignmentAttachmentPanel.setVisibility(View.INVISIBLE);
        } else {
            assignmentAttachmentPanel.setVisibility(View.VISIBLE);
            downloadButton.setVisibility(View.VISIBLE);

        }
        if (attachmentCursor.getCount() == 0) {
            if (checkConnection) {
                System.out.println("fetchAttachmentMetadata---------------- called attachement counter 0");
                fetchAttachmentMetadata();
            } else {
                Toast.makeText(getApplicationContext(), "No internet connection..!", Toast.LENGTH_LONG).show();
            }
        } else {

            attachmentCursor.moveToFirst();
            do {
                attachmentList.add(0, attachmentCursor.getString(1));
                System.out.println(attachmentCursor.getString(1));
                System.out.println("-----------------------------  " + attachmentCursor.getString(1));

                assignmentAttachmentList.add(new AssignmentAttachmentListData(attachmentCursor.getString(1)));
                System.out.println("assignmentAttachmentList-----------------  " + assignmentAttachmentList.size());
            } while (attachmentCursor.moveToNext());

            System.out.println("attachmentPrefixesList.SIZE " + attachmentPrefixesList.size());
            System.out.println("attachmentPrefixesList" + attachmentPrefixesList);
            assignmentAttachmentAdapter = new AssignmentAttachmentAdapter(getApplicationContext(), assignmentAttachmentList);
            attachList.setAdapter(assignmentAttachmentAdapter);
            downloadCompleteStatus = true;
            downloadButton.setVisibility(View.VISIBLE);
            downloadButton.setImageResource(R.drawable.download_check_mark_64);
        }

        if (userRole.equals("Teacher")) {
            approveBtn.setVisibility(View.VISIBLE);
            assignmentCommentPanel.setVisibility(View.GONE);

        }


        if (userRole.equals("Student")) {
            approveBtn.setVisibility(View.INVISIBLE);

            try {


                assignmentStatusComment = getBundleData.getString("AssignStatusComment");
                if (assignmentStatusComment.isEmpty()) {
                    commentText.setText("No Comments");
                } else {
                    commentText.setText(assignmentStatusComment.replace("\\n", "\n").replace("\\", ""));
                }

            } catch (Exception e) {
                System.out.println("assignmentStatusComment******* " + e.getMessage());
            }
        }

        postedOnText.setText(getBundleData.getString("AssignIssueDate"));
        dueDateText.setText(getBundleData.getString("AssignDueDate"));
        submittedByText.setText(getBundleData.getString("AssignSubmittedBy"));

        /*
        Below code
        Set Assignment Aprroval comment in  commentTextView.
         */

        getSupportActionBar().setTitle(getBundleData.getString("AssignSubject"));


        if (userRole.equals("Teacher")) {
            submittedByTextNotation.setVisibility(View.GONE);
            submittedByText.setVisibility(View.GONE);
        }
        String temps = getBundleData.getString("AssignDescription");
        System.out.println("temps---- " + temps);
        descriptiontext.setText(temps.replace("\\n", "\n").replace("\\", ""));


//        attachmentDividerText = (TextView) findViewById(R.id.attachmentsDividerTextView);
        attachList = (ListView) findViewById(R.id.attachmentListView);

        attachList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (downloadCompleteStatus) {
                            // attachmentItemname = (String) attachList.getItemAtPosition(position);
                            AssignmentAttachmentListData getAttachmentName = assignmentAttachmentList.get(position);
                            attachment_Item_name = getAttachmentName.getAttachementName();
                            System.out.println("3333333333333333333333333333333333333333333333333333   " + attachment_Item_name);
                            filePath = "file://" + directory + "/" + attachment_Item_name;
                            System.out.println("File path " + filePath);
                            Uri uri = Uri.parse(filePath);
                            System.out.println("uri " + uri);
                            String extension = uri.getPath();
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);

                            if (extension.contains(".jpg") || extension.contains(".png") || extension.contains(".jpeg") || extension.contains(".bmp")) {
                                Log.d("jpeg and png content", "");
                                intent.setDataAndType(uri, "image/*");
                            } else if (extension.contains(".pdf")) {
                                Log.d("pdf", "");
                                intent.setDataAndType(uri, "application/pdf");
                            } else if (extension.contains(".txt")) {
                                Log.d("txt", " ");
                                intent.setDataAndType(uri, "text/plain");
                            } else if (extension.contains(".doc") || extension.contains(".docx")) {
                                Log.d("doc or docx", " ");
                                intent.setDataAndType(uri, "application/msword");

                            } else if (extension.contains(".ppt") || extension.contains(".pptx")) {
                                // Powerpoint file
                                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                            }
                            //intent.setDataAndType(uri, "image/*");
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Problem opening attachment", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }

        );

        downloadButton.setOnClickListener(
                new View.OnClickListener() {
                    public Dialog about_dlg;

                    @Override
                    public void onClick(View view) {
                        downloadingText.setText("Downloading Attachments");
                        downloadButton.setVisibility(View.INVISIBLE);
                        downloadingText.setTypeface(null, Typeface.ITALIC);

                        downloadProgressBar.setVisibility(View.VISIBLE);

                        try {

//                            final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=sparhtrailtestrbsaccount;AccountKey=bzGziC2z+6qzsv0aD27Uxm5ZKKtplrta6fTbs44Pndbn/9w+xPpfbFPXQi9BH7vBFrAjQvAi03JR9l3jUEIlJw==;";

                            // Retrieve storage account from connection-string.
                            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
                            // Create the blob client.
                            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                            // Get a reference to a container.
                            // The container name must be lower case
                            container = blobClient.getContainerReference("sparshdevmobileapp");
                            if (container.listBlobs() != null) {
                                Log.d("in if ", "list conatin value");
                                downloadCount = assignmentAttachmentList.size();
                                System.out.println("downloadCount------------" + downloadCount);
                                downLoadAttachments();
                            } else {
                                Log.d("in else ", "list does not any contain value ");
                            }

                        } catch (Exception e) {
                            // Output the stack trace.
                            e.printStackTrace();
                        }

                    }
                }

        );
        approveBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent assApproveIntent = new Intent(getApplicationContext(), AssigmentApproval.class);
                        startActivity(assApproveIntent);

                    }
                }

        );

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void downLoadAttachments() {
        AssignmentAttachmentListData ss = assignmentAttachmentList.get(downloadCount - 1);
        fileNameToDownload = ss.getAttachementName();
        System.out.println("fileNameToDownload----------------" + fileNameToDownload);
        new downloadingAttachment().execute();
    }

    private void fetchAttachmentMetadata() {
        System.out.println("fetchAttachmentMetadata CALLED");

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchAssignmentDetail", jsonObjectToFetchAttachmentMetadata);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Assignment detail API exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("  Assignment detail API   response    " + response);
                JsonArray attachmentListArray = response.getAsJsonArray();
                System.out.println("attachmentListArray-------------------------" + attachmentListArray);
                addItemsToAttachList(attachmentListArray);
            }
        });
    }


//        MainActivity.mClient.invokeApi("fetchassignmentdetail", jsonObjectToFetchAttachmentMetadata, new ApiJsonOperationCallback() {
//            @Override
//            public void onCompleted(JsonElement jsonObject, Exception exception, ServiceFilterResponse response) {
//                Log.d("jsonObject", "" + jsonObject);
//                try {
//                    JsonArray attachmentListArray = jsonObject.getAsJsonArray();
//                    System.out.println("attachmentListArray-------------------------" + attachmentListArray);
//                    addItemsToAttachList(attachmentListArray);
//                } catch (Exception e) {
//                    System.out.println("No attachment metadata fetched : May be a null pointer in json response");
//                }
//            }
//        });


    private void addItemsToAttachList(JsonArray attachmentListArray) {
        System.out.println("Inside attachemnt called..........");
        for (int loop = 0; loop <= attachmentListArray.size() - 1; loop++) {
            System.out.println("loop no " + loop);
            JsonObject jsonObjectForIteration = attachmentListArray.get(loop).getAsJsonObject();

            attachmentFileName = jsonObjectForIteration.get("filename").toString();
            attachmentFileURI = jsonObjectForIteration.get("imageUri").toString();

            attachmentFileName = attachmentFileName.substring(1, attachmentFileName.length() - 1);
            attachmentFileURI = attachmentFileURI.substring(1, attachmentFileURI.length() - 1);

            sqlDatabase.execSQL("INSERT INTO ATTACHMENT_METADATA VALUES ('" + assId + "','" + attachmentFileName + "','" + attachmentFileURI + "')");

        }
        attachmentCursor = sqlDatabase.rawQuery("SELECT * FROM ATTACHMENT_METADATA WHERE EVENT_ID ='" + assId + "';", null);
        System.out.println("CURSOR SIZE AFTER INSERTION" + attachmentCursor.getCount());
        if (attachmentCursor != null && attachmentCursor.getCount() > 0) {
            attachmentCursor.moveToFirst();

            do {
                attachmentList.add(0, attachmentCursor.getString(1));
                System.out.println(attachmentCursor.getString(1));

                //to use prefix for attachment download from uri
                //we need to trim URL to get file name
                //ex : https://sodataattachment.blob.core.windows.net/schoolonlineblobattachment/04-Java-OOP-Basics_1453376962454.pdf
                //is converted to schoolonlineblobattachment/04-Java-OOP-Basics_1453376962454

                attachmentPrefix = attachmentCursor.getString(2);
                attachmentPrefix = attachmentPrefix.substring(attachmentPrefix.lastIndexOf("/") + 1, attachmentPrefix.length());
                String s = attachmentCursor.getString(1);
                System.out.println("s------------------------" + s);
                // attachmentPrefixesList.add(0, s);

                assignmentAttachmentList.add(new AssignmentAttachmentListData(s));


            } while (attachmentCursor.moveToNext());
        }

        System.out.println("adapter set started");
        assignmentAttachmentAdapter = new AssignmentAttachmentAdapter(getApplicationContext(), assignmentAttachmentList);
//        attachList.setAdapter(attachAdapter);
        attachList.setAdapter(assignmentAttachmentAdapter);
        downloadButton.setVisibility(View.VISIBLE);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AssignmentDetail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    public class downloadingAttachment extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Iterable<ListBlobItem> valuesofConatainer = container.listBlobs();
            System.out.println(" Values of Container=" + valuesofConatainer);
            System.out.println("fileNameToDownload  :" + fileNameToDownload);
            System.out.println("ListBlobItem  :");
            try {


                for (ListBlobItem blobItem : container.listBlobs(fileNameToDownload)) {
                    System.out.println("STEP 1");
                    // If the item is a blob, not a virtual directory.
                    if (blobItem instanceof CloudBlob) {
                        System.out.println("STEP 2");
                        // Download the item and save it to a file with the same name.
                        CloudBlob blob = (CloudBlob) blobItem;
                        System.out.println("STEP 3");
                        try {
                            System.out.println("directory---------------------------------" + directory);
                            System.out.println("blob.getName()---------------------------------" + blob.getName());
                            System.out.println("directory  blob.getName()---------------------------------" + directory + "/" + blob.getName());
                            blob.download(new FileOutputStream(directory + "/" + blob.getName()));
                            System.out.println("STEP 4");
                            // blob.download(new FileOutputStream(dirname + blob.getStreamWriteSizeInBytes()));
                            System.out.println("blob name***************** ASynk Taskkkkkkkk" + blob.getName());

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception occured" + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (downloadCount > 1) {
                downloadCount--;
                downLoadAttachments();
            } else {
                downloadCompleteStatus = true;
                System.out.println("download complete");
                //Toast.makeText(getApplicationContext(), "Download Complete.", Toast.LENGTH_SHORT).show();
                downloadingText.setText("Attachments");
                downloadingText.setTypeface(null, Typeface.BOLD);
                downloadProgressBar.setVisibility(View.INVISIBLE);
                downloadButton.setVisibility(View.VISIBLE);
                downloadButton.setImageResource(R.drawable.download_check_mark_64);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, 0, Menu.NONE, getBundleData.getString("AssignClassStd") + " " +
                getBundleData.getString("AssignDivision"))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}