package com.posin.fuctiontest.global;

import android.content.Context;

import com.posin.fuctiontest.util.AppUtil;

/**
 * Created by Greetty on 2017/8/31.
 *
 * 应用全部变量
 */

public class AppConfig {

    /**
     * 测试页面标题
     * @param context context
     * @return String[]
     */
    public static String[] getTitleItem(Context context){
        if(AppUtil.isZh(context)){
            return new String[]{"打印机","客显","钱箱","刷卡器","串口","喇叭","触屏"};
        }else{
            return new String[]{"Printer","CustomerDis","CashDrawer","CardReader",
                    "Serial","Horn","Touch"};
        }
    }
}
