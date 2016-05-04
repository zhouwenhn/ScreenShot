package com.chowen.screenshot.screenshot.view;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chowen.screenshot.R;

/**
 * Created by zhouwen on 14-5-11.
 */
public class GuideInfoDialog extends Dialog implements View.OnClickListener {

	private Context context;
	private Button cancelBtn;
	private ImageView infoTopCloseBnt;
	private SharedPreferences sharedPreferences;

	public GuideInfoDialog(Context context) {
		super(context);
		this.context = context;
	}

	public GuideInfoDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		sharedPreferences = context.getSharedPreferences("screenshotfirst",Context.MODE_MULTI_PROCESS);
		sharedPreferences.edit().putBoolean("screenshotIsfirst", true).commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		
		if (!ViewUtil.isTablet(context) && !ViewUtil.isPortraitScreen(context)) {
			this.setContentView(R.layout.small_screenshot_info_dialog);
		}else {
			this.setContentView(R.layout.screenshot_info_dialog);
		}

		initViews();

	}

	private void initViews() {

		cancelBtn = (Button) findViewById(R.id.dialog_button_try);
		cancelBtn.setOnClickListener(this);
		infoTopCloseBnt = (ImageView) findViewById(R.id.dialog_top_close);
		infoTopCloseBnt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.dialog_button_try:
			
			boolean hasRoot = RootCmdRunner.checkRootPermission();
			if (!hasRoot) {
				initSorryDialog();
			} else {
				
				String currentPackageName = PackageUtil.getCurrentTopActivity(context);
				String[] gameInfo = PackageUtil.queryGameInfoWithPackageName(context, currentPackageName);

				if (gameInfo != null && gameInfo.length > 0 && TextUtils.isDigitsOnly(gameInfo[0])) {

					int gameId = Integer.valueOf(gameInfo[0]);
					if (gameId > 0) {

						View toastRoot = LayoutInflater.from(context).inflate(R.layout.screenshot_info_toast, null);
						Toast toast = new Toast(context.getApplicationContext());
						toast.setView(toastRoot);
						toast.setGravity(Gravity.CENTER, 0, 0);
						TextView tv = (TextView) toastRoot.findViewById(R.id.tv);
						tv.setText(R.string.shake_text_toast);
						toast.show();
					}

				}

			}
			dismiss();
			break;
		case R.id.dialog_top_close:
			dismiss();
			break;
		}

	}

	private void initSorryDialog() {

		Dialog srroyDialog = new GuideSorryDialog(context, R.style.guideDialog);
		srroyDialog.show();
		srroyDialog.setCanceledOnTouchOutside(false);
		sharedPreferences.edit().putBoolean("screenshotIsfirst", true).commit();
	}
}
