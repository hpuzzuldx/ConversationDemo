package com.ldx.landingpage.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lidongxiu
 */

public class XIJsonParseUtil {

    public static Map<String,String> getShareMap(String content){
        System.out.println("===========getShareMap==========");
        Map<String,String> xiaoiceDataMap = new HashMap<String,String>();
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        JSONObject objectall = null;

        try {
            objectall = new JSONObject(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (objectall == null )return null;
        if (objectall != null){
            System.out.println("===============objectall==============="+objectall.optString("title"));
            xiaoiceDataMap.put("title",objectall.optString("title"));
            xiaoiceDataMap.put("description", objectall.optString("description"));
            xiaoiceDataMap.put("imageURL",objectall.optString("imageURL"));
            xiaoiceDataMap.put("shareURL",objectall.optString("shareURL"));

        }

        return xiaoiceDataMap;

    }
}
