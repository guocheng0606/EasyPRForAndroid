package com.fosung.libeasypr.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.aiseminar.EasyPR.PlateRecognizer;
import com.fosung.libeasypr.EasyPrBiz;
import com.fosung.libeasypr.EasyPrPath;
import com.fosung.libeasypr.util.BitmapUtil;
import com.fosung.libeasypr.util.CameraManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 车牌预览view
 */
public class EasyPRPreSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    //private Camera                 mCamera;
    private Camera.PictureCallback rawCallback;
    private Camera.ShutterCallback shutterCallback;
    private Camera.PictureCallback pictureCallback;

    private Rect                   mastLayerFrame;//选取框Rect
    private OnRecognizedListener   recognizedListener;      //车牌识别完毕回调
    private OnPictureTakenListener pictureTakenListener;    //拍照回调
    private PlateRecognizer        plateRecognizer;         //车牌识别处理对象
    private ProgressDialog         progressDialog;//车牌识别时弹出的度条

    private boolean isOnRecognizedRestart = true;//识别完成是否重新开始
    private boolean isRecognized          = true;//防止连续识别，做个标识
    private boolean isInit;
    private Context context;

    public EasyPRPreSurfaceView(Context context) {
        this(context, null);
    }

    public EasyPRPreSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyPRPreSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        plateRecognizer = new PlateRecognizer(context);
        setDrawingCacheEnabled(false);

    }

    /**
     * 设置识别完成监听
     */
    public EasyPRPreSurfaceView setRecognizedListener(OnRecognizedListener recognizedListener) {
        this.recognizedListener = recognizedListener;
        return this;
    }

    /**
     * 设置拍照完成监听
     */
    public EasyPRPreSurfaceView setPictureTakenListener(OnPictureTakenListener pictureTakenListener) {
        this.pictureTakenListener = pictureTakenListener;
        return this;
    }

    /**
     * 获取结果后是否重新开始取景
     */
    public EasyPRPreSurfaceView setIsOnRecognizedRestart(boolean isOnRecognizedRestart) {
        this.isOnRecognizedRestart = isOnRecognizedRestart;
        return this;
    }

    /**
     * 设置选取框，选取框大小需要和{@link EasyPRPreViewMaskLayer#setMastLayerFrame(Rect)}的Rect大小一样
     */
    public EasyPRPreSurfaceView setMastLayerFrame(Rect frame) {
        this.mastLayerFrame = frame;
        return this;
    }

    /**
     * 设置解析时的ProgressDialog
     */
    public EasyPRPreSurfaceView setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
        return this;
    }


    /**
     * 车牌识别执行
     */
    public void recognize() {
        if (isRecognized) {
            if (shutterCallback == null || rawCallback == null || pictureCallback == null) {
                throw new IllegalStateException("没有调用initPreView()");
            }

            isRecognized = false;
            if (CameraManager.get().getCamera() != null) {
                CameraManager.get().getCamera().takePicture(shutterCallback, rawCallback, pictureCallback);
            }
        }
    }

    /**
     * 开始执行， 在Activity或者Fragment 的 onStart中调用
     */
    public void onStart() {
        if (!isInit) {
            init();
        }

        CameraManager.get().openDevice(getHolder(),context);
        /*if (null == mCamera) {
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
                reStartPreView();
                mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
            } catch (Exception e) {
                Toast.makeText(context, "摄像头开启失败, 可能是没有获取到摄像头权限,请检查.", Toast.LENGTH_SHORT)
                     .show();
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        } else {
            reStartPreView();
        }*/
    }

    /**
     * 释放Camera，在onStop中调用
     */
    public void onStop() {
        CameraManager.get().closeDvice();
        /*if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }*/
    }

    /**
     * 销毁资源，在onDestroy中调用
     */
    public void onDestroy() {
        plateRecognizer.destroyRes();
    }


    /**
     * 重新开始识别
     */
    /*private void reStartPreView() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(getHolder());
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    /**
     * 开始执行取景
     */
    private void init() {
        if (!isInit) {
            isInit = true;
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(context);
            }

            if (mastLayerFrame == null) {
                mastLayerFrame = EasyPrBiz.getDefRectFrame(context);
            }

            rawCallback = new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {

                }
            };

            shutterCallback = new Camera.ShutterCallback() {
                public void onShutter() {

                }
            };

            pictureCallback = new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (CameraManager.get().getCamera() == null || getContext() == null || ((Activity) getContext()).isFinishing()) {
                        return;
                    }
                    
                    if (progressDialog != null) {
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("正在识别……");
                    }

                    Bitmap map = BitmapUtil.decodeBitmap(data, getWidth(), getHeight());
                    float scaleX = (float) map.getWidth() / getWidth();
                    float scaleY = (float) map.getHeight() / getHeight();

                    String[] paths = new String[4];
                    try {
                        //不同的选取照片会识别出不同结果，所以多次取景
                        BitmapUtil.savePic(map, EasyPrPath.getPhotoTempFilePath(context, ""));//把没有经过通过选取框裁剪的保存
                        int yOffSet = (map.getHeight() - mastLayerFrame.height()) / 2;
                        for (int i = 0; i < paths.length; i++) {
                            int translateY = yOffSet / paths.length * i;
                            Bitmap newMap = Bitmap.createBitmap(map, (int) (mastLayerFrame.left * scaleX), (int) (mastLayerFrame.top * scaleY - translateY),
                                    (int) (mastLayerFrame.width() * scaleX), (int) (mastLayerFrame.height() * scaleY + translateY * 2), null, false);
                            paths[i] = EasyPrPath.getPhotoTempFilePath(context, String.valueOf(i + 1));
                            BitmapUtil.savePic(newMap, paths[i]);
                            newMap.recycle();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        map.recycle();
                    }


                    if (pictureTakenListener != null) {
                        pictureTakenListener.onPictureTaken(paths);
                    }

                    if (getContext() == null || ((Activity) getContext()).isFinishing()) {
                        return;
                    }

                    new RecognizeTask().execute(0);
                    if (CameraManager.get().getCamera() != null) {
                        CameraManager.get().getCamera().stopPreview();
                    }

                }
            };
            getHolder().addCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        onStart();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (CameraManager.get().getCamera() != null) {
            try {
                // 实现自动对焦
                CameraManager.get().getCamera().autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera arg1) {
                        if (success) {
                            CameraManager.get().reStartPreView(getHolder());
                            CameraManager.get().getCamera().cancelAutoFocus();// 只有加上了这一句，才会自动对焦。
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onStop();
    }


    /**
     * 车牌识别相对耗时，需要异步进行
     */
    class RecognizeTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            // 进行车牌识别
            File file = new File(EasyPrPath.getPhotoTempFolderPath(context));
            File[] files = file.listFiles();
            String[] results = null;
            if (files != null && files.length > 0) {
                results = new String[files.length];
                for (int i = 0; i < files.length; i++) {
                    results[i] = plateRecognizer.recognize(files[i].getPath());
                    System.out.println(results[i]);
                }
            }

            return getCorrectText(results);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            progressDialog.dismiss();
            isRecognized = true;

            if (recognizedListener != null) {
                recognizedListener.onRecognized(str);
            }

            if (isOnRecognizedRestart && CameraManager.get().getCamera() != null) {
                CameraManager.get().getCamera().startPreview();
            }
        }

        /**
         * 获取多次取样对比之后修正后的结果
         */
        private String getCorrectText(String[] results) {
            StringBuilder resultBuilder = new StringBuilder();
            if (results != null && results.length > 0) {
                ArrayList<HashMap<String, Integer>> list = new ArrayList<>();//list是每个位置上的记录，HashMap存的是出现的字key和次数value
                final int startIndex = 3;
                for (int i = startIndex; i < startIndex + 7; i++) {//车牌固定为7位
                    HashMap<String, Integer> map = new HashMap<>();
                    list.add(map);
                    for (String str : results) {
                        if (str != null && str.length() > i) {//识别出的结果有可能比普通的字少
                            String c = String.valueOf(str.charAt(i));
                            if ((i > startIndex && isNumOrLetter(c)) || (i == startIndex && !isNumOrLetter(c))) {
                                int count = map.get(c) == null ? 0 : map.get(c);
                                map.put(c, count + 1);
                            }
                        }
                    }
                }

                //上面的操作将出现的字和次数已经放入到list中，现在开始进行选取
                for (HashMap<String, Integer> map : list) {
                    int maxCount = 0;
                    String text = "";
                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                        int count = entry.getValue();
                        if (count > maxCount) {
                            maxCount = count;
                            text = entry.getKey();
                        }
                    }

                    if (text.length() > 0) {
                        resultBuilder.append(text);
                    }
                }
            }
            return resultBuilder.length() > 0 ? resultBuilder.toString() : null;
        }

        private boolean isNumOrLetter(CharSequence str) {
            Pattern p = Pattern.compile("[A-Z0-9]");
            Matcher m = p.matcher(str);
            return m.matches();
        }
    }

    /**
     * 车牌识别完成回调
     */
    public interface OnRecognizedListener {
        void onRecognized(String result);
    }

    /**
     * 拍照完成回调
     */
    public interface OnPictureTakenListener {
        void onPictureTaken(String[] paths);
    }

}
