package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.CalendarFragmentListData;

import java.util.List;

/**
 * Created by ajinkya on 4/8/2016.
 */
public class CalendarEntryListAdapter extends BaseAdapter {

    protected List<CalendarFragmentListData> calendarDataList;
    Context context;
    LayoutInflater inflater;

    public CalendarEntryListAdapter(Context context, List<CalendarFragmentListData> calendarEventList) {
        this.calendarDataList = calendarEventList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return calendarDataList.size();
    }

    @Override
    public CalendarFragmentListData getItem(int position) {
        return calendarDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {

            holder = new ViewHolder();
            view = this.inflater.inflate(R.layout.calendar_list_item, viewGroup, false);

            holder.dateTextView = (TextView) view.findViewById(R.id.calendarDateTextView);
            holder.titleTextView = (TextView) view.findViewById(R.id.calendarTitleTextView);
            holder.timeTextView = (TextView) view.findViewById(R.id.calendarTimeTextView);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        CalendarFragmentListData calendarFragmentListData = calendarDataList.get(i);

        try {
            holder.dateTextView.setText(calendarFragmentListData.getCalendarDate());
            holder.titleTextView.setText(calendarFragmentListData.getCalendarTitle());
            String date = calendarFragmentListData.getCalendarTime();
            System.out.println(" date in CalendarEntryListAdapter" + date);
            if (date.contains("00:00")) {
                holder.timeTextView.setText("");
            } else {
                holder.timeTextView.setText(calendarFragmentListData.getCalendarTime());
            }
        }catch (Exception e){
            System.out.println("Exception " + e.getMessage());
        }
//        holder.timeTextView.setText(calendarFragmentListData.getCalendarTime());

        return view;
    }

    private class ViewHolder {
        private TextView dateTextView;
        private TextView titleTextView;
        private TextView timeTextView;
    }
}
