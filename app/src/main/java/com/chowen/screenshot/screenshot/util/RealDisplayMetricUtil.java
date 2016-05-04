package com.chowen.screenshot.screenshot.util;

import android.app.Activity;
import android.view.Display;

/**
 * Created by zhouwen on 14-5-10.
 */
public class RealDisplayMetricUtil {

	public static int[] GetRealDisplayMetric(Activity context, int mWidth,
			int mHeight) {

		int displayMetricW = 0;
		int displayMetricH = 0;
		int displayMetric[] = { 0, 0 };
		int tempData;
		
		Display mDisplay = context.getWindowManager().getDefaultDisplay();

		int mDisplayWidth = mDisplay.getWidth();
		int mDisplayHeight = mDisplay.getHeight();

		if (mDisplayWidth > mDisplayHeight) {

			tempData = mHeight - mDisplayHeight;

			displayMetricW = mDisplayWidth;
			displayMetricH = mHeight - tempData;

		} else if (mDisplayWidth < mDisplayHeight) {

			tempData = mWidth - mDisplayHeight;
			displayMetricW = mWidth - tempData;
			displayMetricH = mHeight;

		}
		displayMetric[0] = displayMetricW;
		displayMetric[1] = displayMetricH;

		return displayMetric;

	}
}
