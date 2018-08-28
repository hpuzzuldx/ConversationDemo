package com.ldx.conversationbase.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by v-doli1 on 2018/4/11.
 */

public class XIProgressView extends View {
    private int mSelecColor = Color.parseColor("#CBCBCB");
    private int mOtherColor = Color.parseColor("#777777");
    private float mRadius = 0;
    private Paint mSelectedPaint;
    private Paint mOtherPaint;

    public XIProgressView(Context context) {
        this(context, null);
    }

    public XIProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public XIProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

      /*  TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundView);
        mRingWidth = typedArray.getDimension(R.styleable.RoundView_ring_width, 0);
        mRingColor = typedArray.getColor(R.styleable.RoundView_ring_color, Color.parseColor("#CBCBCB"));
        mRadius = typedArray.getDimension(R.styleable.RoundView_android_radius, 0);*/

    private void init() {
        mSelectedPaint = new Paint();
        mOtherPaint = new Paint();
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.FILL);
        mSelectedPaint.setColor(mSelecColor);
        mOtherPaint.setAntiAlias(true);
        mOtherPaint.setStyle(Paint.Style.FILL);
        mOtherPaint.setColor(mOtherColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        float x1 = getWidth() / 3;
        float y1 = getHeight() / 3;
        mRadius = mRadius == 0 ? getWidth() / 20 : mRadius;
        float x2 = getWidth() / 3 + mRadius * 4;
        float y2 = getHeight() / 3;
        float x3 = getWidth() / 3 + mRadius * 8;
        float y3 = getHeight() / 3;
        canvas.drawCircle(x1, y1, mRadius, mSelectedPaint);
        canvas.drawCircle(x2, y2, mRadius, mSelectedPaint);
        canvas.drawCircle(x3, y3, mRadius, mSelectedPaint);
    }
}