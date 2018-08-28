package com.microsoft.xiaoicesdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ldx.conversation.ui.XIConversationChatPage;
import com.ldx.conversationbase.common.XICacheResourceManager;
import com.ldx.conversationbase.common.XIConversationConfig;
import com.ldx.conversationbase.db.XIChatMessageInfo;
import com.ldx.conversationbase.listener.XIChatCardClickListener;
import com.ldx.conversationbase.listener.XIChatMessageDefaultLongClickListener;
import com.microsoft.xiaoicesdkdemo.utils.XIGlideRoundTransform;

public class XIMainActivity extends AppCompatActivity {

    private Button mLandingPageDemo;
    private Button mConversationDemo;
    private Button mOtherConversationDemo;
    private Button mLandingpageClearDemo;
    private Button mLandingpageResetDemo;
    private ImageView xiaoiceIcon ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xidemo_activity_main);
        mLandingPageDemo = (Button) findViewById(R.id.xidemo_landingpage_demo);
        mConversationDemo = (Button) findViewById(R.id.xidemo_conversation_demo);
        mOtherConversationDemo = (Button) findViewById(R.id.xidemo_otherconversation_demo);
        mLandingpageClearDemo = (Button) findViewById(R.id.xidemo_landingpaeappclear_demo);
        mLandingpageResetDemo = (Button) findViewById(R.id.xidemo_landingpaeapplireset_demo);
        xiaoiceIcon = (ImageView)findViewById(R.id.xidemo_xiaoice_demo_icon);
        Glide.with(this).load(R.drawable.touxiang).fitCenter().transform(new XIGlideRoundTransform(this, 5)).into(xiaoiceIcon);

        mLandingpageClearDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                XIConversationChatPage.setXIChatMessageDefaultLongClickListener(new XIChatMessageDefaultLongClickListener() {
                    @Override
                    public void onMessageCopy(View view, XIChatMessageInfo xiChatMessageInfo) {

                    }

                    @Override
                    public void onMessageShare(View view, XIChatMessageInfo xiChatMessageInfo) {

                    }

                    @Override
                    public void onMessageDelete(View view, XIChatMessageInfo xiChatMessageInfo) {

                    }
                });
                XICacheResourceManager.clearAllResource(XIMainActivity.this);
            }
        });

        mOtherConversationDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XIConversationConfig.setConversationUserId("user");
                Intent intent = new Intent(XIMainActivity.this, com.ldx.conversationother.ui.XIConversationChatPage.class);
                startActivity(intent);
            }
        });

        mLandingpageResetDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XIConversationConfig.setConversationUserId("user2");
            }
        });

        mLandingPageDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(XIMainActivity.this, XIDemoLandingPageActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        mConversationDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XIConversationConfig.setConversationUserId("user");
               /* XIConversationChatPage.setXIChatCardClickListener(new XIChatCardClickListener() {
                    @Override
                    public void onCardClick(String s, String s1, String s2, String s3) {

                    }
                });*/
                Intent intent = new Intent(XIMainActivity.this, XIConversationChatPage.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
