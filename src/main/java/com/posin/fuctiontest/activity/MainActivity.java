package com.posin.fuctiontest.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.posin.fuctiontest.R;
import com.posin.fuctiontest.adapter.MyFragmentAdapter;
import com.posin.fuctiontest.fragment.FragmentCard;
import com.posin.fuctiontest.fragment.FragmentFunction;
import com.posin.fuctiontest.fragment.FragmentSerial;
import com.posin.fuctiontest.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";


    private ViewPager mViewPager;
    private LinearLayout ll_function;
    private LinearLayout ll_serial;
    private LinearLayout ll_card;
    private ImageView iv_function;
    private ImageView iv_serial;
    private ImageView iv_card;

    private LayoutInflater layoutInflater;


    private List<Fragment> list = new ArrayList<>();

    //Fragment列表
    private Class fragmentArray[] = {FragmentFunction.class, FragmentSerial.class,
            FragmentCard.class};
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.selector_tab_function,
            R.drawable.selector_tab_serial, R.drawable.selector_tab_card};
    //Tab选项卡的文字
    private String mTextviewArray[] = {"功能测试", "串口测试", "IC卡测试"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //设置隐藏虚拟按键
        AppUtil.hideBottomUIMenu(MainActivity.this);


        //监听软键盘状态，隐藏键盘是，也隐藏底部按钮
        listenerInput();


        initView();
        initEvent();
        initPage();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        AppUtil.hideBottomUIMenu(MainActivity.this);
        Log.e(TAG, "onRestart");

    }

    /**
     * 监听软键盘状态
     */
    private void listenerInput() {
        final LinearLayout ll_main = (LinearLayout) findViewById(R.id.activity_main);

        ll_main.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        ll_main.getWindowVisibleDisplayFrame(rect);
                        int rootInvisibleHeight = ll_main.getRootView().getHeight() - rect.bottom;
                        Log.d(TAG, "lin.getRootView().getHeight()=" + ll_main.getRootView().getHeight() +
                                ",rect.bottom=" + rect.bottom + ",rootInvisibleHeight=" + rootInvisibleHeight);
                        if (rootInvisibleHeight <= 100) {
                            //软键盘隐藏啦
//                            Toast.makeText(MainActivity.this, "软键盘隐藏啦。。。。。。。",
//                                    Toast.LENGTH_SHORT).show();
                        } else {
                            ////软键盘弹出啦
//                    Toast.makeText(MainActivity.this, "软键盘弹出啦。。。。。。。",
//                            Toast.LENGTH_SHORT).show();
                            AppUtil.hideBottomUIMenu(MainActivity.this);
                        }

                    }
                });

    }

    /**
     * 初始化页面
     */
    private void initView() {

        mViewPager = (ViewPager) findViewById(R.id.vp_pager);
        ll_function = (LinearLayout) findViewById(R.id.ll_function);
        ll_serial = (LinearLayout) findViewById(R.id.ll_serial);
        ll_card = (LinearLayout) findViewById(R.id.ll_card);
        iv_function = (ImageView) findViewById(R.id.iv_function);
        iv_serial = (ImageView) findViewById(R.id.iv_serial);
        iv_card = (ImageView) findViewById(R.id.iv_card);


        iv_function.setImageDrawable(getResources().getDrawable(R.mipmap.function_selected));
        iv_serial.setImageDrawable(getResources().getDrawable(R.mipmap.serial_normal));
        iv_card.setImageDrawable(getResources().getDrawable(R.mipmap.card_normal));

    }


    private void initEvent() {
        ll_function.setOnClickListener(this);
        ll_serial.setOnClickListener(this);
        ll_card.setOnClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initPage() {

        List<Fragment> list = new ArrayList<>();
        list.add(0, new FragmentFunction());
        list.add(1, new FragmentSerial());
        list.add(2, new FragmentCard());
//        new MyFragmentAdapter(getFragmentManager(), list);

//        mViewPager.setAdapter();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_function:
                iv_function.setImageDrawable(getResources().getDrawable(R.mipmap.function_selected));
                iv_serial.setImageDrawable(getResources().getDrawable(R.mipmap.serial_normal));
                iv_card.setImageDrawable(getResources().getDrawable(R.mipmap.card_normal));


                break;
            case R.id.ll_serial:
                iv_function.setImageDrawable(getResources().getDrawable(R.mipmap.function_normal));
                iv_serial.setImageDrawable(getResources().getDrawable(R.mipmap.serial_selected));
                iv_card.setImageDrawable(getResources().getDrawable(R.mipmap.card_normal));


                break;
            case R.id.ll_card:
                iv_function.setImageDrawable(getResources().getDrawable(R.mipmap.function_normal));
                iv_serial.setImageDrawable(getResources().getDrawable(R.mipmap.serial_normal));
                iv_card.setImageDrawable(getResources().getDrawable(R.mipmap.card_selected));


                break;
        }
    }
}
