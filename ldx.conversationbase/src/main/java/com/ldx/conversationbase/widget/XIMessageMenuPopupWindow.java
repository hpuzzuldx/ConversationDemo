package com.ldx.conversationbase.widget;

/**
 * Created by v-doli1 on 2017/9/15.
 */

import android.animation.Animator;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.ldx.conversationbase.R;
import com.ldx.conversationbase.common.XIChatConst;
import com.ldx.conversationbase.db.XIChatMessageBean;
import com.ldx.conversationbase.db.XIChatMessageInfo;
import com.ldx.conversationbase.listener.XIChatMessageDefaultLongClickListener;

import com.ldx.conversationbase.utils.basepopup.BasePopupWindow;

public class XIMessageMenuPopupWindow extends BasePopupWindow implements View.OnClickListener {

    private TextView mCopyText;
    private TextView mShareText;
    private boolean showCopy;
    private int screenWidth;
    private int screenHeight;
    public XIChatMessageDefaultLongClickListener xiChatMessageDefaultLongClickListener;
    public XIChatMessageDefaultLongClickListener xiPopmenuChatMessageDefaultLongClickListener;
    public XIChatMessageBean mtbub;

    public void setData(XIChatMessageBean tbub) {
        if (mtbub != null) mtbub = null;
        mtbub = tbub;
    }

    public void setShowCopy(boolean tshowcopy) {
        showCopy = tshowcopy;
    }

    public XIMessageMenuPopupWindow(Activity context, XIChatMessageDefaultLongClickListener mxiChatMessageDefaultLongClickListener, XIChatMessageDefaultLongClickListener mPopMenuChatMessageDefaultLongClickListener) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showCopy = true;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        mCopyText = (TextView) findViewById(R.id.xiconversation_popupmesg_copy);
        mCopyText.setOnClickListener(this);
        findViewById(R.id.xiconversation_popupmesg_share).setOnClickListener(this);
        findViewById(R.id.xiconversation_popupmesg_delete).setOnClickListener(this);
        xiChatMessageDefaultLongClickListener = mxiChatMessageDefaultLongClickListener;
        xiPopmenuChatMessageDefaultLongClickListener = mPopMenuChatMessageDefaultLongClickListener;
    }

    @Override
    protected Animation initShowAnimation() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(getScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0));
        set.addAnimation(getDefaultAlphaAnimation());
        return set;
    }

    @Override
    public Animator initShowAnimator() {
        return null;
    }

    @Override
    public void showPopupWindow(View v) {
        if (!showCopy && mCopyText != null) {
            mCopyText.setVisibility(View.GONE);
        } else if (mCopyText != null) {
            mCopyText.setVisibility(View.VISIBLE);
        }
        int[] viewLocation = new int[2];
        v.getLocationOnScreen(viewLocation);

        if (viewLocation[0] + v.getWidth() < (screenWidth / 2)) {
            setOffsetX(-(getWidth() - v.getWidth() - getWidth() / 2));
        } else {
            setOffsetX(-(getWidth() - v.getWidth() / 2));
        }
        if (viewLocation[1] + v.getHeight() / 2 + getHeight() > screenHeight - 50) {
            setOffsetY(-v.getHeight() / 2 - getHeight());
        } else {
            setOffsetY(-v.getHeight() / 2);
        }
        //default location
        // setOffsetX(-(getWidth() - v.getWidth()));
        // setOffsetY(-v.getHeight() / 2);
        super.showPopupWindow(v);
    }

    @Override
    public View getClickToDismissView() {
        return null;
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.xiconversation_popupwindow_message);
    }

    @Override
    public View initAnimaView() {
        return getPopupWindowView().findViewById(R.id.xiconversation_popupmesg_contianer);
    }

    @Override
    public void onClick(View v) {
        XIChatMessageInfo xiChatMessageInfo = new XIChatMessageInfo();
        if (mtbub.getType() == XIChatConst.FROM_USER_MSG || mtbub.getType() == XIChatConst.TO_USER_MSG) {
            xiChatMessageInfo.setMessageID(mtbub.getId());
            xiChatMessageInfo.setMessageContent(mtbub.getUserContent());
            xiChatMessageInfo.setMessageChatTime(mtbub.getTime());
            xiChatMessageInfo.setMessageType(mtbub.getType());
        } else if (mtbub.getType() == XIChatConst.FROM_USER_IMG || mtbub.getType() == XIChatConst.TO_USER_IMG) {
            xiChatMessageInfo.setMessageID(mtbub.getId());
            xiChatMessageInfo.setMessageImageLocalPath(mtbub.getImageOriginal());
            xiChatMessageInfo.setMessageImageUrl(mtbub.getImageUrl());
            xiChatMessageInfo.setMessageType(mtbub.getType());
            xiChatMessageInfo.setMessageChatTime(mtbub.getTime());
        } else if (mtbub.getType() == XIChatConst.FROM_USER_VOICE || mtbub.getType() == XIChatConst.TO_USER_VOICE) {
            xiChatMessageInfo.setMessageID(mtbub.getId());
            xiChatMessageInfo.setMessageVoiceLength(mtbub.getUserVoiceTime());
            xiChatMessageInfo.setMessageVoiceUrl(mtbub.getUserVoiceUrl());
            xiChatMessageInfo.setMessageVoicePath(mtbub.getUserVoicePath());
            xiChatMessageInfo.setMessageType(mtbub.getType());
            xiChatMessageInfo.setMessageChatTime(mtbub.getTime());
        } else if (mtbub.getType() == XIChatConst.FROM_USER_MULTILE) {
            xiChatMessageInfo.setMessageID(mtbub.getId());
            xiChatMessageInfo.setMessageCardcoverurl(mtbub.getImageUrl());
            xiChatMessageInfo.setMessageCardDescription(mtbub.getCardDescription());
            xiChatMessageInfo.setMessageCardNewsTime(mtbub.getCreateTime());
            xiChatMessageInfo.setMessageCardTitle(mtbub.getCardTitle());
            xiChatMessageInfo.setMessageCardUrl(mtbub.getWebUrl());
            xiChatMessageInfo.setMessageType(mtbub.getType());
        }
        if (v.getId() == R.id.xiconversation_popupmesg_copy) {
            xiChatMessageDefaultLongClickListener.onMessageCopy(v, xiChatMessageInfo);
            xiPopmenuChatMessageDefaultLongClickListener.onMessageCopy(v, xiChatMessageInfo);
            dismiss();
        } else if (v.getId() == R.id.xiconversation_popupmesg_delete) {
            xiChatMessageDefaultLongClickListener.onMessageDelete(v, xiChatMessageInfo);
            xiPopmenuChatMessageDefaultLongClickListener.onMessageDelete(v, xiChatMessageInfo);
            dismiss();
        } else if (v.getId() == R.id.xiconversation_popupmesg_share) {
            xiChatMessageDefaultLongClickListener.onMessageShare(v, xiChatMessageInfo);
            xiPopmenuChatMessageDefaultLongClickListener.onMessageShare(v, xiChatMessageInfo);
            dismiss();
        }

    }
}