package com.ryl.engineer.app;

import android.os.Bundle;
import android.util.Log;
import com.getcapacitor.BridgeActivity;
import android.webkit.WebView;
import com.getcapacitor.Bridge;

public class MainActivity extends BridgeActivity {
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 启用WebView调试
        WebView.setWebContentsDebuggingEnabled(true);
    }
    
    @Override
    public void onBackPressed() {
        try {
            Bridge bridge = this.getBridge();
            if (bridge != null) {
                WebView webView = bridge.getWebView();
                if (webView != null && webView.canGoBack()) {
                    Log.d(TAG, "WebView可以后退，执行WebView后退操作");
                    webView.goBack();
                    return;
                }
            }
            Log.d(TAG, "WebView无法后退，执行默认后退操作");
            super.onBackPressed();
        } catch (Exception e) {
            Log.e(TAG, "后退操作发生异常", e);
            super.onBackPressed();
        }
    }
}
