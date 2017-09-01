package com.posin.fuctiontest.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.posin.fuctiontest.R;
import com.posin.fuctiontest.view.TouchTestNumView;
import com.posin.fuctiontest.view.TouchTestView;

import java.lang.reflect.Method;

public class FullScreenActivity extends AppCompatActivity {

    private static final int SYSTEM_UI_FLAG_IMMERSIVE = 0x00000800;
    private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;

    private static final int SYSTEM_UI_FLAG_HIDE_NAVIGATION =
            // 1 | 2 | 4 | 0x200 | 0x400 | 0x00000100;
            0x00000100
                    | 0x00000200
                    | 0x00000400
                    | 0x00000002 // hide nav bar
                    | 0x00000004 // hide status bar
                    | 0x00001000;

    private void hidSysNavigation() {
        if (Build.VERSION.SDK_INT > 10) {
            try {
                View v = getWindow().getDecorView();
                Method m = View.class.getMethod("setSystemUiVisibility", int.class);
                m.invoke(v, new Object[]{SYSTEM_UI_FLAG_HIDE_NAVIGATION});
                //v.setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_full_screen);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hidSysNavigation();

        initView();
    }

    private void initView() {
        View v = null;
        String func = this.getIntent().getStringExtra("function");
        if ("touch_test".equals(func)) {
            v = new TouchTestView(this,FullScreenActivity.this);

        } else if ("touch_test_num".equals(func)) {
            v = new TouchTestNumView(this);
        }

//		v = new PageTouchTest.TouchTestView(this);

        if (v != null) {
            v.setVisibility(View.VISIBLE);
            setContentView(v);
        } else {
            setContentView(R.layout.activity_full_screen);
        }
        v.requestFocus();
    }

    public void finishActivity(){
        FullScreenActivity.this.finish();
    }
}
