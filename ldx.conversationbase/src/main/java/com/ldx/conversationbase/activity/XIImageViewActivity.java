package com.ldx.conversationbase.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.github.chrisbanes.photoview.PhotoView;
import com.ldx.conversationbase.R;
import com.ldx.conversationbase.fragment.BottomImageFragment;
import com.ldx.conversationbase.utils.XIPictureUtil;
import com.ldx.conversationbase.widget.XIPhotoViewPage;

import java.io.File;
import java.util.ArrayList;

public class XIImageViewActivity extends FragmentActivity {
    private ArrayList<String> imageList;
    private XIPhotoViewPage imageVp;
    private TextView currentTv;
    private TextView totalTv;
    private int currentPage;
    private BottomSheetLayout bottomSheetLayout;
    private ProgressBar pb_loading;
    private ArrayList<PhotoView> mPageViews;
    private ImagePagerAdapter imagePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey("images")) {
                imageList = bundle.getStringArrayList("images");
            }
            if (bundle.containsKey("clickedIndex")) {
                currentPage = bundle.getInt("clickedIndex");
            }
        }
        setContentView(R.layout.xiconversation_activity_chooseimages_view);
        findView();
        init();
    }

    private void init() {
        totalTv.setText("/" + imageList.size());
        mPageViews = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
//            RelativeLayout ll = (RelativeLayout) View.inflate(this, R.layout.xiconversation_images_view_item, null);
            PhotoView photoView = new PhotoView(this);
            mPageViews.add(photoView);
        }
        imagePagerAdapter = new ImagePagerAdapter();
        imageVp.setAdapter(imagePagerAdapter);
        imageVp.setOffscreenPageLimit(3);
        imageVp.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {
                currentPage = index;
                currentTv.setText((index + 1) + "");
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        imageVp.setCurrentItem(currentPage);
        currentTv.setText((currentPage + 1) + "");
    }

    private void loadImage(final PhotoView photoView, int position) {
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XIImageViewActivity.this.finish();
            }
        });
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showMenuSheet();
                return false;
            }
        });
        String url = imageList.get(position);
        pb_loading.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                Glide.with(XIImageViewActivity.this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (resource != null) {
                            photoView.setImageBitmap(resource);
                            photoView.setVisibility(View.VISIBLE);
                        }
                        pb_loading.setVisibility(View.GONE);
                    }
                });
            } else {
                Glide.with(XIImageViewActivity.this).load(new File(url)).into(photoView);
                photoView.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
            }
        }
    }

    protected void findView() {
        pb_loading = (ProgressBar) findViewById(R.id.xiconversation_imageView_loading_pb);
        imageVp = (XIPhotoViewPage) findViewById(R.id.xiconversation_scale_vp_images);
        currentTv = (TextView) findViewById(R.id.xiconversation_scale_tv_imageview_current);
        totalTv = (TextView) findViewById(R.id.xiconversation_scale_tv_imageview_total);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        bottomSheetLayout.setPeekOnDismiss(true);
    }

    private void showMenuSheet() {
        BottomImageFragment bottomImageFragment = new BottomImageFragment();
        bottomImageFragment.setItemClickById(new BottomImageFragment.ItemClickById() {
            @Override
            public void onItemClickById(int id) {
                if (bottomSheetLayout.isSheetShowing()) {
                    bottomSheetLayout.dismissSheet();
                }
                if (id == R.id.xiconversation_save_photo_album_tv) {
                    // save to album
                    String url = imageList.get(imageVp.getCurrentItem());
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        Glide.with(XIImageViewActivity.this).load(url).downloadOnly(new SimpleTarget<File>() {
                            @Override
                            public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                                // saveToGallery(resource.getAbsolutePath());
                                saveToSDCardAndShowGallery(resource.getAbsolutePath());
                            }
                        });
                    } else {
                        saveToSDCardAndShowGallery(url);
                    }
                } else if (id == R.id.xiconversation_share_tv) {
                    // share
                }
            }
        });
        bottomImageFragment.show(getSupportFragmentManager(), R.id.bottomsheet);
    }

    private void saveToGallery(String path) {
        if (XIPictureUtil.saveImageToGallery(XIImageViewActivity.this, path)) {
            Toast.makeText(XIImageViewActivity.this, getResources().getString(R.string.xiconversation_already_save_photo_album), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(XIImageViewActivity.this, getResources().getString(R.string.xiconversation_error_save_photo_album), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToSDCardAndShowGallery(String origiPath) {
        if (XIPictureUtil.saveImageToSDPath(XIImageViewActivity.this, origiPath, XIPictureUtil.getSaveToSDImgPath())) {
            Toast.makeText(XIImageViewActivity.this, getResources().getString(R.string.xiconversation_already_save_photo_album), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(XIImageViewActivity.this, getResources().getString(R.string.xiconversation_error_save_photo_album), Toast.LENGTH_SHORT).show();
        }
    }

    class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (imageList != null) {
                return imageList.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View imageLayout = View.inflate(XIImageViewActivity.this, R.layout.xiconversation_images_view_item, null);
            final PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.xiconversation_imageView_item_giv);
            final ImageView mDownload = (ImageView) imageLayout.findViewById(R.id.xiconversation_imageview_loadtosd);
            String url = imageList.get(position);
            final String urlcopy = url;
            pb_loading.setVisibility(View.VISIBLE);

            mDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(urlcopy)) {
                        if (urlcopy.startsWith("http://") || urlcopy.startsWith("https://")) {
                            Glide.with(XIImageViewActivity.this).load(urlcopy).downloadOnly(new SimpleTarget<File>() {
                                @Override
                                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                                    // saveToGallery(resource.getAbsolutePath());
                                    saveToSDCardAndShowGallery(resource.getAbsolutePath());
                                }
                            });
                        } else {
                            saveToSDCardAndShowGallery(urlcopy);
                        }
                    }
                }
            });
            if (!TextUtils.isEmpty(url)) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    Glide.with(XIImageViewActivity.this).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            if (resource != null) {
                                photoView.setImageBitmap(resource);
                                photoView.setVisibility(View.VISIBLE);
                            }
                            pb_loading.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Glide.with(XIImageViewActivity.this).load(new File(url)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(photoView);
                    photoView.setVisibility(View.VISIBLE);
                    pb_loading.setVisibility(View.GONE);
                }
            }
            container.addView(imageLayout);
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    XIImageViewActivity.this.finish();
                }
            });
            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showMenuSheet();
                    return false;
                }
            });
            return imageLayout;
        }
    }
}
