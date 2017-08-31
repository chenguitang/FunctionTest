package com.posin.fuctiontest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by Greetty on 2017/8/31.
 */

public class MySeekBar extends SeekBar {

    private float mDownX;
    private float mDownY;

    public MySeekBar(Context context) {
        super(context);
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveX = event.getX();
                float mMoveY = event.getY();
                if (Math.abs(mMoveX - mDownX) > Math.abs(mMoveY - mDownY)) {
                    // 上下滑动，拦截事件，不给子控件事件，自己需要处理
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }
}
