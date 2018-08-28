package com.ldx.conversationbase.widget.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldx.conversationbase.R;
import com.ldx.conversationbase.utils.XIScreenUtil;

/**
 * Created by Mao Jiqing on 2016/9/27.
 */
public class XIPullToRefreshLayout extends LinearLayout {
    private ViewDragHelper VDH;
    private View myList;
    private TextView pullText;
    private pulltorefreshNotifier pullNotifier;
    private boolean isPull = true;

    public XIPullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        VDH = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }

    public void setSlideView(View view) {
        init(view);
    }

    private void init(View view) {
        LayoutParams lp1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        myList = view;
        lp1.setMargins(0,0,0,0);
        myList.setBackgroundColor(getContext().getResources().getColor(R.color.xiconversation_chatpage_bg_color));
        myList.setLayoutParams(lp1);
        LayoutParams lp2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, XIScreenUtil.dip2px(getContext(), 100));
        pullText = new TextView(getContext());
        pullText.setText(getResources().getString(R.string.xiconversation_refresh_pulldown_loadmore));
        pullText.setBackgroundColor(getContext().getResources().getColor(R.color.xiconversation_chatpage_bg_color));
        pullText.setGravity(Gravity.CENTER);
        pullText.setLayoutParams(lp2);
        setOrientation(LinearLayout.VERTICAL);
        addView(pullText,lp1);
        addView(myList,lp2);
    }

    public View returnMylist() {
        return myList;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(
                resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            if (pullText.getTop() == 0) {
                viewHeight = pullText.getMeasuredHeight();
                pullText.layout(l, 0, r, viewHeight);
                myList.layout(l, 0, r, b);
                pullText.offsetTopAndBottom(-viewHeight);
            } else {
                pullText.layout(l, pullText.getTop(), r, pullText.getBottom());
                myList.layout(l, myList.getTop(), r, myList.getBottom());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int resolveSizeAndState(int size, int measureSpec,
                                          int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result | (childMeasuredState & MEASURED_STATE_MASK);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean shouldIntercept = VDH.shouldInterceptTouchEvent(event) && isPull;
        return shouldIntercept;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        VDH.processTouchEvent(event);
        return true;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            int childIndex = 1;
            if (changedView == myList) {
                childIndex = 2;
            }
            onViewPosChanged(childIndex, top);
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 1;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            refreshOrNot(releasedChild, yvel);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int finalTop = top;
            if (child == pullText) {
                if (top > 0) {
                    finalTop = 0;
                }
            } else if (child == myList) {
                if (top < 0) {
                    finalTop = 0;
                }
                if (top >= viewHeight) {
                    pullText.setText(getResources().getString(R.string.xiconversation_refresh_startload));
                } else {
                    pullText.setText(getResources().getString(R.string.xiconversation_refresh_pulldown_loadmore));
                }
            }
            int verhei = child.getTop() + (finalTop - child.getTop()) / 2;
            if (verhei >= 400){
                return 400;
            }else{
                return verhei;
            }
        }
    }

    private static int viewHeight;

    private void onViewPosChanged(int viewIndex, int posTop) {
        if (viewIndex == 1) {
            int offsetTopBottom = viewHeight + pullText.getTop()
                    - myList.getTop();
            myList.offsetTopAndBottom(offsetTopBottom);
        } else if (viewIndex == 2) {
            int offsetTopBottom = myList.getTop() - viewHeight
                    - pullText.getTop();
            pullText.offsetTopAndBottom(offsetTopBottom);
        }
        invalidate();
    }

    private void refreshOrNot(View releasedChild, float yvel) {
        int finalTop = 0;
        if (releasedChild == pullText) {
            if (yvel < -40) {
                finalTop = 0;
            } else {
                finalTop = viewHeight;
            }
        } else {
            if (yvel > viewHeight - 5 || releasedChild.getTop() >= viewHeight) {
                finalTop = viewHeight;
                if (null != pullNotifier) {
                    pullNotifier.onPull();
                }
                pullText.setText(getResources().getString(R.string.xiconversation_refresh_loading));
            }
        }

        if (VDH.smoothSlideViewTo(myList, 0, finalTop)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void refreshComplete() {
        if (VDH.smoothSlideViewTo(myList, 0, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void computeScroll() {
        if (VDH.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setpulltorefreshNotifier(pulltorefreshNotifier pullNotifier) {
        this.pullNotifier = pullNotifier;
    }

    public interface pulltorefreshNotifier {
        public void onPull();
    }

    public void setPullGone() {
        isPull = false;
    }
}
