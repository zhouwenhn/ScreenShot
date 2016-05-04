package com.chowen.screenshot.screenshot.util;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ninegame.gamemanagerhd.R;
import cn.ninegame.gamemanagerhd.share.PlatformProxy;

/**
 * Created by zhouwen on 14-5-8.
 */
public class ScreenShotShareAdapter extends BaseAdapter {
	private ArrayList<PlatformProxy> mPlatformProxy;
	private Context mContext;

	public ScreenShotShareAdapter(Context context,
			ArrayList<PlatformProxy> proxyArrayList) {
		mPlatformProxy = proxyArrayList;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mPlatformProxy.size();
	}

	@Override
	public PlatformProxy getItem(int position) {
		return mPlatformProxy.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PlatformProxy proxy = mPlatformProxy.get(position);
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.screenshot_share_item, null);
		}

		ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		imageView.setBackgroundResource(proxy.bgRes);
		imageView.setImageResource(proxy.iconRes);
		name.setText(proxy.name);
		
		return convertView;
	}
}
