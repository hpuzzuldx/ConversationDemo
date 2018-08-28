package com.ldx.conversation.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ldx.conversation.R;
import com.ldx.conversation.common.XIChatConst;
import com.ldx.conversation.utils.XIFileSaveUtil;
import com.ldx.conversation.view.MulLineEditText;
import com.ldx.conversation.view.XIAudioRecordButton;
import com.ldx.conversation.view.XIImagePickDialog;
import com.ldx.conversationbase.camera.XICameraActivity;
import com.ldx.conversationbase.common.bean.XIConversationChatPageConfig;
import com.ldx.conversationbase.listener.XIChatCardClickListener;
import com.ldx.conversationbase.listener.XIChatCardboardScroll;
import com.ldx.conversationbase.listener.XIChatMessageDefaultLongClickListener;
import com.ldx.conversationbase.listener.XIChatMessageDisplayConfigListener;
import com.ldx.conversationbase.listener.XIChatMessageLongClickListener;
import com.ldx.conversationbase.listener.XIUserHeadIconClickListener;
import com.ldx.conversationbase.listener.XIXiaoiceHeadIconClickListener;
import com.ldx.conversationbase.utils.XIImageCheckoutUtil;
import com.ldx.conversationbase.utils.XIKeyBoardUtils;
import com.ldx.conversationbase.utils.XIPictureUtil;
import com.ldx.conversationbase.view.XIConversationChatView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class XIConversationChatPage extends AppCompatActivity {

    private MulLineEditText mMulTextView;
    private ImageView mSendView;
    private XIAudioRecordButton voiceBtn;
    private ImageView mVoiceImage;
    private ImageView mNaviBack;
    private RelativeLayout mOpenCamera;
    public XIConversationChatView xiConversationChatView;
    private static final int IMAGE_SIZE = XIChatConst.XICONVERSATION_IMAGE_MAXSIZE;

    private String camPicPath;
    private String camOriPicPath;
    private File mCurrentPhotoFile;
    private Toast mToast;
    private XIImagePickDialog mImagePickDialog;

    public static void setXIChatCardClickListener(XIChatCardClickListener xiChatCardClickListener) {
        XIConversationChatView.setXIChatCardClickListener(xiChatCardClickListener);
    }

    public static void setXIConversationChatPageConfig(XIConversationChatPageConfig xiConversationChatPageConfig){
        XIConversationChatView.setXIConversationChatPageConfig(xiConversationChatPageConfig);
    }

    public static void setXiaoiceHeadIconClickListener(XIXiaoiceHeadIconClickListener xiaoiceHeadIconClickListener){
        XIConversationChatView.setXiaoiceHeadIconClickListener(xiaoiceHeadIconClickListener);
    }

    public static void setUserHeadIconClickListener(XIUserHeadIconClickListener userHeadIconClickListener){
        XIConversationChatView.setUserHeadIconClickListener(userHeadIconClickListener);
    }

    public static void setXIChatMessageDisplayConfigListener(XIChatMessageDisplayConfigListener xiChatMessageDisplayConfigListener){
        XIConversationChatView.setXIChatMessageDisplayConfigListener(xiChatMessageDisplayConfigListener);
    }

    public static void setXIChatMessageDefaultLongClickListener(XIChatMessageDefaultLongClickListener xiChatMessageDefaultLongClickListener){
        XIConversationChatView.setXIChatMessageDefaultLongClickListener(xiChatMessageDefaultLongClickListener);
    }

    public static void setXIChatMessageLongClickListener(XIChatMessageLongClickListener xiChatMessageLongClickListener){
        XIConversationChatView.setXIChatMessageLongClickListener(xiChatMessageLongClickListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xiconversation_activity_chat_default);
        // bottom view
        View view = getLayoutInflater().inflate(R.layout.xiconversation_rmrb_chatpage_bottomview, null, false);
        xiConversationChatView = (XIConversationChatView) findViewById(R.id.xiconversation_activity_chatpage_chatview);
        mNaviBack = (ImageView) findViewById(R.id.xiconversation_iv_navi_back);
        //bottomview
        mSendView = (ImageView) view.findViewById(R.id.xiconversation_activity_tv_chat_mess);
        voiceBtn = (XIAudioRecordButton) view.findViewById(R.id.xiconversation_activity_btn_chat_voice);
        mVoiceImage = (ImageView) view.findViewById(R.id.xiconversation_activity_iv_chat_voice);
        mOpenCamera = (RelativeLayout) view.findViewById(R.id.xiconversation_opencamera);
        //传入用户定义的bottomView
        xiConversationChatView.setBottomView(view);

        mMulTextView = (MulLineEditText) view.findViewById(R.id.xiconversation_activity_et_chat_mess);
        mSendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voiceBtn.getVisibility() == View.VISIBLE) {
                    reset();
                } else {
                    if (xiConversationChatView.getSendState()) {
                        //利用接口发送信息
                        xiConversationChatView.sendMessage(mMulTextView.getText().toString());
                        mMulTextView.setText("");
                    }
                }
            }
        });

        initEvent();
    }

    private void initEvent() {
        mMulTextView.setOnKeyListener(onKeyListener);
        mMulTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString().trim())) {
                    mSendView.setBackgroundResource(R.drawable.xiconversation_mesgsend_active);
                } else {
                    mSendView.setBackgroundResource(R.drawable.xiconversation_mesgsend_inactive);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mVoiceImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (xiConversationChatView.getSendState()){
                    getAndPermissionRecord(XIConversationChatPage.this);
                }
            }

        });

        mNaviBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //radio callback
        voiceBtn.setAudioFinishRecorderListener(new XIAudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinished(float seconds, String filePath) {
                xiConversationChatView.sendVoice(seconds, filePath);
            }

            @Override
            public void onStart() {
                //tbAdapter.pausePlayVoice();
            }
        });

        mOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String state = Environment.getExternalStorageState();
                if (xiConversationChatView.getSendState()){
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        //getAndPermissionCamera(XIConversationChatPage.this);
                        pickPhoto();
                    } else {
                        showToast(getResources().getString(R.string.xiconversation_memory_have_error));
                    }
                }
            }
        });

        xiConversationChatView.setXIChatCardboardScroll(new XIChatCardboardScroll(){
            @Override
            public void onScroll(View view,int dx,int dy){
                if (dx != 0 || dy != 0){
                    try {
                        XIKeyBoardUtils.hideKeyBoard(XIConversationChatPage.this, mMulTextView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(View view,int scrollState){

            }
        });
    }

    /**
     * reset page
     */
    protected void reset() {
        voiceBtn.setVisibility(View.GONE);
        mMulTextView.setVisibility(View.VISIBLE);
        if (!"".equals(mMulTextView.getText().toString().trim())) {
            mSendView.setBackgroundResource(R.drawable.xiconversation_mesgsend_active);
        } else {
            mSendView.setBackgroundResource(R.drawable.xiconversation_mesgsend_inactive);
        }
        XIKeyBoardUtils.hideKeyBoard(XIConversationChatPage.this, mMulTextView);

    }

    public void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(XIConversationChatPage.this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    private void getAndPermissionCamera(final Context context) {
        AndPermission.with(context)
                .requestCode(XIChatConst.XICONVERSATION_PERMISSION_CAMERA)
                .permission(Permission.CAMERA, Permission.STORAGE)
                .callback(permissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(context, rationale).show();
                    }
                })
                .start();
    }

    private void getAndPermissionRecord(final Context context) {
        AndPermission.with(context)
                .requestCode(XIChatConst.XICONVERSATION_PERMISSION_AUDIO)
                .permission(Permission.MICROPHONE, Permission.STORAGE)
                .callback(permissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(context, rationale).show();
                    }
                })
                .start();
    }

    private void getAndPermissionStroage(final Context context) {
        AndPermission.with(context)
                .requestCode(XIChatConst.XICONVERSATION_PERMISSION_STORAGE)
                .permission(Permission.STORAGE)
                .callback(permissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(context, rationale).show();
                    }
                })
                .start();
    }

    private void getAndPermissionDBStroage(final Context context) {
        AndPermission.with(context)
                .requestCode(XIChatConst.XICONVERSATION_PERMISSION_DB)
                .permission(Permission.STORAGE)
                .callback(permissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(context, rationale).show();
                    }
                })
                .start();
    }

    private void getAndPermissionOPENDBStroage(final Context context) {
        AndPermission.with(context)
                .requestCode(XIChatConst.XICONVERSATION_PERMISSION_OPENDB)
                .permission(Permission.STORAGE)
                .callback(permissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(context, rationale).show();
                    }
                })
                .start();
    }

    private void openGalleryPickImage() {
        Matisse.from(XIConversationChatPage.this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_xiconversation)
                .showSingleMediaType(true)
                .countable(false)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .imageEngine(new GlideEngine())
                .forResult(XIChatConst.FROM_GALLERY);
    }

    private void openCameraGetImage() {
        camPicPath = getSavePicPath();
        camOriPicPath = getOriginalSavePicPath();
        Intent openCameraIntent = new Intent(XIConversationChatPage.this, XICameraActivity.class);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, camOriPicPath);
        openCameraIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(openCameraIntent, XIChatConst.FROM_CAMERA);
    }

    private void startOpenRecordAudio() {
        if (voiceBtn.getVisibility() == View.GONE) {
            mMulTextView.setVisibility(View.GONE);
            voiceBtn.setVisibility(View.VISIBLE);
            XIKeyBoardUtils.hideKeyBoard(XIConversationChatPage.this, mMulTextView);
            mSendView.setBackgroundResource(R.drawable.xiconversation_voicetonormal_keyboard);
        } else {
            mMulTextView.setVisibility(View.VISIBLE);
            voiceBtn.setVisibility(View.GONE);
            XIKeyBoardUtils.showKeyBoard(XIConversationChatPage.this, mMulTextView);
            if (!"".equals(mMulTextView.getText().toString().trim())) {
                mSendView.setBackgroundResource(R.drawable.xiconversation_mesgsend_active);
            } else {
                mSendView.setBackgroundResource(R.drawable.xiconversation_mesgsend_inactive);
            }
        }
    }

    private void showDialog(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSave = false;
                try {
                    String GalPicPath = getSavePicPath();
                    Bitmap bitmap = XIPictureUtil.compressSizeImage(path);
                    if (bitmap != null) {
                        try {
                            FileOutputStream out = new FileOutputStream(GalPicPath);
                            boolean issuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
                            out.flush();
                            out.close();
                            if (issuccess) {
                                isSave = true;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Bitmap tempBitmap = XIPictureUtil.reviewPicRotate(bitmap, GalPicPath);
                    if (tempBitmap != null) {
                        isSave = XIFileSaveUtil.saveBitmap(
                                XIPictureUtil.reviewPicRotate(bitmap, GalPicPath),
                                GalPicPath);
                    }
                    File file = new File(GalPicPath);
                    if (file.exists() && isSave) {
                        xiConversationChatView.sendImage(path, GalPicPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case XIChatConst.FROM_CAMERA:
                    FileInputStream is = null;
                    try {
                        is = new FileInputStream(camOriPicPath);
                        File camFile = new File(camOriPicPath); // image path
                        if (camFile.exists()) {
                            int size = XIImageCheckoutUtil
                                    .getImageSize(XIImageCheckoutUtil
                                            .getLoacalBitmap(camOriPicPath));
                            if (size > IMAGE_SIZE) {
                                showDialog(camOriPicPath);
                            } else {
                                xiConversationChatView.sendImage(camOriPicPath, camOriPicPath);
                            }
                        } else {
                            //showToast(getResources().getString(R.string.xiconversation_sorry_not_have_file));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case XIChatConst.FROM_GALLERY:
                    final String path = Matisse.obtainPathResult(data).get(0);
                    mCurrentPhotoFile = new File(path);
                    if (mCurrentPhotoFile.exists()) {
                        camOriPicPath = XIPictureUtil.getOriginalImgSavePicPath(XIConversationChatPage.this);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap bitmap = XIPictureUtil.compressSizeImage(path, 1024 * 1024);
                                    if (bitmap != null) {
                                        try {
                                            FileOutputStream out = new FileOutputStream(camOriPicPath);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                            out.flush();
                                            out.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (!(new File(camOriPicPath).exists())) {
                                    camOriPicPath = path;
                                }
                                File oriFile = new File(camOriPicPath);
                                if (oriFile.exists()) {
                                    int size = XIImageCheckoutUtil.getImageSize(XIImageCheckoutUtil.getLoacalBitmap(camOriPicPath));
                                    if (size > IMAGE_SIZE) {
                                        showDialog(camOriPicPath);
                                    } else {
                                        final String savetPath = getSavePicPath();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Bitmap bitmap = XIPictureUtil.getBitmap(camOriPicPath);
                                                try {
                                                    XIFileSaveUtil.saveBitmap(bitmap, savetPath);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                xiConversationChatView.sendImage(camOriPicPath, savetPath);
                                            }
                                        }).start();
                                    }
                                } else {
                                    //showToast(getResources().getString(R.string.xiconversation_sorry_not_have_file));
                                }

                            }
                        }).start();
                    }

                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            //cancle
        }
    }

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case XIChatConst.XICONVERSATION_PERMISSION_CAMERA: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, grantPermissions)) {
                        openCameraGetImage();
                    } else {
                        Toast.makeText(XIConversationChatPage.this, getResources().getString(R.string.xiconversation_audio_camera_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case XIChatConst.XICONVERSATION_PERMISSION_AUDIO: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, grantPermissions)) {
                        startOpenRecordAudio();
                    } else {
                        Toast.makeText(XIConversationChatPage.this, getResources().getString(R.string.xiconversation_audio_record_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case XIChatConst.XICONVERSATION_PERMISSION_STORAGE: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, grantPermissions)) {
                        openGalleryPickImage();
                    } else {
                        Toast.makeText(XIConversationChatPage.this, getResources().getString(R.string.xiconversation_audio_gallery_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case XIChatConst.XICONVERSATION_PERMISSION_CAMERA: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, deniedPermissions)) {
                        openCameraGetImage();
                    } else {
                        Toast.makeText(XIConversationChatPage.this, getResources().getString(R.string.xiconversation_audio_camera_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case XIChatConst.XICONVERSATION_PERMISSION_AUDIO: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, deniedPermissions)) {
                        startOpenRecordAudio();
                    } else {
                        Toast.makeText(XIConversationChatPage.this, getResources().getString(R.string.xiconversation_audio_record_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case XIChatConst.XICONVERSATION_PERMISSION_STORAGE: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, deniedPermissions)) {
                        openGalleryPickImage();
                    } else {
                        Toast.makeText(XIConversationChatPage.this, getResources().getString(R.string.xiconversation_audio_gallery_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

            }

            if (AndPermission.hasAlwaysDeniedPermission(XIConversationChatPage.this, deniedPermissions)) {
                AndPermission.defaultSettingDialog(XIConversationChatPage.this, 300).show();
            }
        }
    };

    private String getSavePicPath() {
        return XIPictureUtil.getImgSavePath(XIConversationChatPage.this);
    }

    private String getOriginalSavePicPath() {
        return XIPictureUtil.getOriginalImgSavePicPath(XIConversationChatPage.this);
    }

    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (xiConversationChatView.getSendState()) {
                    xiConversationChatView.sendMessage(mMulTextView.getText().toString());
                    mMulTextView.setText("");
                }
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        xiConversationChatView.recycleResource();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void pickPhoto() {
        try {
            XIKeyBoardUtils.hideKeyBoard(XIConversationChatPage.this, mMulTextView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.xiconversation_imagepick_takephoto) {
                    getAndPermissionCamera(XIConversationChatPage.this);
                } else if (view.getId() == R.id.xiconversation_imagepick_choosephoto) {
                    getAndPermissionStroage(XIConversationChatPage.this);
                }else if(view.getId() == R.id.xiconversation_imagepick_cancel){

                }

                if (mImagePickDialog != null)
                    mImagePickDialog.dismiss();
            }
        };

        mImagePickDialog = new XIImagePickDialog.Builder(this)
                .style(R.style.xiconversationImagePickDialog)
                .width(WindowManager.LayoutParams.MATCH_PARENT)
                .height(WindowManager.LayoutParams.WRAP_CONTENT)
                .gravity(Gravity.BOTTOM)
                .view(R.layout.xiconversation_dialog_image_pick)
                .addViewOnclick(R.id.xiconversation_imagepick_takephoto, listener)
                .addViewOnclick(R.id.xiconversation_imagepick_choosephoto, listener)
                .addViewOnclick(R.id.xiconversation_imagepick_cancel, listener)
                .build();

        mImagePickDialog.show();
    }

}