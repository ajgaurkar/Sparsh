package com.relecotech.androidsparsh.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by yogesh on 07-Oct-16.
 */
public class BusTrackerPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mfragmentsList;
    private ArrayList<String> mfragmentsNameList;

    public BusTrackerPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentsList, ArrayList<String> fragmentsNameList) {
        super(fm);
        mfragmentsList = fragmentsList;
        mfragmentsNameList = fragmentsNameList;
    }

    @Override
    public Fragment getItem(int position) {
        // Fragment frag = null;
        return mfragmentsList.get(position);

    }

    @Override
    public int getCount() {
        return mfragmentsList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = " ";
        for (int titleloop = 0; titleloop < mfragmentsNameList.size(); titleloop++) {
            title = mfragmentsNameList.get(position);
        }
        return title;
    }
}

