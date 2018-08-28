package com.ldx.conversationbase.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ldx.conversationbase.R;
import com.ldx.conversationbase.adapter.XIChatRecyclerAdapter;
import com.ldx.conversationbase.common.DownloadFileService;
import com.ldx.conversationbase.common.XIChatConst;
import com.ldx.conversationbase.common.XIConversationConfig;
import com.ldx.conversationbase.common.bean.XIConversationChatPageConfig;
import com.ldx.conversationbase.db.XICardMessageBean;
import com.ldx.conversationbase.db.XIChatDbManager;
import com.ldx.conversationbase.db.XIChatMessageBean;
import com.ldx.conversationbase.db.XIChatMessageBeanDao;
import com.ldx.conversationbase.db.XIChatMessageInfo;
import com.ldx.conversationbase.listener.XIChatCardClickListener;
import com.ldx.conversationbase.listener.XIChatCardboardScroll;
import com.ldx.conversationbase.listener.XIChatMessageDefaultLongClickListener;
import com.ldx.conversationbase.listener.XIChatMessageDisplayConfigListener;
import com.ldx.conversationbase.listener.XIChatMessageLongClickListener;
import com.ldx.conversationbase.listener.XIKeyBoardStateListener;
import com.ldx.conversationbase.listener.XIUserHeadIconClickListener;
import com.ldx.conversationbase.listener.XIXiaoiceHeadIconClickListener;
import com.ldx.conversationbase.network.XIResponseInformationTool;
import com.ldx.conversationbase.network.callbackinterface.XIAsyncTaskListener;
import com.ldx.conversationbase.network.httpasync.XIInfoGetAsyncTask;
import com.ldx.conversationbase.utils.XIClipboardUtil;
import com.ldx.conversationbase.utils.XIConLogUtil;
import com.ldx.conversationbase.utils.XIConNetWorkUtils;
import com.ldx.conversationbase.utils.XIDateUtils;
import com.ldx.conversationbase.utils.XIFileSaveUtil;
import com.ldx.conversationbase.utils.XIImageCheckoutUtil;
import com.ldx.conversationbase.utils.XIKeyBoardUtils;
import com.ldx.conversationbase.utils.XIPictureUtil;
import com.ldx.conversationbase.utils.XIScreenUtil;
import com.ldx.conversationbase.widget.XIBottomViewLayout;
import com.ldx.conversationbase.widget.XIMediaManager;
import com.ldx.conversationbase.widget.XIMessageMenuPopupWindow;
import com.ldx.conversationbase.widget.pulltorefresh.XIPullToRefreshLayout;
import com.ldx.conversationbase.widget.pulltorefresh.XIPullToRefreshRecyclerView;
import com.ldx.conversationbase.widget.pulltorefresh.XIWrapContentLinearLayoutManager;
import com.ldx.conversationbase.widget.pulltorefresh.base.XIPullToRefreshView;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.RationaleListener;

import com.yanzhenjie.permission.Rationale;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by v-doli1 on 2017/8/31.
 */

public class XIConversationChatView extends LinearLayout {
    private Context mContext;
    public XIPullToRefreshLayout mPullRefreshLayout;
    public boolean isDown = false;
    public int bottomStatusHeight = 0;
    public int listSlideHeight = 0;
    public RelativeLayout activityRootView;
    public XIBottomViewLayout mChatBottom;
    public String mUserid;

    public LinearLayout mChatpageMainPage;
    public LinearLayout mChatpageCornerPage;
    public LinearLayout mChatPageViewContainer;

    private Toast mToast;
    public String userName = "xiaoice";
    public String item[] = {""};
    public List<XIChatMessageBean> tblist = new ArrayList<XIChatMessageBean>();
    public XIChatDbManager mChatDbManager;
    public int page = 0;
    public int number = 15;
    public List<XIChatMessageBean> pagelist = new ArrayList<XIChatMessageBean>();
    public ArrayList<String> imageList = new ArrayList<String>();//adapter image data
    public HashMap<Integer, Integer> imagePosition = new HashMap<Integer, Integer>();//image position
    private static final int IMAGE_SIZE = XIChatConst.XICONVERSATION_IMAGE_MAXSIZE;
    public static final int XICONVERSATION_SEND_OK = 0x1110;
    public static final int XICONVERSATION_REFRESH = 0x0011;
    public static final int XICONVERSATION_RECERIVE_OK = 0x1111;
    public static final int XICONVERSATION_PULL_TO_REFRESH_DOWN = 0x0111;
    public static final int XICONVERSATION_SEND_MESG_NULL = 0x1101;
    public static final int XICONVERSATION_UPDATESTATE = 0x1011;
    public static final int XICONVERSATION_SEND_TEXT_OK = 0x11110;
    public static final int XICONVERSATION_SCROOL_TO_END = 0x11100;
    public static final int XICONVERSATION_COMPUTER_TO_END = 0x11000;
    public static final int XICONVERSATION_MESG_REMOVE = 0x11001;

    private XIMessageMenuPopupWindow xiMessageMenuPopupWindow;

    private XIPullToRefreshRecyclerView mRecycleView;
    private XIChatRecyclerAdapter tbAdapter;
    private XIChatViewSendMessageHandler sendMessageHandler;
    private XIWrapContentLinearLayoutManager wcLinearLayoutManger;
    private int lastPosition = 0;
    private int xiposition = 0;
    private int mOffKeyboardHeight = 0;
    private int mFirstBottomViewHeight = 0;
    private boolean firstbottomState = false;
    private boolean keyboardState = false;

    private LinearLayout mMainChatLayout;
    public boolean canSendMesg = true;

    private XIKeyBoardStateListener xiKeyBoardStateListener;
    public void setXIKeyBoardStateListener(XIKeyBoardStateListener txiKeyBoardStateListener){
        xiKeyBoardStateListener = txiKeyBoardStateListener;
    }

    private XIChatCardboardScroll xiChatCardboardScroll;

    public void setXIChatCardboardScroll(XIChatCardboardScroll tXIChatCardboardScroll){
        xiChatCardboardScroll = tXIChatCardboardScroll;
    }

    public XIConversationChatPageConfig xiConversationChatPageConfig;
    public XIUserHeadIconClickListener userHeadIconClickListener;
    public XIXiaoiceHeadIconClickListener xiaoiceHeadIconClickListener;

    public static XIConversationChatPageConfig mxiConversationChatPageConfig;
    public static XIUserHeadIconClickListener muserHeadIconClickListener;
    public static XIXiaoiceHeadIconClickListener mxiaoiceHeadIconClickListener;

    public XIChatMessageDefaultLongClickListener xiChatMessageDefaultLongClickListener;
    public XIChatMessageLongClickListener xiChatMessageLongClickListener;

    public static XIChatMessageDefaultLongClickListener mxiChatMessageDefaultLongClickListener;
    public static XIChatMessageLongClickListener mxiChatMessageLongClickListener;

    public XIChatMessageDisplayConfigListener xiChatMessageDisplayConfigListener;
    public static XIChatMessageDisplayConfigListener mxiChatMessageDisplayConfigListener;

    public XIChatCardClickListener xiXIChatCardClickListener;
    public static XIChatCardClickListener mXIChatCardClickListener;
    private ViewTreeObserver.OnGlobalLayoutListener globalTreeListener;

    public static void setXIChatCardClickListener(XIChatCardClickListener xiXIChatCardClickListener) {
        XIConversationChatView.mXIChatCardClickListener = xiXIChatCardClickListener;
    }

    public static void setXIChatMessageDisplayConfigListener(XIChatMessageDisplayConfigListener xiChatMessageDisplayConfigListener) {
        XIConversationChatView.mxiChatMessageDisplayConfigListener = xiChatMessageDisplayConfigListener;
    }

    public static void setUserHeadIconClickListener(XIUserHeadIconClickListener userHeadIconClickListener) {
        XIConversationChatView.muserHeadIconClickListener = userHeadIconClickListener;
    }

    public static void setXiaoiceHeadIconClickListener(XIXiaoiceHeadIconClickListener xiaoiceHeadIconClickListener) {
        XIConversationChatView.mxiaoiceHeadIconClickListener = xiaoiceHeadIconClickListener;
    }

    public static void setXIConversationChatPageConfig(XIConversationChatPageConfig xiCActivityConfigBean) {
        XIConversationChatView.mxiConversationChatPageConfig = xiCActivityConfigBean;
    }

    public static void setXIChatMessageDefaultLongClickListener(XIChatMessageDefaultLongClickListener xiChatMessageDefaultLongClickListener) {
        mxiChatMessageDefaultLongClickListener = xiChatMessageDefaultLongClickListener;
    }

    public static void setXIChatMessageLongClickListener(XIChatMessageLongClickListener xiChatMessageLongClickListener) {
        mxiChatMessageLongClickListener = xiChatMessageLongClickListener;
    }

    public XIChatMessageDefaultLongClickListener mPopMenuChatMessageDefaultLongClickListener = new XIChatMessageDefaultLongClickListener() {
        @Override
        public void onMessageCopy(View view, XIChatMessageInfo xiChatMessageInfo) {
            if (!TextUtils.isEmpty(xiChatMessageInfo.getMessageContent()))
                XIClipboardUtil.copy(mContext, xiChatMessageInfo.getMessageContent());
        }

        @Override
        public void onMessageShare(View view, XIChatMessageInfo xiChatMessageInfo) {

        }

        @Override
        public void onMessageDelete(View view, XIChatMessageInfo xiChatMessageInfo) {
            deleteMessage(xiChatMessageInfo.getMessageID());
        }
    };

    public XIConversationChatView(Context context) {
        super(context);
        this.mContext = context;
        initOncreate(context);
    }

    public XIConversationChatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initOncreate(context);
    }

    public XIConversationChatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        try {
            mUserid = ((XIConversationConfig.XISetConfig) XIConversationConfig.UserConfigs.get(XIChatConst.XICONVERSATIONCONFIG)).userId;
        } catch (Exception e) {

        }
        initOncreate(mContext);
    }

    private void initOncreate(Context context) {

        if (mxiConversationChatPageConfig == null) {
            xiConversationChatPageConfig = new XIConversationChatPageConfig();
        } else {
            xiConversationChatPageConfig = mxiConversationChatPageConfig;
            mxiConversationChatPageConfig = null;
        }

        if (mXIChatCardClickListener != null){
            xiXIChatCardClickListener = mXIChatCardClickListener;
            mXIChatCardClickListener = null;
        }

        if (muserHeadIconClickListener != null) {
            userHeadIconClickListener = muserHeadIconClickListener;
            muserHeadIconClickListener = null;
        }
        if (mxiaoiceHeadIconClickListener != null) {
            xiaoiceHeadIconClickListener = mxiaoiceHeadIconClickListener;
            mxiaoiceHeadIconClickListener = null;
        }

        if (mxiChatMessageDefaultLongClickListener != null) {
            xiChatMessageDefaultLongClickListener = mxiChatMessageDefaultLongClickListener;
            mxiChatMessageDefaultLongClickListener = null;
        }
        if (mxiChatMessageLongClickListener != null) {
            xiChatMessageLongClickListener = mxiChatMessageLongClickListener;
            mxiChatMessageLongClickListener = null;
        }

        if (mxiChatMessageDisplayConfigListener != null) {
            xiChatMessageDisplayConfigListener = mxiChatMessageDisplayConfigListener;
            mxiChatMessageDisplayConfigListener = null;
        }

        mMainChatLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xiconversation_activity_chat, null);
        this.mContext = context;
        findView();
        init();
        addView(mMainChatLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    protected void findView() {
        mPullRefreshLayout = (XIPullToRefreshLayout) mMainChatLayout.findViewById(R.id.xiconversation_activity_prl_chat_content);
        activityRootView = (RelativeLayout) mMainChatLayout.findViewById(R.id.xiconversation_activity_rl_chat_tongbao);
        mChatpageMainPage = (LinearLayout) mMainChatLayout.findViewById(R.id.xiconversation_activity_chatpage_main_container);
        mChatpageCornerPage = (LinearLayout) mMainChatLayout.findViewById(R.id.xiconversation_activity_chatpage_left_rightmargin);
        mChatBottom = (XIBottomViewLayout) mMainChatLayout.findViewById(R.id.xiconversation_activity_ll_chat_bottom_container);
        mChatPageViewContainer = (LinearLayout) mMainChatLayout.findViewById(R.id.xiconversation_activity_chatpage_chatviewcontainer);

        if (xiChatMessageDefaultLongClickListener != null && xiChatMessageLongClickListener == null) {
            xiMessageMenuPopupWindow = new XIMessageMenuPopupWindow((Activity) mContext, xiChatMessageDefaultLongClickListener, mPopMenuChatMessageDefaultLongClickListener);
        }

        mPullRefreshLayout.setSlideView(new XIPullToRefreshView(mContext).getSlideView());
        mRecycleView = (XIPullToRefreshRecyclerView) mPullRefreshLayout.returnMylist();
        closeDefaultAnimator(mRecycleView);
        setUserInit();
        ((SimpleItemAnimator) mRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public void setBottomView(View view) {
        if (mChatBottom != null) {
            mChatBottom.setBottomView(view);
        }
    }

    private void setUserInit() {
        try {
            //  mChatpageMainPage
            if (xiConversationChatPageConfig != null && xiConversationChatPageConfig.getChatPageMainBackgroundColor() != -1) {
                mChatpageMainPage.setBackgroundColor(xiConversationChatPageConfig.getChatPageMainBackgroundColor());
            }
        } catch (Exception e) {
        }

        try {
            if (xiConversationChatPageConfig != null && xiConversationChatPageConfig.getChatpageBackgroundColor() != -1) {
                activityRootView.setBackgroundColor(xiConversationChatPageConfig.getChatpageBackgroundColor());
                mPullRefreshLayout.setBackgroundColor(xiConversationChatPageConfig.getChatpageBackgroundColor());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //        mChatpageCornerPage
            if (xiConversationChatPageConfig != null) {
                if (xiConversationChatPageConfig.getChatPageMessageLeftMargin() != -1 && xiConversationChatPageConfig.getChatPageMessageRightMargin() != -1) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mChatpageCornerPage.getLayoutParams();
                    // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(xiConversationChatPageConfig.getChatPageMessageLeftMargin(),
                            0, xiConversationChatPageConfig.getChatPageMessageRightMargin(), 0);
                    mChatpageCornerPage.setLayoutParams(params);
                } else if (xiConversationChatPageConfig.getChatPageMessageLeftMargin() != -1 && xiConversationChatPageConfig.getChatPageMessageRightMargin() == -1) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mChatpageCornerPage.getLayoutParams();
                    params.setMargins(xiConversationChatPageConfig.getChatPageMessageLeftMargin(), 0,
                            getResources().getDimensionPixelSize(R.dimen.xiconversation_chat_top_back_paddingright), 0);
                    mChatpageCornerPage.setLayoutParams(params);
                } else if (xiConversationChatPageConfig.getChatPageMessageRightMargin() != -1 && xiConversationChatPageConfig.getChatPageMessageLeftMargin() == -1) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mChatpageCornerPage.getLayoutParams();
                    params.setMargins(getResources().getDimensionPixelSize(R.dimen.xiconversation_chat_top_back_paddingleft), 0, xiConversationChatPageConfig.getChatPageMessageRightMargin(), 0);
                    mChatpageCornerPage.setLayoutParams(params);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (xiConversationChatPageConfig != null && xiConversationChatPageConfig.getChatPageBackgroundImgId() != -1) {
            try {
                if (xiConversationChatPageConfig.getChatPageBackgroundImgId() != -1
                        && mContext.getResources().getDrawable(xiConversationChatPageConfig.getChatPageBackgroundImgId()) != null) {
                    mRecycleView.setBackgroundDrawable(getResources().getDrawable(xiConversationChatPageConfig.getChatPageBackgroundImgId()));
                    mRecycleView.setBackgroundColor(getResources().getColor(R.color.xiconversation_transparent));
                } else {
                    mRecycleView.setBackgroundDrawable(null);
                    mRecycleView.setBackgroundColor(getResources().getColor(R.color.xiconversation_chatpage_bg_color));
                }
            } catch (Exception e) {
                mRecycleView.setBackgroundColor(getResources().getColor(R.color.xiconversation_chatpage_bg_color));
            }
        }
    }

    protected void init() {
        tbAdapter = new XIChatRecyclerAdapter(mContext, tblist, xiConversationChatPageConfig, userHeadIconClickListener, xiaoiceHeadIconClickListener, xiChatMessageDisplayConfigListener,xiXIChatCardClickListener);
        wcLinearLayoutManger = new XIWrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecycleView.setLayoutManager(wcLinearLayoutManger);
        mRecycleView.setAdapter(tbAdapter);
        sendMessageHandler = new XIChatViewSendMessageHandler(mContext);

        tbAdapter.notifyDataSetChanged();

        tbAdapter.setOnItemLongClickListener(new XIChatRecyclerAdapter.RecyclerViewOnItemLongClickListener() {
            @Override
            public boolean onItemLongClickListener(View view, XIChatMessageBean tbub, int position) {
                if (xiChatMessageLongClickListener != null) {
                    XIChatMessageInfo xiChatMessageInfo = new XIChatMessageInfo();
                    if (tbub.getType() == XIChatConst.FROM_USER_MSG || tbub.getType() == XIChatConst.TO_USER_MSG) {
                        xiChatMessageInfo.setMessageID(tbub.getId());
                        xiChatMessageInfo.setMessageContent(tbub.getUserContent());
                        xiChatMessageInfo.setMessageChatTime(tbub.getTime());
                        xiChatMessageInfo.setMessageType(tbub.getType());
                    } else if (tbub.getType() == XIChatConst.FROM_USER_IMG || tbub.getType() == XIChatConst.TO_USER_IMG) {
                        xiChatMessageInfo.setMessageID(tbub.getId());
                        xiChatMessageInfo.setMessageImageLocalPath(tbub.getImageOriginal());
                        xiChatMessageInfo.setMessageImageUrl(tbub.getImageUrl());
                        xiChatMessageInfo.setMessageType(tbub.getType());
                        xiChatMessageInfo.setMessageChatTime(tbub.getTime());
                    } else if (tbub.getType() == XIChatConst.FROM_USER_VOICE || tbub.getType() == XIChatConst.TO_USER_VOICE) {
                        xiChatMessageInfo.setMessageID(tbub.getId());
                        xiChatMessageInfo.setMessageVoiceLength(tbub.getUserVoiceTime());
                        xiChatMessageInfo.setMessageVoiceUrl(tbub.getUserVoiceUrl());
                        xiChatMessageInfo.setMessageVoicePath(tbub.getUserVoicePath());
                        xiChatMessageInfo.setMessageType(tbub.getType());
                        xiChatMessageInfo.setMessageChatTime(tbub.getTime());
                    }
                    xiChatMessageLongClickListener.OnMessageLongClickListener(view, xiChatMessageInfo, mPopMenuChatMessageDefaultLongClickListener);
                } else {
                    if (xiChatMessageDefaultLongClickListener != null) {
                        if (tbub.getType() == XIChatConst.FROM_USER_IMG || tbub.getType() == XIChatConst.TO_USER_IMG ||
                                tbub.getType() == XIChatConst.FROM_USER_VOICE || tbub.getType() == XIChatConst.TO_USER_VOICE) {
                            xiMessageMenuPopupWindow.setShowCopy(false);
                        } else {
                            xiMessageMenuPopupWindow.setShowCopy(true);
                        }
                        xiMessageMenuPopupWindow.setData(tbub);
                        xiMessageMenuPopupWindow.showPopupWindow(view);
                    }
                }
                return false;
            }

            @Override
            public boolean onMulItemLongClickListener(View view, XICardMessageBean tbub, int position) {
                if (xiChatMessageLongClickListener != null) {
                    XIChatMessageInfo xiChatMessageInfo = new XIChatMessageInfo();
                    if (tbub != null) {
                        xiChatMessageInfo.setMessageID(tbub.getMesgId());
                        xiChatMessageInfo.setMessageCardcoverurl(tbub.getMesgCoverurl());
                        xiChatMessageInfo.setMessageCardDescription(tbub.getMesgDescription());
                        xiChatMessageInfo.setMessageCardNewsTime(tbub.getMesgTime());
                        xiChatMessageInfo.setMessageCardTitle(tbub.getMesgTitle());
                        xiChatMessageInfo.setMessageCardUrl(tbub.getMesgUrl());
                        xiChatMessageInfo.setMessageType(tbub.getMesgType());
                    }
                    xiChatMessageLongClickListener.OnMessageLongClickListener(view, xiChatMessageInfo, mPopMenuChatMessageDefaultLongClickListener);
                } else {
                    if (xiChatMessageDefaultLongClickListener != null) {
                        XIChatMessageBean ctbub = new XIChatMessageBean();
                        ctbub.setId(tbub.getMesgId());
                        ctbub.setType(tbub.getMesgType());
                        ctbub.setImageUrl(tbub.getMesgCoverurl());
                        ctbub.setCardDescription(tbub.getMesgDescription());
                        ctbub.setCardTitle(tbub.getMesgTitle());
                        ctbub.setWebUrl(tbub.getMesgUrl());
                        ctbub.setCreateTime(tbub.getMesgTime());
                        xiMessageMenuPopupWindow.setData(ctbub);
                        xiMessageMenuPopupWindow.setShowCopy(false);
                        xiMessageMenuPopupWindow.showPopupWindow(view);
                    }
                }
                return true;
            }
        });
        tbAdapter.setSendErrorListener(new XIChatRecyclerAdapter.SendErrorListener() {

            @Override
            public void onClick(long beanid) {
                if (!canSendMesg) {
                    Toast.makeText(mContext, getResources().getString(R.string.xiconversation_wait_get_response), Toast.LENGTH_SHORT);
                    return;
                }
                long tbeanId = beanid;
                XIChatMessageBean tbub = null;
                try {
                    int pos = findPositionAtList(tblist, beanid);
                    tbub = tblist.get(pos);
                    //long tbeanId = tbub.getId();
                    tblist.remove(pos);
                    sendMessageHandler.sendEmptyMessage(XICONVERSATION_REFRESH);
                } catch (Exception e) {
                    return;
                }
                try {
                    XIChatMessageBean xiChatMessageBean = new XIChatDbManager().getChatMessageBeanDao(mContext).queryBuilder().where(XIChatMessageBeanDao.Properties.Id.eq(tbeanId)).unique();
                    mChatDbManager.delete(mContext, xiChatMessageBean);
                    int findPos = findPositionAtList(tblist, tbeanId);
                    if (findPos != -1) {
                        XIChatMessageBean tempXIChatMessageBean = tblist.get(findPos);
                        tempXIChatMessageBean.setSendState(XIChatConst.XICONVERSATION_COMPLETED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (canSendMesg) {
                    try {
                        if (tbub.getType() == XIChatConst.TO_USER_VOICE) {
                            sendVoice(tbub.getUserVoiceTime(), tbub.getUserVoicePath());
                        } else if (tbub.getType() == XIChatConst.TO_USER_IMG) {
                            sendImage(tbub.getImageOriginal(), tbub.getImageLocal());
                        } else if (tbub.getType() == XIChatConst.TO_USER_MSG) {
                            reSendMessage(tbub.getUserContent());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        mRecycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                if (xiChatCardboardScroll != null){
                    xiChatCardboardScroll.onScrollStateChanged(view,scrollState);
                }
                switch (scrollState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        tbAdapter.handler.removeCallbacksAndMessages(null);
                        int firstpos = wcLinearLayoutManger.findFirstVisibleItemPosition();
                        int lastastpos = wcLinearLayoutManger.findLastVisibleItemPosition();
                        tbAdapter.setIsGif(true,firstpos,lastastpos);

                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        tbAdapter.handler.removeCallbacksAndMessages(null);
                        tbAdapter.setIsGif(false,0,0);
                        reset();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (xiChatCardboardScroll != null){
                    xiChatCardboardScroll.onScroll(recyclerView,dx,dy);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        controlKeyboardLayout(activityRootView, mPullRefreshLayout);

        mChatDbManager = new XIChatDbManager();
        XIPullToRefreshLayout.pulltorefreshNotifier pullNotifier = new XIPullToRefreshLayout.pulltorefreshNotifier() {
            @Override
            public void onPull() {
                downLoad();
            }
        };
        mPullRefreshLayout.setpulltorefreshNotifier(pullNotifier);

        mRecycleView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    reset();
                }
                return false;
            }
        });

        mPullRefreshLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    reset();
                }
                return false;
            }
        });
        bottomStatusHeight = XIScreenUtil.getBottomStatusHeight(mContext);
        page = (int) mChatDbManager.getPages(mContext.getApplicationContext(), number);
        getAndPermissionOPENDBStroage(mContext);
        initGreetingConfig();
    }

    private void downLoad() {
        if (!isDown) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    getAndPermissionDBStroage(mContext);
                }
            }).start();
        }
    }

    /**
     * reset page
     */
    protected void reset() {

    }

    private void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    private void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    private void compressImageDialog(final String path) {
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
                        sendImage(path, GalPicPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private XIChatMessageBean getTbub(Context context, String username, int type,
                                      String Content, String imageIconUrl, String imageUrl,
                                      String imageLocal, String originImgPath, String userVoicePath, String userVoiceUrl,
                                      Float userVoiceTime, boolean alreadyPlay, int width, int height, @XIChatConst.SendState int sendState) {
        XIChatMessageBean tbub = new XIChatMessageBean();
        tbub.setUserName(username);
        String time = returnTime();
        tbub.setTime(time);
        tbub.setType(type);
        tbub.setUserContent(Content);
        tbub.setImageIconUrl(imageIconUrl);
        tbub.setImageUrl(imageUrl);
        tbub.setUserVoicePath(userVoicePath);
        tbub.setUserVoiceUrl(userVoiceUrl);
        tbub.setUserVoiceTime(userVoiceTime);
        tbub.setSendState(sendState);
        tbub.setImageLocal(imageLocal);
        tbub.setImageOriginal(originImgPath);
        tbub.setAlreadyReady(alreadyPlay);
        tbub.setImgWidth(width);
        tbub.setImgHeight(height);
        tbub.setTimeStamp(XIDateUtils.getLocalTimestamp());
        mChatDbManager.insert(context, tbub);
        return tbub;
    }

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    //layout change
    private void controlKeyboardLayout(final View root, final View needToScrollView) {
        globalTreeListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            private Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                try {
                    ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                    int screenHeight = ((Activity) mContext).getWindow().getDecorView().getRootView().getHeight();
                    int heightDifference = screenHeight - r.bottom;
                    int recyclerHeight = 0;
                    if (wcLinearLayoutManger != null) {
                        recyclerHeight = wcLinearLayoutManger.getRecyclerHeight();
                    }
                    if (heightDifference == bottomStatusHeight) {//heightdifference - bottomview = keyboard
                        if (mChatBottom != null && firstbottomState && mChatBottom.getHeight() != mFirstBottomViewHeight) {
                            int scrollheight = mChatBottom.getHeight() - mFirstBottomViewHeight;
                            if (needToScrollView.getScrollY() != scrollheight) {
                                needToScrollView.scrollTo(0, scrollheight);
                            }
                        } else {
                            needToScrollView.scrollTo(0, 0);
                        }

                        if (xiKeyBoardStateListener != null && keyboardState) {
                            xiKeyBoardStateListener.onKeyBoardState(false);
                            keyboardState = false;
                        }
                        if (mChatBottom != null) {
                            mOffKeyboardHeight = mChatBottom.getHeight();
                        }
                        if (mChatBottom != null && !firstbottomState && mChatBottom.getHeight() > 0) {
                            mFirstBottomViewHeight = mChatBottom.getHeight();
                            firstbottomState = true;
                        }
                    } else {
                        if (xiKeyBoardStateListener != null && !keyboardState) {
                            xiKeyBoardStateListener.onKeyBoardState(true);
                            keyboardState = true;
                        }
                        int itemcount =  wcLinearLayoutManger.getItemCount();
                        int mrecycleViewHeight = 0;
                        try {
                            for (int i = 0;i<itemcount && i <= 10;i++){
                                View view = wcLinearLayoutManger.getChildAt(i);
                                if (view != null){
                                    mrecycleViewHeight = mrecycleViewHeight + view.getHeight();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mrecycleViewHeight = recyclerHeight;
                        }

                        XIConLogUtil.d("=========mrecycleViewHeight=========heightDifference - bottomStatusHeight===========mPullRefreshLayout.getHeight()==============="+mrecycleViewHeight+"     "+(heightDifference - bottomStatusHeight)+"   "+mPullRefreshLayout.getHeight());
                        if ((heightDifference - bottomStatusHeight) >= (mPullRefreshLayout.getHeight() - mrecycleViewHeight)) {

                            int contentHeight = wcLinearLayoutManger == null ? 0 : wcLinearLayoutManger.getHeight();
                            if (mrecycleViewHeight < contentHeight ) {//recycleview height less than layoutmanager
                                listSlideHeight = heightDifference - (contentHeight - mrecycleViewHeight) - XIScreenUtil.getNavigateHeight(mContext);
                                int disbottom = 0;
                                if (mChatBottom != null) {
                                    disbottom = mChatBottom.getHeight() - mFirstBottomViewHeight;
                                    listSlideHeight = listSlideHeight + disbottom;
                                }
                                if (needToScrollView.getScrollY() == listSlideHeight) {
                                    return;
                                }
                                if (listSlideHeight > 0) {
                                    needToScrollView.scrollTo(0, listSlideHeight);
                                    if (wcLinearLayoutManger != null && tbAdapter != null) {
                                        wcLinearLayoutManger.scrollToPositionWithOffset(tbAdapter.getItemCount() - 1, 0);
                                    }
                                }

                            } else {
                                listSlideHeight = heightDifference - XIScreenUtil.getNavigateHeight(mContext);
                                int bottomHeight = 0;
                                if (mChatBottom != null) {
                                    bottomHeight = mChatBottom.getHeight() - mFirstBottomViewHeight;
                                    listSlideHeight = listSlideHeight + bottomHeight;
                                }

                                if (needToScrollView.getScrollY() == listSlideHeight) {
                                    return;
                                }
                                if (wcLinearLayoutManger != null && tbAdapter != null)
                                    wcLinearLayoutManger.scrollToPositionWithOffset(tbAdapter.getItemCount() - 1, 0);
                                needToScrollView.scrollTo(0, listSlideHeight);
                            }
                        } else {
                            listSlideHeight = 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        root.getViewTreeObserver().addOnGlobalLayoutListener(globalTreeListener);
    }

    protected void loadRecords() {
        isDown = true;
        if (pagelist != null) {
            pagelist.clear();
        }
        pagelist = mChatDbManager.loadPages(mContext, page, number);
        xiposition = pagelist.size();
        if (pagelist.size() != 0) {
            pagelist.addAll(tblist);
            if (xiposition != number && page != 0) {//first time load more than number data
                List<XIChatMessageBean> tempPagelist = new ArrayList<XIChatMessageBean>();
                page--;
                tempPagelist = mChatDbManager.loadPages(mContext, page, number);
                tblist.clear();
                tempPagelist.addAll(pagelist);
                xiposition = tempPagelist.size();
                tblist.addAll(tempPagelist);
            } else {
                tblist.clear();
                tblist.addAll(pagelist);
            }

            if (imageList != null) {
                imageList.clear();
            }
            if (imagePosition != null) {
                imagePosition.clear();
            }
            int key = 0;
            int tposition = 0;
            for (XIChatMessageBean cmb : tblist) {
                if (cmb.getType() == XIChatConst.TO_USER_IMG) {
                    if (!TextUtils.isEmpty(cmb.getImageOriginal()) && ((new File(cmb.getImageOriginal())).exists())) {
                        imageList.add(cmb.getImageOriginal());
                    } else {
                        imageList.add(cmb.getImageLocal());
                    }
                    imagePosition.put(key, tposition);
                    tposition++;
                } else if (cmb.getType() == XIChatConst.FROM_USER_IMG) {
                    if (!TextUtils.isEmpty(cmb.getImageUrl())) {
                        imageList.add(cmb.getImageUrl());
                        imagePosition.put(key, tposition);
                        tposition++;
                    }
                }
                key++;
            }
            tbAdapter.setImageList(imageList);
            tbAdapter.setImagePosition(imagePosition);
            sendMessageHandler.sendEmptyMessage(XICONVERSATION_PULL_TO_REFRESH_DOWN);
            if (page == 0) {
                mPullRefreshLayout.refreshComplete();
                mPullRefreshLayout.setPullGone();
            } else {
                page--;
            }
        } else {
            tbAdapter.setImageList(imageList);
            tbAdapter.setImagePosition(imagePosition);
            if (page == 0) {
                mPullRefreshLayout.refreshComplete();
                mPullRefreshLayout.setPullGone();
            }
        }
    }

    public void deleteMessage(long messageid) {
        if (tblist != null && tblist.size() > 0) {
            int pos = findPositionAtList(tblist, messageid);
            if (pos != -1) {
                try {
                    XIChatMessageBean xiChatMessageBean = new XIChatDbManager().getChatMessageBeanDao(mContext).queryBuilder().where(XIChatMessageBeanDao.Properties.Id.eq(messageid)).unique();
                    mChatDbManager.delete(mContext, xiChatMessageBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                tblist.remove(pos);
                Message msg = sendMessageHandler.obtainMessage();
                msg.what = XICONVERSATION_MESG_REMOVE;
                msg.arg1 = pos;
                sendMessageHandler.sendMessage(msg);
            }
        }
    }

    class XIChatViewSendMessageHandler extends Handler {
        WeakReference<Context> mActivity;

        XIChatViewSendMessageHandler(Context activity) {
            mActivity = new WeakReference<Context>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Context theActivity = mActivity.get();
            if (theActivity != null) {
                switch (msg.what) {
                    case XICONVERSATION_REFRESH:
                        tbAdapter.notifyDataSetChanged();
                        int position = tbAdapter.getItemCount() - 1 < 0 ? 0 : tbAdapter.getItemCount() - 1;
                        wcLinearLayoutManger.scrollToPositionWithOffset(position, 0);
                        break;
                    case XICONVERSATION_SEND_TEXT_OK:

                    case XICONVERSATION_SEND_OK:
                        tbAdapter.notifyItemInserted(tblist.size() - 1);
                        wcLinearLayoutManger.scrollToPositionWithOffset(tbAdapter.getItemCount() - 1, 0);
                        sendMessageHandler.sendEmptyMessageDelayed(XICONVERSATION_SCROOL_TO_END, 100);
                        break;
                    case XICONVERSATION_RECERIVE_OK:
                        tbAdapter.notifyItemInserted(tblist.size() - 1);
                        wcLinearLayoutManger.scrollToPositionWithOffset(tbAdapter.getItemCount() - 1, 0);
                        sendMessageHandler.sendEmptyMessageDelayed(XICONVERSATION_SCROOL_TO_END, 100);
                        break;
                    case XICONVERSATION_PULL_TO_REFRESH_DOWN:
                        mPullRefreshLayout.refreshComplete();
                        tbAdapter.notifyDataSetChanged();
                        if (lastPosition > number) {
                            wcLinearLayoutManger.scrollToPositionWithOffset(xiposition, 0);
                        } else {
                            wcLinearLayoutManger.scrollToPositionWithOffset(tbAdapter.getItemCount() - 1, 0);
                            sendMessageHandler.sendEmptyMessageDelayed(XICONVERSATION_SCROOL_TO_END, 100);
                        }
                        isDown = false;
                        lastPosition = tbAdapter.getItemCount();
                        break;
                    case XICONVERSATION_SEND_MESG_NULL:
                        Toast.makeText(theActivity, theActivity.getResources().getString(R.string.xiconversation_send_message_not_null), Toast.LENGTH_SHORT).show();
                        //mEditTextContent.setText("");
                        break;
                    case XICONVERSATION_UPDATESTATE:
                        tbAdapter.notifyItemChanged(msg.arg1);
                        wcLinearLayoutManger.scrollToPositionWithOffset(tbAdapter.getItemCount() - 1, 0);
                        break;
                    case XICONVERSATION_SCROOL_TO_END:
                        wcLinearLayoutManger.scrollToPositionWithOffset(tbAdapter.getItemCount() - 1, 0);
                        int[] bottomViewLocation = new int[2];
                        int[] lastViewLocation = new int[2];
                        int lastpos = -1;
                        View lastView = null;
                        if (wcLinearLayoutManger.findFirstVisibleItemPosition() != -1 && wcLinearLayoutManger.findLastVisibleItemPosition() != -1
                                && wcLinearLayoutManger.findLastVisibleItemPosition() == tbAdapter.getItemCount() - 1) {
                            try {
                                lastpos = wcLinearLayoutManger.findLastVisibleItemPosition();
                                lastView = wcLinearLayoutManger.findViewByPosition(lastpos);
                                if (lastView != null) {
                                    lastView.getLocationOnScreen(lastViewLocation);
                                }
                                mChatBottom.getLocationOnScreen(bottomViewLocation);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if ((wcLinearLayoutManger.findLastVisibleItemPosition() <= tbAdapter.getItemCount() - 1) && lastView != null && mChatBottom != null
                                    && wcLinearLayoutManger.getRecyclerHeight() > bottomViewLocation[1] && bottomViewLocation[1] < lastViewLocation[1] + lastView.getHeight()) {
                                try {
                                    int twolocation = lastViewLocation[1] + lastView.getHeight() - bottomViewLocation[1];
                                    if (twolocation > -14 && mRecycleView != null) {
                                        mRecycleView.scrollBy(0, (twolocation + 14));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        break;

                    case XICONVERSATION_COMPUTER_TO_END:
                        int[] bottomViewLocation2 = new int[2];
                        int[] lastViewLocation2 = new int[2];
                        int lastpos2 = -1;
                        View lastView2 = null;
                        if (wcLinearLayoutManger.findFirstVisibleItemPosition() != -1 && wcLinearLayoutManger.findLastVisibleItemPosition() == tbAdapter.getItemCount() - 1) {
                            try {
                                lastpos2 = wcLinearLayoutManger.findLastVisibleItemPosition();
                                lastView2 = wcLinearLayoutManger.findViewByPosition(lastpos2);
                                lastView2.getLocationOnScreen(lastViewLocation2);
                                mChatBottom.getLocationOnScreen(bottomViewLocation2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if ((wcLinearLayoutManger.findLastVisibleItemPosition() <= tbAdapter.getItemCount() - 1) && lastView2 != null && mChatBottom != null
                                    && wcLinearLayoutManger.getRecyclerHeight() > bottomViewLocation2[1] && bottomViewLocation2[1] < lastViewLocation2[1] + lastView2.getHeight()) {
                                try {
                                    int twolocation2 = lastViewLocation2[1] + lastView2.getHeight() - bottomViewLocation2[1];
                                    if (twolocation2 > -14) {
                                        mRecycleView.scrollBy(0, (twolocation2 + 14));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        break;
                    case XICONVERSATION_MESG_REMOVE:
                        tbAdapter.notifyItemRemoved(msg.arg1);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    XIAsyncTaskListener xiGreetingAsyncTaskListener = new XIAsyncTaskListener() {

        @Override
        public void onComplete(ArrayList<XIChatMessageBean> chatMessageBeenList, long mChatbeanId) {

            for (XIChatMessageBean chatMessageBean : chatMessageBeenList) {
                if (chatMessageBean != null) {
                    try {
                        if (chatMessageBean.getType() == XIChatConst.FROM_USER_MSG){
                            if (!TextUtils.isEmpty(chatMessageBean.getUserContent())){
                                mChatDbManager.insert(mContext, chatMessageBean);
                                tblist.add(chatMessageBean);
                            }
                        }else{
                            mChatDbManager.insert(mContext, chatMessageBean);
                            tblist.add(chatMessageBean);
                        }

                    } catch (Exception e) {
                        tblist.add(chatMessageBean);
                        e.printStackTrace();
                    }

                    if (chatMessageBean.getType() == XIChatConst.FROM_USER_IMG) {
                        try {
                            DownloadFileService downloadImageService = new DownloadFileService(mContext, chatMessageBean.getImageUrl()
                                    , getOriginalSavePicPath(), getSavePicPath(), chatMessageBean.getId(), new DownloadFileService.ImageDownLoadCallBack() {
                                @Override
                                public void onDownloadSuccess(long id, String oripath, String imgpath,boolean isgif) {
                                    if (!TextUtils.isEmpty(oripath)/* && new File(oripath).exists()*/) {
                                        XIChatMessageBean updatebean = new XIChatDbManager().getChatMessageBeanDao(mContext).queryBuilder().where(
                                                XIChatMessageBeanDao.Properties.Id.eq(id)
                                        ).unique();
                                        int[] result = new int[2];
                                        if (isgif){
                                            try {
                                                Movie movie = Movie.decodeFile(oripath);
                                                XIFileSaveUtil.getWidthAndHeight(mContext,movie.width(),movie.height(),result);
                                                updatebean.setGif(true);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            try {
                                                int[] origianl = XIFileSaveUtil.getImageWidthHeight(oripath);
                                                XIFileSaveUtil.getWidthAndHeight(mContext, origianl[0], origianl[1], result);
                                                updatebean.setGif(false);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        updatebean.setImageOriginal(oripath);
                                        updatebean.setImageLocal(oripath);
                                        updatebean.setImgHeight(result[1]);
                                        updatebean.setImgWidth(result[0]);
                                        new XIChatDbManager().getChatMessageBeanDao(mContext).update(updatebean);
                                        int findPos = findPositionAtList(tblist, id);
                                        tblist.get(findPos).setImageLocal(oripath);
                                        tblist.get(findPos).setImageOriginal(oripath);
                                        tblist.get(findPos).setImgHeight(result[1]);
                                        tblist.get(findPos).setImgWidth(result[0]);
                                        Message msg = sendMessageHandler.obtainMessage();
                                        msg.what = XICONVERSATION_UPDATESTATE;
                                        msg.arg1 = findPos;
                                        sendMessageHandler.sendMessage(msg);
                                    }
                                }

                                @Override
                                public void onDownLoadFailed() {

                                }
                            });
                            new Thread(downloadImageService).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!TextUtils.isEmpty(chatMessageBean.getImageUrl())) {
                            if (chatMessageBean != null && !TextUtils.isEmpty(chatMessageBean.getImageOriginal())) {
                                imageList.add(tblist.get(tblist.size() - 1).getImageOriginal());
                                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
                            } else {
                                imageList.add(tblist.get(tblist.size() - 1).getImageUrl());
                                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
                            }

                            if (tbAdapter.getItemCount() == 0) {
                                tbAdapter.setImageList(imageList);
                                tbAdapter.setImagePosition(imagePosition);
                            }
                        }
                    }
                    sendMessageHandler.sendEmptyMessage(XICONVERSATION_RECERIVE_OK);
                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (mChatbeanId == XIChatConst.XICONVERSATION_FIRSTINTO_GREETING_RESPONSEID) {
                sendNewSessionMessage();
            } else if (mChatbeanId == XIChatConst.XICONVERSATION_NEWCONVERSATION_GREETING_RESPONSEID) {

            }
            setChatBottomEnable(true);
        }

        @Override
        public void onError(String error, long mChatbeanId) {
            setChatBottomEnable(true);
            if (mChatbeanId == -2) {
                sendGreetingMessage(XIChatConst.XICONVERSATION_NEWCONVERSATION_GREETING, false);
            }
        }
    };

    public void sendGreetingMessage(final String content, final boolean isFirstIn) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setChatBottomEnable(false);
                if (!TextUtils.isEmpty(content) && isFirstIn) {
                    new XIInfoGetAsyncTask(mContext,
                            xiGreetingAsyncTaskListener, XIChatConst.XICONVERSATION_FIRSTINTO_GREETING_RESPONSEID).execute(XIChatConst.XICONVERSATION_REQUESTTYPE_TEXT, content);
                } else if (!TextUtils.isEmpty(content) && !isFirstIn) {
                    new XIInfoGetAsyncTask(mContext,
                            xiGreetingAsyncTaskListener, XIChatConst.XICONVERSATION_NEWCONVERSATION_GREETING_RESPONSEID).execute(XIChatConst.XICONVERSATION_REQUESTTYPE_TEXT, content);
                } else {
                    setChatBottomEnable(true);
                }
            }
        }).start();
    }

    XIAsyncTaskListener xiAsyncTaskListener = new XIAsyncTaskListener() {
        @Override
        public void onComplete(ArrayList<XIChatMessageBean> chatMessageBeenList, long mChatbeanId) {
            try {
                setChatBottomEnable(true);
                tblist.remove(tblist.size() - 1);
                if (chatMessageBeenList == null || chatMessageBeenList.size() == 0){
                    Message msg = sendMessageHandler.obtainMessage();
                    msg.what = XICONVERSATION_MESG_REMOVE;
                    msg.arg1 = tblist.size() - 1;
                    sendMessageHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                XIChatMessageBean xiChatMessageBean = new XIChatDbManager().getChatMessageBeanDao(mContext).queryBuilder().where(XIChatMessageBeanDao.Properties.Id.eq(mChatbeanId)).unique();
                if (xiChatMessageBean.getSendState() != XIChatConst.XICONVERSATION_COMPLETED) {
                    xiChatMessageBean.setSendState(XIChatConst.XICONVERSATION_COMPLETED);
                    mChatDbManager.update(mContext, xiChatMessageBean);
                    int findPos = findPositionAtList(tblist, mChatbeanId);
                    if (findPos != -1) {
                        XIChatMessageBean tempXIChatMessageBean = tblist.get(findPos);
                        tempXIChatMessageBean.setSendState(XIChatConst.XICONVERSATION_COMPLETED);
                    }
                    Message msg = sendMessageHandler.obtainMessage();
                    msg.what = XICONVERSATION_UPDATESTATE;
                    msg.arg1 = findPos;
                    sendMessageHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (XIChatMessageBean chatMessageBean : chatMessageBeenList) {
                if (chatMessageBean != null) {
                    try {
                        mChatDbManager.insert(mContext, chatMessageBean);
                        tblist.add(chatMessageBean);
                    } catch (Exception e) {
                        tblist.add(chatMessageBean);
                        e.printStackTrace();
                    }

                    if (chatMessageBean.getType() == XIChatConst.FROM_USER_IMG) {
                        try {
                            DownloadFileService downloadImageService = new DownloadFileService(mContext, chatMessageBean.getImageUrl()
                                    , getOriginalSavePicPath(), getSavePicPath(), chatMessageBean.getId(), new DownloadFileService.ImageDownLoadCallBack() {
                                @Override
                                public void onDownloadSuccess(long id, String oripath, String imgpath,boolean isGif) {
                                    if (!TextUtils.isEmpty(oripath) && new File(oripath).exists()) {
                                        XIChatMessageBean updatebean = new XIChatDbManager().getChatMessageBeanDao(mContext).queryBuilder().where(
                                                XIChatMessageBeanDao.Properties.Id.eq(id)
                                        ).unique();
                                        int[] result = new int[2];
                                        result[0] = 0;
                                        result[1] = 0;
                                        if (isGif){
                                            try {
                                                Movie movie = Movie.decodeFile(oripath);
                                                XIFileSaveUtil.getWidthAndHeight(mContext,movie.width(),movie.height(),result);
                                                updatebean.setGif(true);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            try {
                                                int[] origianl = XIFileSaveUtil.getImageWidthHeight(oripath);
                                                XIFileSaveUtil.getWidthAndHeight(mContext, origianl[0], origianl[1], result);
                                                updatebean.setGif(false);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        updatebean.setImageOriginal(oripath);
                                        updatebean.setImageLocal(oripath);
                                        updatebean.setImgHeight(result[1]);
                                        updatebean.setImgWidth(result[0]);
                                        new XIChatDbManager().getChatMessageBeanDao(mContext).update(updatebean);
                                        int findPos = findPositionAtList(tblist, id);
                                        tblist.get(findPos).setImageLocal(oripath);
                                        tblist.get(findPos).setImageOriginal(oripath);
                                        tblist.get(findPos).setImgHeight(result[1]);
                                        tblist.get(findPos).setImgWidth(result[0]);
                                        Message msg = sendMessageHandler.obtainMessage();
                                        msg.what = XICONVERSATION_UPDATESTATE;
                                        msg.arg1 = findPos;
                                        sendMessageHandler.sendMessage(msg);
                                    }
                                }

                                @Override
                                public void onDownLoadFailed() {

                                }
                            });
                            new Thread(downloadImageService).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!TextUtils.isEmpty(chatMessageBean.getImageUrl())) {
                            if (chatMessageBean != null && !TextUtils.isEmpty(chatMessageBean.getImageOriginal())) {
                                imageList.add(tblist.get(tblist.size() - 1).getImageOriginal());
                                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
                            } else {
                                imageList.add(tblist.get(tblist.size() - 1).getImageUrl());
                                imagePosition.put(tblist.size() - 1, imageList.size() - 1);
                            }

                            if (tbAdapter.getItemCount() == 0) {
                                tbAdapter.setImageList(imageList);
                                tbAdapter.setImagePosition(imagePosition);
                            }
                        }
                    }else{
                        sendMessageHandler.sendEmptyMessage(XICONVERSATION_RECERIVE_OK);
                    }

                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onError(String error, long mChatbeanId) {
            try {
                setChatBottomEnable(true);
                tblist.remove(tblist.size() - 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!XIConNetWorkUtils.isNetworkConnected(mContext)) {
                Toast.makeText(mContext, getResources().getString(R.string.xiconversation_check_network_error), Toast.LENGTH_SHORT).show();
            }
            try {
                XIChatMessageBean xiChatMessageBean = new XIChatDbManager().getChatMessageBeanDao(mContext).queryBuilder().where(XIChatMessageBeanDao.Properties.Id.eq(mChatbeanId)).unique();
                xiChatMessageBean.setSendState(XIChatConst.XICONVERSATION_SENDERROR);
                mChatDbManager.update(mContext, xiChatMessageBean);
                int findPos = findPositionAtList(tblist, mChatbeanId);
                if (findPos != -1) {
                    XIChatMessageBean tempXIChatMessageBean = tblist.get(findPos);
                    tempXIChatMessageBean.setSendState(XIChatConst.XICONVERSATION_SENDERROR);
                }
                if (findPos != -1) {
                    Message msg = sendMessageHandler.obtainMessage();
                    msg.what = XICONVERSATION_UPDATESTATE;
                    msg.arg1 = findPos;
                    sendMessageHandler.sendMessage(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void sendMessage(final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!canSendMesg) {
                    return;
                }
                try {
                    setChatBottomEnable(false);
                    if (!TextUtils.isEmpty(content)) {
                        tblist.add(getTbub(mContext, userName, XIChatConst.TO_USER_MSG, content, null, null,
                                null, null, null, null, 0f, true, 0, 0, XIChatConst.XICONVERSATION_COMPLETED));
                        tblist.add(getLoadingBean());
                        sendMessageHandler.sendEmptyMessage(XICONVERSATION_SEND_TEXT_OK);
                        new XIInfoGetAsyncTask(mContext, xiAsyncTaskListener, tblist.get(tblist.size() - 2).getId()).execute(XIChatConst.XICONVERSATION_REQUESTTYPE_TEXT, content);
                    } else {
                        setChatBottomEnable(true);
                        sendMessageHandler.sendEmptyMessage(XICONVERSATION_SEND_MESG_NULL);
                    }
                } catch (Exception e) {
                    setChatBottomEnable(true);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void reSendMessage(final String mesg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!canSendMesg && mContext != null) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, getResources().getString(R.string.xiconversation_wait_get_response), Toast.LENGTH_SHORT);
                            }
                        });
                        return;
                    }
                    setChatBottomEnable(false);
                    String content = mesg.trim();
                    if (!TextUtils.isEmpty(content)) {
                        tblist.add(getTbub(mContext, userName, XIChatConst.TO_USER_MSG, content, null, null,
                                null, null, null, null, 0f, true, 0, 0, XIChatConst.XICONVERSATION_COMPLETED));
                        tblist.add(getLoadingBean());
                        sendMessageHandler.sendEmptyMessage(XICONVERSATION_SEND_OK);
                        new XIInfoGetAsyncTask(mContext, xiAsyncTaskListener, tblist.get(tblist.size() - 2).getId()).execute(XIChatConst.XICONVERSATION_REQUESTTYPE_TEXT, content);
                    } else {
                        setChatBottomEnable(true);
                        sendMessageHandler.sendEmptyMessage(XICONVERSATION_SEND_MESG_NULL);
                    }

                } catch (Exception e) {
                    setChatBottomEnable(true);
                }
            }
        }).start();
    }

    public void sendImage(String filePath) {
        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            File camFile = new File(filePath); // image path
            if (camFile.exists()) {
                int size = XIImageCheckoutUtil
                        .getImageSize(XIImageCheckoutUtil
                                .getLoacalBitmap(filePath));
                if (size > IMAGE_SIZE) {
                    compressImageDialog(filePath);
                } else {
                    sendImage(filePath, filePath);
                }
            } else {
                showToast(getResources().getString(R.string.xiconversation_sorry_not_have_file));
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
    }

    public void sendImage(final String originalFile, final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setChatBottomEnable(false);
                    int[] result = new int[2];
                    try {
                        int[] origianl = XIFileSaveUtil.getImageWidthHeight(filePath);
                        XIFileSaveUtil.getWidthAndHeight(mContext, origianl[0], origianl[1], result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tblist.add(getTbub(mContext, userName, XIChatConst.TO_USER_IMG, null, null, null, filePath, originalFile, null, null,
                            0f, true, (int)result[0], (int)result[1], XIChatConst.XICONVERSATION_COMPLETED));
                    if (!TextUtils.isEmpty(tblist.get(tblist.size() - 1).getImageOriginal())) {
                        imageList.add(tblist.get(tblist.size() - 1).getImageOriginal());
                    } else {
                        imageList.add(tblist.get(tblist.size() - 1).getImageLocal());
                    }

                    imagePosition.put(tblist.size() - 1, imageList.size() - 1);
                    tblist.add(getLoadingBean());
                    sendMessageHandler.sendEmptyMessage(XICONVERSATION_SEND_OK);
                    String baseStr1 = XIResponseInformationTool.getBase64Str(filePath);
                    new XIInfoGetAsyncTask(mContext, xiAsyncTaskListener, tblist.get(tblist.size() - 2).getId()).execute(XIChatConst.XICONVERSATION_REQUESTTYPE_IMAGE, baseStr1);
                } catch (Exception e) {
                    setChatBottomEnable(true);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendVoice(final float seconds, final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setChatBottomEnable(false);
                    tblist.add(getTbub(mContext, userName, XIChatConst.TO_USER_VOICE, null, null, null, null, null,
                            filePath, null, seconds, false, 0, 0, XIChatConst.XICONVERSATION_COMPLETED));
                    tblist.add(getLoadingBean());
                    sendMessageHandler.sendEmptyMessage(XICONVERSATION_SEND_OK);
                    String baseStr1 = XIResponseInformationTool.getBase64Str(filePath);
                    new XIInfoGetAsyncTask(mContext, xiAsyncTaskListener, tblist.get(tblist.size() - 2).getId()).execute(XIChatConst.XICONVERSATION_REQUESTTYPE_AMRSPEECH, baseStr1);
                } catch (Exception e) {
                    setChatBottomEnable(true);
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private int findPositionAtList(List<XIChatMessageBean> tblist, long mChatbeanPos) {
        int position = -1;
        try {
            if (tblist != null) {
                for (int i = 0; i < tblist.size(); i++) {
                    if (tblist.get(i).getId() == mChatbeanPos) {
                        position = i;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return position;
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


    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case XIChatConst.XICONVERSATION_PERMISSION_DB: {
                    if (AndPermission.hasPermission(mContext, grantPermissions)) {
                        loadRecords();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.xiconversation_audio_storage_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                case XIChatConst.XICONVERSATION_PERMISSION_OPENDB: {
                    if (AndPermission.hasPermission(mContext, grantPermissions)) {
                        loadRecords();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.xiconversation_audio_storage_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case XIChatConst.XICONVERSATION_PERMISSION_DB: {
                    if (AndPermission.hasPermission(mContext, deniedPermissions)) {
                        loadRecords();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.xiconversation_audio_storage_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                case XIChatConst.XICONVERSATION_PERMISSION_OPENDB: {
                    if (AndPermission.hasPermission(mContext, deniedPermissions)) {
                        loadRecords();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.xiconversation_audio_storage_permissionerror), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }

            if (AndPermission.hasAlwaysDeniedPermission(mContext, deniedPermissions)) {
                AndPermission.defaultSettingDialog((AppCompatActivity) mContext, 300).show();
            }
        }
    };

    public void recycleResource() {
        if (userHeadIconClickListener != null) userHeadIconClickListener = null;
        if (xiaoiceHeadIconClickListener != null) xiaoiceHeadIconClickListener = null;
        if (xiConversationChatPageConfig != null) xiConversationChatPageConfig = null;
        if (mxiConversationChatPageConfig != null) mxiConversationChatPageConfig = null;
        if (muserHeadIconClickListener != null) muserHeadIconClickListener = null;
        if (mxiaoiceHeadIconClickListener != null) mxiaoiceHeadIconClickListener = null;
        if (xiChatMessageDefaultLongClickListener != null)
            xiChatMessageDefaultLongClickListener = null;
        if (xiChatMessageLongClickListener != null) xiChatMessageLongClickListener = null;
        if (mxiChatMessageDefaultLongClickListener != null)
            mxiChatMessageDefaultLongClickListener = null;
        if (mxiChatMessageLongClickListener != null) mxiChatMessageLongClickListener = null;
        if (mPopMenuChatMessageDefaultLongClickListener != null)
            mPopMenuChatMessageDefaultLongClickListener = null;
        if (mxiChatMessageDisplayConfigListener != null) {
            mxiChatMessageDisplayConfigListener = null;
        }
        if (xiChatMessageDisplayConfigListener != null) {
            xiChatMessageDisplayConfigListener = null;
        }
        if (xiMessageMenuPopupWindow != null) {
            xiMessageMenuPopupWindow = null;
        }

        try {
            if(activityRootView != null){
                activityRootView.getViewTreeObserver().removeGlobalOnLayoutListener(globalTreeListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        XIMediaManager.pause(mContext);
        XIMediaManager.release(mContext);
        cancelToast();
        if (tblist != null)
            tblist.clear();
        tbAdapter.notifyDataSetChanged();
        mRecycleView.setAdapter(null);
        if (sendMessageHandler != null) sendMessageHandler.removeCallbacksAndMessages(null);
    }

    private XIChatMessageBean getLoadingBean() {
        XIChatMessageBean tbub = new XIChatMessageBean();
        String time = returnTime();
        tbub.setTime(time);
        tbub.setType(XIChatConst.FROM_USER_LOADING);
        tbub.setTimeStamp(XIDateUtils.getLocalTimestamp());
        return tbub;
    }

    private String getSavePicPath() {
        return XIPictureUtil.getImgSavePath(mContext);
    }

    private String getOriginalSavePicPath() {
        return XIPictureUtil.getOriginalImgSavePicPath(mContext);
    }

    private void closeDefaultAnimator(RecyclerView recyclerView) {
        recyclerView.getItemAnimator().setAddDuration(0);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.getItemAnimator().setMoveDuration(0);
        recyclerView.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void initGreetingConfig() {
        if (xiConversationChatPageConfig != null) {
            if (xiConversationChatPageConfig.isChatPageShowFirstOpenSignalGreeting() && xiConversationChatPageConfig.isChatPageFirstOpenSignal()) {
                sendFirstOpenMessage();
            } else {
                sendNewSessionMessage();
            }
        }
    }

    public void sendFirstOpenMessage() {
        if (xiConversationChatPageConfig != null) {
            try {
                if (!TextUtils.isEmpty(xiConversationChatPageConfig.getChatPageFirstOpenSignalGreetings().trim())) {
                    if (tblist != null) {
                        tblist.add(getTbub(mContext, userName, XIChatConst.FROM_USER_MSG, xiConversationChatPageConfig.getChatPageFirstOpenSignalGreetings().trim(), null, null, null,
                                null, null, null, 0f, true, 0, 0, XIChatConst.XICONVERSATION_COMPLETED));
                        if (sendMessageHandler != null)
                            sendMessageHandler.sendEmptyMessage(XICONVERSATION_RECERIVE_OK);
                    }
                    sendNewSessionMessage();
                } else {
                    sendGreetingMessage(XIChatConst.XICONVERSATION_FIRSTINTO_GREETING, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendNewSessionMessage() {
        if (xiConversationChatPageConfig != null) {
            try {
                if (xiConversationChatPageConfig.isChatPageShowNewSessionSignalGreeting()) {
                    if (!TextUtils.isEmpty(xiConversationChatPageConfig.getChatPageNewSessionSignalGreetings().trim())) {
                        if (tblist != null) {
                            tblist.add(getTbub(mContext, userName, XIChatConst.FROM_USER_MSG, xiConversationChatPageConfig.getChatPageNewSessionSignalGreetings().trim(), null, null, null,
                                    null, null, null, 0f, true, 0, 0, XIChatConst.XICONVERSATION_COMPLETED));
                            if (sendMessageHandler != null)
                                sendMessageHandler.sendEmptyMessage(XICONVERSATION_RECERIVE_OK);
                        }
                    } else {
                        sendGreetingMessage(XIChatConst.XICONVERSATION_NEWCONVERSATION_GREETING, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setChatBottomEnable(boolean cansend) {
        canSendMesg = cansend;
    }

    //can send
    public boolean getSendState() {
        return canSendMesg;
    }

}
