package com.posin.fuctiontest.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.posin.device.Mifare;
import com.posin.fuctiontest.R;

import butterknife.BindView;

/**
 * Created by Greetty on 2017/8/22.
 * <p>
 * 客显
 */

public class FragmentCardReader extends BaseFragment {

    private View view;

    private Mifare mDev = null;
    private Button mBtnOpen;
    @BindView(R.id.editLog)
    EditText mLogView;


    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_card_reader, null);
        return view;

    }

    @Override
    public void initData() {

    }
}
