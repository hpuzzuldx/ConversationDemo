package com.ldx.conversationbase.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class XIScreenUtil {

	private static int screenWidth = 0;

	private static int screenHeight = 0;
	private static int screenTotalHeight = 0;
	private static int statusBarHeight = 0;

	private static final int TITLE_HEIGHT = 0;

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		return screenWidth;
	}

	public static int getScreenHeight(Context context) {
		int top = 0;
		if (context instanceof Activity) {
			top = ((Activity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
			if (top == 0) {
				top = (int) (TITLE_HEIGHT * getScreenDensity(context));
			}
		}
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		screenHeight = dm.heightPixels - top;
		return screenHeight;
	}

	public static int getScreenTotalHeight(Context context) {
		if (screenTotalHeight != 0) {
			return screenTotalHeight;
		}
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		screenTotalHeight = displayMetrics.heightPixels;
		return screenTotalHeight;
	}

	public static float getScreenDensity(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		return metric.density;
	}

	public static float getScreenDensityDpi(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		return metric.densityDpi;
	}
	public static int getStatusBarHeight(Context context) {
		if (statusBarHeight != 0) {
			return statusBarHeight;
		}
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}
	public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

	//get screen height and include virtual keyboard height
	public static int getScreenHeightIncludeKeyboard(Context context){
		int dpi = 0;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		@SuppressWarnings("rawtypes")
		Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
			Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
			method.invoke(display, displayMetrics);
			dpi=displayMetrics.heightPixels;
		}catch(Exception e){
			e.printStackTrace();
		}
		return dpi;
	}

	/**
	 * get bottom navigator height
	 * @param context
	 * @return
	 */
	public static  int getBottomStatusHeight(Context context){
		int totalHeight = getScreenHeightIncludeKeyboard(context);

		int contentHeight = getScreenHeight(context);

		return totalHeight  - contentHeight;
	}

	/**
	 * title bar height
	 * @return
	 * setContentView layout
	 */
	public static int getTitleHeight(Activity activity){
		return  activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
	}

	/**
	 * get status bar height
	 *
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context)
	{

		int statusHeight = -1;
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return statusHeight;
	}


	public static int getNavigatorHeight(Context context) {
		int result = 0;
		int resourceId=0;
		int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
		if (rid!=0){
			resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
			return context.getResources().getDimensionPixelSize(resourceId);
		}else
			return 0;
	}

	public static  int getNavigateHeight(Context mContext) {
		int resourceId;
		try{
			if (checkDeviceHasNavigationBar(mContext)){
				resourceId = mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
				return mContext.getResources().getDimensionPixelSize(resourceId);
			} else {
				return 0;
			}
		}catch (Exception e){
			e.printStackTrace();
			return 0;
		}
	}

	//have NavigationBar or not
	public static  boolean checkDeviceHasNavigationBar(Context mContext) {
		boolean hasNavigationBar = false;
		Resources rs = mContext.getResources();
		int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavigationBar = rs.getBoolean(id);
		}
		try {
			Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
			Method m = systemPropertiesClass.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
			if ("1".equals(navBarOverride)) {
				hasNavigationBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavigationBar = true;
			}
		} catch (Exception e) {

		}
		return hasNavigationBar;
	}
}