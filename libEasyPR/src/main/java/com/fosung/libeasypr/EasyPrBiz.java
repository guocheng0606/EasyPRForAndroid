package com.fosung.libeasypr;

import android.content.Context;
import android.graphics.Rect;

import com.fosung.libeasypr.util.ScreenUtil;

/**
 *
 */
public class EasyPrBiz {

    /**
     * 获取默认的预览区
     */
    public static Rect getDefRectFrame(Context context) {
        int screenWidth = ScreenUtil.getScreenWidth(context);
        int screenHeight = ScreenUtil.getScreenHeight(context);
        int rectLeft = screenWidth / 9;
        int rectTop = screenHeight * 6 / 14;
        int rectRight = screenWidth * 8 / 9;
        int rectBottom = screenHeight * 8 / 14;
        return new Rect(rectLeft, rectTop, rectRight, rectBottom);
    }
}
