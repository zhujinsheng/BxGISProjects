package com.bxgis.yczw.myInterface;

import java.util.List;

public interface OnImageUploadListener {
    
    void onImageUpload(String photoInfo);
    
    void onImageUploadList(List<String> photoInfos);
}