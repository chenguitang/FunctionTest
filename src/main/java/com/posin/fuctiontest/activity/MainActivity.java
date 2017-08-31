package com.posin.fuctiontest.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.posin.device.SDK;
import com.posin.fuctiontest.R;
import com.posin.fuctiontest.adapter.TabLayoutAdapter;
import com.posin.fuctiontest.impl.TabReselectedListener;
import com.posin.fuctiontest.util.AppUtil;

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

    private boolean mInited = false;  //是否已初始化
    private TabReselectedListener mTabReselectedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        tv_select_fr.setText("打印机测试");
        //设置隐藏虚拟按键
        AppUtil.hideBottomUIMenu(MainActivity.this);

        initEvent();
        initData();

    }

    private void initEvent() {
        tv_exit.setOnClickListener(this);
        mTab.addOnTabSelectedListener(this);

        //监听软键盘状态，隐藏键盘是，也隐藏底部按钮
        listenerInput();
    }

    private void initData() {
        TabLayoutAdapter tabAdapter = new TabLayoutAdapter(this,
                getSupportFragmentManager());
        mViewPager.setAdapter(tabAdapter);
        mTab.setupWithViewPager(mViewPager);
        

        //初始化SDK
        try {
            if (mInited)
                return;
            SDK.init(this);
            mInited = true;

        } catch (Throwable throwable) {
            Log.e(TAG, "error: " + throwable.getMessage());
            throwable.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_exit:
//                MainActivity.this.finish();
                System.exit(0);
                Toast.makeText(this, "退出应用", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        String[] tab_title = getResources().getStringArray(R.array.tab_title);
        tv_select_fr.setText(tab_title[tab.getPosition()] + "测试");
        if (mTabReselectedListener != null)
            mTabReselectedListener.TabReselectedChange(tab.getPosition());
        Log.d(TAG, "选中的位置是： " + tab.getPosition());
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


    public void setTabReselectedListener(TabReselectedListener tabReselectedListener) {
        this.mTabReselectedListener = tabReselectedListener;
    }

}
