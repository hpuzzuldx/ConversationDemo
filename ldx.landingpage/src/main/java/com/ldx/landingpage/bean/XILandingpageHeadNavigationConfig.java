package com.ldx.landingpage.bean;

/**
 * @author lidongxiu
 */

public class XILandingpageHeadNavigationConfig {

    private int navigationHeadBackgroundResource;
    private int navigationHeadBackgroundColor;
    private String navigationHeadTitle;
    private int  navigationHeadTitleColor;
    private int  navigationHeadTitleSize;
    private int navigationBackImage;
    private int navigationBackImageLeftPadding;
    private int activityStatusbarColor;
    private String displayUrl;

    public XILandingpageHeadNavigationConfig() {
        this.navigationHeadBackgroundResource = -1;
        this.navigationHeadBackgroundColor = -1;
        this.navigationHeadTitle = "";
        this.navigationHeadTitleColor = -1;
        this.navigationHeadTitleSize = -1;
        this.navigationBackImage = -1;
        this.navigationBackImageLeftPadding = -1;
        this.activityStatusbarColor = -1;
        this.displayUrl = "";
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public int getActivityStatusbarColor() {
        return activityStatusbarColor;
    }

    public void setActivityStatusbarColor(int activityStatusbarColor) {
        this.activityStatusbarColor = activityStatusbarColor;
    }

    public int getNavigationHeadBackgroundResource() {
        return navigationHeadBackgroundResource;
    }

    public void setNavigationHeadBackgroundResource(int navigationHeadBackgroundResource) {
        this.navigationHeadBackgroundResource = navigationHeadBackgroundResource;
    }

    public int getNavigationHeadBackgroundColor() {
        return navigationHeadBackgroundColor;
    }

    public void setNavigationHeadBackgroundColor(int navigationHeadBackgroundColor) {
        this.navigationHeadBackgroundColor = navigationHeadBackgroundColor;
    }

    public String getNavigationHeadTitle() {
        return navigationHeadTitle;
    }

    public void setNavigationHeadTitle(String navigationHeadTitle) {
        this.navigationHeadTitle = navigationHeadTitle;
    }

    public int getNavigationHeadTitleColor() {
        return navigationHeadTitleColor;
    }

    public void setNavigationHeadTitleColor(int navigationHeadTitleColor) {
        this.navigationHeadTitleColor = navigationHeadTitleColor;
    }

    public int getNavigationHeadTitleSize() {
        return navigationHeadTitleSize;
    }

    public void setNavigationHeadTitleSize(int navigationHeadTitleSize) {
        this.navigationHeadTitleSize = navigationHeadTitleSize;
    }

    public int getNavigationBackImage() {
        return navigationBackImage;
    }

    public void setNavigationBackImage(int navigationBackImage) {
        this.navigationBackImage = navigationBackImage;
    }

    public int getNavigationBackImageLeftPadding() {
        return navigationBackImageLeftPadding;
    }

    public void setNavigationBackImageLeftPadding(int navigationBackImageLeftPadding) {
        this.navigationBackImageLeftPadding = navigationBackImageLeftPadding;
    }

}
