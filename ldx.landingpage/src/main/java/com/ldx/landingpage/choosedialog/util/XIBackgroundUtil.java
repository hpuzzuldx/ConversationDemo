package com.ldx.landingpage.choosedialog.util;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Arrays;

public class XIBackgroundUtil {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void background(View v, Drawable d) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(d);
        } else {
            v.setBackground(d);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void background(View v, Bitmap b) {
        background(v, new BitmapDrawable(v.getResources(), b));
    }

    public static void setDimAmount(float dim, Dialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            dialog.getWindow().setDimAmount(dim);
        } else {
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = dim;
            dialog.getWindow().setAttributes(lp);
        }
    }

    public static void gone(View v, boolean gone) {
        if (gone) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
    }

    public static void setIcon(TextView tv, Drawable icon, int gravity) {
        Drawable left;
        Drawable right = null;
        Drawable bottom = null;
        Drawable top = null;
        if (icon != null) {
            icon.setBounds(0, 0, 120, 120);
        }

        if (gravity > 0) {
            left = gravity == Gravity.LEFT ? icon : null;
            right = gravity == Gravity.RIGHT ? icon : null;
            bottom = gravity == Gravity.BOTTOM ? icon : null;
            top = gravity == Gravity.TOP ? icon : null;
        } else {
            left = icon;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //  tv.setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom);
            tv.setCompoundDrawables(left, top, right, bottom);
        } else {
            //  tv.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
            tv.setCompoundDrawables(left, top, right, bottom);
        }

        // if (bottom + top != 0) {
        // tv.setGravity(Gravity.CENTER);
        //  }
    }

    public static Drawable getAdaptiveRippleDrawable(int normalColor) {
        //根据颜色生成背景drawable
        return new ColorDrawable(normalColor);
    }

    private static Drawable getRippleMask(int color) {
        float[] outerRadii = new float[8];
        // 3 is radius of final ripple,
        // instead of 3 you can give required final radius
        Arrays.fill(outerRadii, 3);

        RoundRectShape r = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(r);
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    public static int darker(int color) {
        int r = Color.red(color);
        int b = Color.blue(color);
        int g = Color.green(color);

        return Color.rgb((int) (r * .9), (int) (g * .9), (int) (b * .9));
    }

}