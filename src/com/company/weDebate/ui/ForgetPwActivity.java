package com.company.weDebate.ui;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.company.weDebate.R;
import com.company.weDebate.base.BaseActivity;
import com.company.weDebate.utils.AndroidUtils;
import com.company.weDebate.widget.CustomToast;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 忘记密码界面
 * 
 */
public class ForgetPwActivity extends BaseActivity implements OnClickListener {

	public final static String TAG = "weDebate";

	private final static int RESET_REQUESTCODE = 0;
	private final static int REQUEST_SUCCESS = 0x01;// 请求成功
	private final static int REQUEST_FAILED = 0x02;// 请求失败

	private TextView titleTv;// 标题
	private LinearLayout backLl;// 返回
	private EditText accountNameEdit;// 账号输入框
	private Button findPwBtn;// 找回密码按钮

	@Override
	protected void initData() {
	}

	@Override
	protected void initView() {
		setContentView(R.layout.forget_pw);
		titleTv = (TextView) this.findViewById(R.id.titleText);
		backLl = (LinearLayout) this.findViewById(R.id.backLl);
		accountNameEdit = (EditText) this.findViewById(R.id.accountNameEdit);
		findPwBtn = (Button) this.findViewById(R.id.findPwBtn);
	}

	@Override
	protected void setAttribute() {
		titleTv.setText(R.string.find_pw_title);
		backLl.setVisibility(View.VISIBLE);
		backLl.setOnClickListener(this);
		findPwBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.backLl:// 返回
			this.finish();
			break;
		case R.id.findPwBtn:// 找回密码按钮
			checkAccount();
			break;
		default:
			break;
		}
	}

	private void checkAccount() {
		// 账号
		String uname = accountNameEdit.getText().toString().trim();
		// 账号不能为空
		if (TextUtils.isEmpty(uname)) {
			CustomToast
			.getToast(ForgetPwActivity.this,
					getString(R.string.account_null_string),
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (!AndroidUtils.isMobileNO(uname)) {
			CustomToast.getToast(ForgetPwActivity.this,
					getString(R.string.account_error_string),
					Toast.LENGTH_SHORT).show();
			return;
		}

		findpw(uname);
	}

	/**
	 * 提交邮箱或者手机号
	 */
	private void findpw(String account) {
		showProgress(null, null);

		RequestMobileCodeCallback callback = new RequestMobileCodeCallback() {

			@Override
			public void done(AVException e) {

				dismissProgress();

				if (e==null) {
					mHandler.obtainMessage(REQUEST_SUCCESS,
							"")
							.sendToTarget();
				} else {
					mHandler.obtainMessage(REQUEST_FAILED,
							getString(R.string.register_send_sms_error))
							.sendToTarget();
				}

			}
		};

		AVService.requestPwdBySmsCode(account, callback);
	}

	/**
	 * handler用于处理网络请求的返回数据
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Object object = msg.obj;
			switch (msg.what) {
			case REQUEST_FAILED:// 请求失败
				if (!TextUtils.isEmpty(String.valueOf(object))) {
					CustomToast.getToast(ForgetPwActivity.this,
							String.valueOf(object), Toast.LENGTH_SHORT).show();
				}
				break;
			case REQUEST_SUCCESS:// 请求成功
				if (!TextUtils.isEmpty(String.valueOf(object))) {
					CustomToast.getToast(ForgetPwActivity.this,
							String.valueOf(object), Toast.LENGTH_SHORT).show();
				}
				Intent intent = new Intent(ForgetPwActivity.this,
						ResetPwActivity.class);
				intent.putExtra("account", accountNameEdit.getText().toString().trim());
				startActivityForResult(intent, RESET_REQUESTCODE);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESET_REQUESTCODE && resultCode == RESULT_OK) {
			this.finish();
		}
	}

}
