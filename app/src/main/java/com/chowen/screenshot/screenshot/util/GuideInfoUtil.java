package com.chowen.screenshot.screenshot.util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import cn.ninegame.gamemanagerhd.R;
import cn.ninegame.gamemanagerhd.share.screenshot.view.GuideInfoDialog;
import cn.ninegame.gamemanagerhd.util.L;

/**
 * Created by zhouwen on 14-5-11.
 */
public class GuideInfoUtil {

	public static void initGuide(final Context context) {

		SharedPreferences 	sharedPreferences = context.getSharedPreferences("screenshotfirst",Context.MODE_MULTI_PROCESS);
		boolean screenShotIsFirst = sharedPreferences.getBoolean("screenshotIsfirst",false);
		L.i("GuideInfoUtil", "screenShotIsFirst1=" + screenShotIsFirst);
		if (!screenShotIsFirst) {
			new Handler().postDelayed(new Runnable() {
				public void run() {

					Dialog dialog = new GuideInfoDialog(context,R.style.guideDialog);
					dialog.show();
					dialog.setCanceledOnTouchOutside(false);

				}
			}, 6 * 1000);
		}
	}

}
