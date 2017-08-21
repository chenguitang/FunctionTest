package com.posin.fuctiontest.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.posin.device.SDK;
import com.posin.fuctiontest.R;
import com.posin.fuctiontest.adapter.MyFragmentAdapter;
import com.posin.fuctiontest.fragment.FragmentCard;
import com.posin.fuctiontest.fragment.FragmentFunction;
import com.posin.fuctiontest.fragment.FragmentSerial;
import com.posin.fuctiontest.fragment.MyFragmentTabHost;
import com.posin.fuctiontest.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    private MyFragmentTabHost mTabHost;
    private ViewPager mViewPager;
    private LayoutInflater layoutInflater;
    private Button mBtnExit;
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
        layoutInflater = LayoutInflater.from(this);

        mViewPager = (ViewPager) findViewById(R.id.vp_pager);
        mViewPager.addOnPageChangeListener(this);

        mTabHost = (MyFragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.vp_pager);
        mBtnExit= (Button) findViewById(R.id.btnExit);

        mTabHost.setOnTabChangedListener(this);
        mBtnExit.setOnClickListener(this);

        //得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).
                    setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.setTag(i);
            //设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(
                    R.drawable.selector_tab_background);
        }
    }

    /**
     * 初始化ViewPager
     */
    private void initPage() {
        FragmentFunction fgFunction = new FragmentFunction();
        FragmentSerial fgSerial = new FragmentSerial();
        FragmentCard fgCard = new FragmentCard();

        list.add(fgFunction);
        list.add(fgSerial);
        list.add(fgCard);

        //绑定Fragment适配器
        mViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), list));
        //设置取消tabHost分割线
        //mTabHost.getTabWidget().setDividerDrawable(null);
    }


    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {

        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        int position = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(position);//把选中的Tab的位置赋给适配器，让它控制页面切换

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //表示在前一个页面滑动到后一个页面的时候，在前一个页面滑动前调用的方法
    }

    @Override
    public void onPageSelected(int position) {
//        state，这事件是在你页面跳转完毕的时候调用的。
//        TabWidget widget = mTabHost.getTabWidget();
//        int oldFocusability = widget.getDescendantFocusability();
//        设置View覆盖子类控件而直接获得焦点
//        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mTabHost.setCurrentTab(position);//根据位置Postion设置当前的Tab
//        widget.setDescendantFocusability(oldFocusability);//设置取消分割线

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        /*position ==1的时候表示正在滑动
         *position==2的时候表示滑动完毕了
         *position==0的时候表示什么都没做，就是停在那。
         */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnExit:
                MainActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
