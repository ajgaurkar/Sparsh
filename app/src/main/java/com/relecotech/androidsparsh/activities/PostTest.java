package com.relecotech.androidsparsh.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.azurecontroller.Assignment;

/**
 * Created by ajinkya on 1/28/2016.
 */
public class PostTest extends Activity {

    MobileServiceTable<Assignment> testTable;
    MobileServiceList<Assignment> returnTable;
    Assignment assignmentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_post);

        Button btnPost = (Button) findViewById(R.id.testPostButton);
        Button btnRead = (Button) findViewById(R.id.testReadButton);
        try {
            testTable = MainActivity.mClient.getTable(Assignment.class);
        } catch (Exception e) {
            System.out.print("exception in table initialization");
        }
        assignmentItem = new Assignment();
        assignmentItem.setAssignment_class("1");
        assignmentItem.setAssignment_description("description");
        assignmentItem.setAssignment_subject("maths");
        assignmentItem.setAssignment_div("B");
        assignmentItem.setAssignment_credit(12);
        assignmentItem.setActive(true);
        assignmentItem.setAssignmnet_grades("B");
        assignmentItem.setAssignment_submitted_by("T002");

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new addRecordCalling().execute();
            }
        });
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new readRecordCalling().execute();
            }
        });

    }

    public class addRecordCalling extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
//                final Assignment assmtResponse = addItemInAssignmentTable(assignmentItem);
                System.out.println("DO IN BACKGROUND STARTED");

                Assignment assignmentEntity = testTable.insert(assignmentItem).get();
                System.out.println("DO IN BACKGROUND eNDED");
//                Assignment assignmentEntity = addItemInAssignmentTable(assignmentItem);
                System.out.println(assignmentEntity);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("DO IN BACKGROUND eXCEPTION");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    public class readRecordCalling extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
//                final Assignment assmtResponse = addItemInAssignmentTable(assignmentItem);
                System.out.println("DO IN BACKGROUND STARTED");

                returnTable = testTable.parameter("regId", "T002").parameter("userRole","Student").parameter("studId","031EF0FF-7108-4396-9F45-9FC12DF20E5C").parameter("schoolClassId","6AA7AC1F-95A6-4BCA-BC25-E1064B274B47").execute().get();
//                returnTable = testTable.dr().get();
                System.out.println("DO IN BACKGROUND eNDED");
//                Assignment assignmentEntity = addItemInAssignmentTable(assignmentItem);
                System.out.println("returnTable SIZE"+returnTable.size());

//                Assignment temp = returnTable.get(0);
//                System.out.println(temp.getAssignment_class());
//                System.out.println(temp.getId());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("DO IN BACKGROUND eXCEPTION");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }
}
