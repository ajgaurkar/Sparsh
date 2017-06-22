package com.relecotech.androidsparsh.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.relecotech.androidsparsh.R;

/**
 * Created by ajinkya on 10/28/2015.
 */
public class ShowDownloadImage extends ActionBarActivity {
    ImageView downloadimageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_download_image);
        downloadimageView = (ImageView) findViewById(R.id.show_download_imageView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        Log.d("Title", "" + title);
        getSupportActionBar().setTitle(title);
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

