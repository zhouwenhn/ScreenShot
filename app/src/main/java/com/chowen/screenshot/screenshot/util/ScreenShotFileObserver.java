package com.chowen.screenshot.screenshot.util;

import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import java.io.File;
import cn.ninegame.gamemanagerhd.service.gamelauncher.GameLaunchMonitor;
import cn.ninegame.gamemanagerhd.service.gamelauncher.GameLauncherInfo;
import cn.ninegame.gamemanagerhd.share.screenshot.view.CropperActivity;
import cn.ninegame.gamemanagerhd.share.screenshot.view.ScreenShotLoadingDialog;
import cn.ninegame.gamemanagerhd.util.L;
import cn.ninegame.gamemanagerhd.util.PackageUtil;

/**
 * Created by zhouwen on 14-4-12.
 * 
 * @since 14-4-12 instructions 文件或者文件夹进行监听
 */
public class ScreenShotFileObserver extends FileObserver {

	private Context mContext;
	private String screenShotDirPath;
	private boolean mHasModify;
	private ScreenShotLoadingDialog mDialog;
	private long rawTime;
	private long currentTime;
	private Message showLoadingMag;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x01) {
				mDialog = ScreenShotLoadingDialog.newInstance(mContext);
				mDialog.show();
				mDialog.setCanceledOnTouchOutside(false);
				L.i("ScreenShotFileObserver", "msg.what==0x01");
			} else if (msg.what == 0x02) {
				mDialog.cancel();
				L.i("ScreenShotFileObserver", "msg.what==0x02");
			}

			super.handleMessage(msg);
		}

	};
	/**
	 * path 是所监听的文件夹或者文件名。
	 */
	public ScreenShotFileObserver(String path, Context context) {

		super(path);
		mContext = context;
		screenShotDirPath = path;
	}

	@Override
	public void onEvent(int event, String path) {
		switch (event) {
		case FileObserver.CREATE:
			mHasModify = false;
			rawTime=System.currentTimeMillis();
			
			showLoadingMag=Message.obtain();
			showLoadingMag.what=0x01;
			handler.sendMessage(showLoadingMag);

			break;
		case FileObserver.CLOSE_WRITE:
			Pair<Integer, GameLauncherInfo> gameInfo = null;
			// file close
			if (mHasModify) {
				final String namePath = screenShotDirPath + File.separator + path;
				String currentPackageName = PackageUtil.getCurrentTopActivity(mContext);
				gameInfo = GameLaunchMonitor.fetchGameInfo(currentPackageName);

				if (gameInfo != null) {
					Intent intent = new Intent(mContext, CropperActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intent.putExtra("bitmapdir", namePath);
					intent.putExtra("gameid", gameInfo.first);
					intent.putExtra("gamename", gameInfo.second.fetchGameName(mContext.getPackageManager()));
					mContext.startActivity(intent);
				}
				showLoadingMag=Message.obtain();
				showLoadingMag.what=0x02;
				handler.sendMessage(showLoadingMag);
			}
			break;
		case FileObserver.MODIFY:
			
			currentTime=System.currentTimeMillis()-rawTime;
			if (currentTime >= 15*1000) {
				mHasModify=false;
				showLoadingMag=Message.obtain();
				showLoadingMag.what=0x02;
				handler.sendMessage(showLoadingMag);
				break;
			}else {
				mHasModify = true;
			}
			break;
		}
	}
}
