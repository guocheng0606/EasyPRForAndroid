package com.fosung.libeasypr.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;


/**
 * 预览的遮罩层
 */
public class EasyPRPreView extends FrameLayout {

    private EasyPRPreSurfaceView surfaceView;
    private EasyPRPreViewMaskLayer maskLayer;

    public EasyPRPreView(Context context) {
        this(context, null);
    }

    public EasyPRPreView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyPRPreView(Context context, @Nullable AttributeSet attrs, int defStylesAttr) {
        super(context, attrs, defStylesAttr);

        surfaceView = new EasyPRPreSurfaceView(context);
        maskLayer = new EasyPRPreViewMaskLayer(context);
        addView(surfaceView);
        addView(maskLayer);
    }

    /**
     * 设置识别完成监听
     */
    public EasyPRPreView setRecognizedListener(EasyPRPreSurfaceView.OnRecognizedListener recognizedListener) {
        surfaceView.setRecognizedListener(recognizedListener);
        return this;
    }

    /**
     * 设置拍照完成监听
     */
    public EasyPRPreView setPictureTakenListener(EasyPRPreSurfaceView.OnPictureTakenListener pictureTakenListener) {
        surfaceView.setPictureTakenListener(pictureTakenListener);
        return this;
    }

    /**
     * 获取结果后是否重新开始取景
     */
    public EasyPRPreView setIsOnRecognizedRestart(boolean isOnRecognizedRestart) {
        surfaceView.setIsOnRecognizedRestart(isOnRecognizedRestart);
        return this;
    }

    /**
     * 设置选取框，选取框大小需要和{@link EasyPRPreViewMaskLayer#setMastLayerFrame(Rect)}的Rect大小一样
     */
    public EasyPRPreView setMastLayerFrame(Rect frame) {
        surfaceView.setMastLayerFrame(frame);
        maskLayer.setMastLayerFrame(frame);
        return this;
    }

    /**
     * 框颜色
     */
    public EasyPRPreView setFrameColor(int frameColor) {
        maskLayer.setFrameColor(frameColor);
        return this;
    }

    /**
     * 激光线颜色
     */
    public EasyPRPreView setLaserColor(int laserColor) {
        maskLayer.setLaserColor(laserColor);
        return this;
    }

    /**
     * 遮罩层颜色
     */
    public EasyPRPreView setMaskColor(int maskColor) {
        maskLayer.setMaskColor(maskColor);
        return this;
    }

    /**
     * 开始执行， 在Activity或者Fragment 的 onStart中调用
     */
    public void onStart() {
        surfaceView.onStart();
        maskLayer.onStart();
    }

    /**
     * 车牌识别执行
     */
    public void recognize() {
        surfaceView.recognize();
    }

    /**
     * 开始执行， 在Activity或者Fragment 的 onStop中调用
     */
    public void onStop() {
        surfaceView.onStop();
        maskLayer.onStop();
    }

    /**
     * 开始执行， 在Activity或者Fragment 的 onDestroy中调用
     */
    public void onDestroy() {
        surfaceView.onDestroy();
    }

}
