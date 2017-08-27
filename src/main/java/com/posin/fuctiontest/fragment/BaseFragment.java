package com.posin.fuctiontest.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Greetty on 2017/8/22.
 */

public abstract class BaseFragment extends Fragment {

    public Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
        initData();
    }

    public <E extends View>E findViewByIds(View view, int id){
        return (E) view.findViewById(id);
    }

    /**
     * 子类必须实现初始化布局的方法
     * @param inflater  LayoutInflater
     * @return view
     */
    public abstract View initView(LayoutInflater inflater);

    /**
     * 初始化事件，可根据需要是否实现
     */
    public void initEvent(){

    }

    /**
     * 初始化数据，可根据需要是否实现
     */
    public void initData() {

    }
}
