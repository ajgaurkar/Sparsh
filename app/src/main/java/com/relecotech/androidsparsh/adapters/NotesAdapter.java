package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.NotesListData;
import com.relecotech.androidsparsh.fragments.Parent_control;

import java.util.List;

/**
 * Created by amey on 10/16/2015.
 */
public class NotesAdapter extends BaseAdapter {

    String user = Parent_control.loggedInUserForParentControl;

    Context context;
    List<NotesListData> notesList;
    LayoutInflater inflater;

    public NotesAdapter(Context context, List<NotesListData> notesList) {
        this.context = context;
        this.notesList = notesList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notesList.size();
    }

    @Override
    public NotesListData getItem(int position) {

        return notesList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d("login_user_role1", user);
        ViewHolder holder;

        if (view == null) {

            holder = new ViewHolder();
            view = this.inflater.inflate(R.layout.notes_list_item, viewGroup, false);

            holder.concernedPersontextView = (TextView) view.findViewById(R.id.concernedPersonTextView);
            holder.noteTextView = (TextView) view.findViewById(R.id.notesNoteTextView);
            holder.tagtextView = (TextView) view.findViewById(R.id.notesTagTextView);
            holder.postDateTimetextView = (TextView) view.findViewById(R.id.notesPostDateTextView);
            holder.statustextView = (TextView) view.findViewById(R.id.notesStatusTextView);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        NotesListData notesListData = notesList.get(i);

        if (user.equals("Teacher")) {
            holder.concernedPersontextView.setText(notesListData.getConcernedStudent());
        }
        if (user.equals("Student")) {
            holder.concernedPersontextView.setText(notesListData.getConcernedTeacher());
        }
        holder.tagtextView.setText(notesListData.getNoteCategory());

        holder.noteTextView.setText(notesListData.getDescription().replace("\\n","\n"));
        holder.postDateTimetextView.setText(notesListData.getPostDatetime());
        holder.statustextView.setText(notesListData.getNotesStatus());
        if (notesListData.getNotesStatus().equals("Replied")) {
            holder.statustextView.setTextColor(Color.parseColor("#004b96"));
        }
        if (notesListData.getNotesStatus().equals("Approved")) {
            holder.statustextView.setTextColor(Color.parseColor("#009688"));
        }
        if (notesListData.getNotesStatus().equals("Denied")) {
            holder.statustextView.setTextColor(Color.RED);
        }
        if (notesListData.getNotesStatus().equals("Pending")) {
            holder.statustextView.setTextColor(Color.parseColor("#8E8E8E"));
        }

        return view;
    }

    private class ViewHolder {
        private TextView concernedPersontextView;
        private TextView noteTextView;
        private TextView tagtextView;
        private TextView postDateTimetextView;
        private TextView statustextView;
    }
}
