package com.ldx.landingpage.common.listener;

import android.webkit.WebView;

/**
 * @author lidongxiu
 */

public interface XIWebChromeClientListener {
    public void onProgressChanged(WebView view, int newProgress);
    public void onReceivedTitle(WebView view, String title);
}
