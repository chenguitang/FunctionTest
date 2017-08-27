package com.posin.fuctiontest.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.posin.fuctiontest.R;
import com.posin.fuctiontest.fragment.FragmentFactory;

import java.util.List;

/**
 * Created by Greetty on 2017/8/22.
 */

public class TabLayoutAdapter extends FragmentPagerAdapter {

    private String[] mTitle;
    private Context mContext;


    public TabLayoutAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
        mTitle = context.getResources().getStringArray(R.array.tab_title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentFactory.createFragment(position);
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }
}
