package com.posin.fuctiontest.fragment;

import java.util.HashMap;

/**
 * Created by Greetty on 2017/8/27.
 *
 *  Fragment工厂类
 */

public class FragmentFactory {
    private static HashMap<Integer, BaseFragment> mBaseFragments = new HashMap<Integer, BaseFragment>();

    public static BaseFragment createFragment(int position) {
        BaseFragment baseFragment = mBaseFragments.get(position);
        if (baseFragment == null) {
            switch (position) {
                case 0:
                    baseFragment = new FragmentPrinter();//打印机
                    break;
                case 1:
                    baseFragment = new FragmentCustomerDis();//客显
                    break;
                case 2:
                    baseFragment = new FragmentCashDrawer();//钱箱
                    break;
                case 3:
                    baseFragment = new FragmentCardReader();//刷卡器
                    break;
                case 4:
                    baseFragment = new FragmentSerial();//串口
                    break;
                case 5:
                    baseFragment = new FragmentSpeaker();//喇叭
                    break;
                case 6:
                    baseFragment = new FragmentTouch();//触摸屏触摸
                    break;
//                case 7:
//                    baseFragment = new FragmentDisplayScr();//屏幕显示
//                    break;
                default:
                    break;
            }
            mBaseFragments.put(position, baseFragment);
        }
        return baseFragment;

    }
}
