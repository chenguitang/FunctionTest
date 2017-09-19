package com.posin.functiontest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;

/**
 * Created by Greetty on 2017/8/31.
 * <p>
 * 显示屏测试
 */

public class TouchTestNumView extends View {

    private Paint mPaint;
    //private MotionEvent mEvent = null;
    private LinkedList<Point> mTouchPos = new LinkedList<Point>();

    public TouchTestNumView(Context context) {
        super(context);
        //创建Paint
        mPaint = new Paint();
        //设置抗锯齿效果
        mPaint.setAntiAlias(true);

        mPaint.setTextSize(30);

        mPaint.setStyle(Paint.Style.FILL);

        setClickable(true);

        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTouchPos.size() < 10000) {
            int c = event.getPointerCount();
            for (int i = 0; i < c; i++) {
                Point p = new Point((int) event.getX(i), (int) event.getY(i));
                for (Point pp : mTouchPos) {
                    if (pp.equals(p))
                        p = null;
                }
                if (p != null)
                    mTouchPos.add(p);
            }
            invalidate();
        }
        //return super.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        String s = "点击测试 : " + mTouchPos.size();
        int sw = this.getWidth();
        int sh = this.getHeight();

        mPaint.setColor(Color.RED);
        //mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawText(s, sw / 2 - 100, sh / 2, mPaint);

        mPaint.setColor(Color.BLUE);
        for (Point p : mTouchPos) {
            canvas.drawCircle(p.x, p.y, 10, mPaint);
        }
    }
}
