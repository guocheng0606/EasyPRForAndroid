/***********************************************************
 * author   colin
 * company  fosung
 * email    wanglin2046@126.com
 * date     16-7-15 下午4:41
 **********************************************************/

/*    
 * 
 * @author		: WangLin  
 * @Company: 	：FCBN
 * @date		: 2015年5月13日 
 * @version 	: V1.0
 */
package com.fosung.libeasypr.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 图片操作工具类
 */
public class BitmapUtil {
    /**
     * 存储图片
     *
     * @param tagPath 目标路径
     */
    public static void savePic(Bitmap bitmap, String tagPath) {
        File file = new File(tagPath);
        FileUtil.checkFilePath(file, false);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据文件路径返回Bitmap
     *
     * @param fileName 文件路径
     * @return Bitmap对象
     */
    public static Bitmap decodeBitmap(String fileName) {
        return BitmapFactory.decodeFile(fileName);
    }

    /**
     * 图片缩放（不按比例拉伸）
     *
     * @param resID     源图片资源
     * @param newWidth  缩放后宽度
     * @param newHeight 缩放后高度
     * @return 缩放后的Bitmap对象
     */
    public static Bitmap zoomImage(Resources res, int resID, int newWidth, int newHeight) {
        Bitmap map = BitmapFactory.decodeResource(res, resID);
        return Bitmap.createScaledBitmap(map, newWidth, newHeight, false);
    }

    /**
     * 图片缩放（不按比例拉伸）
     *
     * @param width  缩放后宽度
     * @param height 缩放后高度
     * @return 缩放后的Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    /***
     * 图片缩放（不按比例拉伸）
     *
     * @param width  缩放后宽度
     * @param height 缩放后高度
     * @return drawable            缩放后的Bitmap对象
     */
    @SuppressWarnings("deprecation")
    public static Drawable zoomDrawable(Drawable drawable, int width, int height) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        return new BitmapDrawable(zoomBitmap(bitmap, width, height));
    }

    /**
     * 图片缩放（等比缩放）
     *
     * @param resID  源图片资源
     * @param width  缩放后宽度
     * @param height 缩放后高度
     * @return 缩放后的Bitmap对象
     */
    public static Bitmap decodeBitmap(Resources res, int resID, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 设为true，BitmapFactory.decodeFile(Stringpath, Options opt)并不会真的返回一个Bitmap给你，仅会把它的宽，高取回
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resID, options);

        // 计算缩放比例
        options.inSampleSize = calculateOriginal(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resID, options);
    }

    /**
     * 图片缩放（等比缩放）
     *
     * @param fileName 图片文件路径
     */
    public static Bitmap decodeBitmap(String fileName, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 设为true，BitmapFactory.decodeFile(Stringpath, Options opt)并不会真的返回一个Bitmap给你，仅会把它的宽，高取回
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);

        // 计算缩放比例
        options.inSampleSize = calculateOriginal(options, width, height);
        options.inJustDecodeBounds = false;
        System.out.println("samplesize:" + options.inSampleSize);
        return BitmapFactory.decodeFile(fileName, options);
    }
    
    /**
     * 图片缩放（等比缩放）
     *
     */
    public static Bitmap decodeBitmap(byte[] arrByte, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 设为true，BitmapFactory.decodeFile(Stringpath, Options opt)并不会真的返回一个Bitmap给你，仅会把它的宽，高取回
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(arrByte, 0, arrByte.length, options);

        // 计算缩放比例
        options.inSampleSize = calculateOriginal(options, width, height);
        options.inJustDecodeBounds = false;
        System.out.println("samplesize:" + options.inSampleSize);
        return BitmapFactory.decodeByteArray(arrByte, 0, arrByte.length, options);
    }


    /**
     * 图片缩放ImageView大小
     *
     * @param fileName 图片文件路径
     * @return 缩放后的图片
     */
    public static Bitmap decodeBitmap(String fileName, ImageView imageView) {
        LayoutParams lay = imageView.getLayoutParams();
        int width = lay.width;
        int height = lay.height;
        final BitmapFactory.Options options = new BitmapFactory.Options();

        // 设为true，BitmapFactory.decodeFile(Stringpath, Options opt)并不会真的返回一个Bitmap给你，仅会把它的宽，高取回
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);

        // 计算缩放比例
        options.inSampleSize = calculateOriginal(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(fileName, options);
    }

    /**
     * 计算图片缩放比例,只能缩放2的指数
     *
     * @param reqWidth  缩放后的宽度
     * @param reqHeight 缩放后的高度
     * @return 计算的缩放比例
     */
    public static int calculateOriginal(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            long totalPixels = width/ inSampleSize * height / inSampleSize;
            final long totalReqPixelsCap = reqWidth * reqHeight;
            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 4;
            }
        }
        return inSampleSize;
    }

    /**
     * Drawable转换为Bitmap
     *
     * @param drawable drawable对象
     * @return Bitmap对象
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565;

        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);

        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);

        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap对象转为Drawable对象
     *
     * @param bitmap bitmap对象
     * @return Drawable对象
     */
    public static Drawable bitmapToDrawable(Resources res, Bitmap bitmap) {
        return new BitmapDrawable(res, bitmap);
    }

    /**
     * 获取圆角图片
     *
     * @param roundPx 圆角度数
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 获取带倒影的图片
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /**
     * 将Bitmap转为Byte[]
     *
     * @param bm Bitmap对象
     * @return byte[]
     */
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 将Byte[]转为Bitmap
     *
     * @param b byte[]
     * @return Bitmap对象
     */
    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
}
