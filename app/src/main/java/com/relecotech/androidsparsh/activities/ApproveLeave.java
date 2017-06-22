package com.relecotech.androidsparsh.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.relecotech.androidsparsh.MainActivity;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.azurecontroller.Parent_zone;

import java.util.HashMap;

/**
 * Created by amey on 10/16/2015.
 */
public class ApproveLeave extends Activity {
//    descriptiontext.setMovementMethod(new ScrollingMovementMethod());// to make assignment description scrollable

    TextView studentnameTextView;
    TextView rollNoTextView;
    TextView leaveFromTextView;
    TextView leaveToTextView;
    TextView dayCountTextView;
    TextView causeTextView;
    TextView leaveStatusTextView;
    EditText replyEditText;
    ImageView backpressarrowimageView;
    MobileServiceTable<Parent_zone> parent_zone_table;
    Parent_zone parent_zone_Item;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    Button leaveApproveBtn;
    Button leaveDenyBtn;
    ProgressDialog approvalDialog;
    String userRole;
    private TextView replyTextHeader;
    Bundle getBundleLeaveData;
    private JsonObject jsonObjectApproveLeaveParameters;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_leave);

        // leaveData = Parent_control.selectedLeavesListData;
        getBundleLeaveData = getIntent().getExtras();


        //All component initialization
        studentnameTextView = (TextView) findViewById(R.id.leaveApproveStudentNameTextView);
        rollNoTextView = (TextView) findViewById(R.id.leaveApproveRollNoTextView);
        leaveFromTextView = (TextView) findViewById(R.id.leavesApproveLeavesFromTextView);
        leaveToTextView = (TextView) findViewById(R.id.leavesApproveLeavesToTextView);
        dayCountTextView = (TextView) findViewById(R.id.leaveApproveLeavesCount);
        causeTextView = (TextView) findViewById(R.id.leavesApproveCauseEditText);
        replyEditText = (EditText) findViewById(R.id.leaveReplyEditText);
        replyTextHeader = (TextView) findViewById(R.id.leaveReplyTextViewHeader);
        leaveStatusTextView = (TextView) findViewById(R.id.leaveStatusTextView);
        backpressarrowimageView = (ImageView) findViewById(R.id.backpressimageView);
        approvalDialog = new ProgressDialog(this);

        leaveApproveBtn = (Button) findViewById(R.id.leaveApproveBtn);
        leaveDenyBtn = (Button) findViewById(R.id.leavesDenyButton);
        parent_zone_table = MainActivity.mClient.getTable("parent_zone", Parent_zone.class);

        studentnameTextView.setText(getBundleLeaveData.getString("StudentName"));
        rollNoTextView.setText("Roll No " + getBundleLeaveData.getString("StudentRollNo"));
        leaveFromTextView.setText(getBundleLeaveData.getString("StartDay"));
        leaveToTextView.setText(getBundleLeaveData.getString("EndDay"));
        dayCountTextView.setText(String.valueOf(getBundleLeaveData.getInt("DaysCount")));
        causeTextView.setText(getBundleLeaveData.getString("Cause").replace("\\n", "\n"));


        causeTextView.setMovementMethod(new ScrollingMovementMethod());// to make description scrollable
        replyEditText.setText(getBundleLeaveData.getString("Reply"));

        parent_zone_Item = new Parent_zone();
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();

        jsonObjectApproveLeaveParameters = new JsonObject();
        //Button visibility logic according to role and status
        userRole = MainActivity.userRole;

        if (getBundleLeaveData.getString("Status").equals("Pending")) {
            Log.d("login_user_role1", userRole);

            if (userRole.equals("Teacher")) {
                Log.d("login_user_role2", userRole);
                leaveApproveBtn.setVisibility(View.VISIBLE);
                leaveDenyBtn.setVisibility(View.VISIBLE);
                leaveStatusTextView.setVisibility(View.INVISIBLE);

                leaveApproveBtn.setText("Approve");
                leaveDenyBtn.setText("Deny");
            }
            if (userRole.equals("Student")) {
                Log.d("login_user_role3", userRole);
                leaveApproveBtn.setVisibility(View.INVISIBLE);
                leaveDenyBtn.setVisibility(View.INVISIBLE);
                leaveStatusTextView.setVisibility(View.VISIBLE);
                leaveStatusTextView.setBackgroundColor(Color.parseColor("#929393"));
                if (getBundleLeaveData.getString("Reply").equals("")) {
                    replyEditText.setVisibility(View.INVISIBLE);
                    replyTextHeader.setVisibility(View.INVISIBLE);
                }
                leaveStatusTextView.setText("Waiting for Approval");
                leaveStatusTextView.setTypeface(null, Typeface.BOLD_ITALIC);
                replyEditText.setKeyListener(null);
            }
        }
        if (getBundleLeaveData.getString("Status").equals("Approved")) {

            leaveApproveBtn.setVisibility(View.INVISIBLE);
            leaveDenyBtn.setVisibility(View.INVISIBLE);
            leaveStatusTextView.setVisibility(View.VISIBLE);
            leaveStatusTextView.setBackgroundColor(Color.parseColor("#009688"));

            leaveStatusTextView.setText("Approved");
            leaveStatusTextView.setTypeface(null, Typeface.BOLD_ITALIC);
            replyEditText.setKeyListener(null);
            if (getBundleLeaveData.getString("Reply").equals("")) {
                replyEditText.setVisibility(View.INVISIBLE);
                replyTextHeader.setVisibility(View.INVISIBLE);
            }

        }
        if (getBundleLeaveData.getString("Status").equals("Denied")) {
            leaveApproveBtn.setVisibility(View.INVISIBLE);
            leaveDenyBtn.setVisibility(View.INVISIBLE);
            leaveStatusTextView.setVisibility(View.VISIBLE);
            leaveStatusTextView.setBackgroundColor(Color.parseColor("#929393"));

            leaveStatusTextView.setText("Denied");
            replyEditText.setKeyListener(null);
            if (getBundleLeaveData.getString("Reply").equals("")) {
                replyEditText.setVisibility(View.INVISIBLE);
                replyTextHeader.setVisibility(View.INVISIBLE);
            }
        }

        leaveApproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(ApproveLeave.this)
                        .setTitle("Confirm Leave Approval")
                        .setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Logic to approve leave here
                                leaveAction("Approved");
                                System.out.println("Approve called ");

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                        .create().show();
            }
        });

        leaveDenyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ApproveLeave.this)
                        .setTitle("Confirm Leave Denial")
                        .setPositiveButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Logic to deny leave here
                                leaveAction("Denied");
                                System.out.println("Deny called ");
//                                setContentView(R.layout.xp);

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                        .create().show();
            }
        });

        backpressarrowimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void leaveAction(String status) {
        System.out.println("leaveAction called ");


        jsonObjectApproveLeaveParameters.addProperty("Reply", replyEditText.getText().toString());
        jsonObjectApproveLeaveParameters.addProperty("Status", status);
        jsonObjectApproveLeaveParameters.addProperty("Id", getBundleLeaveData.getString("LeaveId"));
        jsonObjectApproveLeaveParameters.addProperty("Category", "Leave");
        jsonObjectApproveLeaveParameters.addProperty("Student_id", getBundleLeaveData.getString("StudentId"));
        jsonObjectApproveLeaveParameters.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_REG_ID));


        //new LeaveUpdateTask().execute();
        ApproveLeave();
    }

    private void ApproveLeave() {

        approvalDialog.setMessage(ApproveLeave.this.getString(R.string.loading));
        approvalDialog.setCancelable(false);
        approvalDialog.show();

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("updateParentZoneApi", jsonObjectApproveLeaveParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("Approve Leave exception    " + exception);
                approvalDialog.dismiss();
                new AlertDialog.Builder(ApproveLeave.this)
                        .setTitle(R.string.check_network)
                        .setCancelable(false)
                        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApproveLeave();
                    }
                })
                        .create().show();
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println("Approve Leave  API   response    " + response);
                approvalDialog.dismiss();

                if (response.toString().equals("true")) {

                    new AlertDialog.Builder(ApproveLeave.this)
                            .setTitle("Response Submitted.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).create().show();

                } else {
                    Log.d("leave_parentZoneItem", "is null");
                }
            }
        });
    }
}