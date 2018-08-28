package com.ldx.conversation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * Created by v-doli1 on 2017/9/4.
 * multile_line support imeoptions
 */

public class MulLineEditText extends android.support.v7.widget.AppCompatEditText {
    public MulLineEditText(Context context) {
        super(context);
    }

    public MulLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MulLineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection connection = super.onCreateInputConnection(outAttrs);
        if (connection == null) return null;
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return connection;
    }
}
