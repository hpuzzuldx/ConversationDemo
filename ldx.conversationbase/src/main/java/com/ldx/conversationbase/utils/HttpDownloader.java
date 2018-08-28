package com.ldx.conversationbase.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpDownloader {
    public static void downloadFileFromUrl(String downloadUrl, String fileStr) {
        try {
            File file = new File(fileStr);
            if (file.exists()) {
                file.delete();
            }
            URL url = new URL(downloadUrl);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(fileStr);
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
