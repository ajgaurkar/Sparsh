package com.relecotech.androidsparsh.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.relecotech.androidsparsh.fragments.Calendar_Attendance_Fragment;
import com.relecotech.androidsparsh.fragments.Calendar_Events_Fragment;
import com.relecotech.androidsparsh.fragments.Calendar_Exam_Fragment;
import com.relecotech.androidsparsh.fragments.Calendar_Holidays_Fragment;

/**
 * Created by amey on 4/5/2016.
 */
public class CalenderPagerAdapter extends FragmentStatePagerAdapter {
    int mTabNo;

    public CalenderPagerAdapter(FragmentManager fm, int TabNo) {
        super(fm);
        mTabNo = TabNo;
    }

    @Override
    public Fragment getItem(int position) {

        // Fragment frag = null;
        switch (position) {
            case 0:
                return new Calendar_Events_Fragment();
            case 1:
                return new Calendar_Holidays_Fragment();
            case 2:
                return new Calendar_Exam_Fragment();
            case 3:
                return new Calendar_Attendance_Fragment();
            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return mTabNo;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = " ";
        switch (position) {
            case 0:
                title = "Events";
                break;
            case 1:
                title = "Holidays";
                break;
            case 2:
                title = "Exam";
                break;
            case 3:
                title = "Attendace";
                break;
        }

        return title;
    }
}
