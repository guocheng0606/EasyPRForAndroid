package com.aiseminar.EasyPR;

import android.content.Context;
import android.util.Log;

import com.fosung.libeasypr.EasyPrPath;
import com.fosung.libeasypr.R;
import com.fosung.libeasypr.util.FileUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * 车牌识别Jni调用类
 */
public class PlateRecognizer {
    private Context context;
    private long    mRecognizerPtr; //so库是否初始化成功 !=0 为成功
    private String  svmFilePath;//svm的存储路径
    private String  annFilePath;//ann的存储路径

    static {
        try {
            System.loadLibrary("EasyPR"); // 加载车牌识别库
        } catch (UnsatisfiedLinkError ule) {
            Log.e("PlateRecognizer", "WARNING: Could not load EasyPR library!");
        }
    }

    public static native String stringFromJNI();

    public static native long initPR(String svmpath, String annpath);

    public static native long uninitPR(long recognizerPtr);

    public static native byte[] plateRecognize(long recognizerPtr, String imgpath);


    public PlateRecognizer(Context context) {
        this(context, EasyPrPath.getSvmFile(context), EasyPrPath.getAnnFile(context));
    }

    public PlateRecognizer(Context context, String svmFilePath, String annFilePath) {
        this.context = context;
        this.svmFilePath = svmFilePath;
        this.annFilePath = annFilePath;

        checkAndUpdateModelFile();
        mRecognizerPtr = initPR(EasyPrPath.getSvmFile(context), EasyPrPath.getAnnFile(context));
    }

    /**
     * 检查本地SD卡是否已经保存了机器学习文件，如果没有，则从raw文件夹拷贝
     */
    private void checkAndUpdateModelFile() {
        FileUtil.copyFileFromRaw(context, EasyPrPath.getSvmFile(context), R.raw.easypr_svm);
        FileUtil.copyFileFromRaw(context, EasyPrPath.getAnnFile(context), R.raw.easypr_ann);
    }

    /**
     * so库是否初始化成功
     */
    private boolean isLoadSoSuccessful() {
        return mRecognizerPtr != 0;
    }

    /**
     * 车牌识别函数，调用jni实现
     *
     * @param imagePath 需要识别的图片路径
     */
    public String recognize(String imagePath) {
        File imageFile = new File(imagePath);
        if (!isLoadSoSuccessful() || !imageFile.exists()) {
            return null;
        }

        byte[] retBytes = plateRecognize(mRecognizerPtr, imagePath);
        String result = null;
        try {
            result = new String(retBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void destroyRes() {
        uninitPR(mRecognizerPtr);
        mRecognizerPtr = 0;
    }
}
