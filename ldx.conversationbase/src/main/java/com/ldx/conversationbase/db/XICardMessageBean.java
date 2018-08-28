package com.ldx.conversationbase.db;

/**
 * Created by v-doli1 on 2017/9/4.
 * use for card view list
 */

public class XICardMessageBean {
    private long mesgId;
    private int mesgType;
    private String mesgUrl;
    private String mesgTitle;
    private String mesgDescription;
    private String mesgCoverurl;
    private long   mesgTime;
    private String feedId;
    private String reserved1;

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

    public int getMesgType() {
        return mesgType;
    }

    public void setMesgType(int mesgType) {
        this.mesgType = mesgType;
    }

    public String getMesgUrl() {

        return mesgUrl;
    }

    public void setMesgUrl(String mesgUrl) {
        this.mesgUrl = mesgUrl;
    }

    public String getMesgTitle() {
        return mesgTitle;
    }

    public void setMesgTitle(String mesgTitle) {
        this.mesgTitle = mesgTitle;
    }

    public String getMesgDescription() {
        return mesgDescription;
    }

    public void setMesgDescription(String mesgDescription) {
        this.mesgDescription = mesgDescription;
    }

    public String getMesgCoverurl() {
        return mesgCoverurl;
    }

    public void setMesgCoverurl(String mesgCoverurl) {
        this.mesgCoverurl = mesgCoverurl;
    }

    public long getMesgId() {
        return mesgId;
    }

    public void setMesgId(long mesgId) {
        this.mesgId = mesgId;
    }

    public long getMesgTime() {
        return mesgTime;
    }

    public void setMesgTime(long mesgTime) {
        this.mesgTime = mesgTime;
    }

}
