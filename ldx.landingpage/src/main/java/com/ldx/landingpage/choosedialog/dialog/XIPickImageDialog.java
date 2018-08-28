package com.ldx.landingpage.choosedialog.dialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.ldx.landingpage.choosedialog.bundle.XIPickSetup;
import com.ldx.landingpage.choosedialog.keep.XIKeep;
import com.ldx.landingpage.choosedialog.listeners.XIIPickClick;
import com.ldx.landingpage.choosedialog.listeners.XIIPickResult;
import com.ldx.landingpage.choosedialog.resolver.XIIntentResolver;
import com.ldx.landingpage.common.XILandingpageConsts;

import static android.app.Activity.RESULT_OK;


public class XIPickImageDialog extends XIPickImageBaseDialog {

    public static XIPickImageDialog newInstance(XIPickSetup setup) {
        XIPickImageDialog frag = new XIPickImageDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(XILANDINGPAGE_IDENTIFY_SETUP, setup);
        frag.setArguments(bundle);
        return frag;
    }

    public static XIPickImageDialog build(XIPickSetup setup, XIIPickResult pickResult) {
        XIPickImageDialog d = XIPickImageDialog.newInstance(setup);
        d.setOnPickResult(pickResult);
        return d;
    }

    public static XIPickImageDialog build(Context context, XIIPickResult pickResult) {
        return build(new XIPickSetup(), pickResult);
    }

    public static XIPickImageDialog build(Context context, XIPickSetup picksetup, XIIPickResult pickResult) {

        return build(picksetup, pickResult);
    }

    public static XIPickImageDialog build(XIPickSetup setup) {
        return build(setup, null);
    }

    public static XIPickImageDialog build() {
        return build();
    }

    public XIPickImageDialog show(FragmentActivity fragmentActivity) {
        return show(fragmentActivity.getSupportFragmentManager());
    }

    public XIPickImageDialog show(FragmentManager fragmentManager) {
        super.show(fragmentManager, XILANDINGPAGE_IDENTIFY_DIALOG_FRAGMENT);
        return this;
    }

    @Override
    public void onCameraClick() {
        //launchCamera();
        getAndPermissionCamera(getContext());
    }

    @Override
    public void onGalleryClick() {
        //launchGallery();
        getAndPermissionStroage(getContext());
    }

    @Override
    public XIPickImageDialog setOnClick(XIIPickClick onClick) {
        return (XIPickImageDialog) super.setOnClick(onClick);
    }

    @Override
    public XIPickImageDialog setOnPickResult(XIIPickResult onPickResult) {
        return (XIPickImageDialog) super.setOnPickResult(onPickResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            dismiss();
            dismissAllowingStateLoss();
            setErrorInfo();
            return;
        }

        if ((requestCode == XILandingpageConsts.XILANDINGPAGE_OPEN_CAMERA
                || requestCode == XILandingpageConsts.XILANDINGPAGE_OPEN_GALLERY
                || requestCode == XILandingpageConsts.XILANDINGPAGE_OPEN_SYSTEMDIALOG)
                && resultCode == RESULT_OK) {
            if (requestCode == XILandingpageConsts.XILANDINGPAGE_OPEN_CAMERA ){

            }else if(requestCode == XILandingpageConsts.XILANDINGPAGE_OPEN_GALLERY){

            }
            showProgress(true);
        } else {
            dismissAllowingStateLoss();
        }
        getAsyncResult().execute(data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == XIIntentResolver.REQUESTER) {
            boolean granted = true;

            for (Integer i : grantResults)
                granted = granted && i == PackageManager.PERMISSION_GRANTED;

            if (granted) {
                if (!launchSystemDialog())
                    launchCamera();
            } else {
                dismissAllowingStateLoss();
            }

            XIKeep.with(getActivity()).askedForPermission();
        }
    }

    public static void forceDismiss(FragmentManager fm) {
        Fragment fragment = fm.findFragmentByTag(XILANDINGPAGE_IDENTIFY_DIALOG_FRAGMENT);

        if (fragment != null) {
            DialogFragment dialog = (XIPickImageDialog) fragment;

            if (dialog.isVisible())
                dialog.dismiss();
        }
    }


}


