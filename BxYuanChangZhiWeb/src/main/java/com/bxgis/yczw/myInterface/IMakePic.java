package com.bxgis.yczw.myInterface;


/**
 * @2Do:
 * @Author M2
 * @Version v ${VERSION}
 * @Date 2017/7/15 0015.
 */
public interface IMakePic {

    /**
     * 更新
     * @param onImageUploadListener
     */
    void setPicUploadListener(OnImageUploadListener onImageUploadListener);

    /**
     * 拍照片
     * @param position
     */
    void takePic(int position);

    /**
     * 删除指定的图片
     * @param url
     */
    void delPic(String url);
}
