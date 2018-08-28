package com.ldx.landingpage.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ldx.landingpage.R;
import com.ldx.landingpage.utils.XIStatusBarUtil;

public abstract class XIBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        initView();
        initEvent();
        initData();
    }

    public abstract  void initView();
    public abstract  void initEvent();
    public abstract void initData();

    protected void setStatusBar() {
        XIStatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary),0);
    }
}
