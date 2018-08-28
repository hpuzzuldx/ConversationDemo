package com.ldx.conversationbase.common;

import android.content.Context;
import android.text.TextUtils;

import com.ldx.conversationbase.db.XIChatDbManager;
import com.ldx.conversationbase.utils.MD5Utility;
import com.ldx.conversationbase.utils.XIFileSaveUtil;

/**
 * Created by v-doli1 on 2017/9/8.
 */

public class XICacheResourceManager {

    public static String getDBName(){
        XIConversationConfig.XISetConfig setConfig = (XIConversationConfig.XISetConfig) XIConversationConfig.UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        String userID = setConfig.userId;
        String dbname = "";
        if (!TextUtils.isEmpty(userID) && !userID.trim().equals("") ){
            dbname = userID+".db";
        }else{
            dbname = "tempchat.db";
        }
        return dbname;
    }

    public static String getImagePath(Context context){
        XIConversationConfig.XISetConfig setConfig = (XIConversationConfig.XISetConfig) XIConversationConfig.UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        String  userID = MD5Utility.MD5(MD5Utility.filter(setConfig.userId));
        String dir = XIFileSaveUtil.getBasePath(context)+ XIChatConst.XIIMAGEDATAPATH;
        if (!TextUtils.isEmpty(userID)){
            dir = XIFileSaveUtil.getBasePath(context) +userID+"/"+ XIChatConst.XIIMAGEDATAPATH;
        }
        return dir;
    }

    public static String getOriginalImagePath(Context context){
        XIConversationConfig.XISetConfig setConfig = (XIConversationConfig.XISetConfig) XIConversationConfig.UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        String  userID = MD5Utility.MD5(MD5Utility.filter(setConfig.userId));
        String dir = "";
        if (!TextUtils.isEmpty(userID)){
            dir = XIFileSaveUtil.getBasePath(context) +userID+"/"+ XIChatConst.XIORIGINALIMAGEDATAPATH;
        }else{
            dir = XIFileSaveUtil.getBasePath(context) + XIChatConst.XIIMAGEDATAPATH;
        }
        return dir;
    }

    public static String getVoicePath(Context context){
        XIConversationConfig.XISetConfig setConfig = (XIConversationConfig.XISetConfig) XIConversationConfig.UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        String  userID = MD5Utility.MD5(MD5Utility.filter(setConfig.userId));
        String dir = XIFileSaveUtil.getBasePath(context) + XIChatConst.XIVOICEDATAPATH;
        if (!TextUtils.isEmpty(userID)){
            dir = XIFileSaveUtil.getBasePath(context) +userID+"/"+ XIChatConst.XIVOICEDATAPATH;
        }
        return dir;
    }
    public static void clearDBData(Context context){
        new XIChatDbManager().getChatMessageBeanDao(context).deleteAll();
    }

    public static void clearImageResource(Context context){
        XIFileSaveUtil.deleteDirectory(getImagePath(context));
    }

    public static void clearVoiceResource(Context context){
        XIFileSaveUtil.deleteDirectory(getVoicePath(context));
    }

    public static void clearAllResource(Context context){
        new XIChatDbManager().getChatMessageBeanDao(context).deleteAll();
        clearImageResource(context);
        clearVoiceResource(context);
    }
}
