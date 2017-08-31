package com.posin.fuctiontest.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.posin.fuctiontest.R;
import com.posin.fuctiontest.activity.FullScreenActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Greetty on 2017/8/27.
 * 触屏
 */

public class FragmentDisplayScr extends BaseFragment {

    private View view;
    Unbinder unbinder;


    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_dispaly_scr, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.btn_display_main)
    public void displayMain(){
        Intent intent = new Intent(mContext, FullScreenActivity.class);
        intent.putExtra("function", "touch_test");
        mContext.startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
