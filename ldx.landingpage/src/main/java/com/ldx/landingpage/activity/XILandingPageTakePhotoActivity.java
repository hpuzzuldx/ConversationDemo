package com.ldx.landingpage.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ldx.landingpage.R;
import com.ldx.landingpage.utils.XICameraUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class XILandingPageTakePhotoActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private SurfaceView mSurfaceView;
    private RelativeLayout mViewfinder;
    private RelativeLayout mCancel;
    private RelativeLayout mTakePhoto;
    private RelativeLayout mFlashState;
    private RelativeLayout mTakePhotoBack;
    private RelativeLayout mTakePhotoPush;
    private LinearLayout mTakePhotoContainer;
    private LinearLayout mPushPhotoContainer;
    private ImageView mFlashStateImage;
    private boolean mWaitForTakePhoto;
    private boolean mIsSurfaceReady;
    private File mediaFile;
    private int cameraPosition = 1;//0 front ，1 back
    private int SELECTED_CAMERA = -1;
    private int CAMERA_POST_POSITION = -1;
    private int CAMERA_FRONT_POSITION = -1;

    Camera.Parameters cameraParams;
    private int screenWidth;
    private int screenHeight;
    private String flashCurrentState;

    @Nullable
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xilandingpage_activity_take_photo);
        mViewfinder = (RelativeLayout) findViewById(R.id.xilandingpage_rl_autofocus);
        mTakePhoto = (RelativeLayout) findViewById(R.id.xilandingpage_rl_take_photo);
        mCancel = (RelativeLayout) findViewById(R.id.xilandingpage_rl_viewcancel);
        mSurfaceView = (SurfaceView) findViewById(R.id.xilandingpage_sv_photo_area);
        mFlashState = (RelativeLayout) findViewById(R.id.xilandingpage_rl_flash_state);
        mTakePhotoBack = (RelativeLayout) findViewById(R.id.xilandingpage_rl_take_photo_back);
        mTakePhotoPush = (RelativeLayout) findViewById(R.id.xilandingpage_rl_take_photo_push);
        mTakePhotoContainer = (LinearLayout) findViewById(R.id.xilandingpage_ll_take_photo_container);
        mPushPhotoContainer = (LinearLayout) findViewById(R.id.xilandingpage_ll_push_photo_container);
        mFlashStateImage = (ImageView) findViewById(R.id.xilandingpage_take_photo_iv_flashstate);
        mViewfinder.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mTakePhoto.setOnClickListener(this);
        mFlashState.setOnClickListener(this);
        mTakePhotoBack.setOnClickListener(this);
        mTakePhotoPush.setOnClickListener(this);
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFocus();
            }
        });

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        // screen width
        screenWidth = metric.widthPixels;
        // screen height
        screenHeight = metric.heightPixels;
        flashCurrentState = Camera.Parameters.FLASH_MODE_OFF;

        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(this);
    }

    private void openCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
            } catch (RuntimeException e) {
                Toast.makeText(XILandingPageTakePhotoActivity.this, getResources().getString(R.string.xilandingpage_need_camera_permission), Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        if (cameraParams != null){
            cameraParams = null;
        }
        try{
            cameraParams = mCamera.getParameters();
            cameraParams.setPictureFormat(ImageFormat.JPEG);
            cameraParams.setRotation(90);
            cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            cameraParams.setFlashMode(flashCurrentState);
            List<Camera.Size> sizeList;
            Camera.Size tempSize;
            //full screen
            sizeList = cameraParams.getSupportedPreviewSizes();
            tempSize = XICameraUtil.getSuitblePreviewSize(sizeList, screenWidth, screenHeight);
            cameraParams.setPreviewSize(tempSize.width, tempSize.height);
            //set image size
            sizeList = cameraParams.getSupportedPictureSizes();
            tempSize = XICameraUtil.getSuitblePictureSize(sizeList, screenWidth, screenHeight);
            cameraParams.setPictureSize(tempSize.width, tempSize.height);
            // setting fps
            List<int[]> fpsRangeList = cameraParams.getSupportedPreviewFpsRange();
            int[] fpsRange = XICameraUtil.getSuitblePreviewFpsRange(fpsRangeList);
             cameraParams.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
            // image format
            cameraParams.setPictureFormat(ImageFormat.JPEG);
            cameraParams.setJpegThumbnailQuality(100);
            // quality
            cameraParams.setJpegQuality(100);
            mCamera.setParameters(cameraParams);
            mCamera.setDisplayOrientation(getCameraDisplayOrientation(XILandingPageTakePhotoActivity.this,cameraPosition));

            if (mIsSurfaceReady) {
                startPreview();
            }

        }catch(Exception e){
            startPreview();
        }
    }

    private void reportBug(Camera.Parameters cameraParams, RuntimeException e) {
        final List<Camera.Size> pictureSizes = cameraParams.getSupportedPictureSizes();
        final List<Camera.Size> previewSizes = cameraParams.getSupportedPreviewSizes();
        final StringBuilder sb = new StringBuilder();
        sb.append("surface[").append(mSurfaceView.getWidth()).append(",").append(mSurfaceView.getHeight()).append("]\n");
        buildSizesLog("picture", pictureSizes, sb);
        buildSizesLog("preview", previewSizes, sb);
    }

    private void buildSizesLog(String tag, List<Camera.Size> sizes, StringBuilder sb) {
        sb.append(tag).append("{");
        for (Camera.Size size : sizes) {
            sb.append("[").append(size.width).append(",").append(size.height).append("],");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}\n");
    }

    private void setSurfaceViewSize(Camera.Size size) {
        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        params.height = mSurfaceView.getWidth() * size.width / size.height;
        mSurfaceView.setLayoutParams(params);
    }

    private Camera.Size findBestPictureSize(List<Camera.Size> sizes, Camera.Size defaultSize, float minRatio) {
        final int MIN_PIXELS = 320 * 480;

        sortSizes(sizes);

        Iterator<Camera.Size> it = sizes.iterator();
        while (it.hasNext()) {
            Camera.Size size = it.next();
            if ((float) size.height / size.width <= minRatio) {
                it.remove();
                continue;
            }
            if (size.width * size.height < MIN_PIXELS) {
                it.remove();
            }
        }

        if (!sizes.isEmpty()) {
            return sizes.get(0);
        }

        return defaultSize;
    }

    private Camera.Size findBestPreviewSize(List<Camera.Size> sizes, Camera.Size defaultSize,
                                            Camera.Size pictureSize, float minRatio) {
        final int pictureWidth = pictureSize.width;
        final int pictureHeight = pictureSize.height;
        boolean isBestSize = (pictureHeight / (float) pictureWidth) > minRatio;
        sortSizes(sizes);

        Iterator<Camera.Size> it = sizes.iterator();
        while (it.hasNext()) {
            Camera.Size size = it.next();
            if ((float) size.height / size.width <= minRatio) {
                it.remove();
                continue;
            }
            if (isBestSize && size.width * pictureHeight == size.height * pictureWidth) {
                return size;
            }
        }

        if (!sizes.isEmpty()) {
            return sizes.get(0);
        }

        return defaultSize;
    }

    private static void sortSizes(List<Camera.Size> sizes) {
        Collections.sort(sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                return b.height * b.width - a.height * a.width;
            }
        });
    }

    private void startPreview() {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceView.getHolder());
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    private void closeCamera() {
        if (mCamera == null) {
            return;
        }
        mCamera.cancelAutoFocus();
        stopPreview();
        mCamera.release();
        mCamera = null;
    }

    /**
     * auto focus
     */
    private void requestFocus() {
        if (mCamera == null || mWaitForTakePhoto) {
            return;
        }
        try{
            mCamera.autoFocus(null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * take picture
     */
    private void takePhoto() {
        if (mCamera == null || mWaitForTakePhoto) {
            return;
        }
        mWaitForTakePhoto = true;
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                onTakePhoto(data);
                mWaitForTakePhoto = false;
            }
        });
    }

    private void onTakePhoto(byte[] data) {
        if (mediaFile != null && mediaFile.exists()) {
            mediaFile.delete();
        }
        mediaFile = createFile(XILandingPageTakePhotoActivity.this);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mediaFile);
            fos.write(data);
            fos.flush();
            fos.close();
            stopPreview();
        } catch (Exception e) {

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void changeCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraPosition == 1) {
                //to front
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                    mCamera = Camera.open(i);
                    cameraPosition = 0;
                    openCamera();

                    break;
                }
            } else {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                    mCamera = Camera.open(i);
                    cameraPosition = 1;
                    openCamera();
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                openCamera();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsSurfaceReady = true;
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsSurfaceReady = false;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.xilandingpage_rl_autofocus) {
           // requestFocus();
            changeCamera();
        } else if (id == R.id.xilandingpage_rl_viewcancel) {
            cancelAndExit();
        } else if (id == R.id.xilandingpage_rl_take_photo) {
            takePhoto();
            mTakePhotoContainer.setVisibility(View.GONE);
            mPushPhotoContainer.setVisibility(View.VISIBLE);
        } else if (id == R.id.xilandingpage_rl_flash_state) {
            changetFlashState();
        } else if (id == R.id.xilandingpage_rl_take_photo_back) {
           // requestFocus();
            mTakePhotoContainer.setVisibility(View.VISIBLE);
            mPushPhotoContainer.setVisibility(View.GONE);
            startPreview();
        } else if (id == R.id.xilandingpage_rl_take_photo_push) {
            //submit picture and finish Activity
            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putString("xiTakePhotoPath", mediaFile.getAbsolutePath());
            bundle.putInt("xibackorfront",cameraPosition);
            intent.putExtras(bundle);
            intent.setData(Uri.fromFile(mediaFile));
            setResult(Activity.RESULT_OK, intent);
            closeCamera();
            if (mSurfaceView != null) {
                mSurfaceView = null;
            }
            finish();
        } else {
            return;
        }
    }

    public void changetFlashState() {
        if (mCamera != null) {
            Camera.Parameters p = mCamera.getParameters();
            if (flashCurrentState == Camera.Parameters.FLASH_MODE_ON) {
                flashCurrentState = Camera.Parameters.FLASH_MODE_OFF;
                mFlashStateImage.setImageResource(R.drawable.xilandingpage_img_flash_close);
            } else {
                flashCurrentState = Camera.Parameters.FLASH_MODE_ON;
                mFlashStateImage.setImageResource(R.drawable.xilandingpage_img_flash_open);
            }
            p.setFlashMode(flashCurrentState);
            mCamera.setParameters(p);
        }
    }

    @Override
    public void onBackPressed() {
        cancelAndExit();
    }

    private void cancelAndExit() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public File createFile(Context context) {
        File directory = new File(context.getFilesDir(), "picked");
        directory.mkdirs();
        File saveFile = new File(directory, "tempPick.jpg");
        return saveFile;
    }

    public int getCameraDisplayOrientation(Context context, int cameraId) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = wm.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private void findAvailableCameras() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            switch (info.facing) {
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    CAMERA_FRONT_POSITION = info.facing;
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    CAMERA_POST_POSITION = info.facing;
                    break;
            }
        }
    }
}