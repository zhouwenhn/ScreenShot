package com.chowen.screenshot.screenshot.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.chowen.screenshot.R;
import com.chowen.screenshot.screenshot.util.BitmapCompUtil;
import com.chowen.screenshot.screenshot.util.ScreenShotShareAdapter;
import com.chowen.screenshot.screenshot.util.SnapShot;


/**
 * Created by zhouwen on 14-5-4.
 */
public class ScreenShotShareDialog extends Dialog implements
		ShareCallback<ShareParameter> {

	private int mGameId;
	private String mGameName;
	private GridView mShareGridView;
	private Bitmap mBitmap;
	private ImageView shareImg;
	private Activity mActivity;
	private AbstractPlatform mPlatform;
	private String bitmapPath;

	public ScreenShotShareDialog(Activity context, int theme, int gameId,
			String gameName, Bitmap bitmap) {
		super(context, theme);
		this.mGameId = gameId;
		this.mGameName = gameName;
		this.mBitmap = bitmap;
		mActivity = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (!ViewUtil.isTablet(mActivity)&& !ViewUtil.isPortraitScreen(mActivity)) {
			this.setContentView(R.layout.small_screenshot_share_dialog);
		} else {
			this.setContentView(R.layout.screenshot_share_dialog);
		}

		init();

		TaskExecutor.executeTask(new Runnable() {
			@Override
			public void run() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				bitmapPath = BitmapCompUtil.saveBitmap2File(mBitmap, SnapShot.COMPRESS_PATH_NAME, 92, false);
				
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void init() {

		shareImg = (ImageView) findViewById(R.id.share_img);
		shareImg.setImageBitmap(mBitmap);

		mShareGridView = (GridView) findViewById(R.id.share_gridView);
		mShareGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mShareGridView.setAdapter(new ScreenShotShareAdapter(getContext(),initPlatforms()));
		mShareGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String shareUrl = ShareUtil.getGameShareUrl(mGameId);
						String shareContent = String.format(getContext().getString(R.string.share_game_content), mGameName, shareUrl);

						ShareParameter shareParameter = new ShareParameter();
						shareParameter.setGameId(String.valueOf(mGameId));
						shareParameter.setSharePic(mBitmap);
						shareParameter.setImagePath(bitmapPath);
						shareParameter.setGameName(mGameName);
						shareParameter.setShareText(shareContent);
						shareParameter.setDownloadUrl(shareUrl);

						PlatformProxy proxy = ((ScreenShotShareAdapter) parent.getAdapter()).getItem(position);
						mPlatform = proxy.platformFactory(mActivity, proxy.type, mGameId);
						shareParameter.setStatAction(proxy.statAction);
                        shareImg.setImageBitmap(null);
						mPlatform.doShare(shareParameter, ScreenShotShareDialog.this);
						dismiss();

					}
				});
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (mPlatform != null && mPlatform.isValid() && mPlatform.isDirectShare()) {
			mActivity.finish();
		}
	}

	private ArrayList<PlatformProxy> initPlatforms() {

		String[] platList = getContext().getResources().getStringArray(R.array.platform_list);

		int[] iconRes = new int[] { R.drawable.share_icon_wechat,
				R.drawable.share_icon_circle, R.drawable.share_icon_qq,
				R.drawable.share_icon_qzone, R.drawable.share_icon_weibo,
				R.drawable.share_icon_txweibo, R.drawable.share_icon_renren,
				R.drawable.share_icon_more };

		int[] type = new int[] { PlatformProxy.WECHAT_SESSION,
				PlatformProxy.WECHAT_TIMELINE, PlatformProxy.QQ_FRIEND,
				PlatformProxy.QQ_ZONE, PlatformProxy.SINA_WEIBO,
				PlatformProxy.QQ_WEIBO, PlatformProxy.REN_REN,
				PlatformProxy.MORE };

		ArrayList<PlatformProxy> platforms = new ArrayList<PlatformProxy>();
		for (int i = 0; i < platList.length; i++) {
			PlatformProxy proxy = new PlatformProxy();
			proxy.name = platList[i];
			proxy.iconRes = iconRes[i];
			proxy.type = type[i];
			platforms.add(proxy);
		}

		return platforms;
	}

	@Override
	public void onComplete(ShareParameter parameter) {
		ShareEditDialog editDialog = new ShareEditDialog(mActivity, parameter,mPlatform);
		editDialog.show();
	}

	@Override
	public void onFailed(ShareParameter parameter) {

	}

}
