package com.company.weDebate.base;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVUser;
import com.company.weDebate.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Activity基类，用于统一各个界面的弹出框等
 */
public abstract class BaseActivity extends Activity {
	public boolean isRun = false;// 用于处理viewpager循环播放
	public boolean isDown = false;// 用于处理viewpager循环播放
	private Dialog mProgressDialog;

	private String userId;

	public final static String INTO_CART_FRAGMENT = "INTO_CART_FRAGMENT";
	private MyBroadCastReceiver receiver;

	private class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent != null && INTO_CART_FRAGMENT.equals(intent.getAction())) {
				BaseActivity.this.finish();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		receiver = new MyBroadCastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(INTO_CART_FRAGMENT);
		registerReceiver(receiver, filter);

		AVUser currentUser = AVUser.getCurrentUser();
		if (currentUser != null) {
			userId = currentUser.getObjectId();
		}

		initData();
		initView();
		setAttribute();
	}

	// 初始化数据
	protected abstract void initData();

	// 初始化UI控件
	protected abstract void initView();

	// 初始化UI控件
	protected abstract void setAttribute();

	public String getUserId() {
		return userId;
	}

	protected void onPause() {
		super.onPause();
		AVAnalytics.onPause(this);
	}

	protected void onResume() {
		super.onResume();
		AVAnalytics.onResume(this);
	}

	/**
	 * 显示等待框
	 * 
	 * @param title
	 * @param message
	 */
	public void showProgress(String title, String message) {
		try {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				return;
			}
			mProgressDialog = new Dialog(this, R.style.CustomDialog);
			mProgressDialog.setContentView(R.layout.dialog_progress);
			TextView textView = (TextView) mProgressDialog
					.findViewById(R.id.progress_msg);
			textView.setText(message);
			mProgressDialog.show();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消等待框
	 */
	public void dismissProgress() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}
}
