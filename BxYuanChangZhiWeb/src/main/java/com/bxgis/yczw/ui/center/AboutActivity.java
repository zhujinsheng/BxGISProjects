package com.bxgis.yczw.ui.center;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bxgis.yczw.R;
import com.bxgis.yczw.utils.StringUtil;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;

import java.io.File;
import java.net.URLDecoder;

import xiaozhu.utilwebx5.X5WebChromeClient;
import xiaozhu.utilwebx5.X5WebViewSample;

public class AboutActivity extends AppCompatActivity  {
X5WebChromeClient mX5WebChromeClient = null;
    X5WebViewSample mX5WebViewSample;
    private String path="";
    boolean isFile = false;
    ValueCallback<Uri[]> filePathCallbacks =  null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        path = getIntent().getStringExtra("PATH");
        isFile = getIntent().getBooleanExtra("VIDEO",false);
        mX5WebViewSample = (X5WebViewSample) findViewById(R.id.x5webview);
        if(isFile) {
            WebSettings webSettings = mX5WebViewSample.getSettings();
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        mX5WebChromeClient= new X5WebChromeClient(this);
        mX5WebViewSample.setX5ChromeClient(mX5WebChromeClient);
        mX5WebViewSample.setDownloadListener(new MyWebViewDownLoadListener());
        mX5WebChromeClient.setFileChooseListener(new X5WebChromeClient.FileChoooseListener() {
            @Override
            public void listener(ValueCallback<Uri[]> filePathCallback) {
//                filePathCallback.onReceiveValue();
                filePathCallbacks = filePathCallback;
                Toast.makeText(AboutActivity.this, "回调", Toast.LENGTH_SHORT).show();
            }

        });
        if(StringUtil.isNotEmpty(path,false))
        mX5WebViewSample.loadUrl(path);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 101:
        //相册选择的图片
        if (null != filePathCallbacks) {
            Uri result = data == null || resultCode != RESULT_OK ? null
                    : data.getData();
            if (null != result) {
                Uri[] s = new Uri[]{result};
                filePathCallbacks.onReceiveValue(s);
                filePathCallbacks = null;
            } else { //正常是相机选择返回
//                filePathCallbacks.onReceiveValue(imageUri);
//                filePathCallbacks = null;
            }
        }
            break;
                case 102:
            break;
            }}else if (resultCode == RESULT_CANCELED) {
            if (null != filePathCallbacks) {
                filePathCallbacks.onReceiveValue(null);
                filePathCallbacks = null;
            }

        }
    }

    /**
     * 利用WebView的DownloadListene接口实现文件下载
     */
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast t = Toast.makeText(AboutActivity.this, "需要SD卡。", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return;
            }
            DownloaderTask task = new DownloaderTask();
            task.execute(url);
        }

    }

    //内部类
    private class DownloaderTask extends AsyncTask<String, Void, String> {

        public DownloaderTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String url = params[0];
//          Log.i("tag", "url="+url);
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            fileName = URLDecoder.decode(fileName);
            Log.i("tag", "fileName=" + fileName);

            File directory = Environment.getExternalStorageDirectory();
            File file = new File(directory, fileName);
            if (file.exists()) {
                Log.i("tag", "The file has already exists.");
                return fileName;
            }
            try {
                /**
                 * 网络下载部分
                 */
//                HttpClient client = new DefaultHttpClient();
////                client.getParams().setIntParameter("http.socket.timeout",3000);//设置超时
//                HttpGet get = new HttpGet(url);
//                HttpResponse response = client.execute(get);
//                if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){
//                    HttpEntity entity = response.getEntity();
//                    InputStream input = entity.getContent();
//
//                    writeToSDCard(fileName,input);
//
//                    input.close();
////                  entity.consumeContent();
//                    return fileName;
//                }else{
//                    return null;
//                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return fileName;
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (result == null) {
                Toast t = Toast.makeText(AboutActivity.this, "连接错误！请稍后再试！", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return;
            }

            Toast t = Toast.makeText(AboutActivity.this, "已保存到SD卡。", Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
            File directory = Environment.getExternalStorageDirectory();
            File file = new File(directory, result);
            Log.i("tag", "Path=" + file.getAbsolutePath());
// 打开下载文件
//            Intent intent = getFileIntent(file);
//            startActivity(intent);

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//            showProgressDialog();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }
    }


    /**
     * 如何避免WebView内存泄露
     * 在 Activity 销毁（ WebView ）的时候，先让 WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空。
     */
    @Override
    protected void onDestroy() {
        if (mX5WebViewSample != null) {
            mX5WebViewSample.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mX5WebViewSample.clearHistory();

            ((ViewGroup) mX5WebViewSample.getParent()).removeView(mX5WebViewSample);
            mX5WebViewSample.destroy();
            mX5WebViewSample = null;
        }
        super.onDestroy();
    }
}
