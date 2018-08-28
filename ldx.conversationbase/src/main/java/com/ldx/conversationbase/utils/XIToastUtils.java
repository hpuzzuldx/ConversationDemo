package com.ldx.conversationbase.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by v-doli1 on 2017/9/15.
 */

public class XIToastUtils {

    private static Toast toast;

    public static void showShortToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showLongToast(Context context, String msg) {
        if (toast != null) {
            toast.setText(msg);
        } else {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}

