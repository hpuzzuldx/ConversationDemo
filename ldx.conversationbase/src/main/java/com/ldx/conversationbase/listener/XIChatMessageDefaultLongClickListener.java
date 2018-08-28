package com.ldx.conversationbase.listener;

import android.view.View;

import com.ldx.conversationbase.db.XIChatMessageInfo;

/**
 * Created by v-doli1 on 2017/9/15.
 */

public interface XIChatMessageDefaultLongClickListener {
    public void onMessageCopy(View view, XIChatMessageInfo xiChatMessageInfo);
    public void onMessageShare(View view, XIChatMessageInfo xiChatMessageInfo);
    public void onMessageDelete(View view, XIChatMessageInfo xiChatMessageInfo);
}
