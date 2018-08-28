package com.ldx.conversationbase.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;

import com.ldx.conversationbase.common.XIChatConst;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class XIFileSaveUtil {
    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString() + XIChatConst.XIBASEPATH;

    public static final String voice_dir = SD_CARD_PATH + "/"+XIChatConst.XIVOICEDATAPATH;

    private boolean hasSD = false;
    private String FILESPATH;

    public static String getBasePath(Context context){
        return context.getFilesDir().getPath();
    }

    public static boolean isFileExists(File file) {
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    public static List<String> getFileName(String fileName) {
        List<String> fileList = new ArrayList<String>();
        String path = fileName;
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return null;
        }

        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (!fs.isDirectory()) {
                fileList.add(fs.getName());
            }
        }
        return fileList;
    }

    public static File createSDFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!isFileExists(file))
            if (file.isDirectory()) {
                file.mkdirs();
            } else {
                file.createNewFile();
            }
        return file;
    }

    public static File createSDDirectory(String fileName) throws IOException {
        File file = new File(fileName);
        if (!isFileExists(file))
            file.mkdirs();
        return file;
    }

    public synchronized static boolean writeBytes(String filePath, byte[] data,
                                                  boolean isAppend) {
        try {
            FileOutputStream fos;
            if (isAppend)
                fos = new FileOutputStream(filePath, true);
            else
                fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public synchronized static String readSDFile(String fileName) {
        StringBuffer sb = new StringBuffer();
        File f1 = new File(fileName);
        String str = null;
        try {
            InputStream is = new FileInputStream(f1);
            InputStreamReader input = new InputStreamReader(is, "UTF-8");
            @SuppressWarnings("resource")
            BufferedReader reader = new BufferedReader(input);
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getFILESPATH() {
        return FILESPATH;
    }

    public boolean hasSD() {
        return hasSD;
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        return dirFile.delete();
    }

    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWithFile(dir);
    }

    public static void deleteDirWithFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDirWithFile(file);
        }
        dir.delete();
    }

    public static boolean saveBitmap(Bitmap bm, String picName) {
        try {
            File f = new File(picName);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String encodeBase64File(String path) throws Exception {
        byte[] videoBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        @SuppressWarnings("resource")
        FileInputStream fis = new FileInputStream(new File(path));
        byte[] buf = new byte[1024];
        int n;
        while (-1 != (n = fis.read(buf)))
            baos.write(buf, 0, n);
        videoBytes = baos.toByteArray();
        return Base64.encodeToString(videoBytes, Base64.NO_WRAP);
    }

   //gallery path to truth path
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isOverKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isOverKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    @SuppressLint("NewApi")
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static void getWidthAndHeight(Context context,double width, double height,int [] whwidth) {
        double mwidth = 350;
        double mheight = 300;
        try {
            if(height != 0){
                double maxWidth = getBitmapWidth(context);
                double maxHeight = getBitmapHeight(context);
                double minWidth = getScreenWidth(context) / 3;
                double minHeight = getScreenHeight(context) / 5;
                double firWidth = width;
                double firHeight = height;
                if (firWidth < minWidth){
                    firHeight = (minWidth/firWidth)*firHeight;
                    firWidth = minWidth;
                }

                double secWidth = firWidth;
                double secHeight = firHeight;

                if (firHeight < minHeight){
                    firWidth = (minHeight/firHeight)*secWidth;
                    firHeight = minHeight;
                }

                width = firWidth;
                height = firHeight;

                if (width < maxWidth){
                    if (height < maxHeight){
                        mwidth = width;
                        mheight = height;
                    }else{
                        double tWidht = width;
                        double tHeight = height;
                        mheight = maxHeight;
                        mwidth = (maxHeight/tHeight)*tWidht;
                    }
                }else{
                    double tWidht = width;
                    double tHeight = height;
                    double t2width = maxWidth;
                    double t2height = (maxWidth/tWidht)*tHeight;
                    if (t2height < maxHeight){
                        mheight = t2height;
                        mwidth = t2width;
                    }else;{
                        double t3Widht = t2width;
                        double t3Height =t2height;
                        mheight = maxHeight;
                        mwidth = (maxHeight/t3Height)*t3Widht;
                    }
                }

            }else{
               mwidth = 350;
                mheight = 300;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        whwidth[0]= (int)mwidth;
        whwidth[1]= (int)mheight;
    }

    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public static int getBitmapWidth(Context context) {
        return getScreenWidth(context)  * 3/ 5;
    }

    public static int getBitmapHeight(Context context) {
        return getScreenHeight(context)/3;
    }

    public static int[] getImageWidthHeight(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); 
        return new int[]{options.outWidth,options.outHeight};
    }

}