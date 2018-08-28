package com.ldx.landingpage.choosedialog.bundle;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.ldx.landingpage.R;
import com.ldx.landingpage.choosedialog.enums.XIEPickType;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * dialog style
 */
public class XIPickSetup implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private int titleColor;

    private int backgroundColor;

    private String progressText;
    private int progressTextColor;

    private String cancelText;
    private int cancelTextColor;

    private int cameraTextColor;
    private int galleryTextColor;

    private float dimAmount;
    private boolean flip;

    private int maxSize = 300;
    private int width = 0;
    private int height = 0;

    private XIEPickType[] pickTypes;

    private int cameraIcon;
    private int galleryIcon;

    private String cameraButtonText;
    private String galleryButtonText;

    private boolean systemDialog;

    private int titleTextSize;
    private int cameraTextSize;
    private int galleryTextSize;

    private int cancleTextSize;

    @OrientationMode
    private int buttonOrientation;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LinearLayout.VERTICAL, LinearLayout.HORIZONTAL})
    public @interface OrientationMode {

    }

    @IconGravity
    private int iconGravity;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Gravity.LEFT, Gravity.BOTTOM, Gravity.RIGHT, Gravity.TOP})
    public @interface IconGravity {
    }

    public int getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
    }

    public int getCameraTextSize() {
        return cameraTextSize;
    }

    public void setCameraTextSize(int cameraTextSize) {
        this.cameraTextSize = cameraTextSize;
    }

    public int getGalleryTextSize() {
        return galleryTextSize;
    }

    public void setGalleryTextSize(int galleryTextSize) {
        this.galleryTextSize = galleryTextSize;
    }

    public int getCancleTextSize() {
        return cancleTextSize;
    }

    public void setCancleTextSize(int cancleTextSize) {
        this.cancleTextSize = cancleTextSize;
    }

    public String getCancelText() {
        return cancelText;
    }

    public XIPickSetup setCancelText(String text) {
        this.cancelText = text;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public XIPickSetup setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public XIPickSetup setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public XIPickSetup setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public String getCameraButtonText() {
        return cameraButtonText;
    }

    public XIPickSetup setCameraButtonText(String cameraButtonText) {
        this.cameraButtonText = cameraButtonText;
        return this;
    }

    public String getGalleryButtonText() {
        return galleryButtonText;
    }

    public XIPickSetup setGalleryButtonText(String galleryButtonText) {
        this.galleryButtonText = galleryButtonText;
        return this;
    }

    public int getCameraTextColor() {
        return cameraTextColor;
    }

    public XIPickSetup setCameraTextColor(int buttonTextColor) {
        this.cameraTextColor = buttonTextColor;
        return this;
    }

    public int getGalleryTextColor() {
        return galleryTextColor;
    }

    public XIPickSetup setGalleryTextColor(int buttonTextColor) {
        this.galleryTextColor = buttonTextColor;
        return this;
    }

    public float getDimAmount() {
        return dimAmount;
    }

    public XIPickSetup setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        return this;
    }


    public XIEPickType[] getPickTypes() {
        return pickTypes;
    }

    public XIPickSetup setPickTypes(XIEPickType... pickTypes) {
        this.pickTypes = pickTypes;
        return this;
    }

    public int getCancelTextColor() {
        return cancelTextColor;
    }

    public XIPickSetup setCancelTextColor(int cancelTextColor) {
        this.cancelTextColor = cancelTextColor;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public XIPickSetup setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public XIPickSetup setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public XIPickSetup setHeight(int height) {
        this.height = height;
        return this;
    }

    public boolean isFlipped() {
        return flip;
    }

    public XIPickSetup setFlip(boolean flip) {
        this.flip = flip;
        return this;
    }


    public int getProgressTextColor() {
        return progressTextColor;
    }

    public XIPickSetup setProgressTextColor(int progressTextColor) {
        this.progressTextColor = progressTextColor;
        return this;
    }

    public String getProgressText() {
        return progressText;
    }

    public XIPickSetup setProgressText(String progressText) {
        this.progressText = progressText;
        return this;
    }

    @OrientationMode
    public int getButtonOrientation() {
        return buttonOrientation;
    }

    public XIPickSetup setButtonOrientation(@OrientationMode int buttonOrientation) {
        this.buttonOrientation = buttonOrientation;
        return this;
    }

    public XIPickSetup setButtonOrientationInt(int buttonOrientation) {
        this.buttonOrientation = buttonOrientation;
        return this;
    }


    public int getCameraIcon() {
        return cameraIcon;
    }

    public XIPickSetup setCameraIcon(int cameraIcon) {
        this.cameraIcon = cameraIcon;
        return this;
    }

    public int getGalleryIcon() {
        return galleryIcon;
    }

    public XIPickSetup setGalleryIcon(int galleryIcon) {
        this.galleryIcon = galleryIcon;
        return this;
    }

    public boolean isSystemDialog() {
        return systemDialog;
    }

    public XIPickSetup setSystemDialog(boolean systemDialog) {
        this.systemDialog = systemDialog;
        return this;
    }

    @IconGravity
    public int getIconGravity() {
        return iconGravity;
    }

    public void setIconGravity(@IconGravity int iconGravity) {
        this.iconGravity = iconGravity;
    }

    public void setIconGravityInt(int iconGravity) {
        this.iconGravity = iconGravity;
    }

    public XIPickSetup() {
        this.title = "";
        this.titleColor = -1;
        this.backgroundColor = -1;
        this.progressText = "";
        this.progressTextColor = -1;
        this.cancelText = "";
        this.cancelTextColor = -1;
        this.cameraTextColor = -1;
        this.galleryTextColor = -1;
        this.cameraButtonText = "";
        this.galleryButtonText = "";
        this.systemDialog = false;
        this.titleTextSize = -1;
        this.cameraTextSize = -1;
        this.galleryTextSize = -1;
        this.cancleTextSize = -1;

        setBackgroundColor(Color.WHITE)
                .setTitleColor(Color.DKGRAY)
                .setDimAmount(0.3f)
                .setFlip(false)
                .setMaxSize(300)
                .setWidth(0)
                .setHeight(0)
                .setPickTypes(XIEPickType.CAMERA, XIEPickType.GALLERY)
                .setButtonOrientation(LinearLayout.VERTICAL)
                .setCameraIcon(R.drawable.xilandingpage_dialog_img_cameracolor)
                .setGalleryIcon(R.drawable.xilandingpage_img_gallerycolor)
                .setSystemDialog(false);
    }

}
