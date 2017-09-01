package com.posin.fuctiontest.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.posin.fuctiontest.R;
import com.posin.fuctiontest.activity.FullScreenActivity;
import com.posin.fuctiontest.activity.MainActivity;
import com.posin.fuctiontest.util.AppUtil;

import java.io.InputStream;

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
    private static final int BLOCK_BASE = (1024 / 21);
    //private static final int BLOCK_BASE_Y = (768/48;
    private int BLOCK_SIZE_X = BLOCK_BASE;
    private int BLOCK_SIZE_Y = BLOCK_BASE;

    private int back_position_x = 0;
    private int back_position_y = 0;
    private int back_height = 0;
    private int back_width = 0;
    private Context mContext;
    private FullScreenActivity mFullScreenActivity;

    public TouchTestView(Context context) {
        super(context);

        mPaint = new Paint();

        //mPaint.setAntiAlias(true);

        mPaint.setTextSize(30);

        mPaint.setStyle(Paint.Style.FILL);

        this.mContext=context;

        this.setClickable(true);
    }

    public TouchTestView(Context context, AppCompatActivity activity) {
        super(context);

        mPaint = new Paint();

        //mPaint.setAntiAlias(true);

        mPaint.setTextSize(30);

        mPaint.setStyle(Paint.Style.FILL);

        this.mContext=context;

        this.mFullScreenActivity = (FullScreenActivity) activity;

        this.setClickable(true);
    }

    public void createBitmap() {
        int sw = this.getWidth();
        int sh = this.getHeight();

        //BLOCK_SIZE_X = sw / 24;
        //BLOCK_SIZE_Y = sh / 24;

        Log.d(TAG, "create bitmap : " + sw + "x" + sh);

        if (sw >= 1920 || sh >= 1080) {
            BLOCK_SIZE_X = (int) (BLOCK_BASE * 1.5f);
            BLOCK_SIZE_Y = (int) (BLOCK_BASE * 1.5f);
        }

        mBmp = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBmp);

        mBackground = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
        mCanvasBack = new Canvas(mBackground);

        mPaint.setColor(Color.TRANSPARENT);
        mCanvas.drawRect(0, 0, sw, sh, mPaint);
        String s = AppUtil.isZh(getContext()) ? "触控区域测试" : "Touch Area Test";
        mPaint.setColor(Color.RED);
        mCanvas.drawText(s, sw / 2 - 100, sh / 2, mPaint);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.back_red);
//        mCanvas.drawBitmap(bmp,52,5,mPaint);
        back_height = bmp.getHeight();
        back_width = bmp.getWidth();
        back_position_x = BLOCK_SIZE_X + (BLOCK_SIZE_X - back_width) / 2;
        back_position_y = BLOCK_SIZE_Y + (BLOCK_SIZE_Y - back_height) / 2;

        mCanvas.drawBitmap(bmp, back_position_x, back_position_y, mPaint);

        mPaint.setColor(Color.BLACK);
        for (int y = 0; y < sh; y += BLOCK_SIZE_Y) {
            mCanvasBack.drawLine(0, y, sw, y, mPaint);
        }
        for (int x = 0; x < sw; x += BLOCK_SIZE_X) {
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
        if (mBmp == null)
            return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int c = event.getPointerCount();
            if (c > 0) {
                for (int i = 0; i < c; i++) {
                    int downX = (int) event.getX(i);
                    int downY = (int) event.getY(i);

                    if (downX > BLOCK_SIZE_X && downX < BLOCK_SIZE_X * 2 &&
                            downY > BLOCK_SIZE_Y && downY < BLOCK_SIZE_Y * 2) {
//                        mFullScreenActivity.finish();
                        mFullScreenActivity.finishActivity();
                        Toast.makeText(mFullScreenActivity,
                                AppUtil.isZh(mContext)?"退出触屏测试":"Exit touch screen test",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "退出触屏测试: ");
                    }


                    mPrev[i] = new Point(downX, downY);

                    mPaint.setColor(Color.BLUE);
                    mCanvas.drawRect(downX - 2, downY - 2, downX + 2, downY + 2, mPaint);

                    int x = (downX / BLOCK_SIZE_X) * BLOCK_SIZE_X;
                    int y = (downY / BLOCK_SIZE_Y) * BLOCK_SIZE_Y;
                    mPaint.setColor(Color.GREEN);
                    mCanvasBack.drawRect(x + 1, y + 1, x + BLOCK_SIZE_X - 1, y + BLOCK_SIZE_Y - 1, mPaint);

//                    if (downX > back_position_x && downX < (back_position_x + back_width) &&
//                            downY > back_position_y && downY < (back_position_y + back_height)){
//                        mFullScreenActivity.finish();
//                        Log.e(TAG, "点击返回: ");
//                    }



                }
                invalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int c = event.getPointerCount();
            if (c > 0) {
                Point[] pp = new Point[c];
                for (int i = 0; i < c; i++) {
                    pp[i] = new Point((int) event.getX(i), (int) event.getY(i));
                }
                mPaint.setColor(Color.BLUE);
                for (int i = 0; i < c; i++) {
                    if (mPrev[i] != null) {
                        mCanvas.drawLine(mPrev[i].x, mPrev[i].y, pp[i].x, pp[i].y, mPaint);
                    }
                }
                for (int i = 0; i < mPrev.length; i++) {
                    if (i < c)
                        mPrev[i] = pp[i];
                    else
                        mPrev[i] = null;
                }

                mPaint.setColor(Color.GREEN);

                for (Point p : pp) {
                    int x = (p.x / BLOCK_SIZE_X) * BLOCK_SIZE_X;
                    int y = (p.y / BLOCK_SIZE_Y) * BLOCK_SIZE_Y;
                    mCanvasBack.drawRect(x + 1, y + 1, x + BLOCK_SIZE_X - 1, y + BLOCK_SIZE_Y - 1, mPaint);
                }
                invalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            for (int i = 0; i < mPrev.length; i++) {
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
