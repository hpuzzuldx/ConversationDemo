package com.ldx.conversationother.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldx.conversationother.listener.XIItemClickListener;
import com.ldx.conversationother.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-doli1 on 2018/3/29.
 */

public class KeywordsAdapter extends RecyclerView.Adapter<KeywordsAdapter.RecyclerHolder> {
    private Context mContext;
    private List<String> dataList = new ArrayList<>();
    private XIItemClickListener xiItemClickListener;

    public void setOnItemClickListener(XIItemClickListener onItemClickListener) {
        xiItemClickListener = onItemClickListener;
    }

    public KeywordsAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<String> dataList) {
        if (null != dataList) {
            this.dataList.clear();
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.xiconversation_keywords_itemview, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerHolder holder, final int position) {
        holder.textView.setText(dataList.get(position));
        if (xiItemClickListener != null) {
            holder.textViewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    xiItemClickListener.onItemClick(holder.textViewContainer, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout textViewContainer;

        private RecyclerHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.xiconversation_keywords_toptv);
            textViewContainer = (LinearLayout) itemView.findViewById(R.id.xiconversation_topbar_ll_keywords);
        }
    }
}