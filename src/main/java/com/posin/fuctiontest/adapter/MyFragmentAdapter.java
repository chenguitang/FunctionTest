package com.posin.fuctiontest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Greetty on 2017/8/8.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {


    private List<Fragment> mList;

    public MyFragmentAdapter(FragmentManager fm , List<Fragment> list) {
        super(fm);
        this.mList=list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
