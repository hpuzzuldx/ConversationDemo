package com.ldx.conversationbase.common;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by v-doli1 on 2017/9/5.
 * user config
 */

public class XIConversationConfig {

    public static HashMap<String, XIBaseConfig> UserConfigs = new HashMap();

    static {
        UserConfigs.put(XIChatConst.XICONVERSATIONCONFIG, new XISetConfig());
    }

    interface XIBaseConfig {
        public boolean isConfigured();
    }

    public static class XISetConfig implements XIBaseConfig {
        public String userId = "";
        public String conversationurl = "";
        public String conversationappid = "";
        public String conversationsecret = "";

        @Override
        public boolean isConfigured() {
            return !TextUtils.isEmpty(this.userId);
        }
    }

    public static void setXIConversationConfig(String userId) {
        XISetConfig config = (XISetConfig) UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        config.userId = new String(userId);
    }

    public static void setConversationUserId(String userId) {
        XISetConfig config = (XISetConfig) UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        config.userId = new String(userId);
    }

    public static void setConversationURL(String conurl) {
        XISetConfig config = (XISetConfig) UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        config.conversationurl = new String(conurl);
    }

    public static void setConversationAppid(String conappid) {
        XISetConfig config = (XISetConfig) UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        config.conversationappid = new String(conappid);
    }

    public static void setConversationSecret(String consecret) {
        XISetConfig config = (XISetConfig) UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        config.conversationsecret = new String(consecret);
    }

    public static String getConversationUserId() {
        XISetConfig config = (XISetConfig) UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        return config.userId;
    }

    public static String getConversationURL() {
        XISetConfig config = (XISetConfig) UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        return config.conversationurl;
    }

    public static String getConversationAppid() {
        XISetConfig config = (XISetConfig) UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG);
        return config.conversationappid ;
    }

}
