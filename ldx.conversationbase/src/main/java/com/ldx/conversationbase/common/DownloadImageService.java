package com.ldx.conversationbase.common;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadImageService implements Runnable {
    private String url;
    private Context context;
    private ImageDownLoadCallBack callBack;
    private String imageOrigiPath;
    private String imagePath;
    private File currentFile;
    private long beanid;

    public DownloadImageService(Context context, String url, String mimageOrigiPath, String mimagePath,long dbid, ImageDownLoadCallBack callBack) {
        this.url = url;
        this.callBack = callBack;
        this.context = context;
        this.imageOrigiPath = mimageOrigiPath;
        this.imagePath = mimagePath;
        this.beanid = dbid;
    }

    @Override
    public void run() {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            if (bitmap != null) {
                saveImageToPath(context, bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null && currentFile.exists()) {
                callBack.onDownloadSuccess(beanid,imageOrigiPath,imageOrigiPath);
            } else {
                callBack.onDownLoadFailed();
            }
        }
    }

    public void saveImageToPath(Context context, Bitmap bmp) {
        // save image
        currentFile = new File(imageOrigiPath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(currentFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ImageDownLoadCallBack {
        void onDownloadSuccess(long beanid, String oripath, String imgpath);
        void onDownLoadFailed();
    }
}

