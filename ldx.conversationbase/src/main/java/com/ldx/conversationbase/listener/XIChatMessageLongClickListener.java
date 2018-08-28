package com.ldx.conversationbase.listener;

import android.view.View;

import com.ldx.conversationbase.db.XIChatMessageInfo;

/**
 * Created by v-doli1 on 2017/9/15.
 */

public interface XIChatMessageLongClickListener {
    public void OnMessageLongClickListener(View view, XIChatMessageInfo xiChatMessageInfo, XIChatMessageDefaultLongClickListener xiChatMessageDefaultLongClickListener);
}
