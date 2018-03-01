package com.bxgis.bxportal.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author:zjs
 * @Description: 方便使用bitmap的工具类，提供了bitmap转换，缩略等实用的方法
 * @Created in  2017/11/12.
 * @modified By:
 */

public class BitMapUtils {

    /**
     * 生成一个圆形的图片
     * @param src
     * @return
     */
    public static Bitmap createCircleBitmap(Bitmap src) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);

        int size = Math.min(src.getWidth(), src.getHeight());

        //新建的空白的Bitmap
        Bitmap outBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Rect rect = new Rect(0, 0, size, size);

        Canvas canvas = new Canvas(outBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);


        //核心部分，设置两张图片的相交模式，在这里就是上面绘制的Circle和下面绘制的Bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);

        return outBitmap;

    }

    /**
     *
     * Byte[]转Bitmap
     */
    public static Bitmap bytes2Bitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * Bitmap转Byte[]
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Bitmap转Drawable
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * Drawable转Bitmap
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    /**
     * bitmap转file
     * @param bitmap
     * @param imageFile
     * @return
     */

    public static boolean bitmap2File(Bitmap bitmap, File imageFile) {
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            boolean isOK = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return isOK;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 旋转图像
     */
    public static Bitmap rotateBitmap(Bitmap bmp, int degrees) {
        if (degrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }
        return bmp;
    }

    /**
     * 获取 图片旋转度
     * @param imagePath
     * @return
     */
    public static int getPictureDegree(String imagePath) {
        int i = 0;
        try {
            ExifInterface localExifInterface = new ExifInterface(imagePath);
            int j = localExifInterface.getAttributeInt("Orientation", 1);
            switch (j) {
                case 6:
                    i = 90;
                    break;
                case 3:
                    i = 180;
                    break;
                case 8:
                    i = 270;
                case 4:
                case 5:
                case 7:
                default:
                    break;
            }
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        return i;
    }


    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    private static int mDesiredWidth;

    private static int mDesiredHeight;

    /**
     * 从Resources中加载图片
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置成了true,不占用内存，只获取bitmap宽高
        options.inJustDecodeBounds = true;
        // 初始化options对象
        BitmapFactory.decodeResource(res, resId, options);
        // 得到计算好的options，目标宽、目标高
        options = getBestOptions(options, reqWidth, reqHeight);
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, mDesiredWidth, mDesiredHeight); // 进一步得到目标大小的缩略图
    }

    /**
     * 从SD卡上加载图片
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options = getBestOptions(options, reqWidth, reqHeight);
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, mDesiredWidth, mDesiredHeight);
    }

    /**
     * 计算目标宽度，目标高度，inSampleSize
     *
     * @return BitmapFactory.Options对象
     */
    private static BitmapFactory.Options getBestOptions(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 读取图片长宽
        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;
        // Then compute the dimensions we would ideally like to decode to.
        mDesiredWidth = getResizedDimension(reqWidth, reqHeight, actualWidth, actualHeight);
        mDesiredHeight = getResizedDimension(reqHeight, reqWidth, actualHeight, actualWidth);
        // 根据现在得到计算inSampleSize
        options.inSampleSize = calculateBestInSampleSize(actualWidth, actualHeight, mDesiredWidth, mDesiredHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return options;
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio. 最终得到重新测量的尺寸
     *
     * @param maxPrimary      Maximum size of the primary dimension (i.e. width for max
     *                        width), or zero to maintain aspect ratio with secondary
     *                        dimension
     * @param maxSecondary    Maximum size of the secondary dimension, or zero to maintain
     *                        aspect ratio with primary dimension
     * @param actualPrimary   Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary) {
        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth   Actual width of the bitmap
     * @param actualHeight  Actual height of the bitmap
     * @param desiredWidth  Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    // Visible for testing.
    private static int calculateBestInSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float inSampleSize = 1.0f;
        while ((inSampleSize * 2) <= ratio) {
            inSampleSize *= 2;
        }

        return (int) inSampleSize;
    }

    /**
     * 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     */
    private static Bitmap createScaleBitmap(Bitmap tempBitmap, int desiredWidth, int desiredHeight) {
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
            // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
            Bitmap bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
            tempBitmap.recycle(); // 释放Bitmap的native像素数组
            return bitmap;
        } else {
            return tempBitmap; // 如果没有缩放，那么不回收
        }
    }

    /**
     * 获取圆角图片
     * @param bitmap
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 质量压缩
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            options -= 10;
            if (options > 0) {
                baos.reset();
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * 固定大小压缩
     * @param bitMap
     * @param outWidth
     * @param outHeight
     * @return
     */
    public static Bitmap compressFixBitmap(Bitmap bitMap, int outWidth, int outHeight) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        float scaleWidth = ((float) outWidth) / width;
        float scaleHeight = ((float) outHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
    }

    /**
     * 通过文件路径进行压缩
     * @param file  是原始图片;
     * @param  newpath 压缩后存储的位置；
     * @return
     */
    public static String saveBitmapToFile(File file, String newpath) {
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image
            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();
            // The new size we want to scale to
            final int REQUIRED_SIZE = 100;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
//                LogUtil.d("图片的宽度="+o.outWidth+"高度="+o.outHeight+"缩放比="+scale);
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            // here i override the original image file
//            file.createNewFile();
//            FileOutputStream outputStream = new FileOutputStream(file);
//            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            File aa = new File(newpath);
            FileOutputStream outputStream = new FileOutputStream(aa);
            //choose another format if PNG doesn't suit you
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            String filepath = aa.getAbsolutePath();
//            LogUtil.e("getAbsolutePath", aa.getAbsolutePath());
            return filepath;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 原始保存到系统相册工具方法：
     * 解决保存图片到系统相册出现两张图片的问题
     * @param context
     * @param bitmap
     */
    public static void saveToGallery(Context context, Bitmap bitmap) {
        //Paperless  自定义的文件夹名称
        File appDir = new File(Environment.getExternalStorageDirectory(), "Paperless");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //文件的名称设置为 系统时间.jpg
        String fileName = System.currentTimeMillis() + ".PNG";
        Log.e("MyLog", "saveImageUtil.saveToGallery:  fileName --->>> " + fileName);
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * 解决保存图片到系统相册出现两张图片的问题  用ContentValues取代了try {
         MediaStore.Images.Media.insertImage(context.getContentResolver(),
         file.getAbsolutePath(), fileName, null);
         } catch (Exception e) {
         e.printStackTrace();
         }
       */
        // 其次把文件插入到系统图库
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // 最后通知图库更新
//        context.sendBroadcast(new Intent(android.intent.action.MEDIA_SCANNER_SCAN_DIR, Uri.parse("file://" + path))); //扫描指定文件
//        context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_DIR", Uri.fromFile(new File(dir)))); //扫描指定文件夹
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath())))); //扫描指定文件
    }
}
