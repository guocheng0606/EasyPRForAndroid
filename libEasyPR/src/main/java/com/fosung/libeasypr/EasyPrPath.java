package com.fosung.libeasypr;

import android.content.Context;

/**
 * 文件在SD卡的暂存路径
 */
public class EasyPrPath {

    public static String getPhotoTempFilePath(Context context, String suffix) {
        return context.getExternalCacheDir() + "/easy_pr/temp/CarNumber_" + suffix + ".jpg";
    }

    public static String getPhotoTempFolderPath(Context context) {
        return context.getExternalCacheDir() + "/easy_pr/temp/";
    }

    public static String getSvmFile(Context context) {
        return context.getExternalFilesDir(null) + "/easy_pr/svm.xml";
    }

    public static String getAnnFile(Context context) {
        return context.getExternalFilesDir(null) + "/easy_pr/ann.xml";
    }
}
