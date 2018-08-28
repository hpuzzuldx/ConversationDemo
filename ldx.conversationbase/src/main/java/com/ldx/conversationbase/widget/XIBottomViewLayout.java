package com.ldx.conversationbase.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class XIBottomViewLayout extends LinearLayout {
    private View mBottomView;

    public XIBottomViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBottomView(View view) {
        init(view);
    }

    private void init(View view) {
        LayoutParams lp1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mBottomView = view;
        mBottomView.setLayoutParams(lp1);
        setOrientation(LinearLayout.VERTICAL);
        addView(mBottomView);
    }

    public View getBottomView() {
        return mBottomView;
    }

}
