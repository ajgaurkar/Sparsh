package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.AssignmentAttachmentListData;

import java.util.ArrayList;

/**
 * Created by amey on 1/4/2017.
 */
public class AssignmentAttachmentAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    ArrayList<AssignmentAttachmentListData> assignmentAttachmentList;

    public AssignmentAttachmentAdapter(Context context, ArrayList<AssignmentAttachmentListData> assignmentAttachmentList) {
        this.assignmentAttachmentList = assignmentAttachmentList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return assignmentAttachmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return assignmentAttachmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.assignmentattachment_list_item, parent, false);
            holder.assignmentAttachmentImageView = (ImageView) convertView.findViewById(R.id.assignment_attachment_imageView);
            holder.assignmentAttachmentTextView = (TextView) convertView.findViewById(R.id.assignment_attachment_textView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AssignmentAttachmentListData assignmentAttachmentListData = assignmentAttachmentList.get(position);
        holder.assignmentAttachmentTextView.setText(assignmentAttachmentListData.getAttachementName());

        if (assignmentAttachmentListData.getAttachementName().contains(".jpg") || assignmentAttachmentListData.getAttachementName().contains(".png") || assignmentAttachmentListData.getAttachementName().contains(".jpeg")) {
            holder.assignmentAttachmentImageView.setImageResource(R.drawable.fullimage24);
        } else if (assignmentAttachmentListData.getAttachementName().contains(".pdf")) {
            holder.assignmentAttachmentImageView.setImageResource(R.drawable.pdf24);
        } else if (assignmentAttachmentListData.getAttachementName().contains(".docx")) {
            holder.assignmentAttachmentImageView.setImageResource(R.drawable.document24);
        }
        return convertView;
    }

    private class ViewHolder {

        private TextView assignmentAttachmentTextView;
        private ImageView assignmentAttachmentImageView;

    }
}
