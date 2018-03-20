package com.bxgis.yczw.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;

/**
 * Author : xiaozhu
 * Time:2018/3/1
 * Description:
 *  正常引用：Uri fileUri = FileProvider7.getUriForFile(this, file);
 *  安装APK 注意：FileProvider7.setIntentDataAndType(this,intent, "application/vnd.android.package-archive", file, true);
 *  获取权限
 *  List<ResolveInfo> resInfoList = getPackageManager()
 *  .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
 *  for (ResolveInfo resolveInfo : resInfoList) {
 *  String packageName = resolveInfo.activityInfo.packageName;
 *  grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
 | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
 }
 *
 *对于权限，有两种方式：
 方式一为Intent.addFlags，该方式主要用于针对intent.setData，setDataAndType以及setClipData相关方式传递uri的。
 方式二为grantUriPermission来进行授权
 *
 */
public class FileProvider7 {
    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = getUriForFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static Uri getUriForFile24(Context context, File file) {
        Uri fileUri = android.support.v4.content.FileProvider.getUriForFile(context,
                context.getPackageName() + ".android7.fileprovider",
                file);
        return fileUri;
    }


    public static void setIntentDataAndType(Context context,
                                            Intent intent,
                                            String type,
                                            File file,
                                            boolean writeAble) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(context, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }
}
