package com.ldx.conversationbase.camera;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ldx.conversationbase.R;

public class XICaptureLayout extends FrameLayout implements View.OnClickListener {
    private View captureRetryLayout;
    private ImageView btnCapture;
    private ImageView btnRetry;
    private ImageView btnClose;

    private ClickListener mClickListener;
    private boolean isExpanded;

    public interface ClickListener {
        void onCaptureClick();

        void onOkClick();

        void onRetryClick();

        void onCloseClick();
    }

    public XICaptureLayout(Context context) {
        this(context, null);
    }

    public XICaptureLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XICaptureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        LayoutInflater.from(getContext()).inflate(R.layout.xiconversation_camera_capture_layout, this, true);
        captureRetryLayout = findViewById(R.id.xiconversation_camera_capture_retry_layout);
        btnCapture = (ImageView) findViewById(R.id.xiconversation_camera_capture);
        btnRetry = (ImageView) findViewById(R.id.xiconversation_camera_retry);
        btnClose = (ImageView) findViewById(R.id.xiconversation_camera_close);

        btnCapture.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        btnRetry.setEnabled(false);
        btnClose.setOnClickListener(this);
    }

    public void setClickListener(ClickListener listener) {
        mClickListener = listener;
    }

    public void setExpanded(boolean expanded) {
        if (isExpanded == expanded) {
            return;
        }

        isExpanded = expanded;
        if (isExpanded) {
            expand();
        } else {
            fold();
        }
    }

    @Override
    public void onClick(View v) {
        if (mClickListener == null) {
            return;
        }

        if (v == btnCapture) {
            if (!isExpanded) {
                mClickListener.onCaptureClick();
            } else {
                mClickListener.onOkClick();
            }
        } else if (v == btnRetry) {
            mClickListener.onRetryClick();
        } else if (v == btnClose) {
            mClickListener.onCloseClick();
        }
    }

    private void expand() {
        btnCapture.setImageResource(R.drawable.xiconversation_ic_camera_done);
        btnRetry.setEnabled(true);
        btnClose.setVisibility(GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            playExpandAnimation();
        } else {
            LayoutParams captureParams = (LayoutParams) btnCapture.getLayoutParams();
            captureParams.width = XICameraUtils.dp2px(getContext(), 80);
            captureParams.height = XICameraUtils.dp2px(getContext(), 80);
            captureParams.gravity = Gravity.END;

            LayoutParams layoutParams = (LayoutParams) captureRetryLayout.getLayoutParams();
            layoutParams.width = XICameraUtils.dp2px(getContext(), 280);
            captureRetryLayout.requestLayout();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void playExpandAnimation() {
        ValueAnimator scaleAnimator = ValueAnimator.ofInt(XICameraUtils.dp2px(getContext(), 60), XICameraUtils.dp2px(getContext(), 80));
        scaleAnimator.setInterpolator(new LinearInterpolator());
        scaleAnimator.setDuration(100);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                LayoutParams captureParams = (LayoutParams) btnCapture.getLayoutParams();
                captureParams.width = value;
                captureParams.height = value;
                captureParams.gravity = Gravity.CENTER;
                btnCapture.requestLayout();
            }
        });

        ValueAnimator transAnimator = ValueAnimator.ofInt(XICameraUtils.dp2px(getContext(), 80), XICameraUtils.dp2px(getContext(), 280));
        transAnimator.setInterpolator(new LinearInterpolator());
        transAnimator.setDuration(200);
        transAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                LayoutParams captureParams = (LayoutParams) btnCapture.getLayoutParams();
                captureParams.gravity = Gravity.END;

                LayoutParams layoutParams = (LayoutParams) captureRetryLayout.getLayoutParams();
                layoutParams.width = value;
                captureRetryLayout.requestLayout();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(scaleAnimator, transAnimator);
        animatorSet.start();
    }

    private void fold() {
        btnCapture.setImageResource(0);
        btnRetry.setEnabled(false);
        btnClose.setVisibility(VISIBLE);

        int length = XICameraUtils.dp2px(getContext(), 60);
        LayoutParams captureParams = (LayoutParams) btnCapture.getLayoutParams();
        captureParams.width = length;
        captureParams.height = length;
        captureParams.gravity = Gravity.CENTER;

        LayoutParams layoutParams = (LayoutParams) captureRetryLayout.getLayoutParams();
        layoutParams.width = XICameraUtils.dp2px(getContext(), 80);
        captureRetryLayout.requestLayout();
    }
}
