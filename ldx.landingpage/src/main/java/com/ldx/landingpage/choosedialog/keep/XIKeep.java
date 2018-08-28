package com.ldx.landingpage.choosedialog.keep;

import android.content.Context;
import android.content.SharedPreferences;

public class XIKeep {
    private SharedPreferences pref;

    private static final String ASKED_FOR_PERMISSION = "ASKED_FOR_PERMISSION";

    XIKeep(Context context) {
        this.pref = context.getSharedPreferences("libsdkmsxiaoice", Context.MODE_PRIVATE);
    }

    public static XIKeep with(Context context) {
        return new XIKeep(context);
    }

    public void askedForPermission() {
        pref.edit().putBoolean(ASKED_FOR_PERMISSION, true).commit();
    }

    public boolean neverAskedForPermissionYet() {
        return !pref.getBoolean(ASKED_FOR_PERMISSION, false);
    }

}
