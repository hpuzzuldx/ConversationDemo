package com.ldx.conversationbase.utils;

import android.util.Log;

public class XIConLogUtil
{
    private XIConLogUtil()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;
    private static final String TAG = "xiconversation";

    public static void i(String msg)
    {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg)
    {
        if (isDebug)
            Log.v(TAG, msg);
    }

    public static void i(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void i(String tag, String msg,Throwable tr)
    {
        if (isDebug)
            Log.i(tag, msg,tr);
    }

    public static void d(String tag, String msg,Throwable tr)
    {
        if (isDebug)
            Log.i(tag, msg,tr);
    }

    public static void e(String tag, String msg,Throwable tr)
    {
        if (isDebug)
            Log.i(tag, msg,tr);
    }

    public static void v(String tag, String msg,Throwable tr)
    {
        if (isDebug)
            Log.i(tag, msg,tr);
    }
}