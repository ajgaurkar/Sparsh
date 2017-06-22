package com.relecotech.androidsparsh.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.relecotech.androidsparsh.ConnectionDetector;

import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.adapters.GalleryRecycleAdapter;
import com.relecotech.androidsparsh.controllers.GalleryRecyclerListData;
import com.relecotech.androidsparsh.controllers.Image;
import com.relecotech.androidsparsh.fragments.SlideshowDialogFragment;

import java.util.ArrayList;

public class GalleryRecycler extends AppCompatActivity {

    private Intent getGalIntent;
    private String galleryUrl, galleryTag, galleryPostDate, galleryId;
    private JsonObject jsonObjectGalleryImg;
    private ConnectionDetector connectionDetector;
    private boolean checkConnection;
    private GalleryRecycleAdapter mAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Image> images;
    private GalleryRecyclerListData galleryData;
    ArrayList<GalleryRecyclerListData> galleryList;
    ;
    private String galleryMainImageFolder;
    private String fileName;
    private ArrayList<String> fileNameToSendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_recycler);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        galleryList = new ArrayList<>();
        connectionDetector = new ConnectionDetector(getApplicationContext());
        checkConnection = connectionDetector.isConnectingToInternet();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        images = new ArrayList<>();
        fileNameToSendList = new ArrayList<>();

        getGalIntent = getIntent();
        galleryUrl = getGalIntent.getStringExtra("GalleryUrl");
        galleryTag = getGalIntent.getStringExtra("GalleryTag");
        galleryPostDate = getGalIntent.getStringExtra("GalleryPostDate");
        galleryId = getGalIntent.getStringExtra("GalleryId");

        System.out.println(" galleryPostDate " + galleryPostDate);
        System.out.println("galleryTag" + galleryTag);
        System.out.println("galleryUrl" + galleryUrl);
        System.out.println("galleryId " + galleryId);

        if (checkConnection) {
            FetchGalleryImage();
        }

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        mAdapter = new GalleryRecycleAdapter(getApplicationContext(), galleryList);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryRecycleAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryRecycleAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
//                bundle.putString("galleryUrl", galleryUrl);
//                bundle.putString("fileName", fileNameToSendList.get(position));
//                bundle.putString("mainFolder", galleryList.get(position).getMainFolderName());
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void FetchGalleryImage() {
        jsonObjectGalleryImg = new JsonObject();
//        jsonObjectGalleryImg.addProperty("Gallery_Tag", galleryTag);
//        jsonObjectGalleryImg.addProperty("Gallery_PostDate", galleryPostDate);
        jsonObjectGalleryImg.addProperty("Gallery_Id", galleryId);

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("fetchGalleryImage", jsonObjectGalleryImg);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" Gallery Recycler  Exception    " + exception);
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Gallery Recycler  response    " + response);
                parseGalleryImgesJSON(response);
            }
        });
    }


    private void parseGalleryImgesJSON(JsonElement jsonElement) {
        if (jsonElement == null) {
            //timeOutTimerClass.check = false;
            System.out.println("null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        } else {
            System.out.println("not null $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//            timeOutTimerClass.check = true;
        }
        JsonArray jsonGalArray = jsonElement.getAsJsonArray();
        System.out.println("getJsonListResponse.size()" + jsonGalArray.size());


        if (jsonGalArray.size() == 0) {
            System.out.println("data not received");
        }
//        images.clear();
        for (int loop = 0; loop < jsonGalArray.size(); loop++) {

            JsonObject jsonObjectForIteration = jsonGalArray.get(loop).getAsJsonObject();

            fileName = jsonObjectForIteration.get("file_name").toString().replace("\"", "");
            String imageDescription = jsonObjectForIteration.get("image_description").toString().replace("\"", "");
            galleryMainImageFolder = jsonObjectForIteration.get("gallery_main_image_folder").toString().replace("\"", "");
            String galleryThumbnailFolder = jsonObjectForIteration.get("gallery_thumbnail_folder").toString().replace("\"", "");

            String urlToDownloadImages = galleryUrl + galleryThumbnailFolder + fileName;
            String urlToDownloadLargeImages = galleryUrl + galleryMainImageFolder + fileName;
            System.out.println(" urlToDownloadImages " + urlToDownloadImages);


            fileNameToSendList.add(fileName);

            galleryData = new GalleryRecyclerListData(galleryPostDate, imageDescription, urlToDownloadImages, fileName, galleryMainImageFolder, galleryThumbnailFolder);
            galleryList.add(galleryData);

            mAdapter = new GalleryRecycleAdapter(getApplicationContext(), galleryList);
            recyclerView.setAdapter(mAdapter);


            Image image = new Image();
            image.setName(fileName);
            image.setLarge(urlToDownloadLargeImages);
            image.setDescription(imageDescription);
            images.add(image);

        }

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

}