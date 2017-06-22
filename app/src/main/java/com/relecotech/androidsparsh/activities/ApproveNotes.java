package com.relecotech.androidsparsh.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.relecotech.androidsparsh.controllers.NotesListData;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by amey on 10/16/2015.
 */
public class ApproveNotes extends AppCompatActivity {

    //    TextView concernedPersonNameTextView;
//  TextView rollNoTextView;
    TextView notesCategoryTextView;
    TextView meetingRequestedSchedule;
    TextView notesDescriptionTextView;
    EditText replyEditText;
    ImageView backpressarrowimageView;
    Button meetingApproveBtn;
    Button meetingDenyBtn;
    Button noteReplyBtn;
    String userRole;
    private Parent_zone parentZoneItem;
    private MobileServiceTable<Parent_zone> parentZoneTable;
    private boolean approvalStatus;
    private ProgressDialog approvalDialog;
    private TextView replyTextHeader;
    public NotesListData selectedNoteData;
    public Calendar calendar;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    Bundle getBundleNotesData;
    private JsonObject jsonObjectApproveNotesParameters;
    private String reply;
    private String reply1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_notes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getBundleNotesData = getIntent().getExtras();
        //initializing components
        notesCategoryTextView = (TextView) findViewById(R.id.notesApproveCategory);
        meetingRequestedSchedule = (TextView) findViewById(R.id.notesApproveScheduleMeetTextView);
        notesDescriptionTextView = (TextView) findViewById(R.id.notesApproveDescriptionTextView);
        notesDescriptionTextView.setMovementMethod(new ScrollingMovementMethod());// to make  description scrollable
        replyEditText = (EditText) findViewById(R.id.notesApproveReplyEditText);
        backpressarrowimageView = (ImageView) findViewById(R.id.backpressimageView);
        meetingApproveBtn = (Button) findViewById(R.id.notesApproveMeetingApproveButton);
        meetingDenyBtn = (Button) findViewById(R.id.notesApproveMeetingDenyButton);
        noteReplyBtn = (Button) findViewById(R.id.notesApproveReplyButton);
        replyTextHeader = (TextView) findViewById(R.id.replyHeaderTextView1);
        approvalDialog = new ProgressDialog(this);
        //initializing azure database components
        parentZoneItem = new Parent_zone();
        parentZoneTable = MainActivity.mClient.getTable("parent_zone", Parent_zone.class);

        //setting basic values to all fields
        meetingRequestedSchedule.setText("");
        notesCategoryTextView.setText(getBundleNotesData.getString("Category"));
        notesDescriptionTextView.setText(String.valueOf(getBundleNotesData.getString("Description").replace("\\n", "\n")));
        replyEditText.setText(getBundleNotesData.getString("Reply").replace("\\n", "\n").replace("\\", ""));

        userRole = MainActivity.userRole;

        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();

        jsonObjectApproveNotesParameters = new JsonObject();


        Log.d("login_user_role", userRole);

        if (userRole.equals("Student")) {
            getSupportActionBar().setTitle(getBundleNotesData.getString("ConcernedTeacher"));

            replyEditText.setMovementMethod(new ScrollingMovementMethod());// to make assignment description scrollable
            replyEditText.setKeyListener(null);

            noteReplyBtn.setVisibility(View.VISIBLE);
            meetingApproveBtn.setVisibility(View.INVISIBLE);
            meetingDenyBtn.setVisibility(View.INVISIBLE);
            if (getBundleNotesData.getString("Reply").equals("")) {
                replyEditText.setVisibility(View.INVISIBLE);
                replyTextHeader.setVisibility(View.INVISIBLE);
            }

            if (getBundleNotesData.getString("Category").equals("Meeting")) {

                meetingRequestedSchedule.setText("Requested Schedule" + "\n" + getBundleNotesData.getString("MeetingSchedule"));
                if (getBundleNotesData.getString("Status").equals("Approved")) {
                    noteReplyBtn.setText("Meeting Approved");
                    noteReplyBtn.setBackgroundColor(Color.parseColor("#009688"));

                } else if (getBundleNotesData.getString("Status").equals("Denied")) {
                    noteReplyBtn.setText("Meeting Refused");
                    noteReplyBtn.setBackgroundColor(Color.parseColor("#ff4343"));

                } else {
                    noteReplyBtn.setText("Waiting for Approval");
                    noteReplyBtn.setTypeface(null, Typeface.BOLD_ITALIC);
                }

            } else {

                if (getBundleNotesData.getString("Status").equals("Replied")) {
                    noteReplyBtn.setText("Replied");
                } else {
                    noteReplyBtn.setText("Waiting for Reply");
                    noteReplyBtn.setTypeface(null, Typeface.BOLD_ITALIC);
                }
            }
        }
        if (userRole.equals("Teacher")) {

            getSupportActionBar().setTitle(getBundleNotesData.getString("ConcernedStudent"));
            replyEditText.setHint("Reply");

            if (getBundleNotesData.getString("Category").equals("Meeting")) {
                meetingRequestedSchedule.setText("Requested Schedule" + "\n" + getBundleNotesData.getString("MeetingSchedule"));

                if (getBundleNotesData.getString("Status").equals("Approved")) {

                    noteReplyBtn.setVisibility(View.VISIBLE);
                    meetingApproveBtn.setVisibility(View.INVISIBLE);
                    meetingDenyBtn.setVisibility(View.INVISIBLE);
                    noteReplyBtn.setText(getBundleNotesData.getString("Status"));
                    noteReplyBtn.setBackgroundColor(Color.parseColor("#009688"));
                    replyEditText.setKeyListener(null);

                    if (getBundleNotesData.getString("Reply").equals("")) {
                        replyEditText.setVisibility(View.INVISIBLE);
                        replyTextHeader.setVisibility(View.INVISIBLE);
                    }

                } else if (getBundleNotesData.getString("Status").equals("Denied")) {

                    noteReplyBtn.setText("Meeting Refused");
                    replyEditText.setKeyListener(null);
                    noteReplyBtn.setText("Meeting Refused");
                    noteReplyBtn.setBackgroundColor(Color.parseColor("#ff4343"));
                    meetingApproveBtn.setVisibility(View.INVISIBLE);
                    meetingDenyBtn.setVisibility(View.INVISIBLE);
                    replyEditText.setKeyListener(null);

                    if (getBundleNotesData.getString("Status").equals("")) {
                        replyEditText.setVisibility(View.INVISIBLE);
                        replyTextHeader.setVisibility(View.INVISIBLE);
                    }

                } else {

                    noteReplyBtn.setVisibility(View.INVISIBLE);
                    meetingApproveBtn.setVisibility(View.VISIBLE);
                    meetingDenyBtn.setVisibility(View.VISIBLE);
                    meetingApproveBtn.setText("Approve");
                    meetingDenyBtn.setText("Deny");

                }
            } else {

                noteReplyBtn.setVisibility(View.VISIBLE);
                meetingApproveBtn.setVisibility(View.INVISIBLE);
                meetingDenyBtn.setVisibility(View.INVISIBLE);

                if (getBundleNotesData.getString("Status").equals("Replied")) {

                    noteReplyBtn.setText(getBundleNotesData.getString("Status"));
                    replyEditText.setKeyListener(null);
                    if (getBundleNotesData.getString("Status").equals("")) {
                        replyEditText.setVisibility(View.INVISIBLE);
                        replyTextHeader.setVisibility(View.INVISIBLE);
                    }

                } else {
                    noteReplyBtn.setText("Reply");

                }
            }
        }

        meetingApproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(ApproveNotes.this)
                        .setTitle("Confirm Meeting Approval")
                        .setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Snackbar.make(view, "Approved", Snackbar.LENGTH_SHORT).show();
                                //Logic to Approve Meeting  here
                                noteAction("Approved");

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                        .create().show();
            }
        });

        meetingDenyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(ApproveNotes.this)
                        .setTitle("Confirm Meeting Denial")
                        .setPositiveButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Snackbar.make(view, "Deny", Snackbar.LENGTH_SHORT).show();
                                //Logic to Approve leave here
                                noteAction("Denied");

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                        .create().show();
            }
        });
        noteReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (userRole.equals("Teacher")
                        && !(getBundleNotesData.getString("Status").equals("Replied"))
                        && !(getBundleNotesData.getString("Status").equals("Approved"))) {

                    new AlertDialog.Builder(ApproveNotes.this)
                            .setTitle("Confirm Reply")
                            .setPositiveButton("Reply", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

//                                    Snackbar.make(view, "Reply", Snackbar.LENGTH_SHORT).show();
                                    //Logic to Approve leave here
                                    noteAction("Replied");

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                            .create().show();
                }
            }
        });

    }

    private void noteAction(String status) {

        //jsonObjectApproveNotesParameters.addProperty("Reply", replyEditText.getText().toString());
        reply = replyEditText.getText().toString();
        //reply1 = android.database.DatabaseUtils.sqlEscapeString(reply);
        reply1 = reply.replace("'", "''");
        System.out.println(" reply1 " + reply1);
        jsonObjectApproveNotesParameters.addProperty("Reply", reply1);
        jsonObjectApproveNotesParameters.addProperty("Status", status);
        jsonObjectApproveNotesParameters.addProperty("Id", getBundleNotesData.getString("NotesId"));
        jsonObjectApproveNotesParameters.addProperty("Category", getBundleNotesData.getString("Category"));
        jsonObjectApproveNotesParameters.addProperty("Student_id", getBundleNotesData.getString("StudentId"));
        jsonObjectApproveNotesParameters.addProperty("Teacher_id", userDetails.get(SessionManager.KEY_TEACHER_REG_ID));

        System.out.println("reply :" + replyEditText.getText().toString());
        System.out.println("status :" + status);
        System.out.println("id :" + getBundleNotesData.getString("NotesId"));

        //new NotesUpdateTask().execute();
        ApproveNotes();
    }


    private void ApproveNotes() {

        approvalDialog.setMessage(ApproveNotes.this.getString(R.string.loading));
        approvalDialog.setCancelable(false);
        approvalDialog.show();

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("updateParentZoneApi", jsonObjectApproveNotesParameters);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" ApproveNotes exception    " + exception);
                approvalDialog.dismiss();
                new AlertDialog.Builder(ApproveNotes.this)
                        .setTitle(R.string.check_network)
                        .setCancelable(false)
                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton("Re-Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApproveNotes();
                    }
                })
                        .create().show();
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" ApproveNotes  API   response    " + response);

                if (response.toString().equals("true")) {

                    approvalDialog.dismiss();
                    new AlertDialog.Builder(ApproveNotes.this)
                            .setTitle("Response submitted")
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

    public class NotesUpdateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            approvalDialog.setMessage("Please wait");
            approvalDialog.setCancelable(false);
            approvalDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Parent_zone notesApprovalResponseEntity = parentZoneTable.update(parentZoneItem).get();
                System.out.println("response Id :" + notesApprovalResponseEntity.getId());
                approvalStatus = true;
            } catch (Exception e) {
                e.printStackTrace();
                approvalStatus = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            approvalDialog.dismiss();
            if (approvalStatus) {
                new AlertDialog.Builder(ApproveNotes.this)
                        .setTitle("Response Submitted.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).create().show();
            } else {
                new AlertDialog.Builder(ApproveNotes.this)
                        .setTitle(R.string.check_network)
                        .setCancelable(false)
                        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new NotesUpdateTask().execute();
                    }
                })
                        .create().show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (userRole.equals("Teacher")) {
            menu.add(Menu.NONE, 0, Menu.NONE, "Roll No " + getBundleNotesData.getString("StudentRollNo"))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
