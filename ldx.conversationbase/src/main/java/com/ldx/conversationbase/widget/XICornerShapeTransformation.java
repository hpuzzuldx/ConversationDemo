package com.ldx.conversationbase.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.WindowManager;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

/**
 * Created by Mao Jiqing on 2016/11/4.
 * glide image load transformation
 */

public class XICornerShapeTransformation extends BitmapTransformation {

    private Context mContext;

    public XICornerShapeTransformation(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        double width = toTransform.getWidth();
        double height = toTransform.getHeight();
        if(height != 0){
            double maxWidth = getBitmapWidth();
            double maxHeight = getBitmapHeight();

            if (width < maxWidth){
                if (height < maxHeight){
                    width = width;
                    height = height;
                }else{
                    double tWidht = width;
                    double tHeight = height;
                    height = maxHeight;
                    width = (maxHeight/tHeight)*tWidht;
                }
            }else{
                double tWidht = width;
                double tHeight = height;
                double t2width = maxWidth;
                double t2height = (maxWidth/tWidht)*tHeight;
                if (t2height < maxHeight){
                    height = t2height;
                    width = t2width;
                }else;{
                    double t3Widht = t2width;
                    double t3Height =t2height;
                    height = maxHeight;
                    width = (maxHeight/t3Height)*t3Widht;
                }

            }
        }else{
            width = 350;
            height = 300;
        }


        Bitmap bitmap = pool.get((int)width, (int)height, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        Paint mPaint = new Paint();
        mPaint.setShader(new BitmapShader(toTransform, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        mPaint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, (int)width,(int)height);
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, 20, 20, mPaint);
       /* mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, mPaint);*/
        return bitmap;
    }

    @Override
    public String getId() {
        return "XICornerShapeTransformation" + System.currentTimeMillis()/1000;
    }

    @SuppressWarnings("deprecation")
    public int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    @SuppressWarnings("deprecation")
    public int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public int getBitmapWidth() {
        return getScreenWidth(mContext) *3/ 5;
    }

    public int getBitmapHeight() {
        return getScreenHeight(mContext) / 3;
    }
}