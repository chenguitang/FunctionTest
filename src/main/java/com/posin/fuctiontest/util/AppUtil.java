package com.posin.fuctiontest.util;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Greetty on 2017/8/8.
 */

public class AppUtil {


    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideBottomUIMenu(AppCompatActivity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
