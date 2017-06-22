package com.relecotech.androidsparsh.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.relecotech.androidsparsh.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by amey on 10/16/2015.
 */
public class PickCameraImage extends Activity {

    private static final int CAMERA_REQUEST = 1;
    ImageView imgConfirm;
    Bitmap photo;
    String confirmPhotoToBase64, attachmentPhotoNameLower;
    AssignmentPost assPost;
    // ArrayAdapter<String> attachAdapter;
    //ArrayList<String> attachList;
    //ListView attachListView;
    FilePickerClass filePicker;
    int index;
    File finalFile;
    String path;
    Cursor cursor;
    int idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_for_confirmation);
        imgConfirm = (ImageView) findViewById(R.id.confirmImageView);
        assPost = new AssignmentPost();
        filePicker = new FilePickerClass();
        pic();
    }

    public void pic() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            Log.d("bitmap value ", "" + photo);
            imgConfirm.setImageBitmap(photo);
        }
    }

    public void retake(View view) {
        Toast.makeText(getApplicationContext(), "retake", Toast.LENGTH_SHORT).show();
        pic();
    }

    public void confirm(View view) {
        Toast.makeText(getApplicationContext(), "confirm", Toast.LENGTH_SHORT).show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        confirmPhotoToBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.d("encodedPhotoToBase64 ", "" + confirmPhotoToBase64);
        assPost.encodedLowerToBase64 = confirmPhotoToBase64;
        this.finish();
        Uri tempUri = getImageUri(getApplicationContext(), photo);
        Log.d("URI", "" + tempUri);

        // CALL THIS METHOD TO GET THE ACTUAL PATH
        finalFile = new File(getRealPathFromURI(tempUri));
        String photoPath = finalFile.toString();
        Toast.makeText(getApplicationContext(), "" + finalFile, Toast.LENGTH_LONG).show();

        index = photoPath.lastIndexOf("/");
        Log.d("index", "" + index);
        attachmentPhotoNameLower = photoPath.substring(index + 1);
        Log.d("name", "" + attachmentPhotoNameLower);
        //Log.d("list length", "" + attachList.size());
        assPost.attachList.add(attachmentPhotoNameLower);
        //Log.d("list length", "" + attachList.size());
        assPost.attachListView.setAdapter(assPost.attachAdapter);

    }

    public void closeCam(View view) {
        Toast.makeText(getApplicationContext(), "closeCam", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("String path", path);
        return Uri.parse(path);
        //  return null;
    }

    public String getRealPathFromURI(Uri uri) {
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor.getString(idx);
    }


}
