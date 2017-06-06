package com.fosung.libeasypr.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.fosung.libeasypr.EasyPrBiz;


/**
 * 预览的遮罩层
 */
public class EasyPRPreViewMaskLayer extends View {

    private static final int[] SCANNER_ALPHA   = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long  ANIMATION_DELAY = 30L;  //屏幕刷新时间

    private int     scannerAlpha;
    private Rect    mastLayerFrame;//选取框Rect
    private Paint   paint;
    private int     frameColor;
    private int     laserColor;
    private int     maskColor;
    private int     laserYOffSet;//激光线的Y偏移
    private boolean isStart;//是否开始
    private Context context;

    public EasyPRPreViewMaskLayer(Context context) {
        this(context, null);
    }

    public EasyPRPreViewMaskLayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        frameColor = Color.parseColor("#000000");
        laserColor = Color.parseColor("#FF7FFF00");
        maskColor = Color.parseColor("#66000000");

    }

    /**
     * 设置选取框，选取框大小需要和{@link EasyPRPreSurfaceView#setMastLayerFrame(Rect)}的Rect大小一样
     */
    public EasyPRPreViewMaskLayer setMastLayerFrame(Rect frame) {
        this.mastLayerFrame = frame;
        return this;
    }

    /**
     * 框颜色
     */
    public EasyPRPreViewMaskLayer setFrameColor(int frameColor) {
        this.frameColor = frameColor;
        return this;
    }

    /**
     * 激光线颜色
     */
    public EasyPRPreViewMaskLayer setLaserColor(int laserColor) {
        this.laserColor = laserColor;
        return this;
    }

    /**
     * 遮罩层颜色
     */
    public EasyPRPreViewMaskLayer setMaskColor(int maskColor) {
        this.maskColor = maskColor;
        return this;
    }

    /**
     * 开始执行， 在Activity或者Fragment 的 onStart中调用
     */
    public void onStart() {
        if (mastLayerFrame == null) {
            setMastLayerFrame(EasyPrBiz.getDefRectFrame(context));
        }
        isStart = true;
        postInvalidate();
    }

    /**
     * 结束执行 在Activity或者Fragment 的 onStop中调用
     */
    public void onStop() {
        isStart = false;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mastLayerFrame != null && isStart) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            //画半透明遮罩层区域，遮罩分四块
            paint.setColor(maskColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, width, mastLayerFrame.top - 1, paint);//上部遮罩
            canvas.drawRect(0, mastLayerFrame.top - 1, mastLayerFrame.left - 1, mastLayerFrame.bottom + 1, paint);//左边遮罩
            canvas.drawRect(mastLayerFrame.right + 1, mastLayerFrame.top - 1, width, mastLayerFrame.bottom + 1, paint);//右边遮罩
            canvas.drawRect(0, mastLayerFrame.bottom + 1, width, height, paint);//下部遮罩

            //画红框
            paint.setColor(frameColor);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(mastLayerFrame.left, mastLayerFrame.top, mastLayerFrame.right, mastLayerFrame.bottom, paint);

            //画中间动画的线
/*            paint.setColor(laserColor);
            paint.setStyle(Paint.Style.FILL);
            //            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            laserYOffSet = laserYOffSet >= 60f ? 0 : laserYOffSet;
            int laserY = (int) (mastLayerFrame.top + mastLayerFrame.height() / 60f * laserYOffSet);
            laserY = laserY < mastLayerFrame.top + 1 ? mastLayerFrame.top + 1 : laserY;
            laserY = laserY > mastLayerFrame.bottom - 1 ? mastLayerFrame.bottom - 1 : laserY;
            canvas.drawRect(mastLayerFrame.left + 2, laserY - 1, mastLayerFrame.right - 2, laserY + 1, paint);

            laserYOffSet++;
            //规定时间强制重绘制定区域，实现动画效果
            postInvalidateDelayed(ANIMATION_DELAY, mastLayerFrame.left + 2, mastLayerFrame.top, mastLayerFrame.right - 2, mastLayerFrame.bottom);*/
        }
        super.onDraw(canvas);
    }
}
