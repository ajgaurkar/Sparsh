package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.CalendarDialogListData;

import java.util.List;

/**
 * Created by ajinkya on 4/20/2016.
 */
public class CalendarDialogAdapter extends BaseAdapter {


    Context context;
    List<CalendarDialogListData> entryList;
    LayoutInflater inflater;

    public CalendarDialogAdapter(Context context, List<CalendarDialogListData> entryList) {
        this.context = context;
        this.entryList = entryList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return entryList.size();
    }

    @Override
    public CalendarDialogListData getItem(int position) {

        return entryList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {

            holder = new ViewHolder();
            view = this.inflater.inflate(R.layout.calendar_dialog_list_item, viewGroup, false);

            holder.calEntryTextView = (TextView) view.findViewById(R.id.calendarEntryDialogTextView);
            holder.calTypeView = (View) view.findViewById(R.id.calendarEntryDialogTypeView);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        CalendarDialogListData calendarDialogListData = entryList.get(i);

        holder.calEntryTextView.setText(calendarDialogListData.getCalendarEntry());
        switch (calendarDialogListData.getEntryType()) {
            case "Event":
                holder.calTypeView.setBackgroundResource(R.drawable.blue_circle_bg);
                break;
            case "Attendance":
                holder.calTypeView.setBackgroundResource(R.drawable.red_circe_bg);
                break;
            case "Exam":
                holder.calTypeView.setBackgroundResource(R.drawable.sky_blue_circle_bg);
                break;
            case "Holiday":
                holder.calTypeView.setBackgroundResource(R.drawable.blue_circle_bg);
                break;
        }

        return view;
    }

    private class ViewHolder {
        private TextView calEntryTextView;
        private View calTypeView;
    }
}
