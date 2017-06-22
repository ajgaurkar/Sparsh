package com.relecotech.androidsparsh.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.relecotech.androidsparsh.adapters.AssApproveAdapterAdapter;
import com.relecotech.androidsparsh.controllers.AssApproveListData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import com.relecotech.sparshalpha.azurecontroller.Student;

/**
 * Created by amey on 10/16/2015.
 */
public class AssigmentApproval extends Activity {
    Button assSubmitAndConfirm;
    Spinner assmntApprovalScoresSpinner, assPerformance;
    EditText dialogNotesTextView;
    TextView dialogApprovedScoreTextView;
    String assIdForApproval = AssignmentDetail.assId;
    int maxCreditsForAssApproval = AssignmentDetail.maxCreditsForAssApproval;
    String scoreType = AssignmentDetail.scoreTypeForAssApproval;
    ListView studentNameListView;

    ArrayList<AssApproveListData> studentnameList;
    ArrayList<String> approvalCreditsSppinerList;
    AssApproveListData selectedAssApproveData;
    private HashMap<String, AssApproveListData> getStudentResubmitAssignmentData;
    private HashMap<String, AssApproveListData> getStudentAfterApprovedAssignmentData;
    Map<String, String> hm3 = new HashMap<String, String>();


    //variables for approval list list
    private String studentFirstName;
    private String studentLastName;
    private String studentAssignmentStatus;
    private String studentMiddleName;
    private String studentRollNo;
    private String assmntStatusTableId;
    private String assmntId;
    private String studentId;
    private String credits;
    private String grades;
    private String notes;

    //private ArrayAdapter<AssApproveListData> adapter;
    private ProgressDialog assApproveProgressDialog;
    private ProgressBar progressBar;

    //connection detection components
    private boolean checkconnection;
    private ConnectionDetector connectionDetector;
    private JsonObject jsonObjectParametersAssigmentApproval;

    //components for assmnt_approval through Azure
//    private MobileServiceTable<Student> studentTable;
//    MobileServiceList<Student> studentList;
    private MobileServiceJsonTable mJsonToDoTable;
    JsonArray jsonArray = null;
    private JsonElement jsonObject;
    private JsonArray jsonArrayForStatusUpdation;
    private String approvedMarks;
    private String approvedGrades;
    public View assmntApprovalSnackView;
    HashMap<String, String> userDetails;
    private SessionManager sessionManager;
    private String updatedAt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignment_approve);

        this.overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

//        studentTable = MainActivity.mClient.getTable(Student.class);
        assSubmitAndConfirm = (Button) findViewById(R.id.assApproveSubmitBtn);
        studentNameListView = (ListView) findViewById(R.id.assApproveListView);
        progressBar = (ProgressBar) findViewById(R.id.assApproveProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        studentnameList = new ArrayList<AssApproveListData>();
        approvalCreditsSppinerList = new ArrayList<String>();
        mJsonToDoTable = MainActivity.mClient.getTable("assignment_status");
        assmntApprovalSnackView = (View) findViewById(R.id.assignmentprroval_snack);
        jsonObjectParametersAssigmentApproval = new JsonObject();
        jsonObjectParametersAssigmentApproval.addProperty("assignment_id", assIdForApproval);
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();
        getStudentResubmitAssignmentData = new HashMap<>();
        getStudentAfterApprovedAssignmentData = new HashMap<>();

        //AssApproveAdapterAdapter adapter = new AssApproveAdapterAdapter(getApplicationContext(), studentnameList);
        System.out.println("scoreType : " + scoreType);
        if (scoreType.equals("Grades")) {

            approvalCreditsSppinerList.add("A");
            approvalCreditsSppinerList.add("A+");
            approvalCreditsSppinerList.add("B");
            approvalCreditsSppinerList.add("B+");
            approvalCreditsSppinerList.add("C");
            approvalCreditsSppinerList.add("C+");
            approvalCreditsSppinerList.add("D");
            approvalCreditsSppinerList.add("D+");
            approvalCreditsSppinerList.add("[ Select grades ]");
        } else {
            for (int creditCounter = 1; creditCounter <= maxCreditsForAssApproval; creditCounter++) {
                approvalCreditsSppinerList.add("" + creditCounter);
            }
            approvalCreditsSppinerList.add("[ Select marks ]");
        }

        connectionDetector = new ConnectionDetector(getApplicationContext());
        checkconnection = connectionDetector.isConnectingToInternet();

        //  Fetch student names with status here after connection check
        if (checkconnection) {
            System.out.println("FetchStudentList----------Called ");
            FetchStudentList();
            progressBar.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection..!.", Toast.LENGTH_LONG).show();
        }

        assSubmitAndConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //convert list data to jsonarray
                AssApproveListData assApproveListDataForIteration;
                JsonObject jsonObjectForIteration;
                jsonArrayForStatusUpdation = new JsonArray();
//                for (int i = 0; i < studentnameList.size(); i++) {
//                    assApproveListDataForIteration = studentnameList.get(i);
//
//                    jsonObjectForIteration = new JsonObject();
//                    jsonObjectForIteration.addProperty("assignment_status_id", assApproveListDataForIteration.getAssmntStatusTableId());
//                    jsonObjectForIteration.addProperty("assignment_status_credits", assApproveListDataForIteration.getCredits());
//                    jsonObjectForIteration.addProperty("assignment_status_grades", assApproveListDataForIteration.getGrades());
//                    jsonObjectForIteration.addProperty("assignment_status_notes", assApproveListDataForIteration.getNotes());
//                    jsonObjectForIteration.addProperty("assignment_status_status", assApproveListDataForIteration.getStatus());
//                    jsonObjectForIteration.addProperty("assignment_status_Student_id", assApproveListDataForIteration.getStudentId());
//                    jsonObjectForIteration.addProperty("assignment_submitted_by", userDetails.get(SessionManager.KEY_TEACHER_REG_ID));
//
//                    jsonArrayForStatusUpdation.add(jsonObjectForIteration);
//                }
                System.out.println("getStudentAfterApprovedAssignmentData--Size -------" + getStudentAfterApprovedAssignmentData.size());
                for (Map.Entry<String, AssApproveListData> entry : getStudentAfterApprovedAssignmentData.entrySet()) {

                    assApproveListDataForIteration = entry.getValue();
                    jsonObjectForIteration = new JsonObject();
                    jsonObjectForIteration.addProperty("assignment_status_id", assApproveListDataForIteration.getAssmntStatusTableId());
                    jsonObjectForIteration.addProperty("assignment_status_credits", assApproveListDataForIteration.getCredits());
                    jsonObjectForIteration.addProperty("assignment_status_grades", assApproveListDataForIteration.getGrades());
                    jsonObjectForIteration.addProperty("assignment_status_notes", assApproveListDataForIteration.getNotes());
                    jsonObjectForIteration.addProperty("assignment_status_status", assApproveListDataForIteration.getStatus());
                    jsonObjectForIteration.addProperty("assignment_status_Student_id", assApproveListDataForIteration.getStudentId());
                    jsonObjectForIteration.addProperty("assignment_submitted_by", userDetails.get(SessionManager.KEY_TEACHER_REG_ID));

                    jsonArrayForStatusUpdation.add(jsonObjectForIteration);
                }


                System.out.println("getStudentAfterApprovedAssignmentData------" + getStudentAfterApprovedAssignmentData);
                System.out.println("jsonArrayForStatusUpdation------" + jsonArrayForStatusUpdation);
                approveAssmnt();
            }


        });
        studentNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemPosition, long l) {
                selectedAssApproveData = studentnameList.get(itemPosition);
                showApprovalDialog(itemPosition, selectedAssApproveData);
            }
        });
    }

    private void approveAssmnt() {

        System.out.println("APPROVE  SATARTED");
        assApproveProgressDialog = new ProgressDialog(AssigmentApproval.this);
        assApproveProgressDialog.setMessage(AssigmentApproval.this.getString(R.string.loading));
        assApproveProgressDialog.setCancelable(false);
        assApproveProgressDialog.show();


        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("assignmentApprove", jsonArrayForStatusUpdation);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println("Assignment Approval exception    " + exception);
                // code to dismiss or retry
                new android.support.v7.app.AlertDialog.Builder(AssigmentApproval.this)
                        .setTitle(R.string.check_network)
                        .setCancelable(false)
                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        }).setNegativeButton("Re-Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        approveAssmnt();
                    }
                })
                        .create().show();
            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" Assignment approved   response    " + response);
                assApproveProgressDialog.dismiss();
                finish();
            }
        });
    }


    private void FetchStudentList() {
        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("getStudent_List_For_Assignment_Approve", jsonObjectParametersAssigmentApproval);
        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElementResponse) {
                resultFuture.set(jsonElementResponse);
                System.out.println(" Fetech student list api   response    " + jsonElementResponse);
                jsonArray = jsonElementResponse.getAsJsonArray();
                for (int i = 0; i <= jsonArray.size() - 1; i++) {
                    JsonObject jsonObjectForIteration = jsonArray.get(i).getAsJsonObject();
                    Log.d("jsonObjectForIteration0", "" + jsonObjectForIteration);

                    //fetch data from (assmnt status-Student) JSON
                    studentFirstName = jsonObjectForIteration.get("firstName").toString().replace("\"", "");
                    studentLastName = jsonObjectForIteration.get("lastName").toString().replace("\"", "");
                    studentAssignmentStatus = jsonObjectForIteration.get("assignment_status").toString().replace("\"", "");
                    studentMiddleName = jsonObjectForIteration.get("middleName").toString().replace("\"", "");
                    studentRollNo = jsonObjectForIteration.get("rollNo").toString().replace("\"", "");
                    assmntStatusTableId = jsonObjectForIteration.get("id").toString().replace("\"", "");
                    assmntId = jsonObjectForIteration.get("Assignment_id").toString().replace("\"", "");
                    studentId = jsonObjectForIteration.get("Student_id").toString().replace("\"", "");
                    credits = jsonObjectForIteration.get("credits").toString().replace("\"", "");
                    grades = jsonObjectForIteration.get("grades").toString().replace("\"", "");
                    notes = jsonObjectForIteration.get("notes").toString().replace("\"", "");
//                    updatedAt = jsonObjectForIteration.get("updatedAt").toString().replace("\"", "");


                    AssApproveListData assApproveData = new AssApproveListData(studentFirstName, studentLastName, studentAssignmentStatus, studentRollNo, studentMiddleName, assmntStatusTableId, assmntId, studentId, credits, grades, notes);
                    studentnameList.add(assApproveData);

                }
                AssApproveAdapterAdapter adapter = new AssApproveAdapterAdapter(getApplicationContext(), studentnameList);
                studentNameListView.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Throwable throwable) {
                resultFuture.setException(throwable);
                System.out.println("exception    " + throwable);
            }
        });

    }

    private void showApprovalDialog(final int itemPosition, final AssApproveListData selectedAssApproveData) {

        final AssApproveAdapterAdapter adapterRefresh = new AssApproveAdapterAdapter(getApplicationContext(), studentnameList);

        final String approvalDialogAssmntStatus = selectedAssApproveData.getStatus();

        LayoutInflater flater = this.getLayoutInflater();
        final View view = flater.inflate(R.layout.assmnt_approve_alert_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(selectedAssApproveData.getfName() + " " + selectedAssApproveData.getmName() + " " + selectedAssApproveData.getlName());

        ArrayAdapter<String> creditsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, approvalCreditsSppinerList);
        assmntApprovalScoresSpinner = (Spinner) view.findViewById(R.id.ass_credits_awardes_spinner);
        dialogNotesTextView = (EditText) view.findViewById(R.id.ass_notes_textView);
        dialogApprovedScoreTextView = (TextView) view.findViewById(R.id.approvedScoresTextView);

        creditsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        switch (approvalDialogAssmntStatus) {

            case "Pending":
                assmntApprovalScoresSpinner.setVisibility(View.VISIBLE);
                assmntApprovalScoresSpinner.setAdapter(creditsAdapter);
                assmntApprovalScoresSpinner.setSelection(approvalCreditsSppinerList.size() - 1);
                dialogApprovedScoreTextView.setVisibility(View.INVISIBLE);
                if (scoreType.equals("Grades")) {
                    dialogApprovedScoreTextView.setText(selectedAssApproveData.getGrades());
                } else {
                    dialogApprovedScoreTextView.setText(selectedAssApproveData.getCredits());
                }

                builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //logic to maintain position even after list is changed
                        //it takes few indexes top etc and sets the value for setSelection(index,top)

                        int index = studentNameListView.getFirstVisiblePosition();
                        View v = studentNameListView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        System.out.println("assmntApprovalScoresSpinner :" + assmntApprovalScoresSpinner.getSelectedItem().toString());
                        System.out.println("dialogApprovedScoreTextView :" + dialogApprovedScoreTextView.getText().toString());
                        System.out.println("dialogNotesTextView :" + dialogNotesTextView.getText().toString());


                        if (scoreType.equals("Grades")) {

                            approvedMarks = "--";
                            approvedGrades = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            System.out.println("approvedGrades : " + approvedGrades);
                            if (approvedGrades.equals("[ Select grades ]")) {
                                System.out.println("approvedGrades IN : " + approvedGrades);
                                showErrorMessage("Grades");

                            } else {
                                System.out.println("pending grades OK");
                                updateApprovalList(itemPosition);
                            }
                        } else {

                            approvedGrades = "--";
                            approvedMarks = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            if (approvedMarks.equals("[ Select marks ]")) {
                                showErrorMessage("Marks");
                            } else {
                                System.out.println("pending marks OK");
                                updateApprovalList(itemPosition);
                            }
                        }

                        studentNameListView.setAdapter(adapterRefresh);
                        studentNameListView.setSelectionFromTop(index, top);

                    }
                });

                builder.setNeutralButton("Re-Submit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //logic to maintain position even after list is changed
                        //it takes few indexes top etc and sets the value for setSelection(index,top)
                        int index = studentNameListView.getFirstVisiblePosition();
                        View v = studentNameListView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        AssApproveListData assApproveData = new AssApproveListData(selectedAssApproveData.getfName(),
                                selectedAssApproveData.getlName(), "Re-Submit", selectedAssApproveData.getRollNo(),
                                selectedAssApproveData.getmName(), selectedAssApproveData.getAssmntStatusTableId(),
                                selectedAssApproveData.getAssmntId(), selectedAssApproveData.getStudentId(),
                                selectedAssApproveData.getCredits(), selectedAssApproveData.getGrades(),
                                dialogNotesTextView.getText().toString());

                        studentnameList.set(itemPosition, assApproveData);
                        getStudentAfterApprovedAssignmentData.put(selectedAssApproveData.getStudentId(), assApproveData); // UPADTE STUDENT APPROVED MAP
                        studentNameListView.setAdapter(adapterRefresh);
                        studentNameListView.setSelectionFromTop(index, top);

                    }
                });

                break;

            case "Re-Submit":
                assmntApprovalScoresSpinner.setVisibility(View.VISIBLE);
                assmntApprovalScoresSpinner.setAdapter(creditsAdapter);
                assmntApprovalScoresSpinner.setSelection(approvalCreditsSppinerList.size() - 1);
                dialogNotesTextView.setText(selectedAssApproveData.getNotes());
                dialogApprovedScoreTextView.setVisibility(View.INVISIBLE);

                builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int index = studentNameListView.getFirstVisiblePosition();
                        View v = studentNameListView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        System.out.println("assmntApprovalScoresSpinner : " + assmntApprovalScoresSpinner.getSelectedItem().toString());
                        System.out.println("dialogApprovedScoreTextView :" + dialogApprovedScoreTextView.getText().toString());
                        System.out.println("dialogNotesTextView :" + dialogNotesTextView.getText().toString());


                        if (scoreType.equals("Grades")) {

                            approvedMarks = "--";
                            approvedGrades = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            if (approvedGrades.equals("[ Select grades ]")) {
                                showErrorMessage("Grades");
                            } else {
                                System.out.println("Re-Submit grades OK");
                                updateApprovalList(itemPosition);
                            }
                        } else {

                            approvedGrades = "--";
                            approvedMarks = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            if (approvedMarks.equals("[ Select marks ]")) {
                                showErrorMessage("Marks");
                            } else {
                                System.out.println("Re-Submit marks OK");
                                updateApprovalList(itemPosition);
                            }
                        }

                        studentNameListView.setAdapter(adapterRefresh);
                        studentNameListView.setSelectionFromTop(index, top);

                    }
                });

                builder.setNeutralButton("Re-Submit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //it takes few indexes top etc and sets the value for setSelection(index,top)
                        int index = studentNameListView.getFirstVisiblePosition();
                        View v = studentNameListView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        AssApproveListData assApproveData = new AssApproveListData(selectedAssApproveData.getfName(),
                                selectedAssApproveData.getlName(), "Re-Submit", selectedAssApproveData.getRollNo(),
                                selectedAssApproveData.getmName(), selectedAssApproveData.getAssmntStatusTableId(),
                                selectedAssApproveData.getAssmntId(), selectedAssApproveData.getStudentId(),
                                selectedAssApproveData.getCredits(), selectedAssApproveData.getGrades(),
                                dialogNotesTextView.getText().toString());

                        studentnameList.set(itemPosition, assApproveData);
                        getStudentAfterApprovedAssignmentData.put(selectedAssApproveData.getStudentId(), assApproveData);
                        studentNameListView.setAdapter(adapterRefresh);
                        studentNameListView.setSelectionFromTop(index, top);

                    }
                });

                break;

            case "Approved":
                assmntApprovalScoresSpinner.setVisibility(View.VISIBLE);
                assmntApprovalScoresSpinner.setAdapter(creditsAdapter);
                dialogApprovedScoreTextView.setVisibility(View.INVISIBLE);
                dialogNotesTextView.setText(selectedAssApproveData.getNotes());
                if (scoreType.equals("Grades")) {
                    dialogApprovedScoreTextView.setText(selectedAssApproveData.getGrades());
                    assmntApprovalScoresSpinner.setSelection(approvalCreditsSppinerList.indexOf(selectedAssApproveData.getGrades()));

                } else {
                    dialogApprovedScoreTextView.setText(selectedAssApproveData.getCredits());
                    assmntApprovalScoresSpinner.setSelection(approvalCreditsSppinerList.indexOf(selectedAssApproveData.getCredits()));

                }

                builder.setPositiveButton("Approved", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setNeutralButton("Update", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //logic to maintain position even after list is changed
                        //it takes few indexes top etc and sets the value for setSelection(index,top)

                        int index = studentNameListView.getFirstVisiblePosition();
                        View v = studentNameListView.getChildAt(0);
                        int top = (v == null) ? 0 : v.getTop();

                        System.out.println("assmntApprovalScoresSpinner : " + assmntApprovalScoresSpinner.getSelectedItem().toString());
                        System.out.println("dialogApprovedScoreTextView :" + dialogApprovedScoreTextView.getText().toString());
                        System.out.println("dialogNotesTextView :" + dialogNotesTextView.getText().toString());

                        if (scoreType.equals("Grades")) {
                            approvedMarks = "--";
                            approvedGrades = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            if (approvedGrades.equals("[ Select grades ]")) {
                                showErrorMessage("Grades");

//                                Toast.makeText(AssigmentApproval.this, "Select grades", Toast.LENGTH_SHORT).show();
                            } else {
                                System.out.println("Approved grades OK");
                                updateApprovalList(itemPosition);
                            }
                        } else {
                            approvedGrades = "--";
                            approvedMarks = assmntApprovalScoresSpinner.getSelectedItem().toString();
                            if (approvedMarks.equals("[ Select marks ]")) {

                                showErrorMessage("Marks");

                            } else {
                                System.out.println("Approved marks OK");
                                updateApprovalList(itemPosition);
                            }
                        }

                        studentNameListView.setAdapter(adapterRefresh);
                        studentNameListView.setSelectionFromTop(index, top);

                    }
                });
                break;
        }

        if (approvalDialogAssmntStatus.equals("Approved")) {

            //This condition is only for update approval function
            //these lines for getting dialog btns and then enable/disable their click
            //BCOZ builder.create().show(); using directly does not provide   dialog.getButton() method
            // hence write the full form by converting builder.create().show(); --to-->  AlertDialog dialog = builder.create();
            // dialog.show();
            //dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);


            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        } else {
            //this line for normal dialog function
            builder.create().show();
        }
    }

    private void showErrorMessage(String type) {
        Snackbar.make(assmntApprovalSnackView, "Select " + type, Snackbar.LENGTH_SHORT).show();
    }

    private void updateApprovalList(int position) {

        AssApproveListData assApproveData = new AssApproveListData(selectedAssApproveData.getfName(), selectedAssApproveData.getlName(), "Approved",
                selectedAssApproveData.getRollNo(), selectedAssApproveData.getmName(), selectedAssApproveData.getAssmntStatusTableId(),
                selectedAssApproveData.getAssmntId(), selectedAssApproveData.getStudentId(),
                approvedMarks, approvedGrades, dialogNotesTextView.getText().toString());

        getStudentAfterApprovedAssignmentData.put(selectedAssApproveData.getStudentId(), assApproveData);

        studentnameList.set(position, assApproveData);
    }


}
