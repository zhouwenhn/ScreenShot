package com.chowen.screenshot.screenshot.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.util.HashMap;

import cn.ninegame.gamemanagerhd.NineGameClientApplication;
import cn.ninegame.gamemanagerhd.service.gamelauncher.GameLaunchMonitor;
import cn.ninegame.gamemanagerhd.service.gamelauncher.GameLauncherInfo;
import cn.ninegame.gamemanagerhd.share.screenshot.util.ShakeListener.OnShakeListener;
import cn.ninegame.gamemanagerhd.share.screenshot.view.CropperActivity;
import cn.ninegame.gamemanagerhd.stat.DemoStatis;
import cn.ninegame.gamemanagerhd.util.L;
import cn.ninegame.gamemanagerhd.util.PackageUtil;
import cn.ninegame.gamemanagerhd.util.RootCmdRunner;
import cn.ninegame.gamemanagerhd.util.TaskExecutor;

public class ScreenShotUtil {

	/**
	 * Created by zhouwen on 14-5-6.
	 */
	private final static boolean DEBUG = true;
	private static final String TAG = "ScreenShotUtil";
	private static ScreenShotFileObserver mObserver;
	private static ShakeListener mShakeListener = null;
	private static Vibrator mVibrator = null;
	private static SoundPool sndPool;
	private static HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();
	private static Context mContext;
	private static boolean hasRoot;

	public static void registerScreenShotListener(Context context) {

        String manufacName = android.os.Build.MANUFACTURER;
        mContext = context;
        hasRoot = RootCmdRunner.checkRootPermission();
        if (!hasRoot) {
            if (manufacName.equals("Xiaomi")) {
                mObserver = new ScreenShotFileObserver(Environment.getExternalStorageDirectory() + "/DCIM/Screenshots", mContext);
            } else {
                mObserver = new ScreenShotFileObserver(Environment.getExternalStorageDirectory() + "/Pictures/Screenshots", mContext);
            }

            mObserver.startWatching();
        } else {
            mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            loadSound();
            if(mShakeListener == null){
                mShakeListener = new ShakeListener(mContext);
            }else {
                mShakeListener.start();
            }
            mShakeListener.setOnShakeListener(new ScreenShotShakeLitener());
        }

    }

	public static void unRegisterScreenShotListener() {

		if (!hasRoot) {
            if(mObserver != null){
                mObserver.stopWatching();
            }
		} else {
            if(mShakeListener != null) {
                mShakeListener.stop();
            }
			if(mVibrator != null) {
                mVibrator.cancel();
            }
			if(sndPool != null) {
                sndPool.release();
            }
            if(soundPoolMap != null){
                soundPoolMap.clear();
            }
		}
	}

	private static void loadSound() {

        sndPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);

        try {
            long start = System.currentTimeMillis();
            soundPoolMap.put(0, sndPool.load(mContext.getAssets().openFd("sound/shake_sound_male.mp3"), 1));
            soundPoolMap.put(1, sndPool.load(mContext.getAssets().openFd("sound/shake_match.mp3"), 1));
            if(DEBUG){
                Log.d(TAG, "load sound file take time :" + (System.currentTimeMillis() - start));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static void startVibrato() { 
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
	}

	private static class ScreenShotShakeLitener implements OnShakeListener {

		@Override
		public void onShake() {
			
			mShakeListener.stop();
			String currentPackageName = PackageUtil.getCurrentTopActivity(mContext);
			
			DemoStatis.addStat("btn_shake```");

			final Pair<Integer, GameLauncherInfo> gameInfo = GameLaunchMonitor.fetchGameInfo(currentPackageName);

			if (gameInfo != null) {

				final int gameId = gameInfo.first;
				startVibrato();
				if (sndPool != null && soundPoolMap != null && soundPoolMap.size() > 1) {
					sndPool.play(soundPoolMap.get(0), 1f, 1f, 0, 0, 1.2f);
				}
				new Handler().postDelayed(new Runnable() {
					public void run() {
						mVibrator.cancel();
					}
				}, 2000);

				TaskExecutor.executeTask(new Runnable() {
					@Override
					public void run() {
						
						Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
						boolean hasScreenShot = SnapShot.ScreenShotBitmapFromSnapShot();
						if (hasScreenShot) {
							
							String onShake = "shake";
							Intent intent = new Intent(mContext,CropperActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							intent.putExtra("bitmapdir", onShake);
							intent.putExtra("gameid", gameId);
							intent.putExtra("gamename",gameInfo.second.fetchGameName(mContext.getPackageManager()));
							mContext.startActivity(intent);
							
							if (sndPool != null && soundPoolMap != null && soundPoolMap.size() > 1) {
								sndPool.play(soundPoolMap.get(1), 1f, 1f, 0, 0, 1.0f);
							}
							
						} else {
							
							NineGameClientApplication.getInstance().showToastMessage("截图出错，请再试一次");
						}
						mShakeListener.start();
					}
				});
			} else {
				mShakeListener.start();
			}
			if (DEBUG) {
				L.i(TAG, "currentPackageName=" + currentPackageName + "gameInfo:" + gameInfo);
			}
        }

	}

}
