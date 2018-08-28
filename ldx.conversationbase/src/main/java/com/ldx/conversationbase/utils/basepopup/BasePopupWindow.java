package com.ldx.conversationbase.utils.basepopup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.ldx.conversationbase.R;
import com.ldx.conversationbase.utils.XIKeyBoardUtils;
import com.ldx.conversationbase.utils.XISimpleAnimUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public abstract class BasePopupWindow implements razerdp.basepopup.BasePopup, PopupWindow.OnDismissListener, PopupController {
    private static final String TAG = "BasePopupWindow";

    private PopupWindowProxy mPopupWindow;

    private View mPopupView;
    private WeakReference<Context> mContext;
    protected View mAnimaView;
    protected View mDismissView;

    private boolean autoShowInputMethod = false;
    private OnDismissListener mOnDismissListener;
    private OnBeforeShowCallback mOnBeforeShowCallback;

    private Animation mShowAnimation;
    private Animator mShowAnimator;
    private Animation mExitAnimation;
    private Animator mExitAnimator;

    private boolean isExitAnimaPlaying = false;
    private boolean needPopupFadeAnima = true;

    private int popupGravity = Gravity.NO_GRAVITY;
    private int offsetX;
    private int offsetY;
    private int popupViewWidth;
    private int popupViewHeight;
    private int[] mAnchorViewLocation;
    private boolean relativeToAnchorView;
    private boolean isAutoLocatePopup;
    private boolean showAtDown;
    private boolean dismissWhenTouchOuside;

    private int popupLayoutid;

    public BasePopupWindow(Context context) {
        initView(context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public BasePopupWindow(Context context, int w, int h) {
        initView(context, w, h);
    }

    private void initView(Context context, int w, int h) {
        mContext = new WeakReference<Context>(context);

        mPopupView = onCreatePopupView();
        mAnimaView = initAnimaView();
        if (mAnimaView != null) {
            popupLayoutid = mAnimaView.getId();
        }
        checkPopupAnimaView();

        mPopupWindow = new PopupWindowProxy(mPopupView, w, h, this);
        mPopupWindow.setOnDismissListener(this);
        setDismissWhenTouchOuside(true);

        preMeasurePopupView(w, h);

        setNeedPopupFade(Build.VERSION.SDK_INT <= 22);

        mDismissView = getClickToDismissView();
        if (mDismissView != null && !(mDismissView instanceof AdapterView)) {
            mDismissView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        if (mAnimaView != null && !(mAnimaView instanceof AdapterView)) {
            mAnimaView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        mShowAnimation = initShowAnimation();
        mShowAnimator = initShowAnimator();
        mExitAnimation = initExitAnimation();
        mExitAnimator = initExitAnimator();

        mAnchorViewLocation = new int[2];
    }

    private void checkPopupAnimaView() {

        if (mPopupView != null && mAnimaView != null && mPopupView == mAnimaView) {
            try {
                mPopupView = new FrameLayout(getContext());
                if (popupLayoutid == 0) {
                    ((FrameLayout) mPopupView).addView(mAnimaView);
                } else {
                    mAnimaView = View.inflate(getContext(), popupLayoutid, (FrameLayout) mPopupView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void preMeasurePopupView(int w, int h) {
        if (mPopupView != null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                int contentViewHeight = ViewGroup.LayoutParams.MATCH_PARENT;
                final ViewGroup.LayoutParams layoutParams = mPopupView.getLayoutParams();
                if (layoutParams != null && layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    contentViewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
                ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, contentViewHeight);
                mPopupView.setLayoutParams(p);
            }
            mPopupView.measure(w, h);
            popupViewWidth = mPopupView.getMeasuredWidth();
            popupViewHeight = mPopupView.getMeasuredHeight();
            mPopupView.setFocusableInTouchMode(true);
        }
    }

    protected abstract Animation initShowAnimation();

    public abstract View getClickToDismissView();

    protected Animator initShowAnimator() {
        return null;
    }

    public EditText getInputView() {
        return null;
    }

    protected Animation initExitAnimation() {
        return null;
    }

    protected Animator initExitAnimator() {
        return null;
    }

    public void setNeedPopupFade(boolean needPopupFadeAnima) {
        this.needPopupFadeAnima = needPopupFadeAnima;
        setPopupAnimaStyle(needPopupFadeAnima ? R.style.PopupAnimaFade : 0);
    }

    public boolean isNeedPopupFade() {
        return needPopupFadeAnima;
    }

    public void setPopupAnimaStyle(int animaStyleRes) {
        mPopupWindow.setAnimationStyle(animaStyleRes);
        mPopupWindow.update();
    }

    public void showPopupWindow() {
        if (checkPerformShow(null)) {
            this.showAtDown = false;
            this.relativeToAnchorView = false;
            tryToShowPopup(null);
        }
    }

    public void showPopupWindow(int anchorViewResid) {
        Context context = getContext();
        assert context != null : "context is null";
        if (context instanceof Activity) {
            View v = ((Activity) context).findViewById(anchorViewResid);
            showPopupWindow(v);
        } else {
            Log.e(TAG, "can not get token from context,make sure that context is instance of activity");
        }
    }

    public void showPopupWindow(View v) {
        if (checkPerformShow(v)) {
            this.showAtDown = true;
            this.relativeToAnchorView = true;
            tryToShowPopup(v);
        }
    }

    private void tryToShowPopup(View v) {
        try {
            int offset[];
            if (v != null) {
                offset = calcuateOffset(v);
                if (showAtDown) {
                    mPopupWindow.showAsDropDown(v, offset[0], offset[1]);
                } else {
                    mPopupWindow.showAtLocation(v, popupGravity, offset[0], offset[1]);
                }
            } else {
                Context context = getContext();
                assert context != null : "context is null ! please make sure your activity is not be destroyed";
                if (context instanceof Activity) {
                    mPopupWindow.showAtLocation(((Activity) context).findViewById(android.R.id.content), popupGravity, offsetX, offsetY);
                } else {
                    Log.e(TAG, "can not get token from context,make sure that context is instance of activity");
                }
            }
            if (mShowAnimation != null && mAnimaView != null) {
                mAnimaView.clearAnimation();
                mAnimaView.startAnimation(mShowAnimation);
            }
            if (mShowAnimation == null && mShowAnimator != null && mAnimaView != null) {
                mShowAnimator.start();
            }

            if (autoShowInputMethod && getInputView() != null) {
                getInputView().requestFocus();
                XIKeyBoardUtils.showInputMethod(getInputView(), 150);
            }
        } catch (Exception e) {
            Log.e(TAG, "show error\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private int[] calcuateOffset(View anchorView) {
        int[] offset = {offsetX, offsetY};
        anchorView.getLocationOnScreen(mAnchorViewLocation);
        if (isAutoLocatePopup) {
            final boolean onTop = (getScreenHeight() - (mAnchorViewLocation[1] + offset[1]) < getHeight());
            if (onTop) {
                offset[1] = -anchorView.getHeight() - getHeight() - offset[1];
                showOnTop(mPopupView);
            } else {
                showOnDown(mPopupView);
            }
        }
        return offset;
    }

    public void setAdjustInputMethod(boolean needAdjust) {
        setAdjustInputMethod(needAdjust, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void setAdjustInputMethod(boolean needAdjust, int flag) {
        if (needAdjust) {
            mPopupWindow.setSoftInputMode(flag);
        } else {
            mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
    }

    public void setAutoShowInputMethod(boolean autoShow) {
        this.autoShowInputMethod = autoShow;
        if (autoShow) {
            setAdjustInputMethod(true);
        } else {
            setAdjustInputMethod(false);
        }
    }

    public void setBackPressEnable(boolean backPressEnable) {
        if (backPressEnable) {
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        } else {
            mPopupWindow.setBackgroundDrawable(null);
        }
    }

    public View createPopupById(int resId) {
        if (resId != 0) {
            popupLayoutid = resId;
            return LayoutInflater.from(getContext()).inflate(resId, null);
        } else {
            return null;
        }
    }

    protected View findViewById(int id) {
        if (mPopupView != null && id != 0) {
            return mPopupView.findViewById(id);
        }
        return null;
    }

    public void setPopupWindowFullScreen(boolean needFullScreen) {
        fitPopupWindowOverStatusBar(needFullScreen);
    }

    protected void setViewClickListener(View.OnClickListener listener, View... views) {
        for (View view : views) {
            if (view != null && listener != null) {
                view.setOnClickListener(listener);
            }
        }
    }

    private void fitPopupWindowOverStatusBar(boolean needFullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(true);
                mLayoutInScreen.set(mPopupWindow, needFullScreen);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    public OnBeforeShowCallback getOnBeforeShowCallback() {
        return mOnBeforeShowCallback;
    }

    public void setOnBeforeShowCallback(OnBeforeShowCallback mOnBeforeShowCallback) {
        this.mOnBeforeShowCallback = mOnBeforeShowCallback;
    }

    public void setShowAnimation(Animation showAnimation) {
        if (mShowAnimation != null && mAnimaView != null) {
            mAnimaView.clearAnimation();
            mShowAnimation.cancel();
        }
        if (showAnimation != mShowAnimation) {
            mShowAnimation = showAnimation;
        }
    }

    public Animation getShowAnimation() {
        return mShowAnimation;
    }

    public void setShowAnimator(Animator showAnimator) {
        if (mShowAnimator != null) mShowAnimator.cancel();
        if (showAnimator != mShowAnimator) {
            mShowAnimator = showAnimator;
        }
    }

    public Animator getShowAnimator() {
        return mShowAnimator;
    }

    public void setExitAnimation(Animation exitAnimation) {
        if (mExitAnimation != null && mAnimaView != null) {
            mAnimaView.clearAnimation();
            mExitAnimation.cancel();
        }
        if (exitAnimation != mExitAnimation) {
            mExitAnimation = exitAnimation;
        }
    }

    public Animation getExitAnimation() {
        return mExitAnimation;
    }

    public void setExitAnimator(Animator exitAnimator) {
        if (mExitAnimator != null) mExitAnimator.cancel();
        if (exitAnimator != mExitAnimator) {
            mExitAnimator = exitAnimator;
        }
    }

    public Animator getExitAnimator() {
        return mExitAnimator;
    }

    public Context getContext() {
        return mContext == null ? null : mContext.get();
    }

    public View getPopupWindowView() {
        return mPopupView;
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getPopupGravity() {
        return popupGravity;
    }

    public void setPopupGravity(int popupGravity) {
        this.popupGravity = popupGravity;
    }

    public boolean isRelativeToAnchorView() {
        return relativeToAnchorView;
    }

    public void setRelativeToAnchorView(boolean relativeToAnchorView) {
        setShowAtDown(true);
        this.relativeToAnchorView = relativeToAnchorView;
    }

    public boolean isAutoLocatePopup() {
        return isAutoLocatePopup;
    }

    public void setAutoLocatePopup(boolean autoLocatePopup) {
        setShowAtDown(true);
        isAutoLocatePopup = autoLocatePopup;
    }

    public int getHeight() {
        int height = mPopupView.getHeight();
        return height <= 0 ? popupViewHeight : height;
    }

    public int getWidth() {
        int width = mPopupView.getWidth();
        return width <= 0 ? popupViewWidth : width;
    }

    public boolean isShowAtDown() {
        return showAtDown;
    }

    public void setShowAtDown(boolean showAtDown) {
        this.showAtDown = showAtDown;
    }

    public void setDismissWhenTouchOuside(boolean dismissWhenTouchOuside) {
        this.dismissWhenTouchOuside = dismissWhenTouchOuside;
        if (dismissWhenTouchOuside) {
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        } else {
            mPopupWindow.setFocusable(false);
            mPopupWindow.setOutsideTouchable(false);
            mPopupWindow.setBackgroundDrawable(null);
        }
    }

    public boolean isDismissWhenTouchOuside() {
        return dismissWhenTouchOuside;
    }

    public void dismiss() {
        try {
            mPopupWindow.dismiss();
        } catch (Exception e) {
            Log.e(TAG, "dismiss error");
        }
    }

    @Override
    public boolean onBeforeDismiss() {
        return checkPerformDismiss();
    }

    @Override
    public boolean callDismissAtOnce() {
        boolean hasAnima = false;
        if (mExitAnimation != null && mAnimaView != null) {
            if (!isExitAnimaPlaying) {
                mExitAnimation.setAnimationListener(mAnimationListener);
                mAnimaView.clearAnimation();
                mAnimaView.startAnimation(mExitAnimation);
                isExitAnimaPlaying = true;
                hasAnima = true;
            }
        } else if (mExitAnimator != null) {
            if (!isExitAnimaPlaying) {
                mExitAnimator.removeListener(mAnimatorListener);
                mExitAnimator.addListener(mAnimatorListener);
                mExitAnimator.start();
                isExitAnimaPlaying = true;
                hasAnima = true;
            }
        }
        //如果有动画，则不立刻执行dismiss
        return !hasAnima;
    }

    public void dismissWithOutAnima() {
        if (!checkPerformDismiss()) return;
        try {
            if (mExitAnimation != null && mAnimaView != null) mAnimaView.clearAnimation();
            if (mExitAnimator != null) mExitAnimator.removeAllListeners();
            mPopupWindow.callSuperDismiss();
        } catch (Exception e) {
            Log.e(TAG, "dismiss error");
        }
    }

    private boolean checkPerformDismiss() {
        boolean callDismiss = true;
        if (mOnDismissListener != null) {
            callDismiss = mOnDismissListener.onBeforeDismiss();
        }
        return callDismiss && !isExitAnimaPlaying;
    }

    private boolean checkPerformShow(View v) {
        boolean result = true;
        if (mOnBeforeShowCallback != null) {
            result = mOnBeforeShowCallback.onBeforeShow(mPopupView, v, this.mShowAnimation != null || this.mShowAnimator != null);
        }
        return result;
    }

    private Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            isExitAnimaPlaying = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mPopupWindow.callSuperDismiss();
            isExitAnimaPlaying = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            isExitAnimaPlaying = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isExitAnimaPlaying = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mPopupWindow.callSuperDismiss();
            isExitAnimaPlaying = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    protected Animation getTranslateAnimation(int start, int end, int durationMillis) {
        return XISimpleAnimUtil.getTranslateAnimation(start, end, durationMillis);
    }

    protected Animation getScaleAnimation(float fromX,
                                          float toX,
                                          float fromY,
                                          float toY,
                                          int pivotXType,
                                          float pivotXValue,
                                          int pivotYType,
                                          float pivotYValue) {
        return XISimpleAnimUtil.getScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    protected Animation getDefaultScaleAnimation() {
        return XISimpleAnimUtil.getDefaultScaleAnimation();
    }

    protected Animation getDefaultAlphaAnimation() {
        return XISimpleAnimUtil.getDefaultAlphaAnimation();
    }

    protected AnimatorSet getDefaultSlideFromBottomAnimationSet() {
        return XISimpleAnimUtil.getDefaultSlideFromBottomAnimationSet(mAnimaView);
    }

    public int getScreenHeight() {
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    public int getScreenWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    //------------------------------------------callback-----------------------------------------------
    protected void showOnTop(View mPopupView) {

    }

    protected void showOnDown(View mPopupView) {

    }

    @Override
    public void onDismiss() {
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }
        isExitAnimaPlaying = false;
    }

    //------------------------------------------Interface-----------------------------------------------
    public interface OnBeforeShowCallback {
        /**
         * <b>return ture for perform show</b>
         *
         * @param popupRootView The rootView of popup,it's usually be your layout
         * @param anchorView    The anchorView whitch popup show
         * @param hasShowAnima  Check if show your popup with anima?
         * @return
         */
        boolean onBeforeShow(View popupRootView, View anchorView, boolean hasShowAnima);
    }

    public static abstract class OnDismissListener implements PopupWindow.OnDismissListener {
        /**
         * <b>return ture for perform dismiss</b>
         *
         * @return
         */
        public boolean onBeforeDismiss() {
            return true;
        }
    }
}
