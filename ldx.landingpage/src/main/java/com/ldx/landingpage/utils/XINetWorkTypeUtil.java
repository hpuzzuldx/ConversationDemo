package com.ldx.landingpage.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ldx.landingpage.common.XILandingpageConsts;

/**
 * @author lidongxiu
 */

public class XINetWorkTypeUtil {

    public static int getNetworkType(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo activeNetwork = conn.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    return XILandingpageConsts.Web_State.XILANDINGPAGE_WIFI;
                }
                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return XILandingpageConsts.Web_State.XILANDINGPAGE_GPRS;
                }
            }
            // NO_NETWORK;
            return XILandingpageConsts.Web_State.XILANDINGPAGE_NO_NET;
        } catch (Exception ex) {
            // used for those pad doesn't have data connection (TYPE_MOBILE)
            return XILandingpageConsts.Web_State.XILANDINGPAGE_NO_NET;
        }
    }
}

