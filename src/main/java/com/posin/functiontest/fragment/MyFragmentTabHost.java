package com.posin.functiontest.fragment;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Greetty on 2017/8/10.
 */

public class MyFragmentTabHost extends FragmentTabHost {

    private static final String TAG = "MyFragmentTabHost";

    public MyFragmentTabHost(Context context) {
        super(context);
    }

    public MyFragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onTouchModeChanged(boolean isInTouchMode) {
//        super.onTouchModeChanged(isInTouchMode);
        Log.e(TAG, "isInTouchMode: "+isInTouchMode);

    }
}
