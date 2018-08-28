package com.microsoft.xiaoicesdkdemo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.ldx.landingpage.activity.XILandingPageMainActivity;
import com.ldx.landingpage.activity.XILandingPageMainPage;
import com.ldx.landingpage.bean.XILandingpageHeadNavigationConfig;
import com.ldx.landingpage.common.XILandingpageConsts;

import java.util.Arrays;

public class XIDemoLandingPageActivity extends AppCompatActivity {
    private Button bQuickIntergration;
    private Button bLandingPageUri;
    private Button bFreeIntergration;
    private Button bFreeIntergration2;
    private static final String[] PLANETS = new String[]{"  浙江24小时(Int)",
            "  封面(Int)",
            "  春城晚报(Int)",
            "  凤凰新闻(Int)",
            "  浙江24小时(Prod)",
            "  封面(Prod)",
            "  春城晚报(Prod)"};
    private static final String[] urlPath = new String[]{
            "https://cs-minisite-int.chinacloudsites.cn/Zhejiang24Hours",
            "https://cs-minisite-int.chinacloudsites.cn/FengMian",
            "https://cs-minisite-int.chinacloudsites.cn/ChunCheng",
            "https://csint9.msxiaobing.com/phoenix",
            "https://minisite.msxiaobing.com/Zhejiang24Hours",
            "https://minisite.msxiaobing.com/FengMian",
            "https://minisite.msxiaobing.com/ChunCheng"
    };
    private int currentIndex = 2;
    private String currentUrl = urlPath[currentIndex];
    private String currentText = PLANETS[currentIndex];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xidemo_activity_landingpage);
        bQuickIntergration = (Button) findViewById(R.id.xidemo_activity_landingpage_b_quickintergration);
        bLandingPageUri = (Button) findViewById(R.id.xidemo_activity_landingpage_b_landingpageuri);
        bFreeIntergration = (Button) findViewById(R.id.xidemo_activity_landingpage_b_freeintergration);
        bFreeIntergration2 = (Button) findViewById(R.id.xidemo_activity_landingpage_b_freeintergration2);

        bQuickIntergration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(XIDemoLandingPageActivity.this, XIDemoXILandingPageViewUse.class);
                Bundle bundle = new Bundle();
                bundle.putString(XIDemoXILandingPageViewUse.BUNDLE_KEY_CURRENTURL, currentUrl);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        bLandingPageUri.setText(currentText);

        bLandingPageUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View outerView = LayoutInflater.from(XIDemoLandingPageActivity.this).inflate(R.layout.xidemo_wheel_view, null);
                XIWheelView wv = (XIWheelView) outerView.findViewById(R.id.xidemo_wheel_view_wv);
                wv.setOffset(1);
                wv.setItems(Arrays.asList(PLANETS));
                wv.setSeletion(currentIndex);
                wv.setOnWheelViewListener(new XIWheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {

                        if (selectedIndex <= PLANETS.length){
                            currentIndex = selectedIndex -1;
                            currentText = item;
                            bLandingPageUri.setText(item);
                            currentUrl = urlPath[currentIndex];
                        }
                    }
                });

                new AlertDialog.Builder(XIDemoLandingPageActivity.this)
                        .setTitle(getString(R.string.xidemo_landingpage_wheelview_choose_title))
                        .setView(outerView)
                        .setPositiveButton(getString(R.string.xidemo_wheelview_ok), null)
                        .show();
            }
        });

        bFreeIntergration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   XILandingpageHeadNavigationConfig xildconfig = new XILandingpageHeadNavigationConfig();
                xildconfig.setActivityStatusbarColor(Color.RED);
                xildconfig.setNavigationBackImage(R.drawable.xidemo_icon);
                xildconfig.setNavigationBackImageLeftPadding(10);
                xildconfig.setNavigationHeadTitleSize(22);
                xildconfig.setNavigationHeadTitleColor(Color.BLUE);
                xildconfig.setNavigationHeadTitle("sdfsdfs");
                XILandingPageMainActivity.setXILandingpageHeadNavigationConfig(xildconfig);*/
                Intent intent = new Intent(XIDemoLandingPageActivity.this,XILandingPageMainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(XILandingpageConsts.XILANDINGPAGEMAINPAGECURRURL,currentUrl);
                bundle.putString(XILandingpageConsts.XILANDINGPAGEMAINPAGECURRTEXT,currentText);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        bFreeIntergration2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  XILandingpageHeadNavigationConfig xildconfig = new XILandingpageHeadNavigationConfig();
                xildconfig.setActivityStatusbarColor(Color.RED);
                xildconfig.setNavigationBackImage(R.drawable.xidemo_icon);
                xildconfig.setNavigationBackImageLeftPadding(10);
                xildconfig.setNavigationHeadTitleSize(22);
                xildconfig.setNavigationHeadTitleColor(Color.BLUE);
                xildconfig.setNavigationHeadTitle("sdfsdfsd");
                xildconfig.setDisplayUrl(currentUrl);
                XILandingPageMainPage.setXILandingpageHeadNavigationConfig(xildconfig);*/
                Intent intent = new Intent(XIDemoLandingPageActivity.this, com.ldx.landingpage.activity.XILandingPageMainPage.class);
                Bundle bundle = new Bundle();
                bundle.putString(XILandingpageConsts.XILANDINGPAGEMAINPAGECURRURL,currentUrl);
                bundle.putString(XILandingpageConsts.XILANDINGPAGEMAINPAGECURRTEXT,currentText);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        this.setTitle(R.string.xidemo_activity_landingpage_title);
    }
}
