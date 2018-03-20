package com.bxgis.yczw.widget.toast;

import android.support.annotation.NonNull;
import android.widget.Toast;

/**
 * Toast抛出异常的接口
 */
public interface BadTokenListener {

    void onBadTokenCaught(@NonNull Toast toast);
}
