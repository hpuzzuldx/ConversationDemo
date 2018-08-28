package com.ldx.conversationbase.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Build;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by v-doli1 on 2017/9/28.
 */

public class XIClipboardUtil {
    public static void copy(Context context,String copyStr) {
        if (Build.VERSION.SDK_INT < 11) {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(copyStr);
        } else {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", copyStr);
            clipboardManager.setPrimaryClip(clipData);
        }
    }

    @SuppressLint("NewApi")
    public static String getPasteData(Context context) {
        int apiVersion = Build.VERSION.SDK_INT;
        if (apiVersion >= Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardMgr.hasPrimaryClip() &&
                    (clipboardMgr.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) ||
                            clipboardMgr.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML))) {
                ClipData.Item item = clipboardMgr.getPrimaryClip().getItemAt(0);
                if (item != null && item.getText() != null) {
                    return item.getText().toString();
                }
            }
        } else {
            android.text.ClipboardManager clipboardMgr = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            return clipboardMgr.getText().toString();
        }
        return "";
    }

    public static boolean filterPasteData(String data) {
        boolean filter = false;
        Pattern pattern = Pattern.compile("^(?i)http://.*|^(?i)ftp://.*|^(?i)https://.*|^(?i)www.*|^(?i)file://.*|^(?i)mailto://.*|-?[0-9]+.?[0-9]+|^[0-9]*-[0-9].*|^[0-9]*.[0-9].*|\\([0-9]*\\)[0-9]*");
        Matcher matcher = pattern.matcher(data);
        filter = matcher.matches();
        return filter;
    }
}