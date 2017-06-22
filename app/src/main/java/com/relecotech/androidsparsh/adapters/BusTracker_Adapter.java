package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.BusTrackerListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yogesh on 08-Oct-16.
 */
public class BusTracker_Adapter extends BaseAdapter {
    private List<BusTrackerListData> busTrackerList;
    Context mContext;
    LayoutInflater inflater;
    ArrayList<String> listTime;
    ArrayList<String> listStop;


    // View lookup cache
    private class ViewHolder {
        TextView txtTime;
        TextView txtStop;
        TextView txtBusNo;
        TextView txtRouteNo;
        TextView txtxSchedule;
    }

    public BusTracker_Adapter(Context context, List<BusTrackerListData> busTrackerList) {
        this.busTrackerList = busTrackerList;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
    }


    private int lastPosition = -1;

    @Override
    public int getCount() {
        return busTrackerList.size();
    }

    @Override
    public BusTrackerListData getItem(int position) {

        return busTrackerList.get(position);
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        // BusTrackerListData busTrackerList = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
//            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.bus_listitem, parent, false);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.textTimeVal);
            viewHolder.txtStop = (TextView) convertView.findViewById(R.id.textStopVal);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String time = null, stop = null;
        BusTrackerListData busTrackerListData = busTrackerList.get(position);
//        String schedule = busTrackerListData.getSchedule();
//
//        listTime = new ArrayList<>();
//        listStop = new ArrayList<>();
//        System.out.println("schedule  " + schedule);
//
//        StringTokenizer st = new StringTokenizer(schedule, "$");
//        while (st.hasMoreTokens()) {
//
//            //Got substring here e.g. 01:00 PM%GittiKhadan
//            System.out.println(st.nextToken());
//            String tmpStr = st.nextToken();
//            System.out.println("tmpStr " + tmpStr);
//            //Extract time & location from substring tmpStr
//            time = tmpStr.substring(0, tmpStr.indexOf("%"));
//            stop = tmpStr.substring(tmpStr.indexOf("%") + 1, tmpStr.length());
//
//            System.out.println("time " + time);
//            System.out.println("stop " + stop);
//
//            //Store or set time & location
//                listTime.add(time);
//                listStop.add(stop);
//
//        }

//
//        for(int i = 0; i < listTime.size(); i++) {
//            System.out.println(listTime.get(i)); //prints element i
//            viewHolder.txtTime.setText(listTime.get(i));
//        }

        viewHolder.txtTime.setText(busTrackerListData.getTime());
        viewHolder.txtStop.setText(busTrackerListData.getStop());
//        viewHolder.txtStop.setText(busTrackerListData.getRoute_no());
//        viewHolder.txtStop.setText(busTrackerListData.getSchedule());
//        viewHolder.txtStop.setText(busTrackerListData.getStrech());

        // Return the completed view to render on screen
        return convertView;
    }

}


