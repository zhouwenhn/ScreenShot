package com.chowen.screenshot.screenshot.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chowen.screenshot.R;
import com.chowen.screenshot.screenshot.cropper.widget.CropImageView;
import com.chowen.screenshot.screenshot.util.ScreenShotUtil;
import com.chowen.screenshot.screenshot.util.SnapShot;
import com.chowen.screenshot.screenshot.util.WaterMarkUtil;


/**
 * Created by zhouwen on 14-4-15.
 */
public class CropperActivity extends Activity implements OnClickListener {

	// Static final constants
	private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
	private Bitmap screenShotSrcBitmap = null;
	private CropImageView cropImageView = null;
	private String dirs = null;
	private LinearLayout shareEdit = null;
	private ImageView confirmShare;
	private ImageView rotateScreen;
	private ImageView cancelShareBnt;
	private ImageView setSensorValue;
	private ScreenShotLoadingDialog mDialog;
	private int gameId;
	private String gameName = null;
	private ScreenShotShareDialog shareDialog;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			handleWidget();
		}
	};

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.screenshot_cropper);

		if (ViewUtil.isPortraitScreen(this)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}

		Intent intent = getIntent();
		dirs = intent.getStringExtra("bitmapdir");
		gameId = intent.getIntExtra("gameid", 0);
		gameName = intent.getStringExtra("gamename");
		
		initWidget();
		setBtnListener();

	}

	private void setBtnListener() {

		confirmShare.setOnClickListener(this);
		rotateScreen.setOnClickListener(this);
		cancelShareBnt.setOnClickListener(this);
		setSensorValue.setOnClickListener(this);
	}
	
	private void handleWidget() {
		if (screenShotSrcBitmap != null) {
			cropImageView.setImageBitmap(screenShotSrcBitmap);
			this.getWindow().setBackgroundDrawableResource(android.R.color.black);
			shareEdit.setVisibility(View.VISIBLE);
		} else {
			Toast.makeText(CropperActivity.this, "截图出错，请再试一次",Toast.LENGTH_SHORT).show();
			finish();
		}
		if (dirs.equals("shake")) {
			mDialog.cancel();
		}
	}
	
	private void initWidget() {
		
		shareEdit = (LinearLayout) findViewById(R.id.share_edit);
		confirmShare = (ImageView) findViewById(R.id.confirm_share);
		rotateScreen = (ImageView) findViewById(R.id.rotate_screen);
		cancelShareBnt = (ImageView) findViewById(R.id.cancel_share);
		setSensorValue = (ImageView) findViewById(R.id.setting_sensor_value);
		boolean hasRoot = RootCmdRunner.checkRootPermission();
		if (!hasRoot) {
			setSensorValue.setVisibility(View.GONE);
		}

		// Initialize components of the app
		cropImageView = (CropImageView) findViewById(R.id.CropImageView);

		LayoutParams para = cropImageView.getLayoutParams();
		Display mDisplay = getWindowManager().getDefaultDisplay();
		para.height = mDisplay.getHeight();
		para.width = mDisplay.getWidth();
		cropImageView.setLayoutParams(para);
		 
		if (dirs.equals("shake")) {
			
			mDialog = ScreenShotLoadingDialog.newInstance(CropperActivity.this);
			mDialog.show();
			mDialog.setCanceledOnTouchOutside(false);
			screenShotSrcBitmap = SnapShot.getBitmapSnapShot(CropperActivity.this);
			handleWidget();
			
		}else {
		
			TaskExecutor.executeTask(new Runnable() {
				@Override
				public void run() {
						int count=0;
						while (screenShotSrcBitmap == null) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								L.e("CropperActivity", "InterruptedException - if interrupt() was called for this Thread while it was sleeping");
								e.printStackTrace();
							}
						screenShotSrcBitmap = BitmapFactory.decodeFile(dirs);
						count++;
						if (count == 150 && screenShotSrcBitmap == null) {
							Message message = Message.obtain();
							handler.sendMessage(message);
							break;
						}
						}
					Message message = Message.obtain();
					handler.sendMessage(message);
				}
			});
		
		}
		// Sets initial aspect ratio to 10/10, for demonstration purposes
		cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES,DEFAULT_ASPECT_RATIO_VALUES);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (screenShotSrcBitmap != null) {
			screenShotSrcBitmap.recycle();
			screenShotSrcBitmap = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 新浪微博sso授权回调
		SinaPlatform.onActivityResultCallback(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {

		if (v == confirmShare) {
			showShareDialog();
		} else if (v == rotateScreen) {
			cropImageView.rotateImage(180);
		} else if (v == cancelShareBnt) {
            DemoStatis.addStat("btn_screenshotclose```");
            if(!SettingHelper.containsKey(SettingHelper.PREFS_KEY_SCREEN_CLOSE_FIRST)){
                SettingHelper.setSettingValue(SettingHelper.PREFS_KEY_SCREEN_CLOSE_FIRST,true);
                showAlertCloseDialog();
            }else {
                finish();
            }
		} else if (v == setSensorValue) {
			new AdjustSenorValueDialog(CropperActivity.this).show();
		}

	}

    @Override
    public void onBackPressed() {
        DemoStatis.addStat("btn_screenshotback```");
        super.onBackPressed();
    }


    private void showAlertCloseDialog(){
        MessageBox box = new MessageBox(this);
        box.setTitle("温馨提示")
           .setMessage("若不需要截图功能请选择关闭")
           .setOnMessageBoxButtonClickedListener(new MessageBox.OnMessageBoxButtonClickedListener() {
               @Override
               public void onButton1Click(MessageBox messageBox) {
                   messageBox.dismissDialog();
                   finish();
               }

               @Override
               public void onButton2Click(MessageBox messageBox) {
                   ScreenShotUtil.unRegisterScreenShotListener();
                   SettingHelper.setSettingValue(SettingHelper.PREFS_KEY_SCREEN_SHOT,false);
                   messageBox.dismissDialog();
                   finish();
               }
           });
        box.setButton2("不需要,关闭");
        box.setButton2Background(R.drawable.btn_bg_default_large_selector);
        box.setButton2TextColor(getResources().getColor(R.color.btn_text_color_inverse));
        box.show(true);
    }



    private void showShareDialog() {
		Bitmap croppedBitmap = cropImageView.getCroppedImage();
		Bitmap waterMarkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
		Bitmap waterMarkBtp = WaterMarkUtil.createWaterMarkBitmap(croppedBitmap, waterMarkBitmap);

		shareDialog = new ScreenShotShareDialog(CropperActivity.this, R.style.guideDialog, gameId, gameName, waterMarkBtp);
		shareDialog.show();
		shareDialog.setCanceledOnTouchOutside(false);
		if (shareDialog.isShowing()) {
			shareEdit.setVisibility(View.GONE);
			cropImageView.ControlHiddenCropView(true);
		}
		shareDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				
				shareEdit.setVisibility(View.VISIBLE);
				cropImageView.ControlHiddenCropView(false);
				
			}
		});
        if(screenShotSrcBitmap != croppedBitmap && !croppedBitmap.isRecycled()){
            croppedBitmap.recycle();
        }
        if(!waterMarkBitmap.isRecycled()) {
            waterMarkBitmap.recycle();
        }
	}

}
