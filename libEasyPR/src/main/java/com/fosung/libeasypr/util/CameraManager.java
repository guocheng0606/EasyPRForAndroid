package com.fosung.libeasypr.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by guocheng on 2017/4/20.
 */

public class CameraManager {

    private Camera mCamera;
    private static CameraManager cameraManager;

    public static CameraManager get() {
        if(cameraManager == null)
            cameraManager = new CameraManager();
        return cameraManager;
    }

    public void openDevice(SurfaceHolder holder, Context context){
        if (null == mCamera) {
            try {
                mCamera = Camera.open();
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureFormat(ImageFormat.JPEG);
                // parameters.setPictureSize(surfaceView.getWidth(),
                // surfaceView.getHeight()); // 部分定制手机，无法正常识别该方法。
                // parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);//闪光灯
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
                parameters.setRotation(90);
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                reStartPreView(holder);
                mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
            } catch (Exception e) {
                Toast.makeText(context, "摄像头开启失败, 可能是没有获取到摄像头权限,请检查.", Toast.LENGTH_SHORT)
                        .show();
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        } else {
            reStartPreView(holder);
        }
    }

    public void closeDvice(){
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 重新开始识别
     */
    public void reStartPreView(SurfaceHolder holder) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Camera getCamera() {
        return mCamera;
    }
}
