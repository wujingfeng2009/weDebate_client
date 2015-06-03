package com.company.weDebate.ui;

import com.company.weDebate.R;
import com.company.weDebate.base.BaseActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * 描述：欢迎界面
 */

public class WelcomeActivity extends BaseActivity {
	public final static String LOG_TAG = "pg_600";
	public final static int STATE = 0x03;
	private final static long stateTime = 2000l;// 欢迎页停留时间

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHandler != null && mHandler.hasMessages(STATE)) {
			mHandler.removeMessages(STATE);
		}
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void initView() {
		setContentView(R.layout.welcome);
	}

	@Override
	protected void setAttribute() {
		mHandler.sendEmptyMessageDelayed(STATE, stateTime);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case STATE :
					Intent intent = new Intent(WelcomeActivity.this,
							MainActivity.class);
					intent.putExtra("from", WelcomeActivity.class.getName());
					startActivity(intent);
					WelcomeActivity.this.finish();
					break;
				
				default :
					break;
			}
		}
	};
}
