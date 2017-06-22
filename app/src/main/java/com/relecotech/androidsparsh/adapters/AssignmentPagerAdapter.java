package com.relecotech.androidsparsh.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by amey on 8/20/2016.
 */
public class AssignmentPagerAdapter extends FragmentStatePagerAdapter {
//    int mTabNo;

    private ArrayList<Fragment> mfragmentsList;
    private ArrayList<String> mfragmentsNameList;

    public AssignmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentsList, ArrayList<String> fragmentsNameList) {
        super(fm);
        mfragmentsList = fragmentsList;
        mfragmentsNameList = fragmentsNameList;
    }

    @Override
    public Fragment getItem(int position) {

        System.out.println("!!!!!!!! mfragmentsList.get(position) " + mfragmentsList.get(position));
        System.out.println("!!!!!!!! mfragmentsList position " + position);
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
