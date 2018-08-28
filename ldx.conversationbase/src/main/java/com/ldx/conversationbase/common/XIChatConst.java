package com.ldx.conversationbase.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class XIChatConst {
    public static final String XICONVERSATION_XIAOICE_NAME = "xiaoice";
    public static final String XICONVERSATION_FIRSTINTO_GREETING = "【端用户首次打开信号，内容无法显示】";
    public static final String XICONVERSATION_NEWCONVERSATION_GREETING = "【端用户新对话打开信号，内容无法显示】";
    public static final String XICONVERSATION_LASTTIME_FILENAME = "xiconversation_lastmesg_data";
    public static final String XICONVERSATION_LASTTIME_TEXT = "lastTimeText";
    public static final int XICONVERSATION_IMAGE_MAXSIZE = 1024 *150;

    public static final int XICONVERSATION_SENDING = 0;
    public static final int XICONVERSATION_COMPLETED = 1;
    public static final int XICONVERSATION_SENDERROR = 2;

    public static final int XICONVERSATION_FIRSTINTO_GREETING_RESPONSEID = -2;
    public static final int XICONVERSATION_NEWCONVERSATION_GREETING_RESPONSEID = -3;

    public static final String XICONVERSATIONCONFIG = "XIConversationConfig";
    public static final String XICONVERSATIONRESOURCEURL = "XIConversationResourceUrl";
    public static final String XICONVERSATIONRESOURCEID = "XIConversationResourceId";

    public static final String XICONVERSATION_MAC_NAME = "HmacSHA1";
    public static final String XICONVERSATION_ENCODING = "UTF-8";
    // public static final String XICONVERSATION_CONVERSATIONResponseURI = "https://service.msxiaobing.com/api/Conversation/GetResponse?api-version=2017-06-15";

    //int
   public static final String XICONVERSATION_CONVERSATIONResponseURI = "http://sai-pilot.msxiaobing.com/api/Conversation/GetResponse?api-version=2017-06-15-Int";
    //int
   // public static final String XICONVERSATION_CONVERSATIONResponseURI = "http://sai-pilot.msxiaobing.com/api/ZoChat/GetResponse?api-version=2017-06-15-Int";

    //多条
    // public static final String XICONVERSATION_CONVERSATIONResponseURI = "http://cs-api-gateway-test.chinacloudapp.cn/api/Conversation/GetResponse?api-version=2017-06-15-Int";

  //  public static final String XICONVERSATION_CONVERSATIONResponseURI = "http://cs-api-gateway-test.chinacloudapp.cn/api/Conversation/GetResponse?api-version=2017-06-15-Int";

    public static final String XICONVERSATION_REQUESTTYPE_TEXT = "text";
    public static final String XICONVERSATION_REQUESTTYPE_AMRSPEECH = "amrspeech";
    public static final String XICONVERSATION_REQUESTTYPE_IMAGE = "image";
    public static final String XICONVERSATION_REQUESTTYPE_WAVSPEECH = "wavspeech";

    //Response
    public static final String XICONVERSATION_SERVICE = "Service";
    public static final String XICONVERSATION_ANSWER = "Answer";
    public static final String XICONVERSATION_ERROR = "Error";
    public static final String XICONVERSATION_ERRORCode = "Code";

    public static final String XICONVERSATION_SERVICETYPE_CHAT = "Chat";
    public static final String XICONVERSATION_SERVICETYPE_FAQ = "FAQ";

    public static final String XICONVERSATION_RESPONSETYPE_TEXT = "Text";
    public static final String XICONVERSATION_RESPONSETYPE_SPEECH = "Speech";
    public static final String XICONVERSATION_RESPONSETYPE_IMAGE = "Image";
    public static final String XICONVERSATION_RESPONSETYPE_MULTIPLE = "Card";

    //Text Image card speech
    public static final String XICONVERSATION_RESPONSETYPE = "Type";
    public static final String XICONVERSATION_RESPONSETYPE_TEXTCONTENT = "Content";
    public static final String XICONVERSATION_RESPONSETYPE_IMAGEURL = "Url";
    public static final String XICONVERSATION_RESPONSETYPE_SPEECHURL = "Url";
    public static final String XICONVERSATION_RESPONSETYPE_CARDURL = "Title";
    public static final String XICONVERSATION_RESPONSETYPE_CARDTITLE = "Url";
    public static final String XICONVERSATION_RESPONSETYPE_CARDDES = "Description";
    public static final String XICONVERSATION_RESPONSETYPE_CARDCOV = "CoverUrl";
    public static final String XICONVERSATION_RESPONSETYPE_DURATION = "Duration";
    public static final String XICONVERSATION_RESPONSETYPE_CREATETIME = "CreateTime";
    public static final String XICONVERSATION_RESPONSETYPE_FEEDID = "FeedID";
    public static final String XICONVERSATION_RESPONSETYPE_RESERVED1 = "Reserved1";

    public static final int FROM_USER_MSG = 0;
    public static final int TO_USER_MSG = 1;
    public static final int FROM_USER_IMG = 2;
    public static final int TO_USER_IMG = 3;
    public static final int FROM_USER_VOICE = 4;
    public static final int TO_USER_VOICE = 5;
    public static final int FROM_USER_MULTILE = 6;
    public static final int FROM_USER_LOADING = 7;
    public static final int FROM_USER_ERROR = 8;

    //permission
    public static final int XICONVERSATION_PERMISSION_STORAGE = 1000;
    public static final int XICONVERSATION_PERMISSION_CAMERA = 1001;
    public static final int XICONVERSATION_PERMISSION_AUDIO = 1002;
    public static final int XICONVERSATION_PERMISSION_DB = 1003;
    public static final int XICONVERSATION_PERMISSION_OPENDB = 1004;
    public static final int XICONVERSATION_PERMISSION_WRITE_EXTERNAL = 10001;
    public static final int XICONVERSATION_PERMISSION_READ_EXTERNAL = 10002;

    public static final String XIBASEPATH = "/XISDK/Conversition/";
    public static final String XIVOICEDATAPATH = "voice_data/";
    public static final String XIIMAGEDATAPATH = "image_data/";
    public static final String XIORIGINALIMAGEDATAPATH = "imageoriginal_data/";

    public static final String XICURRENTURL = "xiconversation_currentURL";
    public static final String XICURRENTTEXT ="xiconversation_currentText";

    public static final String XILANDINGPAGEMAINPAGECURRURL = "xilandingpage_mainpage_currentURL";
    public static final String XILANDINGPAGEMAINPAGECURRTEXT = "xilandingpage_mainpage_currentText";

    public static final String XIXIAOICERESPONSEERROR ="xiconversation_responseerror";

    @IntDef({XICONVERSATION_SENDING, XICONVERSATION_COMPLETED, XICONVERSATION_SENDERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SendState {
    }

}
