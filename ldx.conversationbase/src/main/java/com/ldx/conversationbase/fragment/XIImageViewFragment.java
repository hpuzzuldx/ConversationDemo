package com.ldx.conversationbase.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.chrisbanes.photoview.PhotoView;
import com.ldx.conversationbase.R;

import java.io.File;

public class XIImageViewFragment extends Fragment {
    private String imageUrl;
    private ProgressBar loadBar;
    private PhotoView imageGiv;
    private ImageLongClick imageLongClick;
    private ImageView mLoadtosd;

    public interface ImageLongClick{
        void onImageLongClick();
    }
    public void setImageLongClick(ImageLongClick imageLongClick){
        this.imageLongClick = imageLongClick;
    }

    public View onCreateView(android.view.LayoutInflater inflater,
                             android.view.ViewGroup container,
                             android.os.Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xiconversation_images_view_item, container,
                false);
        init(view);
        if (!TextUtils.isEmpty(imageUrl)){
            loadImage(imageUrl);
        }
        return view;
    }

    private void init(View mView) {
        loadBar = (ProgressBar) mView.findViewById(R.id.xiconversation_imageView_loading_pb);
        imageGiv = (PhotoView) mView
                .findViewById(R.id.xiconversation_imageView_item_giv);
        mLoadtosd = (ImageView) mView.findViewById(R.id.xiconversation_imageview_loadtosd);
        imageGiv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        imageGiv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (imageLongClick != null){
                    imageLongClick.onImageLongClick();
                }
                return false;
            }
        });

        mLoadtosd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void loadImage(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            Glide.with(this).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null) {
                        imageGiv.setImageBitmap(resource);
                        loadBar.setVisibility(View.GONE);
                        imageGiv.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
           // imageGiv.setImageURI(Uri.fromFile(new File(url)));
            Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageGiv);
            loadBar.setVisibility(View.GONE);
            imageGiv.setVisibility(View.VISIBLE);
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
