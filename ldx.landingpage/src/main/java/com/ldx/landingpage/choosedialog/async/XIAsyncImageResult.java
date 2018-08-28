package com.ldx.landingpage.choosedialog.async;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.ldx.landingpage.R;
import com.ldx.landingpage.choosedialog.bean.XIPickResult;
import com.ldx.landingpage.choosedialog.bundle.XIPickSetup;
import com.ldx.landingpage.choosedialog.resolver.XIIntentResolver;
import com.ldx.landingpage.utils.XIFileUtil;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.lang.ref.WeakReference;

public class XIAsyncImageResult extends AsyncTask<Intent, Void, XIPickResult> {

    private WeakReference<XIIntentResolver> weakIntentResolver;
    private WeakReference<XIPickSetup> weakSetup;
    private OnFinish onFinish;

    public XIAsyncImageResult(Activity activity, XIPickSetup setup) {
        this.weakIntentResolver = new WeakReference<XIIntentResolver>(new XIIntentResolver(activity, setup));
        this.weakSetup = new WeakReference<XIPickSetup>(setup);
    }

    public XIAsyncImageResult setOnFinish(OnFinish onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    @Override
    protected XIPickResult doInBackground(Intent... intents) {

        //Create  instance
        XIPickResult result = new XIPickResult();
        XIIntentResolver resolver = weakIntentResolver.get();

        if (resolver == null) {
            result.setError(new Error(resolver.getActivity().getString(R.string.xilandingpage_activity_destroyed)));
            return result;
        }
        Intent data = intents[0];
        boolean fromCamera = false;
            try {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    String photoPath = bundle.getString("imagePath");

                    if (!TextUtils.isEmpty(photoPath)) {
                        fromCamera = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setError(e);
                return result;
            }

            try {
                if (fromCamera) {
                    String path = data.getDataString();
                    if (!TextUtils.isEmpty(path)) {
                        File file = new File(path);
                        if (resolver != null && resolver.getActivity() != null){
                            XIFileUtil.addToGallery(resolver.getActivity(),file);
                        }
                        data.setData(Uri.fromFile(file));
                        result.setData(data);
                        result.setPath(path);
                    }

                } else {
                    String path = Matisse.obtainPathResult(data).get(0);
                    if (!TextUtils.isEmpty(path)) {
                        File file = new File(path);
                        if (resolver != null && resolver.getActivity() != null){
                            XIFileUtil.addToGallery(resolver.getActivity(),file);
                        }
                        data.setData(Uri.fromFile(file));
                        result.setData(data);
                        result.setPath(path);
                    }
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                if (data != null) {
                    result.setData(data);
                    return result;
                }
                result.setError(e);
                return result;
            }
    }

    @Override
    protected void onPostExecute(XIPickResult r) {
        if (onFinish != null)
            onFinish.onFinish(r);
    }

    public interface OnFinish {
        void onFinish(XIPickResult pickResult);
    }

}
