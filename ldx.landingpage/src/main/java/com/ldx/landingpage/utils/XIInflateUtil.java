package com.ldx.landingpage.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class XIInflateUtil {

    //$ means findviewbyid
    public static <E extends View> E $(Activity activity, int resId) {
        return (E) activity.findViewById(resId);
    }

    public static <E extends View> E $(View view, int resId) {
        return (E) view.findViewById(resId);
    }

    //# means inflate
    public static <E extends View> E inflate(Context mContext, int resId) {
        return (E) LayoutInflater.from(mContext).inflate(resId, null);
    }
}

