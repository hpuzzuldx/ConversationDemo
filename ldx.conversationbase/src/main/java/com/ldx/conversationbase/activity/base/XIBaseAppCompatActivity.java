package com.ldx.conversationbase.activity.base;

import android.support.v7.app.AppCompatActivity;

import com.ldx.conversationbase.R;
import com.ldx.conversationbase.utils.XIStatusBarUtil;

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
        XIStatusBarUtil.setColor(this, getResources().getColor(R.color.xiconversation_chatpage_statusbardefault_color),0);
    }
}
