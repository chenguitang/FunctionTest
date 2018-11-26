package com.posin.functiontest.util;

import java.io.IOException;
import java.util.ArrayList;

/**
 * FileName: ModelUti
 * Author: Greetty
 * Time: 2018/11/3 16:03
 * Desc: TODO
 */
public class ModelUti {

    private static final String TAG = "ModelUti";

    /**
     * 机器是否为单主板双屏机器，机器只有主屏可触控
     *
     * @return 型号值
     */
    public boolean isDoubleScreenAndSingleTouch() {

        ArrayList<String> list = new ArrayList<>();
        try {
            Proc.suExec("busybox  cat /proc/cpuinfo | grep RK3288", list, 2000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list.size() > 0;
    }

}
