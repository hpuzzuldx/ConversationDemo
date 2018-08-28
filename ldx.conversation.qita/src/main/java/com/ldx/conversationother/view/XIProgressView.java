package com.ldx.conversationother.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ldx.conversationother.R;

/**
 * Created by v-doli1 on 2018/4/11.
 */

public class XIProgressView extends View {
    private int mSelecColor = Color.parseColor("#CCCCCC");
    private int mOtherColor = Color.parseColor("#999999");
    private float mRadius = 0;
    private Paint mSelectedPaint;
    private Paint mOtherPaint;
    private int mCurrentCircle = 0;
    private int circleNum = 3;
    private boolean autoRun = true;

    public XIProgressView(Context context) {
        this(context, null);
    }

    public XIProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XIProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XIChatProgressView);
        mSelecColor = typedArray.getColor(R.styleable.XIChatProgressView_chatprogressview_selected_color, Color.parseColor("#CCCCCC"));
        mOtherColor = typedArray.getColor(R.styleable.XIChatProgressView_chatprogressview_other_color, Color.parseColor("#999999"));
        mRadius = typedArray.getDimension(R.styleable.XIChatProgressView_chatprogressview_radius, 0);
        init();
    }

    private void init() {
        mSelectedPaint = new Paint();
        mOtherPaint = new Paint();
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.FILL);
        mSelectedPaint.setColor(mSelecColor);
        mOtherPaint.setAntiAlias(true);
        mOtherPaint.setStyle(Paint.Style.FILL);
        mOtherPaint.setColor(mOtherColor);
        setInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        mRadius = mRadius == 0 ? getWidth() / 20 : mRadius;
        float x1 = getWidth() / 4;
        float y1 = getHeight() / 2 ;
        float x2 = getWidth() / 4 + mRadius * 4;
        float x3 = getWidth() / 4 + mRadius * 8;
        float y2 = y1 - mRadius/2;
        y1 = y1 + mRadius/2;
        if (mCurrentCircle == 0){
            canvas.drawCircle(x1, y1, mRadius, mSelectedPaint);
            canvas.drawCircle(x2, y2, mRadius, mOtherPaint);
            canvas.drawCircle(x3, y1, mRadius, mOtherPaint);
        }else if (mCurrentCircle == 1){
            canvas.drawCircle(x1, y1, mRadius, mOtherPaint);
            canvas.drawCircle(x2, y2, mRadius, mSelectedPaint);
            canvas.drawCircle(x3, y1, mRadius, mOtherPaint);
        }else if(mCurrentCircle == 2){
            canvas.drawCircle(x1, y1, mRadius, mOtherPaint);
            canvas.drawCircle(x2, y2, mRadius, mOtherPaint);
            canvas.drawCircle(x3, y1, mRadius, mSelectedPaint);
        }

    }

    public void setInvalidate(){
        mCurrentCircle = (mCurrentCircle + 1) % circleNum;
        invalidate();
        if (autoRun){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInvalidate();
                }
            }, 500);
        }
    }

    public void setAutoRun(boolean auto){
        this.autoRun = auto;
    }
}