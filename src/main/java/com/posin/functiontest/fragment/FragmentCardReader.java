package com.posin.functiontest.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.posin.functiontest.R;

import butterknife.ButterKnife;

/**
 * Created by Greetty on 2017/8/22.
 * <p>
 * 客显
 */

public class FragmentCardReader extends BaseFragment {

    private static final String CARD_READER_MANAGER = "card_reader_manager";

    private View view;
    private FragmentCardManager mFragmentCardManager;
    private FragmentManager fm;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_card_reader, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        mFragmentCardManager=new FragmentCardManager();
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_main_card,mFragmentCardManager,CARD_READER_MANAGER);
        ft.commit();
    }

}
