package com.posin.fuctiontest.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.posin.fuctiontest.R;

/**
 * Created by Greetty on 2017/8/27.
 * 喇叭
 */

public class FragmentSpeaker extends BaseFragment {

    private View view;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_speaker, null);
        return view;
    }

    @Override
    public void initData() {


    }
}
