package com.ldx.landingpage.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author lidongxiu
 *
 */

public class XICameraUtil {
    private static final int REQUESTER = 1;

    //system choose dialog ,open camera need
    public static Uri getCameraUriForProvider(Context context, String authority, File file) {
        try {
            //7.0+
            return FileProvider.getUriForFile(context, authority, file);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param context
     * @param providerPackageName use provider authority get authority
     * @return
     */
    public static String getAuthority(Context context, String providerPackageName) {
        return context.getApplicationContext().getPackageName() + providerPackageName;
    }

    /**
     * @param context
     * @param providerPackageId String id   provider  authority  get  authority
     *                          $(applicationid)+".com.com.activ"
     * @return
     */
    public static String getAuthority(Context context, int providerPackageId) {
        return context.getApplicationContext().getPackageName() + context.getString(providerPackageId);
    }

    /**
     *
     *
     * @param context
     * @param filePath share dir
     * @param fileName image name
     * @param isExter  inner or external storage
     * @return
     */
    public static File cameraFile(Context context, String filePath, String fileName, boolean isExter) {
        File saveFile = null;
        if (saveFile == null) {
            if (isExter) {
                File directory = new File(Environment.getExternalStorageDirectory(), filePath);
                directory.mkdirs();
                saveFile = new File(directory, fileName);
            } else {
                File directory = new File(context.getFilesDir(), filePath);
                directory.mkdirs();
                saveFile = new File(directory, fileName);
            }
        }
        return saveFile;
    }

    public static Uri cameraUri(File file) {
        return Uri.fromFile(file);
    }

    public static boolean isCamerasAvailable(Context context) {
        String feature = PackageManager.FEATURE_CAMERA;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            feature = PackageManager.FEATURE_CAMERA_ANY;
        }
        return context.getPackageManager().hasSystemFeature(feature);
    }

    /**
     * need permission
     *
     * @param context
     * @param authority
     * @param file
     * @return
     */
    public static Intent getCameraIntent(Context context, String authority, File file) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0及以上
            // requestCameraPermissions((Activity)context);
            Uri uriForFile = getCameraUriForProvider(context, authority, file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
           /* cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);*/
        } else {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        return cameraIntent;
    }

    private String[] getMandatoryCameraPermissions() {
        return new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    /**
     * resquest permission to use xilandingpage_dialog_img_camera and write files
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean requestCameraPermissions(Activity context) {
        List<String> list = new ArrayList<>();

        for (String permission : getMandatoryCameraPermissions())
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED)
                list.add(permission);
        if (list.isEmpty())
            return true;
        context.requestPermissions(list.toArray(new String[list.size()]), REQUESTER);
        return false;
    }

    public static boolean fromCamera(Intent data, File file) {
        return (data == null || data.getData() == null || data.getData().toString().contains(file.toString()));
    }

    public static Uri cameraUriFromFile(File file) {
        return Uri.fromFile(file);
    }

    public static void startCamera(Activity context, Intent intent) {
        context.startActivityForResult(intent, REQUESTER);
    }

    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean hasCamera2(Context context, boolean stillShot) {
        if (context == null) return false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false;
        if (stillShot && XIManufacturerUtil.isSamsungDevice()) return false;
        try {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] idList = manager.getCameraIdList();
            boolean notNull = true;
            if (idList.length == 0) {
                notNull = false;
            } else {
                for (final String str : idList) {
                    if (str == null || str.trim().isEmpty()) {
                        notNull = false;
                        break;
                    }
                    final CameraCharacteristics characteristics = manager.getCameraCharacteristics(str);
                    //noinspection ConstantConditions
                    final int supportLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if (supportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                        notNull = false;
                        break;
                    }
                }
            }
            return notNull;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    public static File makeTempFile(@NonNull Context context, @Nullable String saveDir, String prefix, String extension) {
        if (saveDir == null)
            saveDir = context.getExternalCacheDir().getAbsolutePath();
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final File dir = new File(saveDir);
        dir.mkdirs();
        return new File(dir, prefix + timeStamp + extension);
    }

    public static Camera.Size getSuitblePreviewSize(List<Camera.Size> sizeList, int screenWidth, int screenHeight) {
        Collections.sort(sizeList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return rhs.height * rhs.width - lhs.height * lhs.width;
            }
        });
        Camera.Size tempSize = null;
        for (Camera.Size size : sizeList) {
            if (size.width * screenWidth == screenHeight * size.height) {
                tempSize = size;//  parameters.setPreviewSize(size.width, size.height);
                break;
            }
        }
        if (tempSize == null) {
            for (Camera.Size size : sizeList) {
                if (size.width * 16 == 9 * size.height) {
                    tempSize = size;//   parameters.setPictureSize(size.width, size.height);
                    break;
                }
            }
        }
        if (tempSize == null) {
            for (Camera.Size size : sizeList) {

                if (size.width * 9 == 16 * size.height) {
                    tempSize = size;//   parameters.setPictureSize(size.width, size.height);
                    break;
                }
            }
        }
        if (tempSize == null) {
            tempSize = sizeList.get(0);
        }
        return tempSize;
    }

    public static Camera.Size getSuitblePictureSize(List<Camera.Size> sizeList, int screenWidth, int screenHeight) {
        Collections.sort(sizeList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return rhs.height * rhs.width - lhs.height * lhs.width;
            }
        });
        Camera.Size tempSize = null;
        List<Camera.Size> tempSizeList = new ArrayList<>();
        if (sizeList.size() > 0) {
            for (Camera.Size size : sizeList) {
                if (size.width > 1080 && size.height > 1080) {
                    continue;
                }
                tempSizeList.add(size);
            }
        }
        if (tempSizeList.size() > 0) {
            for (Camera.Size size : tempSizeList) {
                if (size.width == screenHeight && size.height == screenWidth) {
                    tempSize = size;//   parameters.setPictureSize(size.width, size.height);
                    break;
                }
            }
            if (tempSize == null) {
                for (Camera.Size size : tempSizeList) {
                    if (size.width * screenWidth == screenHeight * size.height) {
                        tempSize = size;//   parameters.setPictureSize(size.width, size.height);
                        break;
                    }
                }
            }
        }
        if (tempSize == null) {
            for (Camera.Size size : tempSizeList) {

                if (size.width * 16 == 9 * size.height) {
                    tempSize = size;//   parameters.setPictureSize(size.width, size.height);
                    break;
                }
            }
        }
        if (tempSize == null) {
            for (Camera.Size size : tempSizeList) {

                if (size.width * 9 == 16 * size.height) {
                    tempSize = size;//   parameters.setPictureSize(size.width, size.height);
                    break;
                }
            }
        }
        if (tempSize == null) {
            tempSize = tempSizeList.get(0);
        }
        return tempSize;
    }

    public static Camera.Size getSuitblePictureSize2(List<Camera.Size> sizeList, int screenWidth, int screenHeight) {
        Camera.Size bestSize = sizeList.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizeList) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

    public static int[] getSuitblePreviewFpsRange(List<int[]> fpsRangeList) {
        Collections.sort(fpsRangeList, new Comparator<int[]>() {
            @Override
            public int compare(int[] lhs, int[] rhs) {
                return rhs[0] * rhs[1] - lhs[0] * lhs[1];
            }
        });
        return fpsRangeList.get(0);
    }

}
