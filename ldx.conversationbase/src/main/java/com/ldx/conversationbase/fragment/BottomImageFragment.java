package com.ldx.conversationbase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;
import com.ldx.conversationbase.R;

/**
 * Created by v-mizhe on 2017/9/11.
 */

public class BottomImageFragment extends BottomSheetFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xiconversation_fragment_bottom_image, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.xiconversation_save_photo_album_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickById != null) {
                    itemClickById.onItemClickById(view.getId());
                }
            }
        });
        view.findViewById(R.id.xiconversation_share_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickById != null) {
                    itemClickById.onItemClickById(view.getId());
                }
            }
        });
    }

    public interface ItemClickById {
        void onItemClickById(int id);
    }

    private ItemClickById itemClickById;

    public void setItemClickById(ItemClickById itemClickById) {
        this.itemClickById = itemClickById;
    }
}
