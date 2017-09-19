package com.posin.functiontest.fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.posin.device.CashDrawer;
import com.posin.functiontest.R;
import com.posin.functiontest.util.UIUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Greetty on 2017/8/22.
 * <p>
 * 客显
 */

public class FragmentCashDrawer extends BaseFragment {

    private static final String TAG = "FragmentCashDrawer";
    private CashDrawer mCashDrawer;

    private final Handler mHandler = new Handler();

    @BindView(R.id.cbCashDrawerStatus)
    CheckBox mCashDrawerStatus;

    // 查询钱箱 "开/关" 状态
    // 目前大部分钱箱不支持此功能
    private final Runnable mUpateCashdrawerStatus = new Runnable() {
        @Override
        public void run() {
            if (mCashDrawer != null) {
                boolean status = mCashDrawer.isOpened();
                mCashDrawerStatus.setChecked(status);

                // 每一秒钟执行一次
                mHandler.postDelayed(this, 1000);
            }
        }
    };


    @Override
    public View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_cashdrawer, null);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void initData() {
        try {
            mCashDrawer = CashDrawer.newInstance();
            mCashDrawerStatus.setClickable(false);
        } catch (Throwable throwable) {
            Log.e(TAG, "error: " + throwable.getMessage());
            throwable.printStackTrace();
        }
    }

    @OnClick(R.id.btn_cash_drawer_pin2)
    public void openCashDrawerPin2() {
        try {
            mCashDrawer.kickOutPin2(100);
        } catch (Throwable e) {
            e.printStackTrace();
            UIUtil.showMsg(mContext, getString(R.string.dialog_title), e.getMessage());
        }
    }

    @OnClick(R.id.btn_cash_drawer_pin5)
    public void openCashDrawerPin5() {
        try {
            mCashDrawer.kickOutPin5(100);
        } catch (Throwable e) {
            e.printStackTrace();
            UIUtil.showMsg(mContext, getString(R.string.dialog_title), e.getMessage());

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "+++++++++++++++++++ onStart() +++++++++++++++++++");
        if (mCashDrawer != null) // 更新钱箱状态
            mHandler.postDelayed(mUpateCashdrawerStatus, 1000);
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "+++++++++++++++++++ onStop() +++++++++++++++++++");
        mHandler.removeCallbacks(mUpateCashdrawerStatus);
    }
}
