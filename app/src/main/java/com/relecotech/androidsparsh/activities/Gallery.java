package com.relecotech.androidsparsh.activities;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.relecotech.androidsparsh.ConnectionDetector;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.GalleryAdapter;
import com.relecotech.androidsparsh.controllers.GalleryListData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ajinkya on 10/21/2015.
 */
public class Gallery extends AppCompatActivity {
    public static String loggedInUserForGalleryListAdapter;
    ArrayList<GalleryListData> galleryList;
    String userRole;
    private ConnectionDetector connectionDetector;
    private boolean checkConnection;
    private SessionManager sessionManager;
    private HashMap<String, String> userDetails;
    private MobileServiceJsonTable mobileServiceJsonTable;
    private String urlToDwnldImage;
    private ListView listViewGallery;
    private String dateToSend;
    private String urlToSend;
    private HashMap<String, ArrayList<GalleryListData>> galleryList_Map;
    private GalleryAdapter galleryAdapter;
    private Handler gallery_Handler;
    private Timer gallery_Timer;
    TimeOutTimerClass timeOutTimerClass;
    long TIMEOUT_TIME = 25000;
    private ProgressDialog galleryProgressDialog;
    private GalleryListData galleryListData;
    private TextView noDataAvailableTextView;


    public Gallery() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewGallery = (ListView) findViewById(R.id.gallerylistview);

        noDataAvailableTextView = (TextView) findViewById(R.id.noDataAvailableTextViewGallery);

//        progressBar = (ProgressBar) findViewById(R.id.galleryprogressbar);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        checkConnection = connectionDetector.isConnectingToInternet();
        sessionManager = new SessionManager(getApplicationContext());


        gallery_Handler = new Handler();
        gallery_Timer = new Timer();
        timeOutTimerClass = new TimeOutTimerClass();

        galleryProgressDialog = new ProgressDialog(this);
        galleryProgressDialog.setCancelable(false);
        galleryProgressDialog.setMessage(getString(R.string.loading));

        userDetails = sessionManager.getUserDetails();
        galleryList = new ArrayList<>();
        galleryList_Map = new HashMap<>();


        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        loggedInUserForGalleryListAdapter = userRole;
//        try {
//            mobileServiceJsonTable = MainActivity.mClient.getTable("Gallery");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (checkConnection) {
            onExecutionStart();
            //new GalAsync().execute();
        } else {
            Toast.makeText(Gallery.this, R.string.no_internet, Toast.LENGTH_LONG).show();
        }

        listViewGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                GalleryListData selectedGalleryListData = galleryList.get(position);
                Intent galIntent = new Intent(Gallery.this, GalleryRecycler.class);
                galIntent.putExtra("GalleryId", selectedGalleryListData.getId());
                galIntent.putExtra("GalleryTag", selectedGalleryListData.getTag());
                galIntent.putExtra("GalleryUrl", urlToSend);
                galIntent.putExtra("GalleryPostDate", dateToSend);
                startActivity(galIntent);
            }
        });

    }


    private void onExecutionStart() {
        DownloadGalleryImages();
        galleryProgressDialog.show();
        gallery_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
        System.out.println("1.   onExecutionStart");
    }

    private void DownloadGalleryImages() {

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        JsonObject jsonObjectParameters = new JsonObject();

        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchGalleryData", jsonObjectParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" gallery API exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("  gallery  API   response    " + response);
                parseGalleryJSON(response);

            }
        });
    }


    private void parseGalleryJSON(JsonElement jsonElement) {

        if (jsonElement == null) {
            timeOutTimerClass.check = false;
            System.out.println("null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        } else {
            System.out.println("not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            timeOutTimerClass.check = true;
        }
        JsonArray jsonGalArray = jsonElement.getAsJsonArray();
        System.out.println("getJsonListResponse.size()" + jsonGalArray.size());


        if (jsonGalArray.size() == 0) {
            System.out.println("data not received");
            noDataAvailableTextView.setVisibility(View.VISIBLE);

        } else {
            noDataAvailableTextView.setVisibility(View.INVISIBLE);
            for (int loop = 0; loop < jsonGalArray.size(); loop++) {

                JsonObject jsonObjectForIteration = jsonGalArray.get(loop).getAsJsonObject();

                String galleryTitle = jsonObjectForIteration.get("gallery_title").toString().replace("\"", "");
                String galleryPostDate = jsonObjectForIteration.get("gallery_post_date").toString().replace("\"", "");
                String gallery_Tags = jsonObjectForIteration.get("gallery_tags").toString().replace("\"", "");
                String active = jsonObjectForIteration.get("active").toString().replace("\"", "");
                String galleryUrl = jsonObjectForIteration.get("gallery_url").toString().replace("\"", "");
                String fileName = jsonObjectForIteration.get("file_name").toString().replace("\"", "");
                int imageCount = jsonObjectForIteration.get("image_count").getAsInt();
                String fileUrl = jsonObjectForIteration.get("file_url").toString().replace("\"", "");
                String imgDesc = jsonObjectForIteration.get("image_description").toString().replace("\"", "");
                String galleryId = jsonObjectForIteration.get("Galley_ID").toString().replace("\"", "");
                String gallery_thumbnail_folder = jsonObjectForIteration.get("gallery_thumbnail_folder").toString().replace("\"", "");

                urlToSend = galleryUrl;
                dateToSend = galleryPostDate;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("d MMM yy", Locale.getDefault());
                    Date datePost = dateFormat.parse(galleryPostDate);
                    targetDateFormat.setTimeZone(TimeZone.getDefault());
                    galleryPostDate = targetDateFormat.format(datePost);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                urlToDwnldImage = galleryUrl + gallery_thumbnail_folder + fileName;
                System.out.println("urlToDwnldImage " + urlToDwnldImage);


                if (!galleryList_Map.containsKey(galleryId)) {
                    galleryListData = new GalleryListData(galleryId, active, galleryPostDate, galleryTitle, imgDesc, gallery_Tags, imageCount, urlToDwnldImage);
                    galleryList.add(0, galleryListData);
                    galleryList_Map.put(galleryId, galleryList);

                } else {
                    System.out.println("galleryList_Map map does not contain key");
                }
            }

        }

        galleryAdapter = new GalleryAdapter(Gallery.this, galleryList);
        listViewGallery.setAdapter(galleryAdapter);
        galleryProgressDialog.dismiss();
    }


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


    public class TimeOutTimerClass extends TimerTask {
        Boolean check = false;

        @Override
        public void run() {
            gallery_Handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("check Gallery " + check);
                    if (!check) {
                        //fetchAlertDataTask.cancel(true);
                        gallery_Timer.cancel();
                        galleryProgressDialog.dismiss();

                        try {

                            new android.app.AlertDialog.Builder(Gallery.this).setCancelable(false)
                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            reScheduleTimer();

                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                                            onBackPressed();
                                            gallery_Timer.purge();
//                                            /*   method is used to remove all cancelled tasks from this timer's task queue.*/
//                                            gallery_Timer.purge();
//                                            if (userRole.equals("Teacher")) {
////
//                                                Fragment fragment = new DashboardTeacherFragment();
//                                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                                                ft.replace(R.id.content_frame, fragment);
//                                                ft.commit();
//
//                                            }
//                                            if (userRole.equals("Student")) {
//
//                                                Fragment fragment = new DashboardStuentFragment();
//                                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                                                ft.replace(R.id.content_frame, fragment);
//                                                ft.commit();
//
//                                            }
                                        }
                                    }).setMessage(R.string.check_network).create().show();

                        } catch (Exception e) {
                            System.out.println("Exception Handle for Alert Dialog");
                        }

                    } else {
                        /* this line of code is used when everything goes normal and cancel the timer. */
                        gallery_Timer.cancel();
                    }
                }
            });
        }
    }

    private void reScheduleTimer() {

        gallery_Timer = new Timer("alertTimer", true);
        timeOutTimerClass = new TimeOutTimerClass();
        DownloadGalleryImages();
        galleryList.clear();
        galleryProgressDialog.show();
        gallery_Timer.schedule(timeOutTimerClass, TIMEOUT_TIME, 1000);
    }
}
