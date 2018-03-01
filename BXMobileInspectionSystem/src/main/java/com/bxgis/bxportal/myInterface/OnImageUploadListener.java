package com.bxgis.bxportal.myInterface;

import java.util.List;

public interface OnImageUploadListener {
    
    void onImageUpload(String photoInfo);
    
    void onImageUploadList(List<String> photoInfos);
}