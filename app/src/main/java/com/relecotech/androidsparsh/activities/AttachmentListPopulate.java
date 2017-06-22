package com.relecotech.androidsparsh.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.relecotech.androidsparsh.R;

import java.util.ArrayList;

/**
 * Created by amey on 10/16/2015.
 */
public class AttachmentListPopulate extends Activity {
    ListView attachListView;
    AssignmentPost aPost;
    ArrayAdapter<String> attachArrayAdapter;
    ArrayList<String> attachArrayList;

    public AttachmentListPopulate() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aPost = new AssignmentPost();
        attachListView = (ListView) findViewById(R.id.listViewAttachment);

        attachArrayList = new ArrayList<String>();
        attachArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, attachArrayList);


    }

    public void populateAttachList(String fileNameValue) {
        attachArrayList.add(fileNameValue);
        attachListView.setAdapter(attachArrayAdapter);
        //attachListView.setAdapter(attachAdapter);
    }
}
