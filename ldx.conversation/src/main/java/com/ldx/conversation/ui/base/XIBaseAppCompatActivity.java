package com.ldx.conversation.ui.base;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by v-doli1 on 2017/12/21.
 */

public class XIBaseAppCompatActivity extends AppCompatActivity {
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    protected void setStatusBar() {
    }
}
