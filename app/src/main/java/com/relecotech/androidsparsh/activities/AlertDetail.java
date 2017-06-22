package com.relecotech.androidsparsh.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.DatabaseHandler;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;

import java.io.File;
import java.io.FileOutputStream;

import static com.relecotech.androidsparsh.activities.AssignmentPost.storageConnectionString;

/**
 * Created by amey on 7/4/2016.
 */
public class AlertDetail extends AppCompatActivity {

    private String alertTitle, alertDescription, alertCategory, alertPostDate, alertSubmittedBy, alertAttachmentCount;
    private TextView alertTitleTextView, alertCategoryTextView, alertPostDateTextView, alertSubmittedByTextView;
    private TextView showAlertAttachmentTextView, alertDescriptionTextView;
    private ImageView alertAttachmentDownloadButton;
    private Cursor attachmentCursor;
    private DatabaseHandler databaseHandler;


    private boolean checkConnection;
    private ConnectionDetector connectionDetector;
    private JsonObject jsonObjectToFetchAttachmentMetadata;
    private String attachment_fileName;
    private String attachment_imageUri;
    private JsonObject jsonObjectForIteration;
    private Intent getAlertIntent;
    private CloudBlobContainer container;
    private String fileNameToDownload;
    private String directory;
    Boolean downloadCompleteStatus = false;
    private String filePath;
    private String attachmentItemname;
    private String attachmentPrefix;
    private CardView attachmentDetailCardView;
    private ProgressBar attachmentDownloadProgressBar;
    private int downloadCount = 0;
    SQLiteDatabase sqlDatabase = MainActivity.schoolOnlineDatabase;
    private String alertId;
    private String userRole;
    private String s;
    private ImageView alertAttachmentIcon;
    private TextView alertsubmitedby;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_detail);


        alertTitleTextView = (TextView) findViewById(R.id.alertdetail_title_textView);
        alertCategoryTextView = (TextView) findViewById(R.id.alertdetail_category_textView);
        alertPostDateTextView = (TextView) findViewById(R.id.alertdetail_postdate_textView);
        alertSubmittedByTextView = (TextView) findViewById(R.id.alertdetail_submittedby_textView);
        alertDescriptionTextView = (TextView) findViewById(R.id.alertdetail_description_textView);
        showAlertAttachmentTextView = (TextView) findViewById(R.id.alertdetail_attachment_textView);
        alertAttachmentDownloadButton = (ImageView) findViewById(R.id.alertdetail_download_imageView);
        attachmentDetailCardView = (CardView) findViewById(R.id.alert_detail_attachment_panel_cv);
        attachmentDownloadProgressBar = (ProgressBar) findViewById(R.id.attachmentDownload_progressBar);
        alertAttachmentIcon = (ImageView) findViewById(R.id.alertdetail_attachment_imageView);
        alertsubmitedby = (TextView) findViewById(R.id.alertdetailtextView20);

        attachmentDownloadProgressBar.setVisibility(View.INVISIBLE);
        userRole = MainActivity.userRole;


        File dir = new File(Environment.getExternalStorageDirectory(), "/Sparsh/Alert_Attachment_Download");
        directory = dir.getPath();
        System.out.println("Directory created " + directory);


        try {
            System.out.println("directory creation block");
            if (dir.mkdirs()) {
                System.out.println("Directory  created insde mkdir");
            } else {
                Log.d("Directory not created", "");
            }
        } catch (Exception e) {
            System.out.println("directory creation EXCEPTIION");
            e.printStackTrace();
        }

        if (userRole.equals("Teacher")) {
            alertsubmitedby.setVisibility(View.GONE);
            alertSubmittedByTextView.setVisibility(View.GONE);
        } else {
            alertSubmittedByTextView.setVisibility(View.VISIBLE);
        }


        sqlDatabase.execSQL("CREATE TABLE IF NOT EXISTS ATTACHMENT_METADATA(EVENT_ID VARCHAR,FILE_NAME VARCHAR,IMG_URI VARCHAR);");

        databaseHandler = new DatabaseHandler(AlertDetail.this);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        checkConnection = connectionDetector.isConnectingToInternet();
        jsonObjectToFetchAttachmentMetadata = new JsonObject();


        getAlertIntent = getIntent();
        alertTitle = getAlertIntent.getStringExtra("AlertTitle");
        alertDescription = getAlertIntent.getStringExtra("AlertDescrition").replace("\\n", "\n").replace("\\", "");
        alertCategory = getAlertIntent.getStringExtra("AlertTag");
        alertPostDate = getAlertIntent.getStringExtra("AlertPostDate");
        alertSubmittedBy = getAlertIntent.getStringExtra("AlertSubmittedBy");
        alertAttachmentCount = getAlertIntent.getStringExtra("AlertAttachmentCount");
        alertId = getAlertIntent.getStringExtra("AlertId");

        System.out.println("alertTitle------------------" + alertTitle);
        System.out.println("alertDescription------------" + alertDescription);
        System.out.println("alertCategory---------------" + alertCategory);
        System.out.println("alertPostDate---------------" + alertPostDate);
        System.out.println("alertSubmittedBy------------" + alertSubmittedBy);
        System.out.println("alertAttachmentCount--------" + alertAttachmentCount);
        System.out.println("alertId" + alertId);

        //attachmentCursor = databaseHandler.getAttachmentData(getAlertIntent.getStringExtra("AlertId"));
        attachmentCursor = sqlDatabase.rawQuery("SELECT * FROM ATTACHMENT_METADATA WHERE EVENT_ID ='" + alertId + "';", null);
        System.out.println("getAlertIntent.getStringExtra(\"AlertId\") ******************** " + getAlertIntent.getStringExtra("AlertId"));
        System.out.println("alertAttachmentCount ###################### " + getAlertIntent.getStringExtra("AlertAttachmentCount"));
        jsonObjectToFetchAttachmentMetadata.addProperty("ALERT_ID", getAlertIntent.getStringExtra("AlertId"));

        if (alertAttachmentCount.equals("0")) {
            attachmentDetailCardView.setVisibility(View.INVISIBLE);
        } else {
            attachmentDetailCardView.setVisibility(View.VISIBLE);
            alertAttachmentDownloadButton.setVisibility(View.VISIBLE);

        }

        System.out.println(" attachmentCursor " + attachmentCursor.getCount());
        if (attachmentCursor.getCount() == 0) {

            if (checkConnection) {
                fetchAttachmentMetadata();
            } else {
                Toast.makeText(AlertDetail.this, R.string.check_network, Toast.LENGTH_LONG).show();
            }

        } else {
            System.out.println("attachment cursor get count not zero");
            attachmentCursor.moveToFirst();
            System.out.println("File name *****" + attachmentCursor.getString(attachmentCursor.getColumnIndex("FILE_NAME")));
            attachmentPrefix = attachmentCursor.getString(attachmentCursor.getColumnIndex("FILE_NAME"));
            System.out.println("attachmentPrefix ****************************   " + attachmentPrefix);
            fileNameToDownload = attachmentPrefix;
            if (fileNameToDownload.contains(".pdf") || fileNameToDownload.contains(".doc")) {
                showAlertAttachmentTextView.setText(fileNameToDownload);
                alertAttachmentIcon.setImageResource(android.R.drawable.btn_star);
            } else {
                showAlertAttachmentTextView.setText(fileNameToDownload);
                alertAttachmentIcon.setImageResource(android.R.drawable.ic_menu_gallery);
            }
            //showAlertAttachmentTextView.setText(fileNameToDownload);
            downloadCompleteStatus = true;
            alertAttachmentDownloadButton.setVisibility(View.VISIBLE);
            alertAttachmentDownloadButton.setImageResource(R.drawable.download_check_mark_64);
        }

        alertTitleTextView.setText(alertTitle);
        alertCategoryTextView.setText(alertCategory);
        alertPostDateTextView.setText(alertPostDate);
        alertSubmittedByTextView.setText(alertSubmittedBy);
        alertDescriptionTextView.setText(alertDescription);


        System.out.println("alertTitle ----------" + alertTitle);
        System.out.println("alertDescription ----------" + alertDescription);
        System.out.println("alertCategory ----------" + alertCategory);
        System.out.println("alertPostDate ----------" + alertPostDate);
        System.out.println("alertSubmittedBy ----------" + alertSubmittedBy);


        alertAttachmentDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attachmentDownloadProgressBar.setVisibility(View.VISIBLE);
                alertAttachmentDownloadButton.setVisibility(View.INVISIBLE);
                downloadAttachment();
                System.out.println("Downloading Called....................");
            }
        });

        showAlertAttachmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadCompleteStatus) {
                    attachmentItemname = showAlertAttachmentTextView.getText().toString();
                    filePath = "file://" + directory + "/" + attachmentItemname;
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

                } else {
                    System.out.println("Download Status False.............");
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchAttachmentMetadata() {

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchAlertDetail", jsonObjectToFetchAttachmentMetadata);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Alert Detail Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Alert Detail  response    " + response);

                JsonArray attachmentListArray = response.getAsJsonArray();
                System.out.println("Attachment Json Array------------------------------------------" + attachmentListArray);
                System.out.println("Array size------------------------------------------" + attachmentListArray.size());

                addAttachmentToDatabase(attachmentListArray);

            }
        });
    }

    private void addAttachmentToDatabase(JsonArray attachmentListArray) {
        for (int loopA = 0; loopA < attachmentListArray.size(); loopA++) {
            jsonObjectForIteration = attachmentListArray.get(0).getAsJsonObject();
            attachment_fileName = jsonObjectForIteration.get("filename").toString().replace("\"", "");
            alertId = getAlertIntent.getStringExtra("AlertId");
            System.out.println(" attachment_fileName" + attachment_fileName);
            attachment_imageUri = jsonObjectForIteration.get("imageUri").toString().replace("\"", "");

            sqlDatabase.execSQL("INSERT INTO ATTACHMENT_METADATA VALUES ('" + alertId + "','" + attachment_fileName + "','" + attachment_imageUri + "')");
            System.out.println("Data inserted .......in  Attachment tables");
        }

        attachmentCursor = sqlDatabase.rawQuery("SELECT * FROM ATTACHMENT_METADATA WHERE EVENT_ID ='" + alertId + "';", null);
        System.out.println("CURSOR SIZE AFTER INSERTION--------" + attachmentCursor.getCount());
        attachmentCursor.moveToFirst();
        do {

            try {
                attachmentPrefix = attachmentCursor.getString(1);
                System.out.println("attachmentPrefix" + attachmentPrefix);
            } catch (RuntimeException r) {
                System.out.println("Runtime  Exception" + r.getMessage());
            }
        } while (attachmentCursor.moveToNext());
        try {
            fileNameToDownload = attachmentPrefix;
            System.out.println("fileNameToDownload" + fileNameToDownload);
            if (fileNameToDownload.contains(".pdf") || fileNameToDownload.contains(".doc")) {
                if (fileNameToDownload.contains(".pdf")) {

                    alertAttachmentIcon.setImageResource(R.drawable.pdf_icon);

                } else {

                    alertAttachmentIcon.setImageResource(R.drawable.document_icon);
                }
                showAlertAttachmentTextView.setText(fileNameToDownload);
            } else {
                showAlertAttachmentTextView.setText(fileNameToDownload);
                alertAttachmentIcon.setImageResource(android.R.drawable.ic_menu_gallery);
            }
            // showAlertAttachmentTextView.setText(fileNameToDownload);

        } catch (RuntimeException r) {
            System.out.println("Runtime Exception R-" + r.getMessage());
        }
    }


    public void downloadAttachment() {
        try {

//            final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=schooltrailtestenvirnmnt;AccountKey=I9utd0Ho/QvxmzP52LmZby6IK2V0l9W5+JTKOOoHuAgw5hwEVHwggQSyXuhnipbi6C9VSU7mtTnTl6GhQfZQmw==;";

            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            // Get a reference to a container.
            // The container name must be lower case
            container = blobClient.getContainerReference("sparshdevmobileapp");
            if (container.listBlobs() != null) {
                Log.d("in if ", "list contain value");
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

            Iterable<ListBlobItem> valuesOfContainer = container.listBlobs();
            System.out.println(" Values of Container=" + valuesOfContainer);
            System.out.println("fileNameToDownload  :" + fileNameToDownload);
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
                            System.out.println("blob name***************** ASync Taskkk" + blob.getName());

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception Occurred" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Earlier Code  not working for Downloading attachment

//            attachmentDownloadProgressBar.setVisibility(View.INVISIBLE);
//            downloadCompleteStatus = true;
//            System.out.println("Download Completed................................");

            if (downloadCount > 1) {
                downloadCount--;
                downloadAttachment();
            } else {
                downloadCompleteStatus = true;
                attachmentDownloadProgressBar.setVisibility(View.INVISIBLE);
                alertAttachmentDownloadButton.setVisibility(View.VISIBLE);
                alertAttachmentDownloadButton.setImageResource(R.drawable.download_check_mark_64);
                System.out.println("download complete");
                Toast.makeText(getApplicationContext(), "Download Complete.", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
