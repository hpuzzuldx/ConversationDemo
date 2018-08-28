package com.ldx.conversationbase.network.callbackinterface;

import com.ldx.conversationbase.db.XIChatMessageBean;

import java.util.ArrayList;

/**
 * Created by v-doli1 on 2017/8/24.
 */

public interface XIAsyncTaskListener {
    void onComplete(ArrayList<XIChatMessageBean> beanList, long mChatbeanId);
    void onError(String error, long mChatbeanId);
}