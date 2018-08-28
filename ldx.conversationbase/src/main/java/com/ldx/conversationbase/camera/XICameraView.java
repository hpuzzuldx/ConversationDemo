package com.ldx.conversationbase.camera;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ldx.conversationbase.R;

public class XICameraView extends FrameLayout implements SurfaceHolder.Callback,
        XICaptureLayout.ClickListener, View.OnClickListener, SensorEventListener {
    private static final String TAG = "XICameraView";
    private SurfaceView mSurfaceView;
    private View mFocusView;
    private View mSwitchWrapper;
    private View mSwitchView;
    private ImageView mPictureView;
    private XICaptureLayout mCaptureLayout;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private SensorManager mSensorManager;
    private CameraListener mCameraListener;
    private Bitmap mPicture;
    private int mSensorRotation;
    private boolean isSurfaceCreated;

    public interface CameraListener {
        void onCapture(Bitmap bitmap);

        void onCameraClose();

        void onCameraError(Throwable th);
    }

    public XICameraView(Context context) {
        this(context, null);
    }

    public XICameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XICameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.BLACK);
        LayoutInflater.from(getContext()).inflate(R.layout.xiconversation_camera_layout, this, true);

        mSurfaceView = (SurfaceView) findViewById(R.id.xiconversation_camera_surface);
        mFocusView = findViewById(R.id.xiconversation_camera_focus);
        mSwitchWrapper = findViewById(R.id.xiconversation_camera_switch_wrapper);
        mSwitchView = findViewById(R.id.xiconversation_camera_switch);
        mPictureView = (ImageView) findViewById(R.id.xiconversation_camera_picture_preview);
        mCaptureLayout = (XICaptureLayout) findViewById(R.id.xiconversation_camera_capture_layout);

        XICameraManager.getInstance().init(getContext());

        mSurfaceView.setOnTouchListener(surfaceTouchListener);
        // fix `java.lang.RuntimeException: startPreview failed` on api 10
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mSurfaceView.getHolder().addCallback(this);
        mCaptureLayout.setClickListener(this);
        mSwitchWrapper.setVisibility(XICameraManager.getInstance().hasMultiCamera() ? VISIBLE : GONE);
        mSwitchWrapper.setOnClickListener(this);

        mGestureDetector = new GestureDetector(getContext(), simpleOnGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), onScaleGestureListener);
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
    }

    public void setCameraListener(CameraListener listener) {
        mCameraListener = listener;
    }

    public void onResume() {

        if (!XICameraManager.getInstance().isOpened() && isSurfaceCreated) {
            XICameraManager.getInstance().open(new XICameraManager.Callback<Boolean>() {
                @Override
                public void onEvent(Boolean success) {
                    if (!success && mCameraListener != null) {
                        mCameraListener.onCameraError(new Exception("open camera failed"));
                    }
                }
            });
        }

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        if (XICameraManager.getInstance().isOpened()) {
            XICameraManager.getInstance().close();
        }
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCameraListener = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        XICameraManager.getInstance().setSurfaceHolder(holder, width, height);

        if (XICameraManager.getInstance().isOpened()) {
            XICameraManager.getInstance().close();
        }

        XICameraManager.getInstance().open(new XICameraManager.Callback<Boolean>() {
            @Override
            public void onEvent(Boolean success) {
                if (!success && mCameraListener != null) {
                    mCameraListener.onCameraError(new Exception("open camera failed"));
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        isSurfaceCreated = false;
        XICameraManager.getInstance().setSurfaceHolder(null, 0, 0);
    }

    @Override
    public void onCaptureClick() {
        XICameraManager.getInstance().takePicture(new XICameraManager.Callback<Bitmap>() {
            @Override
            public void onEvent(Bitmap bitmap) {
                if (bitmap != null) {
                    mSurfaceView.setVisibility(GONE);
                    mSwitchWrapper.setVisibility(GONE);
                    mPictureView.setVisibility(VISIBLE);
                    mPicture = bitmap;
                    mPictureView.setImageBitmap(mPicture);
                    mCaptureLayout.setExpanded(true);
                } else {
                    onRetryClick();
                }
            }
        });
    }

    @Override
    public void onOkClick() {
        if (mPicture != null && mCameraListener != null) {
            mCameraListener.onCapture(mPicture);
        }
    }

    @Override
    public void onRetryClick() {
        mPicture = null;
        mCaptureLayout.setExpanded(false);
        mSurfaceView.setVisibility(VISIBLE);
        mSwitchWrapper.setVisibility(XICameraManager.getInstance().hasMultiCamera() ? VISIBLE : GONE);
        mPictureView.setImageBitmap(null);
        mPictureView.setVisibility(GONE);
    }

    @Override
    public void onCloseClick() {
        if (mCameraListener != null) {
            mCameraListener.onCameraClose();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mSwitchWrapper) {
            XICameraManager.getInstance().switchCamera(new XICameraManager.Callback<Boolean>() {
                @Override
                public void onEvent(Boolean success) {
                    if (!success && mCameraListener != null) {
                        mCameraListener.onCameraError(new Exception("switch camera failed"));
                    }
                }
            });
        }
    }

    private OnTouchListener surfaceTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mScaleGestureDetector.onTouchEvent(event);
            if (mScaleGestureDetector.isInProgress()) {
                return true;
            }

            return mGestureDetector.onTouchEvent(event);
        }
    };

    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if (!XICameraManager.getInstance().isOpened()) {
                return false;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mFocusView.removeCallbacks(timeoutRunnable);
                mFocusView.postDelayed(timeoutRunnable, 1500);

                mFocusView.setVisibility(VISIBLE);
                LayoutParams focusParams = (LayoutParams) mFocusView.getLayoutParams();
                focusParams.leftMargin = (int) e.getX() - focusParams.width / 2;
                focusParams.topMargin = (int) e.getY() - focusParams.height / 2;
                mFocusView.setLayoutParams(focusParams);

                ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFocusView, "scaleX", 1, 0.5f);
                scaleX.setDuration(300);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFocusView, "scaleY", 1, 0.5f);
                scaleY.setDuration(300);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(mFocusView, "alpha", 1f, 0.3f, 1f, 0.3f, 1f, 0.3f, 1f);
                alpha.setDuration(600);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(scaleX).with(scaleY).before(alpha);
                animatorSet.start();

                XICameraManager.Callback<Boolean> focusCallback = new XICameraManager.Callback<Boolean>() {
                    @Override
                    public void onEvent(Boolean success) {
                        if (mFocusView.getTag() == this && mFocusView.getVisibility() == VISIBLE) {
                            mFocusView.setVisibility(INVISIBLE);
                        }
                    }
                };
                mFocusView.setTag(focusCallback);
                XICameraManager.getInstance().setFocus(e.getX(), e.getY(), focusCallback);
            }

            return XICameraManager.getInstance().hasMultiCamera();
        }

        private Runnable timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (mFocusView.getVisibility() == VISIBLE) {
                    mFocusView.setVisibility(INVISIBLE);
                }
            }
        };

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            XICameraManager.getInstance().switchCamera(new XICameraManager.Callback<Boolean>() {
                @Override
                public void onEvent(Boolean success) {
                    if (!success && mCameraListener != null) {
                        mCameraListener.onCameraError(new Exception("switch camera failed"));
                    }
                }
            });
            return true;
        }
    };

    private ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        private float mLastSpan;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float span = detector.getCurrentSpan() - mLastSpan;
            mLastSpan = detector.getCurrentSpan();
            if (XICameraManager.getInstance().isOpened()) {
                XICameraManager.getInstance().setZoom(span);
                return true;
            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mLastSpan = detector.getCurrentSpan();
            return XICameraManager.getInstance().isOpened();
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        int rotation = XICameraUtils.calculateSensorRotation(event.values[0], event.values[1]);
        if (rotation >= 0 && rotation != XICameraManager.getInstance().getSensorRotation()) {
            playRotateAnimation(XICameraManager.getInstance().getSensorRotation(), rotation);
            XICameraManager.getInstance().setSensorRotation(rotation);
            mSensorRotation = rotation;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void playRotateAnimation(int oldRotation, int newRotation) {
        if (!XICameraManager.getInstance().hasMultiCamera()) {
            return;
        }

        int diff = newRotation - oldRotation;
        if (diff > 180) {
            diff = diff - 360;
        } else if (diff < -180) {
            diff = diff + 360;
        }
        RotateAnimation rotate = new RotateAnimation(-oldRotation, -oldRotation - diff, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        mSwitchView.startAnimation(rotate);
    }
}
