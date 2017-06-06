/***********************************************************
 * author   colin
 * company  fosung
 * email    wanglin2046@126.com
 * date     16-7-15 下午4:41
 **********************************************************/
package com.fosung.libeasypr.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 屏幕工具
 */
public class ScreenUtil {

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay()
          .getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay()
          .getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取当前窗口的高度， 该高度是不包含导航栏和状态栏的
     */
    public static int getWindowHeight(Activity activity) {
        return activity.getWindow()
                       .getWindowManager()
                       .getDefaultDisplay()
                       .getHeight();
    }

    /**
     * 精确获取屏幕尺寸（例如：3.5、4.0、5.0寸屏幕）
     *
     * @return 设备尺寸
     */
    public static double getScreenPhysicalSize(Activity activity) {
        DisplayMetrics dm = activity.getResources()
                                    .getDisplayMetrics();
        double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
        return diagonalPixels / (dm.densityDpi);
    }

    /**
     * 判断是否是平板（官方用法）
     */
    public static boolean isTablet(Context context) {
        return (context.getResources()
                       .getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 获取导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight(Activity context) {
        int statusBarHeight = 0;

        try {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = res.getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (statusBarHeight == 0) {
            Rect frame = new Rect();
            context.getWindow()
                   .getDecorView()
                   .getWindowVisibleDisplayFrame(frame);
            statusBarHeight = frame.top;
        }
        return statusBarHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow()
                            .getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow()
                            .getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow()
                .getDecorView()
                .getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 设置灰度
     *
     * @param greyScale true:灰度
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setGreyScale(View v, boolean greyScale) {
        if (greyScale) {
            // Create a paint object with 0 saturation (black and white)
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            Paint greyScalePaint = new Paint();
            greyScalePaint.setColorFilter(new ColorMatrixColorFilter(cm));
            // Create a hardware layer with the greyScale paint
            v.setLayerType(View.LAYER_TYPE_HARDWARE, greyScalePaint);
        } else {
            // Remove the hardware layer
            v.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    /**
     * 判断是否开启了自动亮度调节
     */
    public static boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 获取屏幕的亮度
     */
    public static float getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(
                    resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 设置亮度(手动设置亮度，需要关闭自动设置亮度的开关)
     *
     * @param brightness 0-1
     */
    public static void setBrightness(Activity activity, float brightness) {
        stopAutoBrightness(activity);
        WindowManager.LayoutParams lp = activity.getWindow()
                                                .getAttributes();
        lp.screenBrightness = brightness * (1f / 255f);
        activity.getWindow()
                .setAttributes(lp);
    }

    /**
     * 停止自动亮度调节
     */
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 开启亮度自动调节
     */
    public static void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 保存亮度设置状态
     */
    public static void saveBrightness(ContentResolver resolver, int brightness) {
        Uri uri = Settings.System
                .getUriFor("screen_brightness");
        Settings.System.putInt(resolver, "screen_brightness",
                brightness);
        // resolver.registerContentObserver(uri, true, myContentObserver);
        resolver.notifyChange(uri, null);
    }
}
