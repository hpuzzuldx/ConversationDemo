package com.microsoft.xiaoicesdkdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.ldx.landingpage.bean.XILandingpageImageChooseConfig;
import com.ldx.landingpage.choosedialog.bean.XIPickResult ;
import com.ldx.landingpage.choosedialog.listeners.XIIPickResult;
import com.ldx.landingpage.views.XILandingPageView;

/**
 * use XIDemoXILandingPageViewUse
 */
public class XIDemoXILandingPageViewUse extends AppCompatActivity implements XIIPickResult {

    public final static String BUNDLE_KEY_CURRENTURL = "xidemo_currentURL";

    private com.ldx.landingpage.views.XILandingPageView xilandingpageview;
    private String currentUrl = "https://minisite.msxiaobing.com/ChunCheng/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* XILandingpageImageChooseConfig  imageChooseConfig = new XILandingpageImageChooseConfig();
        imageChooseConfig.setDialogCameraTitle("cameranihao");
        imageChooseConfig.setDialogGalleryTitle("gallerynihao");
        imageChooseConfig.setDialogChooseTitile("choose");
        imageChooseConfig.setDialogCancleTitle("quxiao");
        imageChooseConfig.setDialogBackGroundColor(Color.BLUE);
        imageChooseConfig.setDialogCameraImage(R.drawable.xidemo_icon);
        imageChooseConfig.setDialogGalleryImage(R.drawable.xidemo_icon);
        imageChooseConfig.setDialogCameraTextSize(22);
        imageChooseConfig.setDialogCancleTextSize(22);
        imageChooseConfig.setDialogGalleryTextSize(22);
        imageChooseConfig.setDialogChooseTextSize(15);
        imageChooseConfig.setDialogGalleryTextColor(Color.RED);
        imageChooseConfig.setDialogCameraTextColor(Color.RED);
        imageChooseConfig.setDialogCancleTextColor(Color.RED);
        imageChooseConfig.setDialogChooseTextColor(Color.RED);
        XILandingPageView.setXILandingpageImageChooseConfig(imageChooseConfig);*/
        setContentView(R.layout.xidemo_webview_usedemo);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String tempurl = bundle.getString(BUNDLE_KEY_CURRENTURL);
            if (!TextUtils.isEmpty(tempurl)){
                currentUrl = tempurl;
            }
        }
        xilandingpageview = (com.ldx.landingpage.views.XILandingPageView) findViewById(R.id.xidemo_xilandingpageviewdemo);
        xilandingpageview.loadUrl(currentUrl);
    }

    @Override
    public void onPickResult(XIPickResult pickResult) {
        //此处处理回调结果
        if (xilandingpageview != null) {
            if (pickResult != null) {
                xilandingpageview.onPickResult(pickResult);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        xilandingpageview.recycleResource();
    }
}
