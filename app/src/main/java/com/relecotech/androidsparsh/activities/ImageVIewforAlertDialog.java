package com.relecotech.androidsparsh.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.relecotech.androidsparsh.R;

/**
 * Created by amey on 10/16/2015.
 */
public class ImageVIewforAlertDialog extends Activity {
    ImageView imageView;
    Bitmap bitmapForAttachmentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_alertdialog);
        bitmapForAttachmentView = AssignmentPost.attachlistdata;
        Log.d("Image", "" + bitmapForAttachmentView);
        imageView = (ImageView) findViewById(R.id.imageviewalertdialog);
        imageView.setImageBitmap(bitmapForAttachmentView);
    }
}