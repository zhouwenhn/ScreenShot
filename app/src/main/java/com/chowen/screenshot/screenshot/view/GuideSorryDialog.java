package com.chowen.screenshot.screenshot.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.chowen.screenshot.R;

/**
 * Created by zhouwen on 14-5-11.
 */
public class GuideSorryDialog extends Dialog implements
		View.OnClickListener {

	private Context context;
	private Button understandBtn;
	private ImageView closeBnt;

	public GuideSorryDialog(Context context) {
		super(context);
		this.context = context;
	}

	public GuideSorryDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        if (!ViewUtil.isTablet(context) && !ViewUtil.isPortraitScreen(context)) {
			this.setContentView(R.layout.small_screenshot_srroyinfo_dialog);
		}else {
			this.setContentView(R.layout.screenshot_srroyinfo_dialog);
		}
		
        initViews();
	}

	private void initViews() {

		understandBtn = (Button) findViewById(R.id.dialog_button_understand);
		closeBnt = (ImageView) findViewById(R.id.dialog_top_close_iv);
		understandBtn.setOnClickListener(this);
		closeBnt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {		
		case R.id.dialog_button_understand:			
		case R.id.dialog_top_close_iv:
			dismiss();
			break;
		}
	}

}
