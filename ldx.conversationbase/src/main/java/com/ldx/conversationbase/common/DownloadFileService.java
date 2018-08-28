package com.ldx.conversationbase.common;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.ldx.conversationbase.utils.HttpDownloader;
import com.ldx.conversationbase.utils.XIPictureUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class DownloadFileService implements Runnable {
    private String url;
    private Context context;
    private ImageDownLoadCallBack callBack;
    private String imageOrigiPath;
    private String imagePath;
    private File currentFile;
    private long beanid;

    public DownloadFileService(Context context, String url, String mimageOrigiPath, String mimagePath, long dbid, ImageDownLoadCallBack callBack) {
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
        boolean isgif = false;
        try {
            File loadFile =  Glide
                    .with(context)
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();

            //other loadfile
          /*  String fileStr = XIPictureUtil.getOriginalImgSavePicPath(context);
            HttpDownloader.downloadFileFromUrl(url,fileStr);
            File loadFile = new File(fileStr);*/

            isgif = getType(loadFile.getAbsolutePath());
            if (isgif){
                if (loadFile != null){
                    currentFile = new File(imageOrigiPath);
                    copyFileUsingFileChannels(loadFile, currentFile);
                }
            }else{
                bitmap = XIPictureUtil.getBitmap(loadFile.getAbsolutePath());
                if (bitmap != null) {
                    saveImageToPath(context, bitmap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           if (isgif){
              if (currentFile.exists()){
                  callBack.onDownloadSuccess(beanid,currentFile.getAbsolutePath(),currentFile.getAbsolutePath(),true);
              }else{
                  callBack.onDownLoadFailed();
              }
           }else{
               if (bitmap != null && currentFile.exists()) {
                   callBack.onDownloadSuccess(beanid,currentFile.getAbsolutePath(),currentFile.getAbsolutePath(),false);
               } else {
                   callBack.onDownLoadFailed();
               }
           }
        }
    }

    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
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
        void onDownloadSuccess(long beanid, String oripath, String imgpath,boolean isgif);
        void onDownLoadFailed();
    }

    private boolean isGifFile(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int[] flags = new int[5];
            flags[0] = inputStream.read();
            flags[1] = inputStream.read();
            flags[2] = inputStream.read();
            flags[3] = inputStream.read();
            inputStream.skip(inputStream.available() - 1);
            flags[4] = inputStream.read();
            inputStream.close();
            return flags[0] == 71 && flags[1] == 73 && flags[2] == 70 && flags[3] == 56 && flags[4] == 0x3B;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public  boolean getType(String filePath) throws IOException {

        String fileHead = getFileContent(filePath);
        if (fileHead == null || fileHead.length() == 0) {
            return false;
        }

        fileHead = fileHead.toUpperCase();
        if (fileHead.startsWith("47494638")) {
                return true;
        }
        return false;
    }
    private  String getFileContent(String filePath) throws IOException {

        byte[] b = new byte[28];
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            inputStream.read(b, 0, 28);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        return bytesToHexString(b);
    }

    private  String bytesToHexString(byte[] b){

        StringBuilder stringBuilder = new StringBuilder();
        if (b == null || b.length <= 0) {
            return null;
        }
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xFF;
            String str = Integer.toHexString(v);
            if (str.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }
}

