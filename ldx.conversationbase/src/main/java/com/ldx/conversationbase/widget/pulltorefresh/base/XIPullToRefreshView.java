package com.ldx.conversationbase.widget.pulltorefresh.base;

import android.content.Context;
import android.view.View;

import com.ldx.conversationbase.widget.pulltorefresh.XIPullToRefreshRecyclerView;

/**
 * Created by Mao Jiqing on 2016/10/10.
 */
public class XIPullToRefreshView extends View {

    public XIPullToRefreshView(Context context) {
        super(context);
    }

    public View getSlideView() {
        View baseView = new XIPullToRefreshRecyclerView(getContext());
        return baseView;
    }
}
