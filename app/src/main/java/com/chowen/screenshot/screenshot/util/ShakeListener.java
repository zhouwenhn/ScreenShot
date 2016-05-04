package com.chowen.screenshot.screenshot.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Pair;

import cn.ninegame.gamemanagerhd.fragment.SettingHelper;
import cn.ninegame.gamemanagerhd.service.gamelauncher.GameLaunchMonitor;
import cn.ninegame.gamemanagerhd.service.gamelauncher.GameLauncherInfo;
import cn.ninegame.gamemanagerhd.util.PackageUtil;
import cn.ninegame.gamemanagerhd.util.ViewUtil;


/**
 * Created by zhouwen on 14-5-5.
 * 检测手机摇晃监听器
 */
public class ShakeListener implements SensorEventListener {
	// 速度阈值
	private int SPEED_TABLE_SHRESHOLD = 1200;
    private int SPEED_PHONE_SHRESHOLD = 2200;

    private int mMinSpeedValue = 500;
	// 两次检测的时间间隔
	private static final int UPTATE_INTERVAL_TIME = 100;
	private SensorManager sensorManager;
	private Sensor sensor;
	private OnShakeListener onShakeListener;
	private Context mContext;
	private float lastX;
	private float lastY;
	private float lastZ;
	private long lastUpdateTime;

	public ShakeListener(Context c) {
		mContext = c;
		start();
	}

	public void start() {
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (sensor != null) {
            sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_GAME);
        }

    }

	public void stop() {
		sensorManager.unregisterListener(this);
	}

	public void setOnShakeListener(OnShakeListener listener) {
		onShakeListener = listener;
	}

	public void onSensorChanged(SensorEvent event) {
        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - lastUpdateTime;
        if (timeInterval < UPTATE_INTERVAL_TIME)
            return;
        lastUpdateTime = currentUpdateTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        lastX = x;
        lastY = y;
        lastZ = z;
      
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
        int defaultThreshold =SPEED_TABLE_SHRESHOLD ;
        if(!ViewUtil.isTablet(mContext)){
            defaultThreshold = SPEED_PHONE_SHRESHOLD;
        }
        SPEED_TABLE_SHRESHOLD = SettingHelper.getSettingIntValue(SettingHelper.PREFS_KEY_SENSOR_VALUE, defaultThreshold);
        if (SPEED_TABLE_SHRESHOLD < mMinSpeedValue) {
            SPEED_TABLE_SHRESHOLD = mMinSpeedValue;
        }
		if (speed >= SPEED_TABLE_SHRESHOLD) {

            String currentPackageName = PackageUtil.getCurrentTopActivity(mContext);
			final Pair<Integer, GameLauncherInfo> gameInfo = GameLaunchMonitor.fetchGameInfo(currentPackageName);

			if (gameInfo != null) {
				boolean tryRoot = SnapShot.tryGetRoot();
				if (tryRoot) {

					onShakeListener.onShake();
					SettingHelper.setSettingValue(SettingHelper.PREFS_KEY_TRYGETROOT, true);

				} else {
					SettingHelper.setSettingValue(SettingHelper.PREFS_KEY_TRYGETROOT, false);
					stop();
				}
			}
		}
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public interface OnShakeListener {
		public void onShake();
	}

}