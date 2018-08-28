package com.ldx.conversationother.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldx.conversationother.R;
import com.ldx.conversationother.adapter.KeywordsAdapter;
import com.ldx.conversationother.common.XIWangYiChatConst;
import com.ldx.conversationother.listener.XIButtonInfoCallback;
import com.ldx.conversationother.listener.XILoginCallbackListener;
import com.ldx.conversationother.listener.XILoginListener;
import com.ldx.conversationother.photo.Matisse;
import com.ldx.conversationother.photo.MimeType;
import com.ldx.conversationother.photo.engine.impl.GlideEngine;
import com.ldx.conversationother.utils.TimeUtils;
import com.ldx.conversationother.utils.XIFileSaveUtil;
import com.ldx.conversationother.view.MulLineEditText;
import com.ldx.conversationother.view.XIWordCountEditText;
import com.ldx.conversationbase.camera.XICameraActivity;
import com.ldx.conversationbase.common.bean.XIConversationChatPageConfig;
import com.ldx.conversationbase.listener.XIChatCardClickListener;
import com.ldx.conversationbase.listener.XIChatMessageDefaultLongClickListener;
import com.ldx.conversationbase.listener.XIChatMessageDisplayConfigListener;
import com.ldx.conversationbase.listener.XIChatMessageLongClickListener;
import com.ldx.conversationbase.listener.XIKeyBoardStateListener;
import com.ldx.conversationbase.listener.XIUserHeadIconClickListener;
import com.ldx.conversationbase.listener.XIXiaoiceHeadIconClickListener;
import com.ldx.conversationbase.utils.XIImageCheckoutUtil;
import com.ldx.conversationbase.utils.XIKeyBoardUtils;
import com.ldx.conversationbase.utils.XIPictureUtil;
import com.ldx.conversationbase.utils.XISharedPreferencesUtils;
import com.ldx.conversationbase.view.XIConversationChatView;
import com.ldx.conversationother.listener.XIItemClickListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.ldx.conversationbase.listener.XIChatCardboardScroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XIConversationChatPage extends AppCompatActivity {
    private static final int IMAGE_SIZE = XIWangYiChatConst.XICONVERSATION_IMAGE_MAXSIZE;
    private XIWordCountEditText mXIWordCountEditText;
    private MulLineEditText mMulTextView;
    private TextView mSendView;
    private RelativeLayout mSendContainer;
    private ImageView mNaviBack;
    private LinearLayout mBottomImageContainer;
    private LinearLayout mOpenCamera;

    public XIConversationChatView xiConversationChatView;
    private RecyclerView mRecycleView;
    private LinearLayoutManager mLinearLayoutManager;
    private KeywordsAdapter mKeywordsAdapter;

    private XILoginCallbackListener mLoginCallBack;

    private XILoginListener mXILoginListener;
    private static XILoginListener sXILoginListener;

    public static void setXILoginListener(XILoginListener mXILoginListener) {
        XIConversationChatPage.sXILoginListener = mXILoginListener;
    }

    public static void setXIConversationChatPageConfig(XIConversationChatPageConfig xiConversationChatPageConfig){
        XIConversationChatView.setXIConversationChatPageConfig(xiConversationChatPageConfig);
    }

    public static void setXIChatCardClickListener(XIChatCardClickListener xiChatCardClickListener) {
        XIConversationChatView.setXIChatCardClickListener(xiChatCardClickListener);
    }

    public static void setXiaoiceHeadIconClickListener(XIXiaoiceHeadIconClickListener xiaoiceHeadIconClickListener){
        XIConversationChatView.setXiaoiceHeadIconClickListener(xiaoiceHeadIconClickListener);
    }

    public static void setUserHeadIconClickListener(XIUserHeadIconClickListener userHeadIconClickListener){
        XIConversationChatView.setUserHeadIconClickListener(userHeadIconClickListener);
    }

    public static void setXIChatMessageDisplayConfigListener(XIChatMessageDisplayConfigListener  xiChatMessageDisplayConfigListener){
        XIConversationChatView.setXIChatMessageDisplayConfigListener(xiChatMessageDisplayConfigListener);
    }

    public static void setXIChatMessageDefaultLongClickListener(XIChatMessageDefaultLongClickListener xiChatMessageDefaultLongClickListener){
        XIConversationChatView.setXIChatMessageDefaultLongClickListener(xiChatMessageDefaultLongClickListener);
    }

    public static void setXIChatMessageLongClickListener(XIChatMessageLongClickListener xiChatMessageLongClickListener){
        XIConversationChatView.setXIChatMessageLongClickListener(xiChatMessageLongClickListener);
    }

    private ArrayList<String> mDataList = new ArrayList<>();

    private String camPicPath;
    private String camOriPicPath;
    private File mCurrentPhotoFile;
    private Toast mToast;
    private boolean hasLogin = true;
    private boolean hasRedPoint = false;
    private XIButtonInfoCallback xiButtonInfoCallback;
    private TextWatcher mTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xiconversation_activity_chat_default);
        mXILoginListener = XIConversationChatPage.sXILoginListener;
        sXILoginListener = null;

        getInitParameter();
        // bottom view
        View view = getLayoutInflater().inflate(R.layout.xiconversation_wangyi_chatpage_bottomviewwordcount, null, false);
        xiConversationChatView = (XIConversationChatView) findViewById(R.id.xiconversation_activity_chatpage_chatview);
        mNaviBack = (ImageView) findViewById(R.id.xiconversation_iv_navi_back);
        //bottomview
        mSendView = (TextView) view.findViewById(R.id.xiconversation_activity_tv_chat_mess);
        mSendContainer = (RelativeLayout) view.findViewById(R.id.xiconversation_sendview_container);
        mRecycleView = (RecyclerView) view.findViewById(R.id.xiconversation_wangyi_toprecycleview);
        mBottomImageContainer = (LinearLayout) view.findViewById(R.id.xiconversation_bottomview_bottomviewcontainer);
        //  mBottomImageContainer.setVisibility(View.GONE);
        mOpenCamera = (LinearLayout) view.findViewById(R.id.xiconversation_bottomview_opencamera);
        //传入用户定义的bottomView
        xiConversationChatView.setBottomView(view);

        mXIWordCountEditText = (XIWordCountEditText) view.findViewById(R.id.xiconversation_activity_et_chat_mess);
        mMulTextView = mXIWordCountEditText.getEditText();
        mMulTextView.setMaxLines(3);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecycleView.setLayoutManager(mLinearLayoutManager);
        mKeywordsAdapter = new KeywordsAdapter(this);
        mRecycleView.setAdapter(mKeywordsAdapter);
        mKeywordsAdapter.setData(mDataList);

        mSendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasLogin) {
                    if (mXILoginListener != null) {
                        mXILoginListener.conversationWorldCupViewControllerLoginRequest(mLoginCallBack);
                    }
                    return;
                }
                if (xiConversationChatView.getSendState()) {
                    //利用接口发送信息
                    xiConversationChatView.sendMessage(mMulTextView.getText().toString());
                    mMulTextView.setText("");
                }
            }
        });

        initEvent();
        getButtonInfo();
    }

    private void getInitParameter() {
        try {
            Intent intent = getIntent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        xiButtonInfoCallback = new XIButtonInfoCallback() {
            @Override
            public void buttonInfo(String info) {
                if (!TextUtils.isEmpty(info)) {
                    try {
                        mDataList.add("足球");
                        mDataList.add("新闻");
                        mDataList.add("体育");
                        mDataList.add("热点");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mDataList != null && mDataList.size() > 0) {
                                    mKeywordsAdapter.setData(mDataList);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        mLoginCallBack = new XILoginCallbackListener() {
            @Override
            public void loginCompletionHandler(boolean login, String userid) {

            }
        };
        mMulTextView.setOnKeyListener(onKeyListener);

        mTextWatcher = new TextWatcher() {
            private int editStart;
            private int editEnd;
            public void afterTextChanged(Editable s) {
                editStart = mMulTextView.getSelectionStart();
                editEnd = mMulTextView.getSelectionEnd();
                mMulTextView.removeTextChangedListener(mTextWatcher);
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    // mSendView.setEnabled(true);
                   //  mSendView.setTextColor(getResources().getColor(R.color.xiconversation_chatpage_bottomsendbutton));
                } else {
                    // mSendView.setEnabled(false);
                   //  mSendView.setTextColor(getResources().getColor(R.color.xiconversation_chatpage_bottom_cannotsend));
                }
                mMulTextView.addTextChangedListener(mTextWatcher);
                mXIWordCountEditText.setRightCount();
                if (mXIWordCountEditText.getInputCount() > mXIWordCountEditText.getMaxNum()){
                    mSendView.setTextColor(getResources().getColor(R.color.xiconversation_chatpage_bottom_cannotsend));
                    mSendView.setEnabled(false);
                    mXIWordCountEditText.getBottomControl().setTextColor(getResources().getColor(R.color.xiconversation_chatpage_bottom_moremaxworld));
                }else{
                    mSendView.setEnabled(true);
                    mSendView.setTextColor(getResources().getColor(R.color.xiconversation_chatpage_bottomsendbutton));
                    mXIWordCountEditText.getBottomControl().setTextColor(getResources().getColor(R.color.xiconversation_chatpage_bottom_notmoremaxworld));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

            public void onTextChanged(CharSequence s, int start, int before,int count) {}
        };

        mMulTextView.addTextChangedListener(mTextWatcher);

        mNaviBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mKeywordsAdapter.setOnItemClickListener(new XIItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!hasLogin) {
                    if (mXILoginListener != null) {
                        mXILoginListener.conversationWorldCupViewControllerLoginRequest(mLoginCallBack);
                    }
                    return;
                }
                if (xiConversationChatView.getSendState()) {
                    String sendStr = mDataList.get(position);
                    xiConversationChatView.sendMessage(sendStr);
                }
            }
        });

        xiConversationChatView.setXIKeyBoardStateListener(new XIKeyBoardStateListener() {
            @Override
            public void onKeyBoardState(boolean up) {
                if (up) {
                    mBottomImageContainer.setVisibility(View.VISIBLE);
                    mSendContainer.setVisibility(View.VISIBLE);
                } else {
                    mBottomImageContainer.setVisibility(View.GONE);
                    mSendContainer.setVisibility(View.GONE);
                }
            }
        });

        xiConversationChatView.setXIChatCardboardScroll(new XIChatCardboardScroll(){
            @Override
            public void onScroll(View view,int dx,int dy){
                if (dx != 0 || dy != 0){
                    XIKeyBoardUtils.hideKeyBoard(XIConversationChatPage.this, mMulTextView);
                }
            }

            @Override
            public void onScrollStateChanged(View view,int scrollState){

            }
        });

        mOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // getAndPermissionStroage(XIConversationChatPage.this);
                if (!hasLogin) {

                }
                XIKeyBoardUtils.hideKeyBoard(XIConversationChatPage.this, mMulTextView);
                getAndPermissionCamera(XIConversationChatPage.this);
            }
        });

    }

    /**
     * reset page
     */
    protected void reset() {
        mMulTextView.setVisibility(View.VISIBLE);
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
                .requestCode(XIWangYiChatConst.XICONVERSATION_PERMISSION_CAMERA)
                .permission(Permission.CAMERA, Permission.STORAGE)
                .callback(permissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
                       // AndPermission.rationaleDialog(context, rationale).show();
                        new android.support.v7.app.AlertDialog.Builder(XIConversationChatPage.this)
                                .setTitle(getResources().getString(R.string.xiconversation_commondialog_dialogtitle))
                                .setMessage(getResources().getString(R.string.xiconversation_commondialog_dialogmesg))
                                .setNegativeButton(getResources().getString(R.string.xiconversation_commondialog_notallow), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        rationale.cancel();
                                    }
                                })
                                .setPositiveButton(getResources().getString(R.string.xiconversation_commondialog_allow), (new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        rationale.resume();
                                    }
                                }))
                                .show();
                    }
                })
                .start();
    }

    private void getAndPermissionRecord(final Context context) {
        AndPermission.with(context)
                .requestCode(XIWangYiChatConst.XICONVERSATION_PERMISSION_AUDIO)
                .permission(Permission.MICROPHONE, Permission.STORAGE)
                .callback(permissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
                        AndPermission.rationaleDialog(context, rationale).show();
                    }
                })
                .start();
    }

    private void getAndPermissionStroage(final Context context) {
        AndPermission.with(context)
                .requestCode(XIWangYiChatConst.XICONVERSATION_PERMISSION_STORAGE)
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
                .theme(R.style.Matisse_Zhihu)
                .showSingleMediaType(true)
                .countable(false)
                .capture(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .imageEngine(new GlideEngine())
                .forResult(XIWangYiChatConst.FROM_GALLERY);
    }

    private void openCameraGetImage() {
        camPicPath = getSavePicPath();
        camOriPicPath = getOriginalSavePicPath();
        Intent openCameraIntent = new Intent(XIConversationChatPage.this, XICameraActivity.class);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, camOriPicPath);
        openCameraIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(openCameraIntent, XIWangYiChatConst.FROM_CAMERA);
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
                case XIWangYiChatConst.FROM_CAMERA:
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
                case XIWangYiChatConst.FROM_GALLERY:
                    final String path = Matisse.obtainPathResult(data).get(0);
                   if (TextUtils.isEmpty(path))return;
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
                case XIWangYiChatConst.XICONVERSATION_PERMISSION_CAMERA: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, grantPermissions)) {
                       // openCameraGetImage();
                        openGalleryPickImage();
                    } else {
                        Toast.makeText(XIConversationChatPage.this, getResources().getString(R.string.xiconversation_audio_camera_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case XIWangYiChatConst.XICONVERSATION_PERMISSION_AUDIO: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, grantPermissions)) {
                       /* startOpenRecordAudio();*/
                    } else {
                        Toast.makeText(XIConversationChatPage.this, getResources().getString(R.string.xiconversation_audio_record_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case XIWangYiChatConst.XICONVERSATION_PERMISSION_STORAGE: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, grantPermissions)) {
                        //openGalleryPickImage();
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
                case XIWangYiChatConst.XICONVERSATION_PERMISSION_CAMERA: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, deniedPermissions)) {
                       // openCameraGetImage();
                        openGalleryPickImage();
                    } else {
                        Toast.makeText(XIConversationChatPage.this, getResources().getString(R.string.xiconversation_audio_camera_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case XIWangYiChatConst.XICONVERSATION_PERMISSION_AUDIO: {
                    if (AndPermission.hasPermission(XIConversationChatPage.this, deniedPermissions)) {
                      /*  startOpenRecordAudio();*/
                    } else {
                        Toast.makeText(XIConversationChatPage.this, getResources().getString(R.string.xiconversation_audio_record_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case XIWangYiChatConst.XICONVERSATION_PERMISSION_STORAGE: {
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
                if (xiConversationChatView.getSendState() && hasLogin) {
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
        XISharedPreferencesUtils.put(XIConversationChatPage.this, XIWangYiChatConst.XICONVERSATION_CHATCLOSE_FILE, XIWangYiChatConst.XICONVERSATION_CHATCLOSE_LASTTIME, TimeUtils.getCurrentStringStamp());
        if (sXILoginListener != null) sXILoginListener = null;
        if (mXILoginListener != null) mXILoginListener = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getButtonInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String buttonInfo = "wangluoqingqiushuju";
                    if (xiButtonInfoCallback != null){
                        xiButtonInfoCallback.buttonInfo(buttonInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}