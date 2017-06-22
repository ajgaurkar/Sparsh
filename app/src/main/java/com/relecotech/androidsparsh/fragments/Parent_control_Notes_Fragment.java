package com.relecotech.androidsparsh.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.activities.ApproveNotes;
import com.relecotech.androidsparsh.adapters.NotesAdapter;
import com.relecotech.androidsparsh.controllers.NotesListData;

import java.util.List;

/**
 * Created by amey on 8/6/2016.
 */
public class Parent_control_Notes_Fragment extends Fragment {
    ListView parentcontrolListView;
    private NotesAdapter notesAdapter;
    private List<NotesListData> getNotesListData;
    private NotesListData selectedNoteslistData;
    private TextView parentcontrolNoDataAvailable_Notes_TextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.parent_control_fragment, container, false);
        parentcontrolListView = (ListView) rootView.findViewById(R.id.parent_control_listView);
        parentcontrolNoDataAvailable_Notes_TextView = (TextView) rootView.findViewById(R.id.no_data_available_prent_control_textView);
        Bundle getbundledata = this.getArguments();
        getNotesListData = (List<NotesListData>) getbundledata.getSerializable("NotesList");

//        System.out.println("getNotesListData - Value--------" + getNotesListData);

        try {

            if (!getNotesListData.isEmpty() || getNotesListData != null) {
                System.out.println("Value---------------------------" + getNotesListData.size());
                System.out.println("Value---------------------------" + getNotesListData);
                notesAdapter = new NotesAdapter(getActivity(), getNotesListData);
                parentcontrolListView.setAdapter(notesAdapter);

            } else {
                System.out.println("Parents Zone Notes List is null");
                parentcontrolNoDataAvailable_Notes_TextView.setVisibility(View.VISIBLE);
                parentcontrolNoDataAvailable_Notes_TextView.setText(R.string.noDataAvailable);
            }
        } catch (Exception e) {
            parentcontrolNoDataAvailable_Notes_TextView.setVisibility(View.VISIBLE);
            parentcontrolNoDataAvailable_Notes_TextView.setText(R.string.noDataAvailable);
            System.out.println("getMessage Notes Fragments----" + e.getMessage());


        }

        parentcontrolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedNoteslistData = getNotesListData.get(position);
                Intent notesApproveIntent = new Intent(getActivity(), ApproveNotes.class);
                Bundle notesBundle = new Bundle();
                notesBundle.putString("Description", selectedNoteslistData.getDescription());
                notesBundle.putString("Category", selectedNoteslistData.getNoteCategory());
                notesBundle.putString("PostTime", selectedNoteslistData.getPostDatetime());
                notesBundle.putString("MeetingSchedule", selectedNoteslistData.getMeetingScheduleDateTime());
                notesBundle.putString("Status", selectedNoteslistData.getNotesStatus());
                notesBundle.putString("Reply", selectedNoteslistData.getReply());
                notesBundle.putString("ConcernedTeacher", selectedNoteslistData.getConcernedTeacher());
                notesBundle.putString("ConcernedStudent", selectedNoteslistData.getConcernedStudent());
                notesBundle.putString("StudentRollNo", selectedNoteslistData.getStudentRollNo());
                notesBundle.putString("StudentId", selectedNoteslistData.getStudentId());
                notesBundle.putString("NotesId", selectedNoteslistData.getNoteId());

                System.out.println("Description" + selectedNoteslistData.getDescription());
                System.out.println("Category" + selectedNoteslistData.getNoteCategory());
                System.out.println("PostTime" + selectedNoteslistData.getPostDatetime());
                System.out.println("MeetingSchedule" + selectedNoteslistData.getMeetingScheduleDateTime());
                System.out.println("Status" + selectedNoteslistData.getNotesStatus());
                System.out.println("Reply" + selectedNoteslistData.getReply());
                System.out.println("ConcernedTeacher" + selectedNoteslistData.getConcernedTeacher());
                System.out.println("ConcernedStudent" + selectedNoteslistData.getConcernedStudent());
                System.out.println("StudentRollNo" + selectedNoteslistData.getStudentRollNo());
                System.out.println("StudentId" + selectedNoteslistData.getStudentId());
                System.out.println("NotesId" + selectedNoteslistData.getNoteId());

                notesApproveIntent.putExtras(notesBundle);
                startActivity(notesApproveIntent);
            }
        });
        return rootView;
    }
}
