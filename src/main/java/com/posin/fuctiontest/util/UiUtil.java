package com.posin.fuctiontest.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.posin.fuctiontest.activity.MainActivity;


/**
 * Created by Greetty on 2017/8/28.
 */

public class UIUtil {

    /**
     * @param context context
     * @param title   标题
     * @param msg     提示文本
     */
    public static void showMsg(final Context context, String title, String msg) {
        android.content.DialogInterface.OnClickListener c = new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
                .setPositiveButton("确定", c).show();
    }

}
