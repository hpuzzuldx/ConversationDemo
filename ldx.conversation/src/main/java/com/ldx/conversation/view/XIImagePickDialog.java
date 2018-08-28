package com.ldx.conversation.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ldx.conversationbase.utils.XIScreenUtil;

public class XIImagePickDialog extends Dialog {

    private int height, width, gravity;
    private boolean cancelTouchOut;
    private View view;

    private XIImagePickDialog(Builder builder) {
        super(builder.context);
        init(builder);
    }

    private XIImagePickDialog(Builder builder, int resStyle) {
        super(builder.context, resStyle);
        init(builder);
    }

    private void init(Builder builder){
        height = builder.height;
        width = builder.width;
        gravity = builder.gravity;
        cancelTouchOut = builder.cancelTouchOut;
        view = builder.mView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(view);

        setCanceledOnTouchOutside(cancelTouchOut);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = gravity;
        lp.height = height;
        lp.width = width;
        win.setAttributes(lp);
    }

    public static final class Builder {

        private Context context;
        private int height, width, gravity = Gravity.CENTER;
        private boolean cancelTouchOut = true;
        private View mView;
        private int resStyle = -1;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder view(int resView) {
            mView = LayoutInflater.from(context).inflate(resView, null);
            return this;
        }

        public Builder view(View view) {
            mView = view;
            return this;
        }

        public Builder gravity(int val) {
            gravity = val;
            return this;
        }

        public Builder height(int val) {
            height = val;
            return this;
        }

        public Builder width(int val) {
            width = val;
            return this;
        }

        public Builder heightDp(int val) {
            height = XIScreenUtil.dip2px(context, val);
            return this;
        }

        public Builder widthDp(int val) {
            width = XIScreenUtil.dip2px(context, val);
            return this;
        }

        public Builder heightDimenRes(int dimenRes) {
            height = context.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        public Builder widthDimenRes(int dimenRes) {
            width = context.getResources().getDimensionPixelOffset(dimenRes);
            return this;
        }

        public Builder style(int resStyle) {
            this.resStyle = resStyle;
            return this;
        }

        public Builder cancelTouchOut(boolean val) {
            cancelTouchOut = val;
            return this;
        }

        public Builder addViewOnclick(int viewRes,View.OnClickListener listener){
            mView.findViewById(viewRes).setOnClickListener(listener);
            return this;
        }

        public XIImagePickDialog build() {
            if (resStyle != -1) {
                return new XIImagePickDialog(this, resStyle);
            } else {
                return new XIImagePickDialog(this);
            }
        }
    }
}
