package com.ldx.landingpage.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;

import com.ldx.landingpage.utils.XILogUtil;

import java.util.ArrayList;
import java.util.List;

public class XICameraUtils {
    private static final String TAG = "XICameraUtils";

    public static void setPreviewParams(Point surfaceSize, Camera.Parameters parameters) {
        if (surfaceSize.x <= 0 || surfaceSize.y <= 0 || parameters == null) {
            return;
        }

        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = findProperSize(surfaceSize, previewSizeList);
        if (previewSize != null) {
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            XILogUtil.d(TAG, "previewSize: width: " + previewSize.width + ", height: " + previewSize.height);
        }

        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        Camera.Size pictureSize = findProperSize(surfaceSize, pictureSizeList);
        if (pictureSize != null) {
            parameters.setPictureSize(pictureSize.width, pictureSize.height);
            XILogUtil.d(TAG, "pictureSize: width: " + pictureSize.width + ", height: " + pictureSize.height);
        }

        List<String> focusModeList = parameters.getSupportedFocusModes();
        if (focusModeList != null && focusModeList.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        List<Integer> pictureFormatList = parameters.getSupportedPictureFormats();
        if (pictureFormatList != null && pictureFormatList.contains(ImageFormat.JPEG)) {
            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setJpegQuality(100);
        }
    }

    private static Camera.Size findProperSize(Point surfaceSize, List<Camera.Size> sizeList) {
        if (surfaceSize.x <= 0 || surfaceSize.y <= 0 || sizeList == null) {
            return null;
        }

        int surfaceWidth = surfaceSize.x;
        int surfaceHeight = surfaceSize.y;

        List<List<Camera.Size>> ratioListList = new ArrayList<>();
        for (Camera.Size size : sizeList) {
            addRatioList(ratioListList, size);
        }

        final float surfaceRatio = (float) surfaceWidth / surfaceHeight;
        List<Camera.Size> bestRatioList = null;
        float ratioDiff = Float.MAX_VALUE;
        for (List<Camera.Size> ratioList : ratioListList) {
            float ratio = (float) ratioList.get(0).width / ratioList.get(0).height;
            float newRatioDiff = Math.abs(ratio - surfaceRatio);
            if (newRatioDiff < ratioDiff) {
                bestRatioList = ratioList;
                ratioDiff = newRatioDiff;
            }
        }

        Camera.Size bestSize = null;
        int diff = Integer.MAX_VALUE;
        assert bestRatioList != null;
        for (Camera.Size size : bestRatioList) {
            int newDiff = Math.abs(size.width - surfaceWidth) + Math.abs(size.height - surfaceHeight);
            if (size.height >= surfaceHeight && newDiff < diff) {
                bestSize = size;
                diff = newDiff;
            }
        }

        if (bestSize != null) {
            return bestSize;
        }

        diff = Integer.MAX_VALUE;
        for (Camera.Size size : bestRatioList) {
            int newDiff = Math.abs(size.width - surfaceWidth) + Math.abs(size.height - surfaceHeight);
            if (newDiff < diff) {
                bestSize = size;
                diff = newDiff;
            }
        }

        return bestSize;
    }

    private static void addRatioList(List<List<Camera.Size>> ratioListList, Camera.Size size) {
        float ratio = (float) size.width / size.height;
        for (List<Camera.Size> ratioList : ratioListList) {
            float mine = (float) ratioList.get(0).width / ratioList.get(0).height;
            if (ratio == mine) {
                ratioList.add(size);
                return;
            }
        }

        List<Camera.Size> ratioList = new ArrayList<>();
        ratioList.add(size);
        ratioListList.add(ratioList);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void setFocusArea(Point surfaceSize, Camera.Parameters parameters, float x, float y) {
        if (surfaceSize.x <= 0 || surfaceSize.y <= 0 || parameters == null) {
            return;
        }

        if (parameters.getMaxNumFocusAreas() > 0) {
            Rect focusRect = calculateTapArea(surfaceSize, x, y, 1f);
            List<Camera.Area> focusAreas = new ArrayList<>(1);
            focusAreas.add(new Camera.Area(focusRect, 800));
            parameters.setFocusAreas(focusAreas);
        }

        if (parameters.getMaxNumMeteringAreas() > 0) {
            Rect meteringRect = calculateTapArea(surfaceSize, x, y, 1.5f);
            List<Camera.Area> meteringAreas = new ArrayList<>(1);
            meteringAreas.add(new Camera.Area(meteringRect, 800));
            parameters.setMeteringAreas(meteringAreas);
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
    }

    private static Rect calculateTapArea(Point surfaceSize, float x, float y, float coefficient) {
        float focusAreaSize = 200;
        int areaSize = (int) (focusAreaSize * coefficient);
        int surfaceWidth = surfaceSize.x;
        int surfaceHeight = surfaceSize.y;
        int centerX = (int) (x / surfaceHeight * 2000 - 1000);
        int centerY = (int) (y / surfaceWidth * 2000 - 1000);
        int left = clamp(centerX - (areaSize / 2), -1000, 1000);
        int top = clamp(centerY - (areaSize / 2), -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);
        return new Rect(left, top, right, bottom);
    }

    private static int clamp(int x, int min, int max) {
        return Math.min(Math.max(x, min), max);
    }

    public static boolean setZoom(Point surfaceSize, Camera.Parameters parameters, float span) {
        if (surfaceSize.x <= 0 || surfaceSize.y <= 0 || parameters == null) {
            return false;
        }

        int maxZoom = parameters.getMaxZoom();
        int unit = surfaceSize.y / 5 / maxZoom;
        int zoom = (int) (span / unit);
        int lastZoom = parameters.getZoom();
        int currZoom = lastZoom + zoom;
        currZoom = clamp(currZoom, 0, maxZoom);
        parameters.setZoom(currZoom);
        return lastZoom != currZoom;
    }

    public static int calculateSensorRotation(float x, float y) {
        if (Math.abs(x) > 6 && Math.abs(y) < 4) {
            if (x > 6) {
                return 270;
            } else {
                return 90;
            }
        } else if (Math.abs(y) > 6 && Math.abs(x) < 4) {
            if (y > 6) {
                return 0;
            } else {
                return 180;
            }
        }

        return -1;
    }

    public static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}
