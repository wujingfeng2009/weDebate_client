package com.company.weDebate.ui;

import com.company.weDebate.base.BaseActivity;
import com.company.weDebate.helper.GetTokenHelper;
import com.company.weDebate.preferences.MyPreference;
import com.company.weDebate.R;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * 描述：欢迎界面
 */

public class WelcomeActivity extends BaseActivity {
	public final static String LOG_TAG = "weDebate";
	public final static int STATE = 0x01;
	private final static long stateTime = 3000l;// 欢迎页停留时间
	private MyPreference sp;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHandler != null && mHandler.hasMessages(STATE)) {
			mHandler.removeMessages(STATE);
		}
	}

	@Override
	protected void initData() {
		sp = new MyPreference(WelcomeActivity.this);
		
		GetTokenHelper instance = GetTokenHelper.getInstance();
		instance.doGetToken(this);
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
				if (sp.getPerferenceIsFirst()) {
					startActivity(new Intent(WelcomeActivity.this, GuideActivity.class));
				}else  {
					Intent intent = new Intent(WelcomeActivity.this,
							MainActivity.class);
					intent.putExtra("from", WelcomeActivity.class.getName());
					startActivity(intent);
				}
				
				WelcomeActivity.this.finish();
				break;

			default :
				break;
			}
		}
	};
}
