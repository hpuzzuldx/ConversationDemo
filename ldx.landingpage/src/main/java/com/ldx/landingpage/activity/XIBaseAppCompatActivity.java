package com.ldx.landingpage.activity;

import android.support.v7.app.AppCompatActivity;

import com.ldx.landingpage.R;
import com.ldx.landingpage.utils.XIStatusBarUtil;

/**
 * @author lidongxiu
 */

public class XIBaseAppCompatActivity extends AppCompatActivity {
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    protected void setStatusBar() {
        XIStatusBarUtil.setColor(this, getResources().getColor(R.color.xilandingpage_sdkxiaoice_defaultstatusbar),0);
    }
}
