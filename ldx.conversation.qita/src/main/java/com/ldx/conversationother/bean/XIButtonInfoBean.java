package com.ldx.conversationother.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by v-doli1 on 2018/4/2.
 */

public class XIButtonInfoBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String service;
    private String impressionID;
    private ArrayList<String> contentList;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getImpressionID() {
        return impressionID;
    }

    public void setImpressionID(String impressionID) {
        this.impressionID = impressionID;
    }

    public ArrayList<String> getContentList() {
        return contentList;
    }

    public void setContentList(ArrayList<String> contentList) {
        this.contentList = contentList;
    }
}
