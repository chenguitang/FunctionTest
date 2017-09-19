package com.posin.functiontest.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.posin.functiontest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Greetty on 2017/8/29.
 * <p>
 * ID卡测试
 */

public class FragmentIDCardReader extends BaseFragment {

    private static final String ID_CARD_READER = "id_card_reader";

    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.etMsr)
    EditText etMsr;
    Unbinder unbinder;

    private FragmentCardManager mFragmentCardManager;
    private FragmentManager fm;
    private View view;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_id_card, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        mFragmentCardManager = new FragmentCardManager();
        fm = getFragmentManager();
    }

    @OnClick(R.id.btn_card_id_clear)
    public void cardIDClear(){
        etMsr.getEditableText().clear();
    }

    @OnClick(R.id.btn_card_id_back)
    public void cardIDBack(){
        if (fm == null)
            fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_main_card, mFragmentCardManager, ID_CARD_READER);
        ft.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
