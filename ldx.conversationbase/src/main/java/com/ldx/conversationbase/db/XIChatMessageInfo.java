package com.ldx.conversationbase.db;

/**
 * Created by v-doli1 on 2017/9/15.
 */

public class XIChatMessageInfo {
    private long   messageID;
    private String messageContent;
    private String messageVoiceUrl;
    private String messageVoicePath;
    private float messageVoiceLength;
    private String messageImageLocalPath;
    private String messageImageUrl;
    private String messageCardUrl;
    private String messageCardTitle;
    private String messageCardDescription;
    private String messageCardcoverurl;
    private long messageCardNewsTime;
    private boolean messageIsSend;
    private int messageType;

    private String messageChatTime;

    public  XIChatMessageInfo(){
        this.messageID = -1;
        this.messageContent = "";
        this.messageVoiceUrl = "";
        this.messageVoicePath = "";
        this.messageVoiceLength = -1;
        this.messageImageLocalPath = "";
        this.messageImageUrl = "";
        this.messageCardUrl = "";
        this.messageCardTitle = "";
        this.messageCardDescription = "";
        this.messageCardcoverurl = "";
        this.messageCardNewsTime = -1;
        this.messageIsSend = false;
        this.messageType = -1;
        this.messageChatTime = "";
    }

    public XIChatMessageInfo(long messageID,
                             String messageContent,
                             String messageVoiceUrl,
                             String messageVoicePath,
                             float messageVoiceLength,
                             String messageImageLocalPath,
                             String messageImageUrl,
                             String messageCardUrl,
                             String messageCardTitle,
                             String messageCardDescription,
                             String messageCardcoverurl,
                             long messageCardNewsTime,
                             boolean messageIsSend,
                             int messageType,
                             String messageChatTime) {
        this.messageID = messageID;
        this.messageContent = messageContent;
        this.messageVoiceUrl = messageVoiceUrl;
        this.messageVoicePath = messageVoicePath;
        this.messageVoiceLength = messageVoiceLength;
        this.messageImageLocalPath = messageImageLocalPath;
        this.messageImageUrl = messageImageUrl;
        this.messageCardUrl = messageCardUrl;
        this.messageCardTitle = messageCardTitle;
        this.messageCardDescription = messageCardDescription;
        this.messageCardcoverurl = messageCardcoverurl;
        this.messageCardNewsTime = messageCardNewsTime;
        this.messageIsSend = messageIsSend;
        this.messageType = messageType;
        this.messageChatTime = messageChatTime;
    }

    public String getMessageVoicePath() {
        return messageVoicePath;
    }

    public void setMessageVoicePath(String messageVoicePath) {
        this.messageVoicePath = messageVoicePath;
    }

    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageVoiceUrl() {
        return messageVoiceUrl;
    }

    public void setMessageVoiceUrl(String messageVoiceUrl) {
        this.messageVoiceUrl = messageVoiceUrl;
    }

    public float getMessageVoiceLength() {
        return messageVoiceLength;
    }

    public void setMessageVoiceLength(float messageVoiceLength) {
        this.messageVoiceLength = messageVoiceLength;
    }

    public String getMessageImageLocalPath() {
        return messageImageLocalPath;
    }

    public void setMessageImageLocalPath(String messageImageLocalPath) {
        this.messageImageLocalPath = messageImageLocalPath;
    }

    public String getMessageImageUrl() {
        return messageImageUrl;
    }

    public void setMessageImageUrl(String messageImageUrl) {
        this.messageImageUrl = messageImageUrl;
    }

    public String getMessageCardUrl() {
        return messageCardUrl;
    }

    public void setMessageCardUrl(String messageCardUrl) {
        this.messageCardUrl = messageCardUrl;
    }

    public String getMessageCardTitle() {
        return messageCardTitle;
    }

    public void setMessageCardTitle(String messageCardTitle) {
        this.messageCardTitle = messageCardTitle;
    }

    public String getMessageCardDescription() {
        return messageCardDescription;
    }

    public void setMessageCardDescription(String messageCardDescription) {
        this.messageCardDescription = messageCardDescription;
    }

    public String getMessageCardcoverurl() {
        return messageCardcoverurl;
    }

    public void setMessageCardcoverurl(String messageCardcoverurl) {
        this.messageCardcoverurl = messageCardcoverurl;
    }

    public long getMessageCardNewsTime() {
        return messageCardNewsTime;
    }

    public void setMessageCardNewsTime(long messageCardNewsTime) {
        this.messageCardNewsTime = messageCardNewsTime;
    }

    public boolean isMessageIsSend() {
        return messageIsSend;
    }

    public void setMessageIsSend(boolean messageIsSend) {
        this.messageIsSend = messageIsSend;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageChatTime() {
        return messageChatTime;
    }

    public void setMessageChatTime(String messageChatTime) {
        this.messageChatTime = messageChatTime;
    }
}
