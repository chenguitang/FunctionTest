package com.posin.fuctiontest.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.posin.fuctiontest.R;
import com.posin.fuctiontest.adapter.TabLayoutAdapter;
import com.posin.fuctiontest.fragment.FragmentCard;
import com.posin.fuctiontest.fragment.FragmentCashDrawer;
import com.posin.fuctiontest.fragment.FragmentCustomerDis;
import com.posin.fuctiontest.fragment.FragmentFunction;
import com.posin.fuctiontest.fragment.FragmentCardReader;
import com.posin.fuctiontest.fragment.FragmentPrinter;
import com.posin.fuctiontest.fragment.FragmentSerial;
import com.posin.fuctiontest.util.AppUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.tab)
    TabLayout mTab;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tv_select_fr)
    TextView tv_select_fr;   //选中测试类
    @BindView(R.id.tv_exit)
    TextView tv_exit;  //退出


    //Fragment列表
    private Class fragmentArray[] = {FragmentFunction.class, FragmentSerial.class,
            FragmentCard.class};
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.selector_tab_function,
            R.drawable.selector_tab_serial, R.drawable.selector_tab_card};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        tv_select_fr.setText("打印机测试");
        //设置隐藏虚拟按键
        AppUtil.hideBottomUIMenu(MainActivity.this);
        //监听软键盘状态，隐藏键盘是，也隐藏底部按钮
        listenerInput();

        initEvent();
        initData();

    }

    private void initEvent() {
        tv_exit.setOnClickListener(this);
        mTab.addOnTabSelectedListener(this);
    }

    private void initData() {
        TabLayoutAdapter tabAdapter = new TabLayoutAdapter(this,
                getSupportFragmentManager());
        mViewPager.setAdapter(tabAdapter);
        mTab.setupWithViewPager(mViewPager);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_exit:
                MainActivity.this.finish();
                Toast.makeText(this, "退出应用", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
//        Toast.makeText(this, "选中的位置是： "+tab.getPosition(), Toast.LENGTH_SHORT).show();
        String[] tab_title = getResources().getStringArray(R.array.tab_title);
        tv_select_fr.setText(tab_title[tab.getPosition()]+"测试");
        Log.e(TAG, "选中的位置是： " + tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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

}
