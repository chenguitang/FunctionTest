package com.posin.functiontest.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.posin.functiontest.R;
import com.posin.functiontest.activity.FullScreenActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Greetty on 2017/8/27.
 * 触屏
 */

public class FragmentTouch extends BaseFragment {

    @BindView(R.id.ll_touch)
    LinearLayout llTouch;
    Unbinder unbinder;
    private View view;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_touch, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
    }

    @OnClick(R.id.btn_touch_main)
    public void touchMain(){
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
