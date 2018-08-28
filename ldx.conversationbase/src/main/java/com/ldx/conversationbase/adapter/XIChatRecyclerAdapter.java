package com.ldx.conversationbase.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ldx.conversationbase.R;
import com.ldx.conversationbase.activity.XINewsDetailViewActivity;
import com.ldx.conversationbase.common.XIChatConst;
import com.ldx.conversationbase.common.XIGlideCircleTransform;
import com.ldx.conversationbase.common.bean.XIConversationChatPageConfig;
import com.ldx.conversationbase.db.XICardMessageBean;
import com.ldx.conversationbase.db.XIChatDbManager;
import com.ldx.conversationbase.db.XIChatMessageBean;
import com.ldx.conversationbase.db.XIChatMessageBeanDao;
import com.ldx.conversationbase.jsonparse.XIChatResponseParse;
import com.ldx.conversationbase.listener.XIChatCardClickListener;
import com.ldx.conversationbase.listener.XIChatMessageDisplayConfigListener;
import com.ldx.conversationbase.listener.XIUserHeadIconClickListener;
import com.ldx.conversationbase.listener.XIXiaoiceHeadIconClickListener;
import com.ldx.conversationbase.activity.XIImageViewActivity;
import com.ldx.conversationbase.utils.XIConLogUtil;
import com.ldx.conversationbase.utils.XIConNetWorkUtils;
import com.ldx.conversationbase.utils.XIFileSaveUtil;
import com.ldx.conversationbase.widget.XIBubbleImageView;
import com.ldx.conversationbase.widget.XICornerShapeTransformation;
import com.ldx.conversationbase.widget.XICustomShapeTransformation;
import com.ldx.conversationbase.widget.XIMediaManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

/**
 * come from github chatdemo
 */
public class XIChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<XIChatMessageBean> userList = new ArrayList<XIChatMessageBean>();
    private ArrayList<String> imageList = new ArrayList<String>();
    private HashMap<Integer, Integer> imagePosition = new HashMap<Integer, Integer>();
    private int mMinItemWith;
    private int mMaxItemWith;
    public Handler handler;
    private Animation an;
    private SendErrorListener sendErrorListener;

    private int voicePlayPosition = -1;
    private LayoutInflater mLayoutInflater;
    private boolean isGif = false;

    public XIConversationChatPageConfig mActivityConfigBean;
    public XIUserHeadIconClickListener mHeadIconClickListener;
    public XIXiaoiceHeadIconClickListener mXiaoiceHeadIconClickListener;
    public XIChatMessageDisplayConfigListener mXIChatMessageDisplayConfigListener;
    public XIChatCardClickListener mXIChatCardClickListener;

    public interface RecyclerViewOnItemLongClickListener {
        boolean onItemLongClickListener(View view, XIChatMessageBean tbub, int position);
        boolean onMulItemLongClickListener(View view, XICardMessageBean tbub, int position);
    }
    public RecyclerViewOnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(RecyclerViewOnItemLongClickListener itemLongClickListener) {
        this.onItemLongClickListener = itemLongClickListener;
    }

    public interface SendErrorListener {
        public void onClick(long position);
    }

    public void setSendErrorListener(SendErrorListener sendErrorListener) {
        this.sendErrorListener = sendErrorListener;
    }

    public XIChatRecyclerAdapter(Context context, List<XIChatMessageBean> userList) {
        this.context = context;
        this.userList = userList;
        mActivityConfigBean = null;
        mHeadIconClickListener = null;
        mXiaoiceHeadIconClickListener = null;
        mLayoutInflater = LayoutInflater.from(context);
        WindowManager wManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wManager.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWith = (int) (outMetrics.widthPixels * 0.5f);
        mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);
        handler = new Handler();
    }

    public XIChatRecyclerAdapter(Context context, List<XIChatMessageBean> userList, XIConversationChatPageConfig tActivityConfigBean,
                                 XIUserHeadIconClickListener tHeadIconClickListener,
                                 XIXiaoiceHeadIconClickListener tXiaoiceHeadIconClickListener,
                                 XIChatMessageDisplayConfigListener tXIChatMessageDisplayConfigListener
    ) {
        this.context = context;
        this.userList = userList;
        mActivityConfigBean = tActivityConfigBean;
        mHeadIconClickListener = tHeadIconClickListener;
        mXiaoiceHeadIconClickListener = tXiaoiceHeadIconClickListener;
        mXIChatMessageDisplayConfigListener = tXIChatMessageDisplayConfigListener;

        mLayoutInflater = LayoutInflater.from(context);
        WindowManager wManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wManager.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWith = (int) (outMetrics.widthPixels * 0.5f);
        mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);
        handler = new Handler();
    }

    public XIChatRecyclerAdapter(Context context, List<XIChatMessageBean> userList, XIConversationChatPageConfig tActivityConfigBean,
                                 XIUserHeadIconClickListener tHeadIconClickListener,
                                 XIXiaoiceHeadIconClickListener tXiaoiceHeadIconClickListener,
                                 XIChatMessageDisplayConfigListener tXIChatMessageDisplayConfigListener,
                                 XIChatCardClickListener tXIChatCardClickListener) {
        this.context = context;
        this.userList = userList;
        mActivityConfigBean = tActivityConfigBean;
        mHeadIconClickListener = tHeadIconClickListener;
        mXiaoiceHeadIconClickListener = tXiaoiceHeadIconClickListener;
        mXIChatMessageDisplayConfigListener = tXIChatMessageDisplayConfigListener;
        mXIChatCardClickListener = tXIChatCardClickListener;
        mLayoutInflater = LayoutInflater.from(context);
        WindowManager wManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wManager.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWith = (int) (outMetrics.widthPixels * 0.5f);
        mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);
        handler = new Handler();
    }

    public void setIsGif(boolean isGif,int first,int last) {
        try {
            this.isGif = isGif;
            if (isGif){
                for (int i= first;i<= last;i++){
                    if (userList.get(i).getIsGif()){
                        notifyItemChanged(i);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public void setImagePosition(HashMap<Integer, Integer> imagePosition) {
        this.imagePosition = imagePosition;
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case XIChatConst.FROM_USER_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xiconversation_msgfrom_list_item, parent, false);
                holder = new FromUserMsgViewHolder(view);
                break;
            case XIChatConst.FROM_USER_IMG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xiconversation_imagefrom_list_item, parent, false);
                holder = new FromUserImageViewHolder(view);
                break;
            case XIChatConst.FROM_USER_VOICE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xiconversation_voicefrom_list_item, parent, false);
                holder = new FromUserVoiceViewHolder(view);
                break;
            case XIChatConst.FROM_USER_MULTILE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xiconversation_multiplefrom_list_item, parent, false);
                holder = new FromUserMultipleViewHolder(view);
                break;
            case XIChatConst.FROM_USER_LOADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xiconversation_loading_list_item, parent, false);
                holder = new FromLoadingViewHolder(view);
                break;
            case XIChatConst.TO_USER_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xiconversation_msgto_list_item, parent, false);
                holder = new ToUserMsgViewHolder(view);
                break;
            case XIChatConst.TO_USER_IMG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xiconversation_imageto_list_item, parent, false);
                holder = new ToUserImgViewHolder(view);
                break;
            case XIChatConst.TO_USER_VOICE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xiconversation_voiceto_list_item, parent, false);
                holder = new ToUserVoiceViewHolder(view);
                break;
        }
        return holder;
    }

    class FromUserMsgViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private TextView chat_time;
        private TextView content;

        public FromUserMsgViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.xiconversation_fromusermsg_tb_other_user_icon);
            chat_time = (TextView) view.findViewById(R.id.xiconversation_fromusermsg_chat_time);
            content = (TextView) view.findViewById(R.id.xiconversation_fromusermsg_content);
        }
    }

    class FromUserImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private TextView chat_time;
        private pl.droidsonroids.gif.GifImageView image_Msg;

        public FromUserImageViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.xiconversation_fromuserimg_tb_other_user_icon);
            chat_time = (TextView) view.findViewById(R.id.xiconversation_fromuserimg_chat_time);
            image_Msg = (pl.droidsonroids.gif.GifImageView) view
                    .findViewById(R.id.xiconversation_fromuserimg_image_message);
        }
    }

    class FromUserMultipleViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout container;
        private ImageView headicon;
        private TextView chat_time;
        RecyclerView recyclerView;
        TextView showmorenews;

        public FromUserMultipleViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.xiconversation_frommultiple_tb_other_user_icon);
            chat_time = (TextView) view.findViewById(R.id.xiconversation_frommultiple_chat_time);
            recyclerView = (RecyclerView) view.findViewById(R.id.xiconversation_frommultiple_recycleview);
            showmorenews = (TextView) view.findViewById(R.id.xiconversation_show_morenews);
            container = (RelativeLayout) view.findViewById(R.id.xiconversation_activity_multiplefrom_container);
        }
    }

    class FromLoadingViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private TextView chat_time;
        private ImageView loadingimg;

        public FromLoadingViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.xiconversation_loading_tb_other_user_icon);
            chat_time = (TextView) view.findViewById(R.id.xiconversation_loading_time);
            loadingimg = (ImageView) view.findViewById(R.id.xiconversation_loading_send_fail_img);
        }
    }

    class FromUserVoiceViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private TextView chat_time;
        private LinearLayout voice_group;
        private TextView voice_time;
        private FrameLayout voice_image;
        private View receiver_voice_unread;
        private View voice_anim;

        public FromUserVoiceViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.xiconversation_fromuservoice_tb_other_user_icon);
            chat_time = (TextView) view.findViewById(R.id.xiconversation_fromuservoice_chat_time);
            voice_group = (LinearLayout) view
                    .findViewById(R.id.xiconversation_fromuservoice_voice_group);
            voice_time = (TextView) view
                    .findViewById(R.id.xiconversation_fromuservoice_voice_time);
            receiver_voice_unread = (View) view
                    .findViewById(R.id.xiconversation_fromuservoice_receiver_voice_unread);
            voice_image = (FrameLayout) view
                    .findViewById(R.id.xiconversation_fromuservoice_voice_receiver_image);
            voice_anim = (View) view
                    .findViewById(R.id.xiconversation_fromuservoice_id_receiver_recorder_anim);
        }
    }

    class ToUserMsgViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private TextView chat_time;
        private TextView content;
        private ImageView sendFailImg;

        public ToUserMsgViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.xiconversation_tousermes_tb_my_user_icon);
            chat_time = (TextView) view
                    .findViewById(R.id.xiconversation_tousermes_mychat_time);
            content = (TextView) view
                    .findViewById(R.id.xiconversation_tousermes_mycontent);
            sendFailImg = (ImageView) view
                    .findViewById(R.id.xiconversation_tousermes_send_fail_img);
        }
    }

    class ToUserImgViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private TextView chat_time;
        private LinearLayout image_group;
        private pl.droidsonroids.gif.GifImageView image_Msg;
        private ImageView sendFailImg;

        public ToUserImgViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.xiconversation_touserimg_tb_my_user_icon);
            chat_time = (TextView) view
                    .findViewById(R.id.xiconversation_touserimg_mychat_time);
            sendFailImg = (ImageView) view
                    .findViewById(R.id.xiconversation_touserimg_mysend_fail_img);
            image_group = (LinearLayout) view
                    .findViewById(R.id.xiconversation_touserimg_image_group);
            image_Msg = (pl.droidsonroids.gif.GifImageView) view
                    .findViewById(R.id.xiconversation_touserimg_image_message);
        }
    }

    class ToUserVoiceViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private TextView chat_time;
        private LinearLayout voice_group;
        private TextView voice_time;
        private FrameLayout voice_image;
        private View receiver_voice_unread;
        private View voice_anim;
        private ImageView sendFailImg;

        public ToUserVoiceViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.xiconversation_touservoice_tb_my_user_icon);
            chat_time = (TextView) view
                    .findViewById(R.id.xiconversation_touservoice_mychat_time);
            voice_group = (LinearLayout) view
                    .findViewById(R.id.xiconversation_touservoice_voice_group);
            voice_time = (TextView) view
                    .findViewById(R.id.xiconversation_touservoice_voice_time);
            voice_image = (FrameLayout) view
                    .findViewById(R.id.xiconversation_touservoice_voice_image);
            voice_anim = (View) view
                    .findViewById(R.id.xiconversation_touservoice_id_recorder_anim);
            sendFailImg = (ImageView) view
                    .findViewById(R.id.xiconversation_touservoice_mysend_fail_img);
        }
    }

    /**
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        XIChatMessageBean tbub = userList.get(position);
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case XIChatConst.FROM_USER_MSG:
                fromMsgUserLayout((FromUserMsgViewHolder) holder, tbub, position);
                break;
            case XIChatConst.FROM_USER_IMG:
                fromImgUserLayout((FromUserImageViewHolder) holder, tbub, position);
                break;
            case XIChatConst.FROM_USER_VOICE:
                fromVoiceUserLayout((FromUserVoiceViewHolder) holder, tbub, position);
                break;
            case XIChatConst.FROM_USER_MULTILE:
                fromMultipleUserLayout((FromUserMultipleViewHolder) holder, tbub, position);
                break;
            case XIChatConst.TO_USER_MSG:
                toMsgUserLayout((ToUserMsgViewHolder) holder, tbub, position);
                break;
            case XIChatConst.TO_USER_IMG:
                toImgUserLayout((ToUserImgViewHolder) holder, tbub, position);
                break;
            case XIChatConst.TO_USER_VOICE:
                toVoiceUserLayout((ToUserVoiceViewHolder) holder, tbub, position);
                break;
            case XIChatConst.FROM_USER_LOADING:
                fromLoadingUserLayout((FromLoadingViewHolder) holder, tbub, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return userList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void fromMsgUserLayout(final FromUserMsgViewHolder holder, final XIChatMessageBean tbub, final int position) {
        if (onItemLongClickListener != null && holder.content != null){
            holder.content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClickListener(holder.content,tbub,position);
                    return true;
                }
            });
        }
        int localres = R.drawable.xiconversation_left_user_headicon;
        setHeadiconResource(mActivityConfigBean, holder.headicon, mActivityConfigBean.getChatPageLeftHeadIconId(), mActivityConfigBean.getChatPageLeftHeadIconPath(), localres);
        if (mActivityConfigBean != null) {
            try {
                if (mActivityConfigBean.getChatPageLeftBubbleImgId() != -1 && context.getResources().getDrawable(mActivityConfigBean.getChatPageLeftBubbleImgId()) != null) {
                    holder.content.setBackgroundResource(mActivityConfigBean.getChatPageLeftBubbleImgId());
                } else {
                    holder.content.setBackgroundResource(R.drawable.xiconversation_chatfrom_bg_focused);
                }
            } catch (Exception e) {
                holder.content.setBackgroundResource(R.drawable.xiconversation_chatfrom_bg_focused);
            }
        } else {
            holder.content.setBackgroundResource(R.drawable.xiconversation_chatfrom_bg_focused);
        }
        if (mXiaoiceHeadIconClickListener != null) {
            holder.headicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mXiaoiceHeadIconClickListener.OnHeadIconClick(view);
                }
            });
        }
        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
                    .getTime());
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(tbub.getTime(), null);
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }
        try{
            if (mActivityConfigBean != null && mActivityConfigBean.getChatPageMessageTextSize() != -1){
                holder.content.setTextSize(TypedValue.COMPLEX_UNIT_SP,mActivityConfigBean.getChatPageMessageTextSize());
            }else{
                holder.content.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimensionPixelOffset(R.dimen.xiconversation_chat_activity_fontsize));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if (mActivityConfigBean != null && mActivityConfigBean.getChatPageLeftXiaoiceMessageTextColor() != -1){
                holder.content.setTextColor(mActivityConfigBean.getChatPageLeftXiaoiceMessageTextColor());
            }else{
                holder.content.setTextColor(context.getResources().getColor(R.color.xiconversation_chat_leftfont_color));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        holder.content.setVisibility(View.VISIBLE);
        holder.content.setText(getClickableHtml(tbub.getUserContent()));
        holder.content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void fromImgUserLayout(final FromUserImageViewHolder holder, final XIChatMessageBean tbub, final int position) {
        int localres = R.drawable.xiconversation_left_user_headicon;
        setHeadiconResource(mActivityConfigBean, holder.headicon, mActivityConfigBean.getChatPageLeftHeadIconId(), mActivityConfigBean.getChatPageLeftHeadIconPath(), localres);
       // holder.image_Msg.setImageResource(R.drawable.xiconversation_loadingprocess_imagedefault);
        if (mHeadIconClickListener != null) {
            holder.headicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHeadIconClickListener.OnHeadIconClick(view);
                }
            });
        }
        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
                    .getTime());
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(tbub.getTime(), null);
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }

        final String imageSrc = tbub.getImageLocal() == null ? "" : tbub
                .getImageLocal();
        final String imageUrlSrc = tbub.getImageUrl() == null ? "" : tbub
                .getImageUrl();
        File file = new File(imageSrc);
        final boolean hasLocal = !imageSrc.equals("")
                && XIFileSaveUtil.isFileExists(file);
        int res;
        res = getResId(mActivityConfigBean, mActivityConfigBean.getChatPageLeftBubbleImgId(), R.drawable.xiconversation_chatfrom_bg_focused);
        if (hasLocal){
            try {
                if (tbub.getImgHeight() > 0) {
                    holder.image_Msg.setMinimumHeight(tbub.getImgHeight());
                    holder.image_Msg.setMaxHeight(XIFileSaveUtil.getBitmapHeight(context));
                } else {
                    holder.image_Msg.setMaxHeight(XIFileSaveUtil.getBitmapHeight(context));
                }
            } catch (Exception e) {
                Glide.with(context).load(R.drawable.xiconversation_loadpicture_error) .placeholder(R.drawable.xiconversation_loadpicture_error).transform(new XICornerShapeTransformation(context)).dontAnimate().error(R.drawable.xiconversation_loadpicture_error).into(holder.image_Msg);
            }
            if (tbub.isGif()){
                GifDrawable gifDrawable = null;
                try {
                    gifDrawable = new GifDrawable(imageSrc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (gifDrawable != null){
                    holder.image_Msg.setImageDrawable(gifDrawable);
                    try {
                        if (isGif && !gifDrawable.isRunning()){
                            gifDrawable.start();
                        }else if(!isGif){
                            gifDrawable.stop();
                        }else{
                            gifDrawable.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Glide.with(context).load(imageSrc).placeholder(R.drawable.xiconversation_loadpicture_error).transform(new XICornerShapeTransformation(context)).dontAnimate().error(R.drawable.xiconversation_loadpicture_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.image_Msg);
                }
            }else{
                Glide.with(context).load(imageSrc).placeholder(R.drawable.xiconversation_loadpicture_error).transform(new XICornerShapeTransformation(context)).dontAnimate().error(R.drawable.xiconversation_loadpicture_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.image_Msg);
            }
        }else{
            holder.image_Msg.setMinimumHeight(XIFileSaveUtil.getBitmapHeight(context)/3);
          /*  ViewGroup.LayoutParams layoutParams = holder.image_Msg.getLayoutParams();
            layoutParams.height = XIFileSaveUtil.getBitmapHeight(context);
            holder.image_Msg.setLayoutParams(layoutParams);*/

            if (!TextUtils.isEmpty(imageUrlSrc)){
                Glide.with(context).load(imageUrlSrc).placeholder(R.drawable.xiconversation_loadpicture_error).transform(new XICornerShapeTransformation(context)).dontAnimate().error(R.drawable.xiconversation_loadpicture_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.image_Msg);
            }else{
                Glide.with(context).load(R.drawable.xiconversation_loadpicture_error).placeholder(R.drawable.xiconversation_loadpicture_error).transform(new XICornerShapeTransformation(context)).dontAnimate().error(R.drawable.xiconversation_loadpicture_error).into(holder.image_Msg);
            }
        }

        if (onItemLongClickListener != null && holder.image_Msg != null){
            holder.image_Msg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClickListener(holder.image_Msg,tbub,position);
                    return true;
                }
            });
        }
        holder.image_Msg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pausePlayVoice();
                Intent intent = new Intent(context, XIImageViewActivity.class);
                intent.putStringArrayListExtra("images", imageList);
                intent.putExtra("clickedIndex", imagePosition.get(position));
                context.startActivity(intent);
            }
        });
    }

    private void fromLoadingUserLayout(final FromLoadingViewHolder holder, final XIChatMessageBean tbub, final int position) {
        int localres = R.drawable.xiconversation_left_user_headicon;
        setHeadiconResource(mActivityConfigBean, holder.headicon, mActivityConfigBean.getChatPageLeftHeadIconId(), mActivityConfigBean.getChatPageLeftHeadIconPath(), localres);
        if (mHeadIconClickListener != null) {
            holder.headicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHeadIconClickListener.OnHeadIconClick(view);
                }
            });
        }
        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
                    .getTime());
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(tbub.getTime(), null);
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }
        try {
            an = AnimationUtils.loadAnimation(context,
                    R.anim.xiconversation_update_loading_progressbar_anim);
            LinearInterpolator lin = new LinearInterpolator();
            an.setInterpolator(lin);
            an.setRepeatCount(-1);
            holder.loadingimg
                    .setBackgroundResource(R.drawable.xiconversation_xsearch_loading);
            holder.loadingimg.startAnimation(an);
            an.startNow();
           // holder.loadingimg.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fromMultipleUserLayout(final FromUserMultipleViewHolder holder, final XIChatMessageBean tbub, final int position) {
        int localres = R.drawable.xiconversation_left_user_headicon;
        setHeadiconResource(mActivityConfigBean, holder.headicon, mActivityConfigBean.getChatPageLeftHeadIconId(), mActivityConfigBean.getChatPageLeftHeadIconPath(), localres);
        int res;
        res = getResId(mActivityConfigBean, mActivityConfigBean.getChatPageLeftBubbleImgId(), R.drawable.xiconversation_chatfrom_bg_focused);
        holder.showmorenews.setBackgroundResource(res);
        if (mHeadIconClickListener != null) {
            holder.headicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHeadIconClickListener.OnHeadIconClick(view);
                }
            });
        }
        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
                    .getTime());
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(tbub.getTime(), null);
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }
        String cardString = tbub.getJsonString();
        ArrayList<XICardMessageBean> cardlist = new ArrayList<>();
        if (!TextUtils.isEmpty(cardString)) {
            cardlist = XIChatResponseParse.praseCardJson(cardString);
        }
        if (cardlist.size() < 3) {
            holder.showmorenews.setVisibility(View.GONE);
        } else {
            holder.showmorenews.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        XICardRecyclerAdapter xiChatRecyclerAdapter=null;
        if (onItemLongClickListener != null){
            xiChatRecyclerAdapter = new XICardRecyclerAdapter(context, cardlist,tbub.getId(),onItemLongClickListener,mXIChatMessageDisplayConfigListener,mXIChatCardClickListener);
        }else{
            xiChatRecyclerAdapter = new XICardRecyclerAdapter(context, cardlist,tbub.getId(),onItemLongClickListener,mXIChatMessageDisplayConfigListener,mXIChatCardClickListener);
        }
      if (xiChatRecyclerAdapter != null)
        holder.recyclerView.setAdapter(xiChatRecyclerAdapter);
    }

    private void fromVoiceUserLayout(final FromUserVoiceViewHolder holder, final XIChatMessageBean tbub, final int position) {
        int localres = R.drawable.xiconversation_left_user_headicon;
        setHeadiconResource(mActivityConfigBean, holder.headicon, mActivityConfigBean.getChatPageLeftHeadIconId(), mActivityConfigBean.getChatPageLeftHeadIconPath(), localres);
        if (mHeadIconClickListener != null) {
            holder.headicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHeadIconClickListener.OnHeadIconClick(view);
                }
            });
        }

        if (onItemLongClickListener != null && holder.voice_group != null){
            holder.voice_group.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClickListener(holder.voice_group,tbub,position);
                    return true;
                }
            });
        }
        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1).getTime());
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(tbub.getTime(), null);
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }
        holder.voice_group.setVisibility(View.VISIBLE);
        if (holder.receiver_voice_unread != null){
            setVoiceUnreadImage(holder.receiver_voice_unread);
        }
        if (holder.receiver_voice_unread != null && tbub.getAlreadyReady()) {
            holder.receiver_voice_unread.setVisibility(View.GONE);
        } else {
            holder.receiver_voice_unread.setVisibility(View.VISIBLE);
        }

        AnimationDrawable drawable;
        holder.voice_anim.setId(position);
        if (position == voicePlayPosition && XIMediaManager.isPlaying()) {
            holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_receiver_voiceicon);
            holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_voice_play_receiver);
            drawable = (AnimationDrawable) holder.voice_anim.getBackground();
            drawable.start();
        } else {
            holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_receiver_voiceicon);
        }
        holder.voice_group.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (holder.receiver_voice_unread != null)
                    holder.receiver_voice_unread.setVisibility(View.GONE);
                if (!userList.get(position - 1).getAlreadyReady()) {
                    userList.get(position - 1).setAlreadyReady(true);
                    XIChatMessageBean xiChatMessageBean = new XIChatDbManager().getChatMessageBeanDao(context).queryBuilder().where(XIChatMessageBeanDao.Properties.Id.eq(tbub.getId())).unique();
                    xiChatMessageBean.setAlreadyReady(true);
                    new XIChatDbManager().update(context, xiChatMessageBean);
                }

                holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_receiver_voiceicon);
                if (voicePlayPosition == position && XIMediaManager.isPlaying()) {
                    pausePlayVoice();
                } else if (position == voicePlayPosition && XIMediaManager.isPause()) {
                    XIMediaManager.play(context);
                    AnimationDrawable drawable;
                    holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_voice_play_receiver);
                    drawable = (AnimationDrawable) holder.voice_anim.getBackground();
                    drawable.start();
                } else {
                    pausePlayVoice();
                    voicePlayPosition = holder.voice_anim.getId();
                    AnimationDrawable drawable;
                    holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_voice_play_receiver);
                    drawable = (AnimationDrawable) holder.voice_anim.getBackground();
                    drawable.start();
                    String voicePath = tbub.getUserVoicePath() == null ? "" : tbub.getUserVoicePath();
                    File file = new File(voicePath);
                    if (!(!voicePath.equals("") && XIFileSaveUtil.isFileExists(file))) {
                        voicePath = tbub.getUserVoiceUrl() == null ? "" : tbub.getUserVoiceUrl();
                    }

                    if (!TextUtils.isEmpty(voicePath) ){
                        XIMediaManager.playSound(context,voicePath,
                                new MediaPlayer.OnCompletionListener() {

                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        voicePlayPosition = -1;
                                        XIMediaManager.releaseAudioFocus(context);
                                        holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_receiver_voiceicon);
                                    }
                                });
                    }
                }
            }

        });
        float voiceTime = tbub.getUserVoiceTime();
        BigDecimal b = new BigDecimal(voiceTime);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        int voicefinalTime = (int) Math.ceil(f1);
        holder.voice_time.setText(voicefinalTime + "\"");
        ViewGroup.LayoutParams lParams = holder.voice_image
                .getLayoutParams();
        lParams.width = (int) (mMinItemWith + mMaxItemWith / 60f
                * tbub.getUserVoiceTime());
        holder.voice_image.setLayoutParams(lParams);
    }

    private void toMsgUserLayout(final ToUserMsgViewHolder holder, final XIChatMessageBean tbub, final int position) {
        int localres = R.drawable.xiconversation_right_user_headicon;
        setHeadiconResource(mActivityConfigBean, holder.headicon, mActivityConfigBean.getChatPageRightHeadIconId(), mActivityConfigBean.getChatPageRightHeadIconPath(), localres);

        if (mHeadIconClickListener != null) {
            holder.headicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHeadIconClickListener.OnHeadIconClick(view);
                }
            });
        }

        if (onItemLongClickListener != null && holder.content != null){
            holder.content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClickListener(holder.content,tbub,position);
                    return true;
                }
            });
        }

        switch (tbub.getSendState()) {
            case XIChatConst.XICONVERSATION_SENDING:
                an = AnimationUtils.loadAnimation(context,
                        R.anim.xiconversation_update_loading_progressbar_anim);
                LinearInterpolator lin = new LinearInterpolator();
                an.setInterpolator(lin);
                an.setRepeatCount(-1);
                holder.sendFailImg
                        .setBackgroundResource(R.drawable.xiconversation_xsearch_loading);
                holder.sendFailImg.startAnimation(an);
                an.startNow();
                holder.sendFailImg.setVisibility(View.VISIBLE);
                tbub.setSendState(XIChatConst.XICONVERSATION_COMPLETED);
                break;

            case XIChatConst.XICONVERSATION_COMPLETED:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg.setVisibility(View.GONE);
                break;

            case XIChatConst.XICONVERSATION_SENDERROR:
                holder.sendFailImg.clearAnimation();
                setSendFileImage(holder.sendFailImg);
                holder.sendFailImg.setVisibility(View.VISIBLE);
                holder.sendFailImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (XIConNetWorkUtils.isNetworkConnected(context)) {
                            if (sendErrorListener != null) {
                                sendErrorListener.onClick(tbub.getId());
                            }
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.xiconversation_check_no_network_notresend), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                break;
        }
        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
                    .getTime());
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(tbub.getTime(), null);
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }

        try{
            //sp
            if (mActivityConfigBean != null && mActivityConfigBean.getChatPageMessageTextSize() != -1){
                holder.content.setTextSize(TypedValue.COMPLEX_UNIT_SP,mActivityConfigBean.getChatPageMessageTextSize());
            }else{
                holder.content.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimensionPixelOffset(R.dimen.xiconversation_chat_activity_fontsize));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            if (mActivityConfigBean != null && mActivityConfigBean.getChatPageRightUserMessageTextColor() != -1){
                holder.content.setTextColor(mActivityConfigBean.getChatPageRightUserMessageTextColor());
            }else{
                holder.content.setTextColor(context.getResources().getColor(R.color.xiconversation_white));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.content.setVisibility(View.VISIBLE);
        holder.content.setText(tbub.getUserContent());
    }

    private void setSendFileImage(ImageView sendFailImg) {
        try{
            if (mActivityConfigBean != null && mActivityConfigBean.getChatPageMessageSendErrorImageId() != -1
                    && context.getResources().getDrawable(mActivityConfigBean.getChatPageMessageSendErrorImageId()) != null){
                sendFailImg
                        .setBackgroundResource(mActivityConfigBean.getChatPageMessageSendErrorImageId());
            }else{
                sendFailImg
                        .setBackgroundResource(R.drawable.xiconversation_msg_state_fail_resend_pressed);

            }
        }catch(Exception e){
            sendFailImg
                    .setBackgroundResource(R.drawable.xiconversation_msg_state_fail_resend_pressed);
            e.printStackTrace();
        }
    }

    private void setVoiceUnreadImage(View unreadimg) {
        try{
            if (mActivityConfigBean != null && mActivityConfigBean.getChatPageVoiceNotReadImageId() != -1
                    && context.getResources().getDrawable(mActivityConfigBean.getChatPageVoiceNotReadImageId()) != null){
                unreadimg
                        .setBackgroundResource(mActivityConfigBean.getChatPageVoiceNotReadImageId());
            }else{
                unreadimg
                        .setBackgroundResource(R.drawable.xiconversation_msg_chat_voice_unread);

            }
        }catch(Exception e){
            unreadimg
                    .setBackgroundResource(R.drawable.xiconversation_msg_chat_voice_unread);
            e.printStackTrace();
        }
    }

    private void toImgUserLayout(final ToUserImgViewHolder holder, final XIChatMessageBean tbub, final int position) {
        int localres = R.drawable.xiconversation_right_user_headicon;
        setHeadiconResource(mActivityConfigBean, holder.headicon, mActivityConfigBean.getChatPageRightHeadIconId(), mActivityConfigBean.getChatPageRightHeadIconPath(), localres);

        if (mXiaoiceHeadIconClickListener != null) {
            holder.headicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mXiaoiceHeadIconClickListener.OnHeadIconClick(view);
                }
            });
        }

        if (onItemLongClickListener != null && holder.image_Msg != null){
            holder.image_Msg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClickListener(holder.image_Msg,tbub,position);
                    return true;
                }
            });
        }

        switch (tbub.getSendState()) {
            case XIChatConst.XICONVERSATION_SENDING:
                an = AnimationUtils.loadAnimation(context,
                        R.anim.xiconversation_update_loading_progressbar_anim);
                LinearInterpolator lin = new LinearInterpolator();
                an.setInterpolator(lin);
                an.setRepeatCount(-1);
                holder.sendFailImg
                        .setBackgroundResource(R.drawable.xiconversation_xsearch_loading);
                holder.sendFailImg.startAnimation(an);
                an.startNow();
                holder.sendFailImg.setVisibility(View.VISIBLE);
                tbub.setSendState(XIChatConst.XICONVERSATION_COMPLETED);
                break;

            case XIChatConst.XICONVERSATION_COMPLETED:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg.setVisibility(View.GONE);
                break;

            case XIChatConst.XICONVERSATION_SENDERROR:
                holder.sendFailImg.clearAnimation();
                setSendFileImage(holder.sendFailImg);
                holder.sendFailImg.setVisibility(View.VISIBLE);
                holder.sendFailImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (XIConNetWorkUtils.isNetworkConnected(context)) {
                            if (sendErrorListener != null) {
                                sendErrorListener.onClick(tbub.getId());
                            }
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.xiconversation_check_no_network_notresend), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                break;
        }

        /* time */
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
                    .getTime());
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(tbub.getTime(), null);
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }

        holder.image_group.setVisibility(View.VISIBLE);
        final String imageSrc = tbub.getImageLocal() == null ? "" : tbub
                .getImageLocal();
        final String imageOriginal = tbub.getImageOriginal() == null ? "" : tbub.getImageOriginal();

        File file = new File(imageSrc);
        final boolean hasLocal = ! TextUtils.isEmpty(imageSrc) && XIFileSaveUtil.isFileExists(file);

        int res;
        res = getResId(mActivityConfigBean, mActivityConfigBean.getChatPageRightBubbleImgId(), R.drawable.xiconversation_chatto_bg_focused);
        try {
            if (tbub.getImgHeight() > 0) {
                holder.image_Msg.setMinimumHeight(tbub.getImgHeight());
                holder.image_Msg.setMaxHeight(XIFileSaveUtil.getBitmapHeight(context));
            } else {
                holder.image_Msg.setMinimumHeight(XIFileSaveUtil.getBitmapHeight(context));
            }
        } catch (Exception e) {

        }

        if (hasLocal){
            Glide.with(context).load(imageSrc).placeholder(R.drawable.xiconversation_loadpicture_error).transform(new XICornerShapeTransformation(context)).dontAnimate().error(R.drawable.xiconversation_loadpicture_error).into(holder.image_Msg);
        }else{
            File orifile = new File(imageOriginal);
            boolean haveoriginal = ! TextUtils.isEmpty(imageOriginal)
                    && XIFileSaveUtil.isFileExists(orifile);
            if (haveoriginal){
                Glide.with(context).load(imageOriginal).placeholder(R.drawable.xiconversation_loadpicture_error).transform(new XICornerShapeTransformation(context)).dontAnimate().error(R.drawable.xiconversation_loadpicture_error).into(holder.image_Msg);
            }else{
                Glide.with(context).load(R.drawable.xiconversation_loadpicture_error).placeholder(R.drawable.xiconversation_loadpicture_error).transform(new XICornerShapeTransformation(context)).dontAnimate().error(R.drawable.xiconversation_loadpicture_error).into(holder.image_Msg);
            }
        }

        holder.image_Msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pausePlayVoice();
                Intent intent = new Intent(context, XIImageViewActivity.class);
                intent.putStringArrayListExtra("images", imageList);
                intent.putExtra("clickedIndex", imagePosition.get(position));
                context.startActivity(intent);
            }
        });
    }

    private void toVoiceUserLayout(final ToUserVoiceViewHolder holder, final XIChatMessageBean tbub, final int position) {

        int localres = R.drawable.xiconversation_right_user_headicon;
        setHeadiconResource(mActivityConfigBean, holder.headicon, mActivityConfigBean.getChatPageRightHeadIconId(), mActivityConfigBean.getChatPageRightHeadIconPath(), localres);

        if (mXiaoiceHeadIconClickListener != null) {
            holder.headicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mXiaoiceHeadIconClickListener.OnHeadIconClick(view);
                }
            });
        }

        if (onItemLongClickListener != null && holder.voice_group != null){
            holder.voice_group.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClickListener(holder.voice_group,tbub,position);
                    return true;
                }
            });
        }
        switch (tbub.getSendState()) {
            case XIChatConst.XICONVERSATION_SENDING:
                an = AnimationUtils.loadAnimation(context,
                        R.anim.xiconversation_update_loading_progressbar_anim);
                LinearInterpolator lin = new LinearInterpolator();
                an.setInterpolator(lin);
                an.setRepeatCount(-1);
                holder.sendFailImg
                        .setBackgroundResource(R.drawable.xiconversation_xsearch_loading);
                holder.sendFailImg.startAnimation(an);
                an.startNow();
                holder.sendFailImg.setVisibility(View.VISIBLE);
                break;

            case XIChatConst.XICONVERSATION_COMPLETED:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg.setVisibility(View.GONE);
                break;

            case XIChatConst.XICONVERSATION_SENDERROR:
                holder.sendFailImg.clearAnimation();
                setSendFileImage(holder.sendFailImg);
                holder.sendFailImg.setVisibility(View.VISIBLE);
                holder.sendFailImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (XIConNetWorkUtils.isNetworkConnected(context)) {
                            if (sendErrorListener != null) {
                                sendErrorListener.onClick(tbub.getId());
                            }
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.xiconversation_check_no_network_notresend), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                break;
        }

        // time
        if (position != 0) {
            String showTime = getTime(tbub.getTime(), userList.get(position - 1)
                    .getTime());
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(tbub.getTime(), null);
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }
        holder.voice_group.setVisibility(View.VISIBLE);
        if (holder.receiver_voice_unread != null){
            setVoiceUnreadImage(holder.receiver_voice_unread);
        }
        if (holder.receiver_voice_unread != null)
            holder.receiver_voice_unread.setVisibility(View.GONE);

        AnimationDrawable drawable;
        holder.voice_anim.setId(position);
        if (position == voicePlayPosition && XIMediaManager.isPlaying()) {
            holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_send_voiceicon);
            holder.voice_anim
                    .setBackgroundResource(R.drawable.xiconversation_voice_play_send);
            drawable = (AnimationDrawable) holder.voice_anim
                    .getBackground();
            drawable.start();
        } else {
            holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_send_voiceicon);
        }
        String voicePath =  tbub.getUserVoicePath();
        if (TextUtils.isEmpty(voicePath) || voicePath.equals("")){
            holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_send_voiceicon);
            Toast.makeText(context, context.getResources().getString(R.string.xiconversation_error_voice_already_delete), Toast.LENGTH_SHORT).show();
        }else{
            holder.voice_group.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (holder.receiver_voice_unread != null)
                        holder.receiver_voice_unread.setVisibility(View.GONE);
                    holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_send_voiceicon);
                    if (position == voicePlayPosition && XIMediaManager.isPlaying()) {
                        pausePlayVoice();
                    } else if (position == voicePlayPosition && XIMediaManager.isPause()) {
                        AnimationDrawable drawable;
                        holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_voice_play_send);
                        drawable = (AnimationDrawable) holder.voice_anim.getBackground();
                        drawable.start();
                        XIMediaManager.play(context);
                    } else {
                        pausePlayVoice();
                        String voicePath =  tbub.getUserVoicePath();
                        if (TextUtils.isEmpty(voicePath) || voicePath.equals("")){
                            Toast.makeText(context, context.getResources().getString(R.string.xiconversation_error_voice_already_delete), Toast.LENGTH_SHORT).show();
                        }else{
                            voicePlayPosition = holder.voice_anim.getId();
                            AnimationDrawable drawable;
                            holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_voice_play_send);
                            drawable = (AnimationDrawable) holder.voice_anim.getBackground();
                            drawable.start();

                            if (!TextUtils.isEmpty(voicePath)){
                                XIMediaManager.playSound(context,voicePath,
                                        new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                voicePlayPosition = -1;
                                                XIMediaManager.releaseAudioFocus(context);
                                                holder.voice_anim.setBackgroundResource(R.drawable.xiconversation_send_voiceicon);
                                            }
                                        });
                            }
                        }
                    }
                }

            });

        }

        float voiceTime = tbub.getUserVoiceTime();
        BigDecimal b = new BigDecimal(voiceTime);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        int voicefinalTime = (int) Math.ceil(f1);
        holder.voice_time.setText(voicefinalTime + "\"");
        ViewGroup.LayoutParams lParams = holder.voice_image
                .getLayoutParams();
        lParams.width = (int) (mMinItemWith + mMaxItemWith / 60f
                * tbub.getUserVoiceTime());
        holder.voice_image.setLayoutParams(lParams);
    }

    @SuppressLint("SimpleDateFormat")
    public String getTime(String time, String before) {
        String show_time = null;
        if (before != null) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date now = df.parse(time);
                java.util.Date date = df.parse(before);
                long l = now.getTime() - date.getTime();
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                if (min >= 5) {
                    show_time = time.substring(11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            show_time = time.substring(11);
        }
        String getDay = getDay(time);
        if (show_time != null && getDay != null)
            show_time = getDay + " " + show_time;
        return show_time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public String getDay(String time) {
        String showDay = null;
        String nowTime = returnTime();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date now = df.parse(nowTime);
            java.util.Date date = df.parse(time);
            boolean istody = DateUtils.isToday(date.getTime());
            long l = now.getTime() - date.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            if (day >= 365) {
                showDay = time.substring(0, 10);
            } else if ((day >= 1 && day < 365) || !istody) {
                showDay = time.substring(5, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showDay;
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            ByteArrayOutputStream out;
            FileInputStream fis = new FileInputStream(url);
            BufferedInputStream bis = new BufferedInputStream(fis);
            out = new ByteArrayOutputStream();
            @SuppressWarnings("unused")
            int hasRead = 0;
            byte[] buffer = new byte[1024 * 2];
            while ((hasRead = bis.read(buffer)) > 0) {
                out.write(buffer);
                out.flush();
            }
            out.close();
            fis.close();
            bis.close();
            byte[] data = out.toByteArray();
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 3;
            return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void pausePlayVoice() {
        try {
            if (voicePlayPosition != -1) {
                View voicePlay = (View) ((Activity) context).findViewById(voicePlayPosition);
                if (voicePlay != null) {
                    if (getItemViewType(voicePlayPosition) == XIChatConst.FROM_USER_VOICE) {
                        voicePlay.setBackgroundResource(R.drawable.xiconversation_receiver_voiceicon);
                    } else {
                        voicePlay.setBackgroundResource(R.drawable.xiconversation_send_voiceicon);
                    }
                }
                XIMediaManager.pause(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getResId(XIConversationChatPageConfig mActivityConfigBean, int resourceId, int defaultResourceId) {
        int res = defaultResourceId;
        if (mActivityConfigBean != null) {
            try {
                if (resourceId != -1 && context.getResources().getDrawable(resourceId) != null) {
                    res = resourceId;
                }
            } catch (Exception e) {
            }
        }
        return res;
    }

    private void setHeadiconResource(XIConversationChatPageConfig mActivityConfigBean, ImageView imageView, int iconid, String iconurl, int localid) {
        if (imageView != null) {
            if (mActivityConfigBean != null) {
                try {
                    if (iconid != -1 && context.getResources().getDrawable(iconid) != null) {
                        imageView.setImageResource(iconid);
                    } else if (!TextUtils.isEmpty(iconurl)) {
                        Glide.with(context).load(iconurl).override(50,50).fitCenter().dontAnimate().error(localid).transform(new XIGlideCircleTransform(context)).into(imageView);
                    } else {
                        imageView.setImageResource(localid);
                    }
                } catch (Exception e) {
                    imageView.setImageResource(localid);
                }
            } else {
                imageView.setImageResource(localid);
            }
        }
    }

    private void setLinkClickable(final SpannableStringBuilder clickableHtmlBuilder,
                                  final URLSpan urlSpan) {
        int start = clickableHtmlBuilder.getSpanStart(urlSpan);
        int end = clickableHtmlBuilder.getSpanEnd(urlSpan);
        int flags = clickableHtmlBuilder.getSpanFlags(urlSpan);
        ClickableSpan clickableSpan = new ClickableSpan() {

            public void onClick(View view) {
                String url = urlSpan.getURL();
                //Intent intent = new Intent(context,com.microsoft.xiaoicesdk.landingpage.activity.XILandingPageMainPage.class);
                Intent intent = new Intent(context,XINewsDetailViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(XIChatConst.XILANDINGPAGEMAINPAGECURRURL,url);
                bundle.putString(XIChatConst.XILANDINGPAGEMAINPAGECURRTEXT,"");
                intent.putExtras(bundle);
                context.startActivity(intent);
                XIConLogUtil.d("technical ability url:"+url);
            }
            public void updateDrawState(TextPaint ds) {
                ds.setColor(context.getResources().getColor(R.color.xiconversation_chatpage_url_text_color));
                ds.setUnderlineText(false);
            }

        };
        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags);
    }

    private CharSequence getClickableHtml(String html) {
        boolean havaurl = false;
        Spanned spannedHtml = null;
        if (Build.VERSION.SDK_INT >= 24) {
            spannedHtml = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
        } else {
            spannedHtml = Html.fromHtml(html);
        }
        SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(spannedHtml);
        URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
        if (urls.length == 0){
            return html.replace("\\n", "\n").replace("\\r", "\r");
        }
        for (final URLSpan span : urls) {
            setLinkClickable(clickableHtmlBuilder, span);
        }
        return clickableHtmlBuilder;
    }

}
