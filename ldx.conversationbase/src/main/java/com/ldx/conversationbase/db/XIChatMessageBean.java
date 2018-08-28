package com.ldx.conversationbase.db;

import com.ldx.conversationbase.common.XIChatConst;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class XIChatMessageBean {
    @Id
    private Long id;
    @Property(nameInDb = "UserId")
    private String UserId;
    @Property(nameInDb = "UserName")
    private String UserName;
    @Property(nameInDb = "UserHeadIcon")
    private String UserHeadIcon;
    @Property(nameInDb = "UserContent")
    private String UserContent;
    @Property(nameInDb = "time")
    private String time;
    @Property(nameInDb = "type")
    private int type;
    @Property(nameInDb = "Messagetype")
    private int Messagetype;
    @Property(nameInDb = "UserVoiceTime")
    private float UserVoiceTime;
    @Property(nameInDb = "UserVoicePath")
    private String UserVoicePath;
    @Property(nameInDb = "UserVoiceUrl")
    private String UserVoiceUrl;
    @Property(nameInDb = "sendState")
    private @XIChatConst.SendState
    int sendState;
    @Property(nameInDb = "imageUrl")
    private String imageUrl;
    @Property(nameInDb = "imageIconUrl")
    private String imageIconUrl;
    @Property(nameInDb = "imageLocal")
    private String imageLocal;
    @Property(nameInDb = "imageOriginal")
    private String imageOriginal;
    @Property(nameInDb = "jsonString")
    private String jsonString;
    @Property(nameInDb =  "alreadyReady")
    private boolean alreadyReady;
    @Property(nameInDb = "cardTitle")
    private String cardTitle;
    @Property(nameInDb = "cardDescription")
    private String cardDescription;
    @Property(nameInDb = "webUrl")
    private String webUrl;
    @Property(nameInDb = "imgWidth")
    private int imgWidth;
    @Property(nameInDb = "imgHeight")
    private int imgHeight;
    @Property(nameInDb = "createTime")
    private long createTime;
    @Property(nameInDb = "timeStamp")
    private long timeStamp;
    @Property(nameInDb = "isGif")
    private boolean isGif = false;
    @Property(nameInDb = "feedId")
    private String feedId;
    @Property(nameInDb = "reserved1")
    private String reserved1;
    @Generated(hash = 901107279)
    public XIChatMessageBean(Long id, String UserId, String UserName,
            String UserHeadIcon, String UserContent, String time, int type,
            int Messagetype, float UserVoiceTime, String UserVoicePath,
            String UserVoiceUrl, int sendState, String imageUrl,
            String imageIconUrl, String imageLocal, String imageOriginal,
            String jsonString, boolean alreadyReady, String cardTitle,
            String cardDescription, String webUrl, int imgWidth, int imgHeight,
            long createTime, long timeStamp, boolean isGif, String feedId,
            String reserved1) {
        this.id = id;
        this.UserId = UserId;
        this.UserName = UserName;
        this.UserHeadIcon = UserHeadIcon;
        this.UserContent = UserContent;
        this.time = time;
        this.type = type;
        this.Messagetype = Messagetype;
        this.UserVoiceTime = UserVoiceTime;
        this.UserVoicePath = UserVoicePath;
        this.UserVoiceUrl = UserVoiceUrl;
        this.sendState = sendState;
        this.imageUrl = imageUrl;
        this.imageIconUrl = imageIconUrl;
        this.imageLocal = imageLocal;
        this.imageOriginal = imageOriginal;
        this.jsonString = jsonString;
        this.alreadyReady = alreadyReady;
        this.cardTitle = cardTitle;
        this.cardDescription = cardDescription;
        this.webUrl = webUrl;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.createTime = createTime;
        this.timeStamp = timeStamp;
        this.isGif = isGif;
        this.feedId = feedId;
        this.reserved1 = reserved1;
    }
    @Generated(hash = 143456767)
    public XIChatMessageBean() {
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public boolean isGif() {
        return isGif;
    }

    public void setGif(boolean gif) {
        isGif = gif;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserId() {
        return this.UserId;
    }
    public void setUserId(String UserId) {
        this.UserId = UserId;
    }
    public String getUserName() {
        return this.UserName;
    }
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }
    public String getUserHeadIcon() {
        return this.UserHeadIcon;
    }
    public void setUserHeadIcon(String UserHeadIcon) {
        this.UserHeadIcon = UserHeadIcon;
    }
    public String getUserContent() {
        return this.UserContent;
    }
    public void setUserContent(String UserContent) {
        this.UserContent = UserContent;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getMessagetype() {
        return this.Messagetype;
    }
    public void setMessagetype(int Messagetype) {
        this.Messagetype = Messagetype;
    }
    public float getUserVoiceTime() {
        return this.UserVoiceTime;
    }
    public void setUserVoiceTime(float UserVoiceTime) {
        this.UserVoiceTime = UserVoiceTime;
    }
    public String getUserVoicePath() {
        return this.UserVoicePath;
    }
    public void setUserVoicePath(String UserVoicePath) {
        this.UserVoicePath = UserVoicePath;
    }
    public String getUserVoiceUrl() {
        return this.UserVoiceUrl;
    }
    public void setUserVoiceUrl(String UserVoiceUrl) {
        this.UserVoiceUrl = UserVoiceUrl;
    }
    public int getSendState() {
        return this.sendState;
    }
    public void setSendState(int sendState) {
        this.sendState = sendState;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getImageIconUrl() {
        return this.imageIconUrl;
    }
    public void setImageIconUrl(String imageIconUrl) {
        this.imageIconUrl = imageIconUrl;
    }
    public String getImageLocal() {
        return this.imageLocal;
    }
    public void setImageLocal(String imageLocal) {
        this.imageLocal = imageLocal;
    }
    public String getImageOriginal() {
        return this.imageOriginal;
    }
    public void setImageOriginal(String imageOriginal) {
        this.imageOriginal = imageOriginal;
    }
    public String getJsonString() {
        return this.jsonString;
    }
    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
    public boolean getAlreadyReady() {
        return this.alreadyReady;
    }
    public void setAlreadyReady(boolean alreadyReady) {
        this.alreadyReady = alreadyReady;
    }
    public String getCardTitle() {
        return this.cardTitle;
    }
    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }
    public String getCardDescription() {
        return this.cardDescription;
    }
    public void setCardDescription(String cardDescription) {
        this.cardDescription = cardDescription;
    }
    public String getWebUrl() {
        return this.webUrl;
    }
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
    public int getImgWidth() {
        return this.imgWidth;
    }
    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }
    public int getImgHeight() {
        return this.imgHeight;
    }
    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }
    public long getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public long getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public boolean getIsGif() {
        return this.isGif;
    }
    public void setIsGif(boolean isGif) {
        this.isGif = isGif;
    }
}
