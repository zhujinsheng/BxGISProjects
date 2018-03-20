/**
 * @Title: UpdateReceiver.java
 * @Package com.elong.pullnew.receiver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author Siyu.Jiang
 * @date 2014年5月13日 下午6:01:34
 */
package com.bxgis.yczw.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import java.io.File;

/**
 * 类描述: 该广播是弹出下载进度跳对话框
 * 注意： 只能广播动态注册，因为动态注册时，BroadcastReceiver的onReceive(Context context, Intent intent)的实参context是调用sendBroadcast()的那个context；
 * Created by 小朱 gis事业部 on 2017/4/10
 * 版本 V2.0
 */
public class UpdateReceiver extends BroadcastReceiver {

    private Context context;
    private ProgressDialog mProgressDialog = null;
    private String updateDescription;
    private boolean forceUpdate = false;

    /**
     * @param updateDescription 更新描述
     * @param forceUpdate       是否强制更新
     */
    public UpdateReceiver(String updateDescription, boolean forceUpdate) {
        this.updateDescription = updateDescription;
        this.forceUpdate = forceUpdate;
    }

    public UpdateReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        if (mProgressDialog == null) {
            mProgressDialog = getProgDialog();
        }

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        int percent = intent.getIntExtra("percent", -1);
        if (percent != -1)
            mProgressDialog.setProgress(percent);
        else {
            mProgressDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
        }
        if (percent == 100) {
            System.out.println("xiaozhu更新测试安装" + String.valueOf(forceUpdate));
            String filePath = intent.getStringExtra("filePath");
            Uri uri = Uri.fromFile(new File(filePath));
            final Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(installIntent);
            mProgressDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            mProgressDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    context.startActivity(installIntent);
                }
            });
            mProgressDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
            mProgressDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProgressDialog.dismiss();
                }
            });
        } else {
            mProgressDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            mProgressDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
        }
    }

    private ProgressDialog getProgDialog() {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        if (updateDescription != null)
            progressDialog.setMessage(updateDescription);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "安装", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        if (forceUpdate)
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "退出", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
        else {
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        return progressDialog;
    }
}
