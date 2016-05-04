package com.chowen.screenshot.screenshot.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhouwen on 14-4-22.
 */
public class BitmapCompUtil {

	public static Bitmap loadBitmapFromUri(Context context, Uri uri,
			int sampleSize) {
		Bitmap bitmap = null;
		InputStream is = null;

		try {
			is = context.getContentResolver().openInputStream(uri);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = sampleSize;
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(is, null, opts);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (is != null) {
				try {
					is.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return bitmap;
	}

	/**
	 * 保存指定大小的bitmap对象到文件
	 * 
	 * @param context
	 * @param uri
	 * @param file
	 * @param limitSize
	 *            Bitmap最长边的长度，最短边按等比缩放
	 * @return 输出文件的路径
	 */
	public static String saveResizeBitmap2File(Context context, Uri uri,
			File file, int limitSize) {
		BitmapFactory.Options opts = loadBitmapOptions(context, uri);
		int reqWidth = opts.outWidth;
		int reqHeight = opts.outHeight;
		int factor = 1;

		if (reqWidth > limitSize || reqHeight > limitSize) {
			if (reqWidth > reqHeight) {
				factor = reqWidth / limitSize;

			} else {
				factor = reqHeight / limitSize;
			}
		}

		int simple = factor > 0 ? factor : 1;

		if (factor > 1) {
			simple += 1; // 排除原图长宽小于限制大小的情况
		}

		Bitmap bmp = loadBitmapFromUri(context, uri, simple);
		if (bmp == null) {
			return null;
		}
		int height = bmp.getHeight();
		int width = bmp.getWidth();
		// StringBuilder builder = new StringBuilder()
		// .append(file.getAbsolutePath())
		// .append("_")
		// .append(width).append("x").append(height);
		File detail = new File(file.getAbsolutePath().toString()); // 在原来的文件路径后面补全图片的长宽信息,
																	// 格式：_960*500

		if (saveBitmap2File(bmp, detail, 95, true)) {
			return detail.getAbsolutePath();

		} else {
			return null;
		}
	}

    /**
     * 保存Bitmap对象为到文件
     *
     * @param srcBitmap
     * @param file
     * @param quality
     * @param recycleOrig
     * @return
     */
    public static boolean saveBitmap2File(Bitmap srcBitmap,  File file ,int quality, boolean recycleOrig) {
        if (srcBitmap == null || file == null) {
            return false;
        }
        if (file.exists()) {
            file.delete();
        }

        if (quality < 0) {
            quality = 0;

        } else if (quality > 100) {
            quality = 100;
        }

        boolean saveOk = true;
        BufferedOutputStream bos;

        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();

        } catch (Exception e) {
            saveOk = false;
            e.printStackTrace();
        }

        if (recycleOrig && !srcBitmap.isRecycled()) {
            srcBitmap.recycle();
        }

        return saveOk;
    }

	/**
	 * 保存Bitmap对象为到文件
	 * 
	 * @param srcBitmap
	 * @param file
	 * @param quality
	 * @param recycleOrig
	 * @return
	 */
	public static String saveBitmap2File(Bitmap srcBitmap, String path,int quality, boolean recycleOrig) {
		if (srcBitmap == null || path == null) {
			return null;
		}
        File file = new File(path);
		if (file.exists()) {
			file.delete();
		}

		if (quality < 0) {
			quality = 0;

		} else if (quality > 100) {
			quality = 100;
		}

		BufferedOutputStream bos;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			srcBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
			bos.flush();
			bos.close();

		} catch (Exception e) {
			e.printStackTrace();
            return null;
		}

		if (recycleOrig && !srcBitmap.isRecycled()) {
			srcBitmap.recycle();
		}

		return path;
	}

	public static BitmapFactory.Options loadBitmapOptions(Context context,
			Uri uri) {
		BitmapFactory.Options opts = null;
		InputStream is = null;

		try {
			is = context.getContentResolver().openInputStream(uri);
			opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, opts);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (is != null) {
				try {
					is.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return opts;
	}

	public static Uri getContentUri(Context context, File imageFile) {
		String filePath = imageFile.getAbsolutePath();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID },
				MediaStore.Audio.Media.DATA + "=? ", new String[] { filePath },
				null);

		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/audio/media");
			return Uri.withAppendedPath(baseUri, "" + id);

		}
		return null;
	}

}
