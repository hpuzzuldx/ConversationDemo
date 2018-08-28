package com.ldx.landingpage.choosedialog.resolver;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import com.ldx.landingpage.R;
import com.ldx.landingpage.camera.XICameraActivity;
import com.ldx.landingpage.choosedialog.bundle.XIPickSetup;
import com.ldx.landingpage.choosedialog.enums.XIEPickType;
import com.ldx.landingpage.choosedialog.keep.XIKeep;
import com.ldx.landingpage.common.XILandingpageConsts;
import com.ldx.landingpage.utils.XIFileUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XIIntentResolver {

    private Activity activity;

    private XIPickSetup setup;
    private Intent galleryIntent;
    private Intent cameraIntent;
    private File saveFile;
    public static final int REQUESTER = 99;
    private boolean isCamera = false;

    public XIIntentResolver(Activity activity, XIPickSetup setup) {
        this.activity = activity;
        this.setup = setup;
    }

    //have can use Activity?
    private Intent loadSystemPackages(Intent intent) {
        List<ResolveInfo> resInfo = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_SYSTEM_ONLY);

        if (!resInfo.isEmpty()) {
            String packageName = resInfo.get(0).activityInfo.packageName;
            intent.setPackage(packageName);
        }
        return intent;
    }

    public boolean isCamerasAvailable() {
        String feature = PackageManager.FEATURE_CAMERA;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            feature = PackageManager.FEATURE_CAMERA_ANY;
        }
        return activity.getPackageManager().hasSystemFeature(feature);
    }

    private Intent getSysCameraIntent() {
        if (cameraIntent == null) {
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUriForProvider());
            applyProviderPermission();
        }
        return cameraIntent;
    }

    private Intent getCameraIntent() {
        if (cameraIntent == null) {
            cameraIntent = new Intent(activity, XICameraActivity.class);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, XIFileUtil.getSaveToSDImgPath());
            cameraIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // cameraIntent = new Intent(activity,  XILandingPageTakePhotoActivity.class);
        }
        return cameraIntent;
    }

    //camera take picture page
    public void launchCamera(Fragment listener) {
        isCamera = true;
        listener.startActivityForResult(getCameraIntent(), XILandingpageConsts.XILANDINGPAGE_OPEN_CAMERA);
    }

    /**
     * Granting permissions to write and read for available cameras to file provider.
     * 7.0+ provider
     */
    private void applyProviderPermission() {
        List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, cameraUriForProvider(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private File cameraFile() {
        if (saveFile == null) {
            File directory = new File(activity.getFilesDir(), "picked");
            directory.mkdirs();
            saveFile = new File(directory, activity.getString(R.string.xilandingpage_image_file_name));
        }

        return saveFile;
    }

    public Uri cameraUri() {
        if (saveFile != null) {
            return Uri.fromFile(saveFile);
        } else {
            return null;
        }
    }

    /**
     * manifest
     *
     * @return
     */
    private String getAuthority() {
        return activity.getApplication().getPackageName() + activity.getString(R.string.xilandingpage_provider_package);
    }

    private Uri cameraUriForProvider() {
        try {
            //7.0+
            return FileProvider.getUriForFile(activity, getAuthority(), cameraFile());
        } catch (Exception e) {
            if (e.getMessage().contains("ProviderInfo.loadXmlMetaData")) {
                throw new Error(activity.getString(R.string.xilandingpage_provider_package));
            } else {
                throw e;
            }
        }
    }

    //image intent
    private Intent getGalleryIntent() {
        if (galleryIntent == null) {
            galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType(activity.getString(R.string.xilandingpage_image_content_type));
        }
        return galleryIntent;
    }

    public void launchGallery(Fragment listener) {
        isCamera = false;
        //  listener.startActivityForResult(loadSystemPackages(getGalleryIntent()), XILandingpageConsts.XILANDINGPAGE_OPEN_GALLERY);
        try {
            Matisse.from(listener)
                    .choose(MimeType.ofImage())
                    .theme(R.style.Matisse_xiconversation)
                    .showSingleMediaType(true)
                    .countable(false)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    .imageEngine(new GlideEngine())
                    .forResult(XILandingpageConsts.XILANDINGPAGE_OPEN_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launchSystemChooser(Fragment listener) {
        isCamera = false;
        Intent chooserIntent;
        List<Intent> intentList = new ArrayList<>();

        boolean showCamera = XIEPickType.CAMERA.inside(setup.getPickTypes());
        boolean showGallery = XIEPickType.GALLERY.inside(setup.getPickTypes());

        if (showGallery)
            intentList.add(getGalleryIntent());

        if (showCamera && isCamerasAvailable() && !wasCameraPermissionDeniedForever())
            intentList.add(getSysCameraIntent());

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), setup.getTitle());
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
            listener.startActivityForResult(chooserIntent, XILandingpageConsts.XILANDINGPAGE_OPEN_SYSTEMDIALOG);
        }
    }

    private String[] getMandatoryCameraPermissions() {
        return new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public boolean wasCameraPermissionDeniedForever() {
        if (XIKeep.with(activity).neverAskedForPermissionYet())
            return false;

        for (String permission : getMandatoryCameraPermissions()) {
            if (((ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED)
                    && (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * resquest permission to use xilandingpage_dialog_img_camera and write files
     */
    public boolean requestCameraPermissions(Fragment listener) {
        List<String> list = new ArrayList<>();

        for (String permission : getMandatoryCameraPermissions())
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED)
                list.add(permission);
        if (list.isEmpty())
            return true;
        listener.requestPermissions(list.toArray(new String[list.size()]), REQUESTER);
        return false;
    }

    public boolean fromCamera(Intent data) {
        return (data == null || data.getData() == null || (saveFile != null ? data.getData().toString().contains(saveFile.toString()) : true));
    }

    public Activity getActivity() {
        return activity;
    }
}
