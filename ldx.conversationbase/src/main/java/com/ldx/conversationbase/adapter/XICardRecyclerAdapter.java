package com.ldx.conversationbase.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ldx.conversationbase.R;
import com.ldx.conversationbase.common.XIChatConst;
import com.ldx.conversationbase.db.XICardMessageBean;
import com.ldx.conversationbase.listener.XIChatCardClickListener;
import com.ldx.conversationbase.listener.XIChatMessageDisplayConfigListener;
import com.ldx.conversationbase.activity.XINewsDetailViewActivity;
import com.ldx.conversationbase.utils.XIConLogUtil;
import com.ldx.conversationbase.utils.XIDateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-doli1 on 2017/9/4.
 */

public class XICardRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<XICardMessageBean> mCardList = new ArrayList<XICardMessageBean>();
    private XIChatRecyclerAdapter.RecyclerViewOnItemLongClickListener mOnItemLongClickListener;
    public XIChatMessageDisplayConfigListener mXIChatMessageDisplayConfigListener;
    public XIChatCardClickListener mXIChatCardClickListener;
    private long mesgId;

    public XICardRecyclerAdapter(Context context,ArrayList<XICardMessageBean> cardlist,
                                 XIChatMessageDisplayConfigListener tXIChatMessageDisplayConfigListener){
        mContext = context;
        mCardList = cardlist;
        mXIChatMessageDisplayConfigListener =  tXIChatMessageDisplayConfigListener;

    }

    public XICardRecyclerAdapter(Context context,ArrayList<XICardMessageBean> cardlist,long tmesgId,
                                 XIChatRecyclerAdapter.RecyclerViewOnItemLongClickListener onItemLongClickListener,
                                 XIChatMessageDisplayConfigListener tXIChatMessageDisplayConfigListener){
        mContext = context;
        mCardList = cardlist;
        mesgId =tmesgId;
        mOnItemLongClickListener = onItemLongClickListener;
        mXIChatMessageDisplayConfigListener =  tXIChatMessageDisplayConfigListener;

    }

    public XICardRecyclerAdapter(Context context,ArrayList<XICardMessageBean> cardlist,long tmesgId,
                                 XIChatRecyclerAdapter.RecyclerViewOnItemLongClickListener onItemLongClickListener,
                                 XIChatMessageDisplayConfigListener tXIChatMessageDisplayConfigListener,
                                 XIChatCardClickListener tXIChatCardClickListener){
        mContext = context;
        mCardList = cardlist;
        mesgId =tmesgId;
        mOnItemLongClickListener = onItemLongClickListener;
        mXIChatMessageDisplayConfigListener =  tXIChatMessageDisplayConfigListener;
        mXIChatCardClickListener = tXIChatCardClickListener;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xiconversation_multiplefrom_cardview_item, parent, false);
        RecyclerView.ViewHolder holder = new FromUserMultipleViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
          fromMultipleUserLayout((FromUserMultipleViewHolder)holder,mCardList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    class FromUserMultipleViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_Msg;
        private TextView  new_title;
        private TextView  new_time;
        private LinearLayout cardview_container;

        public FromUserMultipleViewHolder(View view) {
            super(view);
            image_Msg = (ImageView) view
                    .findViewById(R.id.xiconversation_frommultiple_card_image_message);
            new_title = (TextView)view
                    .findViewById(R.id.xiconversation_frommultiple_card_text_title);
            new_time = (TextView)view
                    .findViewById(R.id.xiconversation_frommultiple_card_time_title);
            cardview_container = (LinearLayout)view.findViewById(R.id.xiconversation_cardview_container);
        }
    }

    private void fromMultipleUserLayout(final FromUserMultipleViewHolder holder, final XICardMessageBean tbub, final int position) {
        final String imageUrlSrc = tbub.getMesgCoverurl() == null ? "" : tbub
                .getMesgCoverurl();
        if (imageUrlSrc != null){
            Glide.with(mContext).load(imageUrlSrc).placeholder(R.drawable.xiconversation_loadpicture_error).fitCenter().dontAnimate().error(R.drawable.xiconversation_loadpicture_error).diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.image_Msg);
        }
        holder.new_title.setText(tbub.getMesgTitle());
       // holder.new_time.setText(XIDateUtils.getNormalYMDTime(tbub.getMesgTime()*1000L));getSpecitalUtcString
        if (mXIChatMessageDisplayConfigListener != null){
            holder.new_time.setText(mXIChatMessageDisplayConfigListener.configCardNewsTimeFormat(tbub.getMesgTime()*1000L));
        }else{
            holder.new_time.setText(XIDateUtils.getUtcString(tbub.getMesgTime()*1000L));
        }

        holder.cardview_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null){
                    tbub.setMesgId(mesgId);
                    mOnItemLongClickListener.onMulItemLongClickListener(holder.cardview_container,tbub,position);
                }
                return true;
            }
        });
        holder.cardview_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mXIChatCardClickListener != null){
                    String feedid = "";
                    String title = "";
                    String mesgurl = "";

                    if (tbub != null){
                        if (tbub.getFeedId() != null){
                            feedid = tbub.getFeedId();
                        }
                        if (tbub.getMesgTitle() != null){
                            title = tbub.getMesgTitle();
                        }
                        if (tbub.getMesgUrl() != null){
                            mesgurl = tbub.getMesgUrl();
                        }
                    }

                    try {
                        mXIChatCardClickListener.onCardClick(feedid,title,mesgurl,tbub.getReserved1());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Intent intent = new Intent(mContext,XINewsDetailViewActivity.class);
                    XIConLogUtil.d("====="+tbub.getMesgUrl());
                    Bundle bundle = new Bundle();
                    bundle.putString(XIChatConst.XICURRENTURL,tbub.getMesgUrl());
                    bundle.putString(XIChatConst.XICURRENTTEXT,tbub.getMesgTitle());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            }
        });
    }
}
