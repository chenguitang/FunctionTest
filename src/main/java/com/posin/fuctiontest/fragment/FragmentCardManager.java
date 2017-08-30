package com.posin.fuctiontest.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.posin.fuctiontest.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Greetty on 2017/8/30.
 * <p>
 * 刷卡器测试管理
 */

public class FragmentCardManager extends BaseFragment {

    private static final String IC_CARD_READER = "ic_card_reader";
    private static final String ID_CARD_READER = "id_card_reader";

    private View view;
    private FragmentICCardReader mFragmentICCardReader;
    private FragmentIDCardReader mFragmentIDCardReader;
    private FragmentManager fm;


    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_card_manager, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        mFragmentICCardReader = new FragmentICCardReader();
        mFragmentIDCardReader = new FragmentIDCardReader();
        fm = getFragmentManager();
    }

    @OnClick(R.id.btn_car_ic)
    public void startICTest() {
        if (fm == null)
            fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_main_card,mFragmentICCardReader,IC_CARD_READER);
        ft.commit();
    }

    @OnClick(R.id.btn_card_id_msr)
    public void startIDTest() {
        if (fm == null)
            fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_main_card,mFragmentIDCardReader,ID_CARD_READER);
        ft.commit();
    }

}
