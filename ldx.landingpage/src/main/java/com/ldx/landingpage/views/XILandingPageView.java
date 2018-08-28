package com.ldx.landingpage.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ldx.landingpage.R;
import com.ldx.landingpage.bean.XILandingpageImageChooseConfig;
import com.ldx.landingpage.choosedialog.bean.XIPickResult;
import com.ldx.landingpage.choosedialog.bundle.XIPickSetup;
import com.ldx.landingpage.choosedialog.dialog.XIPickImageDialog;
import com.ldx.landingpage.choosedialog.listeners.XIIPickResult;
import com.ldx.landingpage.common.XILandingpageConsts;
import com.ldx.landingpage.common.listener.XIWebChromeClientListener;
import com.ldx.landingpage.common.listener.XIWebViewClientListener;
import com.ldx.landingpage.utils.XIImageFilePath;
import com.ldx.landingpage.utils.XINetWorkTypeUtil;

/**
 * @author lidongixu
 */

public class XILandingPageView extends WebView {

    private WebSettings settings;
    private Context mContext;
    private boolean is5HighVersion = false;
    private XIIPickResult mPickResult;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;

    private XILandingpageImageChooseConfig xiXILandingpageImageChooseConfig;
    private static  XILandingpageImageChooseConfig mxiXILandingpageImageChooseConfig;

    public static void setXILandingpageImageChooseConfig(XILandingpageImageChooseConfig xiXILandingpageImageChooseConfig) {
        XILandingPageView.mxiXILandingpageImageChooseConfig = xiXILandingpageImageChooseConfig;
    }

    private XIWebChromeClientListener xiWebChromeClientListener;
    private static XIWebChromeClientListener mxiWebChromeClientListener;

    public static void setXIWebChromeClientListener(XIWebChromeClientListener xiXIWebChromeClientListener) {
        XILandingPageView.mxiWebChromeClientListener = xiXIWebChromeClientListener;
    }

    private XIWebViewClientListener xiXIWebViewClientListener;
    private static XIWebViewClientListener mxiXIWebViewClientListener;

    public static void setXIWebViewClientListener(XIWebViewClientListener xiWebViewClientListener){
        mxiXIWebViewClientListener = xiWebViewClientListener;
    }

    public XILandingPageView(Context context) {
        this(context, null);
        mContext = context;
        this.mPickResult = (XIIPickResult) context;
    }

    public XILandingPageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XILandingPageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        this.mPickResult = (XIIPickResult) context;
        if (XILandingPageView.mxiXILandingpageImageChooseConfig != null){
            xiXILandingpageImageChooseConfig= XILandingPageView.mxiXILandingpageImageChooseConfig;
            mxiXILandingpageImageChooseConfig = null;
        }else{
            xiXILandingpageImageChooseConfig = new XILandingpageImageChooseConfig();
        }

        if (XILandingPageView.mxiWebChromeClientListener != null){
            xiWebChromeClientListener = XILandingPageView.mxiWebChromeClientListener;
            XILandingPageView.mxiWebChromeClientListener =null;
        }

        if (XILandingPageView.mxiXIWebViewClientListener != null){
            xiXIWebViewClientListener = XILandingPageView.mxiXIWebViewClientListener;
            XILandingPageView.mxiXIWebViewClientListener =null;
        }
        init(context);
    }

    @SuppressLint("JavascriptInterface")
    public void init(Context mContext) {
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
        // this.addJavascriptInterface(new XIXiaoIceShareObject(),"XiaoIceSdkShareObject");
        setWebViewClient(webViewClient);
        setWebChromeClient(mWebChromeClient);
        settings = getSettings();
        initSetWebView();
        saveData(settings);
        newWin(settings);
       // loadUrl(BuildConfig.xilandingpage_mainpage);
    }

    private void initSetWebView() {
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setLoadsImagesAutomatically(true);
        settings.setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(0);
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        try {
            String ua = settings.getUserAgentString();
            settings.setUserAgentString(ua + XILandingpageConsts.XILANDINGPAGE_LANDINGPAGE_UA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveData(WebSettings mWebSettings) {
        //cache setting
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        String appCachePath = mContext.getApplicationContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
        if (XINetWorkTypeUtil.getNetworkType(mContext) == XILandingpageConsts.Web_State.XILANDINGPAGE_NO_NET) {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }
    }

    private void newWin(WebSettings mWebSettings) {
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }


    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (xiXIWebViewClientListener != null){
                xiXIWebViewClientListener.onPageStarted(view,url,favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (xiXIWebViewClientListener != null){
                xiXIWebViewClientListener.onPageFinished(view,url);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (xiXIWebViewClientListener != null){
                xiXIWebViewClientListener.onReceivedError(view, errorCode, description, failingUrl);
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (xiXIWebViewClientListener != null){
                xiXIWebViewClientListener.onReceivedError(view, request, error);
            }
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        // android 5.0
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;
            is5HighVersion = true;
            //XIPickSetup pickSetup = new XIPickSetup();
            setupChooseImageDialog();
            return true;
        }

        //The undocumented magic method override
        //Eclipse will swear at you if you try to put @Override here
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            is5HighVersion = false;
            mUploadMessage = uploadMsg;
            setupChooseImageDialog();

        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            is5HighVersion = false;
            mUploadMessage = uploadMsg;
            setupChooseImageDialog();
        }

        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            is5HighVersion = false;
            mUploadMessage = uploadMsg;
            setupChooseImageDialog();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (xiWebChromeClientListener != null){
                xiWebChromeClientListener.onProgressChanged(view,newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
           if (xiWebChromeClientListener != null){
               xiWebChromeClientListener.onReceivedTitle(view ,title);
           }
        }
    };

    private void setupChooseImageDialog() {
        XIPickSetup pickSetup = new XIPickSetup();

        if(xiXILandingpageImageChooseConfig != null && !TextUtils.isEmpty(xiXILandingpageImageChooseConfig.getDialogChooseTitile().trim())){
            pickSetup.setTitle(xiXILandingpageImageChooseConfig.getDialogChooseTitile().trim());
        }else{
            pickSetup.setTitle(mContext.getResources().getString(R.string.xilandingpage_dialog_choose));
        }

        if(xiXILandingpageImageChooseConfig != null && !TextUtils.isEmpty(xiXILandingpageImageChooseConfig.getDialogCancleTitle().trim())){
            pickSetup.setCancelText(xiXILandingpageImageChooseConfig.getDialogCancleTitle().trim());
        }else{
            pickSetup.setCancelText(mContext.getResources().getString(R.string.xilandingpage_dialog_cancel));
        }

        if(xiXILandingpageImageChooseConfig != null && !TextUtils.isEmpty(xiXILandingpageImageChooseConfig.getDialogCameraTitle().trim())){
            pickSetup.setCameraButtonText(xiXILandingpageImageChooseConfig.getDialogCameraTitle().trim());
        }else{
            pickSetup.setCameraButtonText(mContext.getResources().getString(R.string.xilandingpage_dialog_camera_title));
        }

        if(xiXILandingpageImageChooseConfig != null && !TextUtils.isEmpty(xiXILandingpageImageChooseConfig.getDialogGalleryTitle().trim())){
            pickSetup.setGalleryButtonText(xiXILandingpageImageChooseConfig.getDialogGalleryTitle().trim());
        }else{
            pickSetup.setGalleryButtonText(mContext.getResources().getString(R.string.xilandingpage_dialog_gallery_title));
        }

        pickSetup.setProgressText(mContext.getResources().getString(R.string.xilandingpage_dialog_loading));

        if (xiXILandingpageImageChooseConfig != null && xiXILandingpageImageChooseConfig.getDialogCameraImage() != -1 && getResources().getDrawable(xiXILandingpageImageChooseConfig.getDialogCameraImage()) != null){
            pickSetup.setCameraIcon(xiXILandingpageImageChooseConfig.getDialogCameraImage());
        }
        if (xiXILandingpageImageChooseConfig != null && xiXILandingpageImageChooseConfig.getDialogCameraImage() != -1 && getResources().getDrawable(xiXILandingpageImageChooseConfig.getDialogGalleryImage()) != null){
            pickSetup.setGalleryIcon(xiXILandingpageImageChooseConfig.getDialogGalleryImage());
        }

        if (xiXILandingpageImageChooseConfig != null
                && xiXILandingpageImageChooseConfig.getDialogChooseTextColor() != -1){
            pickSetup.setTitleColor(xiXILandingpageImageChooseConfig.getDialogChooseTextColor());
        }else{
            pickSetup.setTitleColor(Color.DKGRAY);
        }

        if (xiXILandingpageImageChooseConfig != null
                && xiXILandingpageImageChooseConfig.getDialogBackGroundColor() != -1){
            pickSetup.setBackgroundColor(xiXILandingpageImageChooseConfig.getDialogBackGroundColor());
        }else{
            pickSetup.setBackgroundColor(Color.WHITE);
        }

        if (xiXILandingpageImageChooseConfig != null
                && xiXILandingpageImageChooseConfig.getDialogCameraTextColor() != -1){
            pickSetup.setCameraTextColor(xiXILandingpageImageChooseConfig.getDialogCameraTextColor());
        }else{
            pickSetup.setCameraTextColor(mContext.getResources().getColor(R.color.xilandingpage_option_text_pressed));
        }

        if (xiXILandingpageImageChooseConfig != null
                && xiXILandingpageImageChooseConfig.getDialogGalleryTextColor() != -1){
            pickSetup.setGalleryTextColor(xiXILandingpageImageChooseConfig.getDialogGalleryTextColor());
        }else{
            pickSetup.setGalleryTextColor(mContext.getResources().getColor(R.color.xilandingpage_option_text_pressed));
        }

        if (xiXILandingpageImageChooseConfig != null
                && xiXILandingpageImageChooseConfig.getDialogCancleTextColor() != -1){
            pickSetup.setCancelTextColor(xiXILandingpageImageChooseConfig.getDialogCancleTextColor());
        }else{
            pickSetup.setCancelTextColor(mContext.getResources().getColor(R.color.xilandingpage_option_text_pressed));
        }
        if (xiXILandingpageImageChooseConfig != null
                && xiXILandingpageImageChooseConfig.getDialogChooseTextSize() != -1){
            pickSetup.setTitleTextSize(xiXILandingpageImageChooseConfig.getDialogChooseTextSize());
        }

        if (xiXILandingpageImageChooseConfig != null
                && xiXILandingpageImageChooseConfig.getDialogCameraTextSize() != -1){
            pickSetup.setCameraTextSize(xiXILandingpageImageChooseConfig.getDialogCameraTextSize());
        }

        if (xiXILandingpageImageChooseConfig != null
                && xiXILandingpageImageChooseConfig.getDialogGalleryTextSize() != -1){
            pickSetup.setGalleryTextSize(xiXILandingpageImageChooseConfig.getDialogGalleryTextSize());
        }

        if (xiXILandingpageImageChooseConfig != null
                && xiXILandingpageImageChooseConfig.getDialogCancleTextSize() != -1){
            pickSetup.setCancleTextSize(xiXILandingpageImageChooseConfig.getDialogCancleTextSize());
        }

        XIPickImageDialog.build(mContext, pickSetup, mPickResult).show((FragmentActivity) mContext);
    }

    public void initInputFileForm() {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
        }
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    public void onPickResult(XIPickResult r) {

        if (r.getError() != null) {
            initInputFileForm();
            return;
        }
        if (!is5HighVersion) {
            //4.4
            if (null == mUploadMessage) return;
            Uri result = r.getData() == null || r.getError() != null ? null
                    : r.getData().getData();
            if (result != null) {
                String imagePath = XIImageFilePath.getPath(mContext, result);
                if (!TextUtils.isEmpty(imagePath)) {
                    result = Uri.parse("file:///" + imagePath);
                }
            }
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (is5HighVersion && mFilePathCallback != null) {
            // 5.0+
            Uri[] results = null;
            if (r.getData() != null) {
                if (r.getData().getData() != null)
                    results = new Uri[]{r.getData().getData()};
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        } else {
            initInputFileForm();
            return;
        }
    }

    public void recycleResource(){
        if (xiXILandingpageImageChooseConfig != null )xiXILandingpageImageChooseConfig = null;
        if (mxiXILandingpageImageChooseConfig != null )mxiXILandingpageImageChooseConfig = null;
        if (xiWebChromeClientListener != null )xiWebChromeClientListener = null;
        if (mxiWebChromeClientListener != null )mxiWebChromeClientListener = null;
        if (xiXIWebViewClientListener != null )xiXIWebViewClientListener = null;
        if (mxiXIWebViewClientListener != null )mxiXIWebViewClientListener = null;
        this.destroy();
    }
}
