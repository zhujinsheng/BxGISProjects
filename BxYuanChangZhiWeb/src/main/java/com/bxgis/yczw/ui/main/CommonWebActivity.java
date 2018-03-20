package com.bxgis.yczw.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bxgis.yczw.R;
import com.bxgis.yczw.utils.SPUtils;
import com.bxgis.yczw.utils.StringUtil;
import com.jaeger.library.StatusBarUtil;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.util.ArrayList;

import xiaozhu.utilwebx5.BaseX5WebActivity;
import xiaozhu.utilwebx5.X5WebChromeClient;
import xiaozhu.utilwebx5.X5WebViewClient;
import xiaozhu.utilwebx5.X5WebViewSample;

public class CommonWebActivity extends BaseX5WebActivity {
    LinearLayout activity_login;
    X5WebChromeClient mX5WebChromeClient = null;
    LinearLayout.LayoutParams params = null;
    X5WebViewSample x5webview;
    private String path = "";

    @Override
    protected void initView() {
        //避免输入法界面弹出后遮挡输入光标的问题
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_web);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);//（这个对宿主没什么影响，建议声明）
        StatusBarUtil.setColor(CommonWebActivity.this, ContextCompat.getColor(this, R.color.center_inspection), 0);
        path = getIntent().getStringExtra("PATH");
        initHardwareAccelerate();
        activity_login = (LinearLayout) findViewById(R.id.ll_login);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected X5WebViewSample getmX5WebView() {
        x5webview = new X5WebViewSample(this);
        x5webview.setLayoutParams(params);
        activity_login.addView(x5webview);
//        x5webview_login = (X5WebViewSample) findViewById(R.id.x5webview_login);
        return x5webview;
    }

    @Override
    protected void start() {

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        // 网页内容的宽度是否可大于WebView控件的宽度
        webSettings.setLoadWithOverviewMode(false);
        mX5WebChromeClient = new X5WebChromeClient(this);
        mWebView.setX5ChromeClient(mX5WebChromeClient);
//        mWebView.setDownloadListener(new AboutActivity.MyWebViewDownLoadListener());
        mX5WebChromeClient.setFileChooseListener(new X5WebChromeClient.FileChoooseListener() {
            @Override
            public void listener(ValueCallback<Uri[]> filePathCallback) {
//                filePathCallback.onReceiveValue();
                uploadFiles = filePathCallback;
//                Toast.makeText(CommonWebActivity.this, "回调", Toast.LENGTH_SHORT).show();
            }

        });
        mWebView.setWebViewClient(new X5WebViewClient() {
            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
//                mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
            }
        });
        if (StringUtil.isNotEmpty(path, false)) {
//            mWebView.loadUrl(path);
            mWebView.loadUrlByCookie(CommonWebActivity.this,path, (String) SPUtils.get(CommonWebActivity.this,"_sso_token",""));  // 同步coket
        }

        //去除QQ浏览器推广广告
        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ArrayList<View> outView = new ArrayList<View>();
                getWindow().getDecorView().findViewsWithText(outView,"QQ浏览器",View.FIND_VIEWS_WITH_TEXT);
                if(outView.size()>0){
                    outView.get(0).setVisibility(View.GONE);
                }
            }
        });

    }


    private void setCookie(String url,String token) {
        String StringCookie = "_sso_token=" + token + ";path=/";
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null);
            cookieManager.flush();
        } else {
            cookieManager.removeSessionCookie();
            CookieSyncManager.getInstance().sync();
        }
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, StringCookie);
    }
    /**
     * 启用硬件加速
     * 或者直接在配置文件上添加  <activity  android:hardwareAccelerated="true"  .... />
     */
    private void initHardwareAccelerate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getWindow().setFlags(
                        android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //处理本地拍照
        switch (requestCode) {
            case RESULT_OK:
                break;
        }
        //父类回调实现文件上传的处理
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
