package com.posin.fuctiontest.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.posin.fuctiontest.R;

/**
 * Created by Greetty on 2017/8/22.
 *
 * 客显
 */

public class FragmentPrinter extends BaseFragment {


    @Override
    public View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_printer, null);
        return view;

    }

    @Override
    public void initData() {

    }
}
