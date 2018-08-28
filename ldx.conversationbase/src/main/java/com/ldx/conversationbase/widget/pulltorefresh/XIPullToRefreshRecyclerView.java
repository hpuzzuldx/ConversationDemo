package com.ldx.conversationbase.widget.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Mao Jiqing on 2016/9/27.
 */
public class XIPullToRefreshRecyclerView extends RecyclerView {
    boolean allowDragBottom = true;
    float downY = 0;
    boolean needConsumeTouch = true;

    public XIPullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public XIPullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downY = ev.getRawY();
            needConsumeTouch = true;
            if (getMyScrollY() == 0) {
                allowDragBottom = true;
            } else {
                allowDragBottom = false;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (!needConsumeTouch) {
                getParent().requestDisallowInterceptTouchEvent(false);
                try {
                    if (getLayoutManager() != null && ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition() == 0)
                    ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(0,0);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return false;
            } else if (allowDragBottom) {
                if (downY - ev.getRawY() < -2) {
                    needConsumeTouch = false;
                    getParent().requestDisallowInterceptTouchEvent(false);
                    try {
                        if (getLayoutManager() != null && ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition() == 0)
                        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(0,0);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }
            }
        }

      /* if (!needConsumeTouch){
            try {
                if (getLayoutManager() != null && ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition() == 0 )
                ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(0,0);
            }catch(Exception e){
                e.printStackTrace();
            }
        }*/
        getParent().requestDisallowInterceptTouchEvent(needConsumeTouch);
        return super.dispatchTouchEvent(ev);
    }

    public int getMyScrollY() {
        View c = getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int top = c.getTop();
        if (firstVisiblePosition == 0 && top < 0)return 0;
        return  -top + firstVisiblePosition * c.getHeight();
    }

}