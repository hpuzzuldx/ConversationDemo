package com.ldx.landingpage.choosedialog.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldx.landingpage.R;
import com.ldx.landingpage.choosedialog.async.XIAsyncImageResult;
import com.ldx.landingpage.choosedialog.bean.XIPickResult;
import com.ldx.landingpage.choosedialog.bundle.XIPickSetup;
import com.ldx.landingpage.choosedialog.enums.XIEPickType;
import com.ldx.landingpage.choosedialog.listeners.XIIPickClick;
import com.ldx.landingpage.choosedialog.listeners.XIIPickResult;
import com.ldx.landingpage.choosedialog.resolver.XIIntentResolver;
import com.ldx.landingpage.choosedialog.util.XIBackgroundUtil;
import com.ldx.landingpage.common.XILandingpageConsts;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

public abstract class XIPickImageBaseDialog extends DialogFragment implements XIIPickClick {
    //identify fragment dialog
    protected static final String XILANDINGPAGE_IDENTIFY_SETUP = "XILandingPageBaseDialog";
    public static final String XILANDINGPAGE_IDENTIFY_DIALOG_FRAGMENT = XIPickImageBaseDialog.class.getSimpleName();

    private XIPickSetup setup;
    private XIIntentResolver resolver;

    private boolean showCamera = true;
    private boolean showGallery = true;

    private LinearLayout card;
    private LinearLayout llButtons;
    private TextView tvTitle;
    private TextView tvCamera;
    private TextView tvGallery;
    private TextView tvCancel;
    private TextView tvProgress;

    private LinearLayout vFirstLayer;
    private LinearLayout vSecondLayer;

    private Boolean validProviders = null;

    private XIIPickResult onPickResult;
    private XIIPickClick onClick;
    private boolean dissMissOther = false;
    private boolean isFromCamera = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xilandingpage_image_choose_dialog, null, false);

        onAttaching();
        onInitialize();
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dissMissOther = true;
                    String errstr = "dismisserror";
                    try{
                        errstr = getString(R.string.xilandingpage_error_dismiss);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    dismiss();
                    Error e = new Error();
                    if (onPickResult != null) {
                        onPickResult.onPickResult(new XIPickResult().setError(e));
                    }
                }
                return true;
            }
        });
        this.getDialog().setCanceledOnTouchOutside(true);
        if (isValidProviders()) {
            onBindViewsHolders(view);

            if (!launchSystemDialog()) {
                onBindViews(view);
                onBindViewListeners();
                onSetup(this.getActivity());
            }
        } else {
            return delayedDismiss();
        }
        return view;
    }

    private void onAttaching() {
        if (onClick == null) {
            if (getActivity() instanceof XIIPickClick) {
                onClick = (XIIPickClick) getActivity();
            } else {
                onClick = this;
            }
        }

        if (onPickResult == null && getActivity() instanceof XIIPickResult)
            onPickResult = (XIIPickResult) getActivity();
    }

    protected void onInitialize() {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        this.setup = (XIPickSetup) getArguments().getSerializable(XILANDINGPAGE_IDENTIFY_SETUP);
        this.resolver = new XIIntentResolver(getActivity(), setup);
    }

    private boolean isValidProviders() {
        if (validProviders == null) {
            validProviders = true;

            showCamera = XIEPickType.CAMERA.inside(setup.getPickTypes()) && ((onClick == null) || (resolver.isCamerasAvailable() && !resolver.wasCameraPermissionDeniedForever()));
            showGallery = XIEPickType.GALLERY.inside(setup.getPickTypes());

            if (!(showCamera || showGallery)) {
                Error e = new Error(getString(R.string.xilandingpage_no_providers));

                validProviders = false;

                if (onPickResult != null) {
                    onError(e);
                } else {
                    throw e;
                }
            }
        }
        return validProviders;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dissMissOther) {
            dissMissOther = false;
        } else {
            String errstr = "dismisserror";
            try{
                errstr = getString(R.string.xilandingpage_error_dismiss);
            }catch (Exception e){
                e.printStackTrace();
            }

            Error e = new Error(errstr);
            if (onPickResult != null) {
                onPickResult.onPickResult(new XIPickResult().setError(e));
            }
        }
    }

    private void onBindViewsHolders(View v) {
        card = (LinearLayout) v.findViewById(R.id.xilandingpage_ll_dialog_container);
        vFirstLayer = (LinearLayout) v.findViewById(R.id.xilandingpage_ll_first_layer);
        vSecondLayer = (LinearLayout) v.findViewById(R.id.xilandingpage_ll_second_layer);
    }

    private void onBindViews(View v) {
        llButtons = (LinearLayout) v.findViewById(R.id.xilandingpage_ll_buttons_holder);
        tvTitle = (TextView) v.findViewById(R.id.xilandingpage_tv_dialog_title);
        tvCamera = (TextView) v.findViewById(R.id.xilandingpage_tv_choose_camera);
        tvGallery = (TextView) v.findViewById(R.id.xilandingpage_tv_choose_gallery);
        tvCancel = (TextView) v.findViewById(R.id.xilandingpage_tv_choose_cancel);
        tvProgress = (TextView) v.findViewById(R.id.xilandingpage_tv_loading_text);
    }

    private void onBindViewListeners() {
        tvCancel.setOnClickListener(listener);
        tvCamera.setOnClickListener(listener);
        tvGallery.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.xilandingpage_tv_choose_cancel) {
                dissMissOther = true;
                Error e = new Error(getString(R.string.xilandingpage_no_providers));
                if (onPickResult != null) {
                    onPickResult.onPickResult(new XIPickResult().setError(e));
                }
                dismiss();
            } else {
                if (view.getId() == R.id.xilandingpage_tv_choose_camera) {
                    onClick.onCameraClick();
                } else if (view.getId() == R.id.xilandingpage_tv_choose_gallery) {
                    onClick.onGalleryClick();
                }
            }
        }
    };

    private void onSetup(Context context) {
        if (setup.getBackgroundColor() != -1) {
            card.setBackgroundColor(setup.getBackgroundColor());
            if (showCamera)
                XIBackgroundUtil.background(tvCamera, XIBackgroundUtil.getAdaptiveRippleDrawable(setup.getBackgroundColor()));

            if (showGallery)
                XIBackgroundUtil.background(tvGallery, XIBackgroundUtil.getAdaptiveRippleDrawable(setup.getBackgroundColor()));
        }

        tvTitle.setTextColor(setup.getTitleColor());

        if (setup.getCameraTextColor() != 0 && setup.getCameraTextColor() != -1) {
            tvCamera.setTextColor(setup.getCameraTextColor());
        }

        if (setup.getTitleTextSize() != -1){
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,setup.getTitleTextSize());
        }

        if (setup.getCameraTextSize() != -1){
            tvCamera.setTextSize(TypedValue.COMPLEX_UNIT_SP,setup.getCameraTextSize());
        }

        if (setup.getGalleryTextSize() != -1){
            tvGallery.setTextSize(TypedValue.COMPLEX_UNIT_SP,setup.getGalleryTextSize());
        }

        if (setup.getCancleTextSize() != -1){
            tvCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP,setup.getCancleTextSize());
        }

        if (setup.getGalleryTextColor() != 0 && setup.getGalleryTextColor() != -1) {
            tvGallery.setTextColor(setup.getGalleryTextColor());
        }

        if (setup.getProgressTextColor() != 0 && setup.getProgressTextColor() != -1)
            tvProgress.setTextColor(setup.getProgressTextColor());

        if (setup.getCancelTextColor() != 0 && setup.getCancelTextColor() != -1 )
            tvCancel.setTextColor(setup.getCancelTextColor());

        if (!TextUtils.isEmpty(setup.getCameraButtonText())){
            tvCamera.setText(setup.getCameraButtonText());
        }

        if (!TextUtils.isEmpty(setup.getGalleryButtonText())){
            tvGallery.setText(setup.getGalleryButtonText());
        }

        if(!TextUtils.isEmpty(setup.getCancelText()))
        tvCancel.setText(setup.getCancelText());

        if(!TextUtils.isEmpty(setup.getTitle()))
        tvTitle.setText(setup.getTitle());

        if(!TextUtils.isEmpty(setup.getProgressText()))
        tvProgress.setText(setup.getProgressText());

        showProgress(false);

        XIBackgroundUtil.gone(tvCamera, !showCamera);
        XIBackgroundUtil.gone(tvGallery, !showGallery);

        llButtons.setOrientation(setup.getButtonOrientation() == LinearLayout.HORIZONTAL ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);

        XIBackgroundUtil.setIcon(tvCamera, context.getResources().getDrawable(setup.getCameraIcon()), setup.getIconGravity());
        XIBackgroundUtil.setIcon(tvGallery, context.getResources().getDrawable(setup.getGalleryIcon()), setup.getIconGravity());

        XIBackgroundUtil.setDimAmount(setup.getDimAmount(), getDialog());
    }

    protected void showProgress(boolean show) {
        XIBackgroundUtil.gone(card, false);
        XIBackgroundUtil.gone(vFirstLayer, show);
        XIBackgroundUtil.gone(vSecondLayer, !show);
    }

    public void setErrorInfo() {
        Error e = new Error(getString(R.string.xilandingpage_no_providers));
        if (onPickResult != null) {
            onPickResult.onPickResult(new XIPickResult().setError(e));
        }
    }

    protected void launchCamera() {
        if (resolver.requestCameraPermissions(this)){
            resolver.launchCamera(this);
            isFromCamera = true;
        }
    }

    protected void launchGallery() {
        isFromCamera = false;
        resolver.launchGallery(this);
    }

    protected boolean launchSystemDialog() {
        if (setup.isSystemDialog()) {
            card.setVisibility(View.GONE);

            if (showCamera) {
                if (resolver.requestCameraPermissions(this))
                    resolver.launchSystemChooser(this);
            } else {
                resolver.launchSystemChooser(this);
            }

            return true;
        } else {
            return false;
        }
    }

    protected XIPickImageBaseDialog setOnPickResult(XIIPickResult onPickResult) {
        this.onPickResult = onPickResult;
        return this;
    }

    protected XIPickImageBaseDialog setOnClick(XIIPickClick onClick) {
        this.onClick = onClick;
        return this;
    }

    protected XIAsyncImageResult getAsyncResult() {
        return new XIAsyncImageResult(getActivity(), setup).setOnFinish(new XIAsyncImageResult.OnFinish() {
            @Override
            public void onFinish(XIPickResult pickResult) {
                if (onPickResult != null)
                    onPickResult.onPickResult(pickResult);
                dismissAllowingStateLoss();
            }
        });
    }

    @Override
    public Context getContext() {
        Context context = super.getContext();

        if ((context == null) && (resolver != null))
            context = resolver.getActivity();

        return context;
    }

    private View delayedDismiss() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dissMissOther = true;
                dismiss();
            }
        }, 20);

        return new View(getContext());
    }

    protected void onError(Error e) {
        if (onPickResult != null) {
            onPickResult.onPickResult(new XIPickResult().setError(e));
            dissMissOther = true;
            dismissAllowingStateLoss();
        }
    }

    public void getAndPermissionCamera(final Context context) {
        try{
            AndPermission.with(context)
                    .requestCode(XILandingpageConsts.XILANDINGPAGE_PERMISSION_CAMERA)
                    .permission(Permission.CAMERA, Permission.STORAGE)
                    .callback(permissionListener)
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                            AndPermission.rationaleDialog(context, rationale).show();
                        }
                    })
                    .start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getAndPermissionStroage(final Context context) {
        try {
            AndPermission.with(context)
                    .requestCode(XILandingpageConsts.XILANDINGPAGE_PERMISSION_GALLERY)
                    .permission(Permission.STORAGE)
                    .callback(permissionListener)
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                            AndPermission.rationaleDialog(context, rationale).show();
                        }
                    })
                    .start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case XILandingpageConsts.XILANDINGPAGE_PERMISSION_CAMERA: {
                    if (AndPermission.hasPermission(getContext(), grantPermissions)) {
                       launchCamera();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.xilandingpage_camera_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                case XILandingpageConsts.XILANDINGPAGE_PERMISSION_GALLERY: {
                    if (AndPermission.hasPermission(getContext(), grantPermissions)) {
                        launchGallery();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.xilandingpage_gallery_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case XILandingpageConsts.XILANDINGPAGE_PERMISSION_CAMERA: {
                    if (AndPermission.hasPermission(getContext(), deniedPermissions)) {
                        launchCamera();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.xilandingpage_camera_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                case XILandingpageConsts.XILANDINGPAGE_PERMISSION_GALLERY: {
                    if (AndPermission.hasPermission(getContext(), deniedPermissions)) {
                        launchGallery();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.xilandingpage_gallery_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }

            if (AndPermission.hasAlwaysDeniedPermission(getContext(), deniedPermissions)) {
                AndPermission.defaultSettingDialog((AppCompatActivity) getContext(), 300).show();
            }
        }
    };

}
