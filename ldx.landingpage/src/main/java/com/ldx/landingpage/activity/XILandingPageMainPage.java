package com.ldx.landingpage.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldx.landingpage.views.XILandingPageView;
import com.ldx.landingpage.R;
import com.ldx.landingpage.bean.XILandingpageHeadNavigationConfig;
import com.ldx.landingpage.bean.XILandingpageImageChooseConfig;
import com.ldx.landingpage.choosedialog.bean.XIPickResult;
import com.ldx.landingpage.choosedialog.listeners.XIIPickResult;
import com.ldx.landingpage.common.XILandingpageConsts;
import com.ldx.landingpage.common.listener.XILandingpageHeadRightClickListener;
import com.ldx.landingpage.common.listener.XIWebChromeClientListener;
import com.ldx.landingpage.common.listener.XIWebViewClientListener;
import com.ldx.landingpage.utils.XIJsonParseUtil;
import com.ldx.landingpage.utils.XINetWorkTypeUtil;
import com.ldx.landingpage.utils.XIStatusBarUtil;
import com.ldx.landingpage.utils.XIToastUtils;

import java.util.HashMap;
import java.util.Map;

public class XILandingPageMainPage extends XIBaseAppCompatActivity implements XIIPickResult {

    private XILandingPageView xiLandingpageWebView;
    private String currentUrl = "";
    private String currentText = "";
    private ProgressBar mProgressbar;
    private ImageView iv_navi_back;
    private TextView toolBarText;
    private RelativeLayout header_actionbar;
    private ImageView mToolBarRightImage;
    private RelativeLayout mLoadErrorContainer;
    private String mShareStr = "";
    private Map<String, String> mShareMap = new HashMap<String,String>();

    private boolean isError = false;

    private XILandingpageHeadRightClickListener xiLandingpageHeadRightClickListener;
    private static XILandingpageHeadRightClickListener mXILandingpageHeadRightClickListener;

    private XILandingpageHeadNavigationConfig xiLandingpageHeadNavigationConfig;
    private static  XILandingpageHeadNavigationConfig mxiLandingpageHeadNavigationConfig;

    public static void setXILandingpageHeadRightClickListener(XILandingpageHeadRightClickListener tLandingpageHeadRightClickListener){
        XILandingPageMainPage.mXILandingpageHeadRightClickListener = tLandingpageHeadRightClickListener;
    }

    public static void setXILandingpageHeadNavigationConfig(XILandingpageHeadNavigationConfig xiLandingpageHeadNavigationConfig) {
        XILandingPageMainPage.mxiLandingpageHeadNavigationConfig = xiLandingpageHeadNavigationConfig;
    }

    public static void setXILandingpageImageChooseConfig(XILandingpageImageChooseConfig xiXILandingpageImageChooseConfig) {
        XILandingPageView.setXILandingpageImageChooseConfig(xiXILandingpageImageChooseConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (XILandingPageMainPage.mxiLandingpageHeadNavigationConfig != null){
            xiLandingpageHeadNavigationConfig = XILandingPageMainPage.mxiLandingpageHeadNavigationConfig;
            XILandingPageMainPage.mxiLandingpageHeadNavigationConfig = null;
        }else{
            xiLandingpageHeadNavigationConfig = new XILandingpageHeadNavigationConfig();
        }

        if (XILandingPageMainPage.mXILandingpageHeadRightClickListener != null){
            xiLandingpageHeadRightClickListener = XILandingPageMainPage.mXILandingpageHeadRightClickListener;
            XILandingPageMainPage.mXILandingpageHeadRightClickListener = null;
        }

        XILandingPageView.setXIWebChromeClientListener(new XIWebChromeClientListener() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100){
                    isError = false;
                    mProgressbar.setVisibility(View.GONE);
                } else{
                    if (mProgressbar.getVisibility() == View.GONE){
                        mProgressbar.setVisibility(View.VISIBLE);
                    }
                    mProgressbar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (toolBarText != null && title != null){
                    toolBarText.setText(title);
                }

                if (title.contains("404")) {
                    view.loadUrl("about:blank");
                    mLoadErrorContainer.setVisibility(View.VISIBLE);
                    isError = true;
                }
                if (!TextUtils.isEmpty(title) && title.toLowerCase().contains("error")) {
                    view.loadUrl("about:blank");
                    mLoadErrorContainer.setVisibility(View.VISIBLE);
                    isError = true;
                }
            }
        });

        XILandingPageView.setXIWebViewClientListener(new XIWebViewClientListener() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                isError = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (toolBarText != null && view != null && view.getTitle() != null) {
                    toolBarText.setText(view.getTitle());
                }
                if (!isError) {
                    isError = false;
                    mProgressbar.setVisibility(View.GONE);
                    mLoadErrorContainer.setVisibility(View.GONE);
                    xiLandingpageWebView.setVisibility(View.VISIBLE);
                } else {
                    isError = true;
                    mProgressbar.setVisibility(View.GONE);
                    mLoadErrorContainer.setVisibility(View.VISIBLE);
                }
            }

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                xiLandingpageWebView.removeAllViews();
                isError = true;
                mLoadErrorContainer.setVisibility(View.VISIBLE);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String tempurl = bundle.getString(XILandingpageConsts.XILANDINGPAGEMAINPAGECURRURL);
            if (!TextUtils.isEmpty(tempurl)){
                currentUrl = tempurl;
            }
            String temptext = bundle.getString(XILandingpageConsts.XILANDINGPAGEMAINPAGECURRTEXT);
            if (!TextUtils.isEmpty(temptext)){
                currentText = temptext;
            }
        }
        setContentView(R.layout.xilandingpage_display_webview_mainpage);
        initView();
        initEvent();
        if (toolBarText != null)
            toolBarText.setText(currentText);
        navigateSetup();
        if (xiLandingpageHeadNavigationConfig != null && !TextUtils.isEmpty(xiLandingpageHeadNavigationConfig.getDisplayUrl().trim())){
            xiLandingpageWebView.loadUrl(xiLandingpageHeadNavigationConfig.getDisplayUrl().trim());
        }else {
            if (!TextUtils.isEmpty(currentUrl)) {
                xiLandingpageWebView.loadUrl(currentUrl);
            }
        }
    }


    public void initView() {
        xiLandingpageWebView = (XILandingPageView) findViewById(R.id.xilandingpage_activity_display_mainpage_wb);
        iv_navi_back = (ImageView) findViewById(R.id.xilandingpage_iv_navi_back);
        mToolBarRightImage = (ImageView) findViewById(R.id.xilandingpage_iv_navi_rightimage);
        header_actionbar = (RelativeLayout) findViewById(R.id.xilandingpage_header_actionbar);
        mProgressbar = (ProgressBar) findViewById(R.id.xilandingpage_mainpage_pb_loading);
        toolBarText = (TextView) findViewById(R.id.xilandingpage_tv_navi_header);
        mLoadErrorContainer = (RelativeLayout) findViewById(R.id.xilandingpage_online_error_btn_retry);

        xiLandingpageWebView.addJavascriptInterface(new XIXiaoIceShareObject(),"XiaoIceSdkShareObject");

        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(XILandingPageMainPage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
        if (xiLandingpageHeadRightClickListener != null){
            mToolBarRightImage.setVisibility(View.VISIBLE);
        }else{
            mToolBarRightImage.setVisibility(View.GONE);
        }

    }

    public void initEvent() {
        iv_navi_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (xiLandingpageWebView.canGoBack()) {
                    xiLandingpageWebView.goBack();
                } else {
                    finish();
                }*/
               finish();
            }
        });

        mLoadErrorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (XINetWorkTypeUtil.getNetworkType(XILandingPageMainPage.this) !=XILandingpageConsts.Web_State.XILANDINGPAGE_NO_NET ) {
                    isError = false;
                    xiLandingpageWebView.loadUrl(currentUrl);
                    xiLandingpageWebView.setVisibility(View.VISIBLE);
                    mLoadErrorContainer.setVisibility(View.GONE);
                } else {
                    XIToastUtils.showShortToast(XILandingPageMainPage.this, getResources().getString(R.string.xilandingpage_global_dataloaderror));
                }
            }
        });


        mToolBarRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (xiLandingpageHeadRightClickListener != null && mShareMap != null && mShareMap.size() >0){
                    try {
                        String title = "";
                        String description = "";
                        String imageURL = "";
                        String shareURL = "";

                        try {
                            title = mShareMap.get("title");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            description = mShareMap.get("description");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            imageURL = mShareMap.get("imageURL");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            shareURL = mShareMap.get("shareURL");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (xiLandingpageHeadRightClickListener != null
                                && !TextUtils.isEmpty(shareURL)
                                && !TextUtils.isEmpty(imageURL)){
                            xiLandingpageHeadRightClickListener.onClickEvent(XILandingPageMainPage.this,
                                    title,  description,  imageURL,  shareURL);
                        }else{
                            Toast.makeText(XILandingPageMainPage.this,getResources().getString(R.string.xilandingpage_shareerror_nodata),Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (xiLandingpageWebView.canGoBack()) {
                        xiLandingpageWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPickResult(XIPickResult pickResult) {
        if (xiLandingpageWebView != null) {
            if (pickResult != null) {
                xiLandingpageWebView.onPickResult(pickResult);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mxiLandingpageHeadNavigationConfig != null)mxiLandingpageHeadNavigationConfig = null;
        if (xiLandingpageHeadNavigationConfig != null)xiLandingpageHeadNavigationConfig = null;
        if (XILandingPageMainPage.mXILandingpageHeadRightClickListener != null)XILandingPageMainPage.mXILandingpageHeadRightClickListener = null;
        if (xiLandingpageHeadRightClickListener != null)xiLandingpageHeadRightClickListener = null;
        xiLandingpageWebView.recycleResource();
    }

    public class XIXiaoIceShareObject {

        @JavascriptInterface
        public void onShare(String shareMap){
            mShareStr = shareMap;
            mShareMap = XIJsonParseUtil.getShareMap(mShareStr);
        }
    }

    public void loadShareData(String mShareStr){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//sdk>19

            String script = "javascript:callJS()";
            xiLandingpageWebView.evaluateJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String responseJson) {

                }
            });
        } else {//sdk<19
            xiLandingpageWebView.loadUrl("javascript:getParams()");
        }
    }
}
