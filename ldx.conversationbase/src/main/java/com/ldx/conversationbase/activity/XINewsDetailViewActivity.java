package com.ldx.conversationbase.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldx.conversationbase.R;
import com.ldx.conversationbase.common.XIChatConst;
import com.ldx.conversationbase.utils.XIConNetWorkUtils;
import com.ldx.conversationbase.utils.XIToastUtils;

/**
 * Created by v-doli1 on 2017/9/6.
 */

public class XINewsDetailViewActivity extends AppCompatActivity {

    private WebView mWebView;
    private LinearLayout ll_webview_main_container;
    private WebSettings settings;
    private ImageView iv_navi_back;
    private Uri saveUri;
    private String currentUrl = "";
    private String currentText = "";
    private TextView toolBarText;
    private RelativeLayout header_actionbar;
    private ProgressBar mProgressbar;
    private RelativeLayout mLoadError;
    private boolean loadError = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String tempurl = bundle.getString(XIChatConst.XICURRENTURL);
            if (!TextUtils.isEmpty(tempurl)){
                currentUrl = tempurl;
            }
            String temptext = bundle.getString(XIChatConst.XICURRENTTEXT);
            if (!TextUtils.isEmpty(temptext)){
                currentText = temptext;
            }
        }
        initView();
        initEvent();
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        settings = mWebView.getSettings();
        initSetWebView();
        saveData(settings);
        newWin(settings);
        if ( !TextUtils.isEmpty(currentText) && toolBarText != null )
            toolBarText.setText(currentText);
        if (!TextUtils.isEmpty(currentUrl)){
          //  mWebView.loadUrl(currentUrl);
            mWebView.loadUrl("https://www.baidu.com");
        }else{
            mWebView.loadUrl("file:///android_asset/index.html");
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
        settings.setAllowFileAccess(true);
        settings.setBlockNetworkLoads(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

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
        setContentView(R.layout.xiconversation_activity_show_newsdetails);
       // XIStatusBarUtil.setColor(this, getResources().getColor(R.color.xiconversation_white));
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )) {
            ActivityCompat.requestPermissions(XINewsDetailViewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        mWebView = (WebView) findViewById(R.id.xiconversation_newsdetails_webview);
        iv_navi_back = (ImageView) findViewById(R.id.xiconversation_iv_navi_back);
        ll_webview_main_container = (LinearLayout) findViewById(R.id.xiconversation_news_webview_container);
        header_actionbar = (RelativeLayout) findViewById(R.id.xiconversation_header_actionbar);
        mProgressbar = (ProgressBar) findViewById(R.id.xiconversation_news_pb_loading);
        toolBarText = (TextView) findViewById(R.id.xiconversation_tv_navi_header);
        mLoadError = (RelativeLayout) findViewById(R.id.xiconversation_online_error_btn_retry);
        mLoadError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLoadError.getVisibility() == View.VISIBLE){
                    if (XIConNetWorkUtils.isNetworkConnected(XINewsDetailViewActivity.this)){
                        loadError =false;
                        mWebView.removeAllViews();
                        mWebView.reload();
                        mLoadError.setVisibility(View.GONE);
                    }else{
                        XIToastUtils.showShortToast(XINewsDetailViewActivity.this,getResources().getString(R.string.xiconversation_check_network_error));
                    }
                }
            }
        });
    }

    public void initEvent() {
        iv_navi_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
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
        if (!XIConNetWorkUtils.isNetworkConnected(XINewsDetailViewActivity.this)) {
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
            loadError = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (toolBarText!= null && view != null && view.getTitle() != null){
                toolBarText.setText(view.getTitle());
            }

            if (!loadError) {
                loadError = false;
                //success
                mProgressbar.setVisibility(View.GONE);
                mLoadError.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
            } else {
                loadError = true;
                mProgressbar.setVisibility(View.GONE);
                mLoadError.setVisibility(View.VISIBLE);
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            loadError = true;
            mLoadError.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            loadError = true;
            mLoadError.setVisibility(View.VISIBLE);
        }

        public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed() ;
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

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
                if (toolBarText!= null && title != null){
                    toolBarText.setText(title);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

           try{
               if(title != null && !TextUtils.isEmpty(title)&&title.toLowerCase().contains("error")){
                   loadError = true;
                   mLoadError.setVisibility(View.VISIBLE);
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    };

}