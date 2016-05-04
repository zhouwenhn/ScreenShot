package com.chowen.screenshot.screenshot.util;

import java.io.ByteArrayOutputStream;

import cn.ninegame.gamemanagerhd.util.BitmapUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ResizeBitmap {
	/**
	 * Note: this method may return null
	 * Created by zhouwen on 14-5-8.
	 * @param fileName
	 * @param reqWidth
	 * @param reqHeight
	 * @return a bitmap decoded from the specified file
	 */
	public static Bitmap decodeSampledBitmapFromBytes(Bitmap bitmap,
			int reqWidth, int reqHeight) {
		try {
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// BitmapFactory.decodeFile(fileName, options);

			// Calculate inSampleSize
			int inSampleSize = BitmapUtil.calculateInSampleSize(options,
					reqWidth, reqHeight);
			options.inSampleSize = inSampleSize;

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;

			byte[] bitmapBytes = Bitmap2Bytes(bitmap);
			return BitmapFactory.decodeByteArray(bitmapBytes, 0,
					bitmapBytes.length, options);

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
}
