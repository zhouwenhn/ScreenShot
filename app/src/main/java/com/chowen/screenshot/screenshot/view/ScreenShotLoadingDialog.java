package com.chowen.screenshot.screenshot.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.chowen.screenshot.R;

/**
 * Created by zhouwen on 14-6-9.
 */
public class ScreenShotLoadingDialog extends Dialog {
    private static final String TAG = "DataLoadingDailog";
    private static final boolean DEBUG = false;

    private static ScreenShotLoadingDialog instance;
    private static int instanceContextHashCode = -1;

    public static ScreenShotLoadingDialog newInstance(Activity activity) {
        if(activity == null){
            return null;
        }
        if (instance == null || instanceContextHashCode != activity.hashCode()) {
            if (DEBUG) Log.d(TAG, "newInstance, instance=" + instance);
            instance = new ScreenShotLoadingDialog(activity);
            instanceContextHashCode = activity.hashCode();
        }
        return instance;
    }
    
    public static ScreenShotLoadingDialog newInstance(Context context) {
        if(context == null){
            return null;
        }
        if (instance == null || instanceContextHashCode != context.hashCode()) {
            if (DEBUG) Log.d(TAG, "newInstance, instance=" + instance);
            instance = new ScreenShotLoadingDialog(context);
            instanceContextHashCode = context.hashCode();
        }
        return instance;
    }

    private ScreenShotLoadingDialog(Activity context) {
        super(context, android.R.style.Theme_DeviceDefault_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.screenshot_loading_progress_dialog);
        setCancelable(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    
    private ScreenShotLoadingDialog(Context context) {
        super(context, android.R.style.Theme_DeviceDefault_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        setContentView(R.layout.screenshot_loading_progress_dialog);
        setCancelable(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}
