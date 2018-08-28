package com.ldx.landingpage.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldx.landingpage.R;
import com.ldx.landingpage.bean.XILandingpageHeadNavigationConfig;
import com.ldx.landingpage.choosedialog.bean.XIPickResult;
import com.ldx.landingpage.choosedialog.bundle.XIPickSetup;
import com.ldx.landingpage.choosedialog.dialog.XIPickImageDialog;
import com.ldx.landingpage.choosedialog.listeners.XIIPickResult;
import com.ldx.landingpage.common.XILandingpageConsts;
import com.ldx.landingpage.utils.XIImageFilePath;
import com.ldx.landingpage.utils.XINetWorkTypeUtil;
import com.ldx.landingpage.utils.XIStatusBarUtil;

public class XILandingPageMainActivity extends XIBaseAppCompatActivity implements XIIPickResult {

    private WebView mWebView;
    private LinearLayout ll_webview_main_container;
    private WebSettings settings;
    private ProgressBar mProgressbar;
    private ImageView iv_navi_back;
    boolean is5HighVersion = false;
    private Uri saveUri;
    private String currentUrl = "https://minisite.msxiaobing.com/ChunCheng/";
    private String currentText = "";
    private TextView toolBarText;
    private RelativeLayout header_actionbar;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;

    private XILandingpageHeadNavigationConfig xiLandingpageHeadNavigationConfig;
    private static  XILandingpageHeadNavigationConfig mxiLandingpageHeadNavigationConfig;

    public static void setXILandingpageHeadNavigationConfig(XILandingpageHeadNavigationConfig xiLandingpageHeadNavigationConfig) {
        XILandingPageMainActivity.mxiLandingpageHeadNavigationConfig = xiLandingpageHeadNavigationConfig;
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (XILandingPageMainActivity.mxiLandingpageHeadNavigationConfig != null){
            xiLandingpageHeadNavigationConfig = XILandingPageMainActivity.mxiLandingpageHeadNavigationConfig;
            mxiLandingpageHeadNavigationConfig = null;
        }else{
            xiLandingpageHeadNavigationConfig = new XILandingpageHeadNavigationConfig();
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            try{
                String tempurl = bundle.getString(XILandingpageConsts.XILANDINGPAGEMAINPAGECURRURL);
                if (!TextUtils.isEmpty(tempurl)){
                    currentUrl = tempurl;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                String temptext = bundle.getString(XILandingpageConsts.XILANDINGPAGEMAINPAGECURRTEXT);
                if (!TextUtils.isEmpty(temptext)){
                    currentText = temptext;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        setContentView(R.layout.xilandingpage_activity_xiaoice_mainpage);
        initView();
        initEvent();
        initData();
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        settings = mWebView.getSettings();
        initSetWebView();
        saveData(settings);
        newWin(settings);
        if (toolBarText != null)
            toolBarText.setText(currentText);
        navigateSetup();

       // mWebView.loadUrl(BuildConfig.xilandingpage_mainpage);//R.string.xilandingpage_mainpage
       // mWebView.loadUrl(BuildConfig.xilandingpage_audiopage);//R.string.xilandingpage_mainpage
        if (xiLandingpageHeadNavigationConfig != null && !TextUtils.isEmpty(xiLandingpageHeadNavigationConfig.getDisplayUrl().trim())){
            mWebView.loadUrl(xiLandingpageHeadNavigationConfig.getDisplayUrl().trim());
        }else{
            if (!TextUtils.isEmpty(currentUrl)){
                mWebView.loadUrl(currentUrl);
            }
        }
    }

    private void navigateSetup() {
        try {
            if (xiLandingpageHeadNavigationConfig != null && xiLandingpageHeadNavigationConfig.getActivityStatusbarColor() != -1){
                XIStatusBarUtil.setColor(this,xiLandingpageHeadNavigationConfig.getActivityStatusbarColor());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (xiLandingpageHeadNavigationConfig != null && xiLandingpageHeadNavigationConfig.getNavigationBackImageLeftPadding() != -1){
                header_actionbar.setPadding(xiLandingpageHeadNavigationConfig.getNavigationBackImageLeftPadding(),
                        (int)getResources().getDimensionPixelSize(R.dimen.xilandingpage_top_back_paddingtop),
                        (int)getResources().getDimensionPixelSize(R.dimen.xilandingpage_top_back_paddingright),
                        (int)getResources().getDimensionPixelSize(R.dimen.xilandingpage_top_back_paddingbottom));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            if (xiLandingpageHeadNavigationConfig != null ){
                if (xiLandingpageHeadNavigationConfig.getNavigationHeadBackgroundResource() != -1
                        && getResources().getDrawable(xiLandingpageHeadNavigationConfig.getNavigationHeadBackgroundResource())!= null){
                    header_actionbar.setBackgroundResource(xiLandingpageHeadNavigationConfig.getNavigationHeadBackgroundResource());
                }else{
                    if (xiLandingpageHeadNavigationConfig.getNavigationHeadBackgroundColor() != -1){
                        header_actionbar.setBackgroundColor(xiLandingpageHeadNavigationConfig.getNavigationHeadBackgroundColor());
                    }else{
                        header_actionbar.setBackgroundResource(R.drawable.xilandingpage_border_header);
                    }
                }
            }else{
                header_actionbar.setBackgroundResource(R.drawable.xilandingpage_border_header);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            if (xiLandingpageHeadNavigationConfig != null && xiLandingpageHeadNavigationConfig.getNavigationHeadTitleSize() != -1){
                toolBarText.setTextSize(TypedValue.COMPLEX_UNIT_SP,xiLandingpageHeadNavigationConfig.getNavigationHeadTitleSize());
            }else{
                toolBarText.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.xilandingpage_head_titlesize));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (xiLandingpageHeadNavigationConfig != null && xiLandingpageHeadNavigationConfig.getNavigationHeadTitleColor() != -1){
                toolBarText.setTextColor(xiLandingpageHeadNavigationConfig.getNavigationHeadTitleColor());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (xiLandingpageHeadNavigationConfig != null && !TextUtils.isEmpty(xiLandingpageHeadNavigationConfig.getNavigationHeadTitle())){
                toolBarText.setText(xiLandingpageHeadNavigationConfig.getNavigationHeadTitle().trim());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (xiLandingpageHeadNavigationConfig != null && xiLandingpageHeadNavigationConfig.getNavigationBackImage() != -1
                    && getResources().getDrawable(xiLandingpageHeadNavigationConfig.getNavigationBackImage()) != null){
                iv_navi_back.setImageResource(xiLandingpageHeadNavigationConfig.getNavigationBackImage());
            }else{
                iv_navi_back.setImageResource(R.drawable.xilandingpage_back_arrow_black);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable("ImageUri", saveUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        saveUri = savedInstanceState.getParcelable("ImageUri");
    }

    public void setHeaderActionbarVisibleorHidden(boolean showflag) {
        header_actionbar.setVisibility(showflag ? View.VISIBLE : View.GONE);
    }

    /**
     * init webView
     */
    private void initSetWebView() {
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setLoadsImagesAutomatically(true);

        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(0);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void initView() {
        mWebView = (WebView) findViewById(R.id.xilandingpage_xiaoice_mainpage_webview);
        iv_navi_back = (ImageView) findViewById(R.id.xilandingpage_iv_navi_back);
        ll_webview_main_container = (LinearLayout) findViewById(R.id.xilandingpage_ll_webview_main_container);
        header_actionbar = (RelativeLayout) findViewById(R.id.xilandingpage_header_actionbar);
        mProgressbar = (ProgressBar) findViewById(R.id.xilandingpage_mainpage_pb_loading);
        toolBarText = (TextView) findViewById(R.id.xilandingpage_tv_navi_header);

        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(XILandingPageMainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
    }

    public void initEvent() {
        iv_navi_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    finish();
                }*/
               finish();
            }
        });
    }

    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        if (mxiLandingpageHeadNavigationConfig != null)mxiLandingpageHeadNavigationConfig = null;
        if (xiLandingpageHeadNavigationConfig != null)xiLandingpageHeadNavigationConfig = null;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * catch setting
     *
     * @param mWebSettings
     */
    private void saveData(WebSettings mWebSettings) {
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
        if (XINetWorkTypeUtil.getNetworkType(this) == XILandingpageConsts.Web_State.XILANDINGPAGE_NO_NET) {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }
    }

    /**
     * multule
     */
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
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (toolBarText!= null && view != null && view.getTitle() != null){
                toolBarText.setText(view.getTitle());
            }
            mProgressbar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        // android 5.0 android5.0 sdk
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;
            is5HighVersion = true;
            XIPickSetup xiPickSetup = new XIPickSetup();
            XIPickImageDialog.build(XILandingPageMainActivity.this,xiPickSetup, XILandingPageMainActivity.this).show(XILandingPageMainActivity.this);

            return true;
        }

        //The undocumented magic method override
        //Eclipse will swear at you if you try to put @Override here
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            is5HighVersion = false;
            mUploadMessage = uploadMsg;
            XIPickSetup xiPickSetup = new XIPickSetup();
            XIPickImageDialog.build(XILandingPageMainActivity.this,xiPickSetup, XILandingPageMainActivity.this).show(XILandingPageMainActivity.this);
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            is5HighVersion = false;
            mUploadMessage = uploadMsg;
            XIPickSetup xiPickSetup = new XIPickSetup();
            XIPickImageDialog.build(XILandingPageMainActivity.this,xiPickSetup, XILandingPageMainActivity.this).show(XILandingPageMainActivity.this);
        }

        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            is5HighVersion = false;
            mUploadMessage = uploadMsg;
            XIPickSetup xiPickSetup = new XIPickSetup();
            XIPickImageDialog.build(XILandingPageMainActivity.this,xiPickSetup, XILandingPageMainActivity.this).show(XILandingPageMainActivity.this);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress == 100){
                mProgressbar.setVisibility(View.GONE);
            } else{
                if (mProgressbar.getVisibility() == View.GONE){
                    mProgressbar.setVisibility(View.VISIBLE);
                }
                mProgressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            try{
                if (toolBarText != null && !TextUtils.isEmpty(title)){
                    toolBarText.setText(title);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    /**
     * onActivityResult callback
     *
     * @param r
     */
    @Override
    public void onPickResult(XIPickResult r) {
        if (r.getError() != null) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
            return;
        }
        if (!is5HighVersion) {
            //4.4 version callback
            if (null == mUploadMessage) return;
            Uri result = r.getData() == null || r.getError() != null ? null
                    : r.getData().getData();
            if (result != null) {
                String imagePath = XIImageFilePath.getPath(this, result);
                if (!TextUtils.isEmpty(imagePath)) {
                    result = Uri.parse("file:///" + imagePath);
                    saveUri = result;
                }
            } else {
                result = saveUri;
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
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
            return;
        }
    }
}

