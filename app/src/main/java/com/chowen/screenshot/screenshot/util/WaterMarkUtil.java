package com.chowen.screenshot.screenshot.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import cn.ninegame.gamemanagerhd.util.L;

/**
 * Created by zhouwen on 14-4-15.
 */
public class WaterMarkUtil {

    private static final int MAX_SIZE = 1600;
    private static String TAG = "WaterMarkUtil";

    /**
	 * 加文字水印
	 * 
	 * @return 位图对象
	 */
	public static Bitmap createBitmap(Bitmap src, String str) {
		int w = src.getWidth();
		int h = src.getHeight();
		Bitmap bmpTemp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmpTemp);
		Paint p = new Paint();
		p.setDither(true); // 获取更清晰的图像采样
		p.setFilterBitmap(true);
		String familyName = "宋体";
		Typeface font = Typeface.create(familyName, Typeface.BOLD);
		p.setColor(Color.GRAY);
		p.setTypeface(font);
		p.setTextSize(34);
		p.setTypeface(Typeface.DEFAULT_BOLD);
		canvas.drawBitmap(src, 0, 0, p);
		canvas.drawText(str, w / 2, h - 20, p);
		canvas.save(Canvas.ALL_SAVE_FLAG);

		canvas.restore();
		return bmpTemp;
	}

	/**
	 * 加 图片水印
	 * 
	 * @return 位图对象
	 */
	public static Bitmap createWaterMarkBitmap(Bitmap srcBitmap, Bitmap watermark) {
		Bitmap desBmp;
		int desWidth;
		int desHeight;
		if (srcBitmap == null) {
			return null;
		}
		int w = srcBitmap.getWidth();
		int h = srcBitmap.getHeight();

        float ratio = (float)w / h;

		if (w <= MAX_SIZE || h <= MAX_SIZE) {
			// 创建一个新的和SRC长度宽度一样的位图
			desBmp = Bitmap.createBitmap(w,h,srcBitmap.getConfig());
            desWidth = w;
            desHeight = h;
		} else {
            if(w > h ){
                desWidth = MAX_SIZE;
                desHeight = (int) (desWidth / ratio);
            }else {
                desHeight = MAX_SIZE;
                desWidth = (int) (desHeight * ratio);
            }
            desBmp = Bitmap.createBitmap(desWidth,desHeight,srcBitmap.getConfig());
		}

		Canvas cv = new Canvas(desBmp);
		cv.drawBitmap(srcBitmap, 0, 0, null);// 在 0，0坐标开始画入src
		Paint paint = new Paint();

		// 加入图片
		if (watermark != null) {
			paint.setAlpha(240);
			int wh = watermark.getHeight();
			int left = 20;
			int top = (desBmp.getHeight() - wh) - 10;
			cv.drawBitmap(watermark, left, top, paint);
		}

        L.d(TAG,"newBip width:"+desWidth +",newBip height:"+desHeight +"scale ratio:"+ratio +
                "watermark width:"+watermark.getWidth() +",watermark height:"+watermark.getHeight());
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		return desBmp;
	}

}
