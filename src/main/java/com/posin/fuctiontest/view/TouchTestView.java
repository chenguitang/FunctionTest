package com.posin.fuctiontest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Greetty on 2017/8/31.
 * <p>
 * 触摸测试页面
 */

public class TouchTestView extends View {

    private static final String TAG = "TouchTestView";

    private Paint mPaint;

    private Bitmap mBackground = null;
    private Bitmap mBmp = null;
    private Canvas mCanvas = null;
    private Canvas mCanvasBack = null;

    public TouchTestView(Context context) {
        super(context);

        mPaint = new Paint();

        //mPaint.setAntiAlias(true);

        mPaint.setTextSize(30);

        mPaint.setStyle(Paint.Style.FILL);

        this.setClickable(true);
    }

    private static final int BLOCK_BASE = (1024/21);
    //private static final int BLOCK_BASE_Y = (768/48;

    private int BLOCK_SIZE_X = BLOCK_BASE;
    private int BLOCK_SIZE_Y = BLOCK_BASE;

    public void createBitmap() {
        int sw = this.getWidth();
        int sh = this.getHeight();

        //BLOCK_SIZE_X = sw / 24;
        //BLOCK_SIZE_Y = sh / 24;

        Log.d(TAG, "create bitmap : "+sw+"x"+sh);

        if(sw >= 1920 || sh >= 1080) {
            BLOCK_SIZE_X = (int) (BLOCK_BASE*1.5f);
            BLOCK_SIZE_Y = (int) (BLOCK_BASE*1.5f);
        }

        mBmp = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBmp);

        mBackground = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
        mCanvasBack = new Canvas(mBackground);

        mPaint.setColor(Color.TRANSPARENT);
        mCanvas.drawRect(0, 0, sw, sh, mPaint);

        String s = "触控区域测试";
        mPaint.setColor(Color.RED);
        mCanvas.drawText(s, sw/2-100, sh/2, mPaint);

        mPaint.setColor(Color.BLACK);
        for(int y=0; y<sh; y+=BLOCK_SIZE_Y) {
            mCanvasBack.drawLine(0, y, sw, y, mPaint);
        }
        for(int x=0; x<sw; x+=BLOCK_SIZE_X) {
            mCanvasBack.drawLine(x, 0, x, sh, mPaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        createBitmap();
    }

    private static final int MAX_POINTS = 10;
    private Point[] mPrev = new Point[MAX_POINTS];

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mBmp == null)
            return true;

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            int c = event.getPointerCount();
            if(c > 0) {
                for(int i=0; i<c; i++) {
                    int x = (int)event.getX(i);
                    int y = (int)event.getY(i);

                    mPrev[i] = new Point(x, y);

                    mPaint.setColor(Color.BLUE);
                    mCanvas.drawRect(x-2, y-2, x+2, y+2, mPaint);

                    x = (x / BLOCK_SIZE_X) * BLOCK_SIZE_X;
                    y = (y / BLOCK_SIZE_Y) * BLOCK_SIZE_Y;
                    mPaint.setColor(Color.GREEN);
                    mCanvasBack.drawRect(x+1,  y+1, x+BLOCK_SIZE_X-1, y+BLOCK_SIZE_Y-1, mPaint);
                }
                invalidate();
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            int c = event.getPointerCount();
            if(c > 0) {
                Point[] pp = new Point[c];
                for(int i=0; i<c; i++) {
                    pp[i] = new Point((int)event.getX(i), (int)event.getY(i));
                }
                mPaint.setColor(Color.BLUE);
                for(int i=0; i<c; i++) {
                    if(mPrev[i] != null) {
                        mCanvas.drawLine(mPrev[i].x, mPrev[i].y, pp[i].x, pp[i].y, mPaint);
                    }
                }
                for(int i=0; i<mPrev.length; i++) {
                    if(i<c)
                        mPrev[i] = pp[i];
                    else
                        mPrev[i] = null;
                }

                mPaint.setColor(Color.GREEN);

                for(Point p : pp) {
                    int x = (p.x / BLOCK_SIZE_X) * BLOCK_SIZE_X;
                    int y = (p.y / BLOCK_SIZE_Y) * BLOCK_SIZE_Y;
                    mCanvasBack.drawRect(x+1,  y+1, x+BLOCK_SIZE_X-1, y+BLOCK_SIZE_Y-1, mPaint);
                }
                invalidate();
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) {
            for(int i=0; i<mPrev.length; i++) {
                mPrev[i] = null;
            }
        }
        //return super.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBackground, 0, 0, null);
        canvas.drawBitmap(mBmp, 0, 0, null);
    }
}
