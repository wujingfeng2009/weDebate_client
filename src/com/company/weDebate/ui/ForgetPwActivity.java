package com.company.weDebate.ui;

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

import com.weetmall.weetmall.R;
import com.weetmall.weetmall.base.BaseActivity;
import com.weetmall.weetmall.bean.ForgetPwBean;
import com.weetmall.weetmall.net.HttpManager;
import com.weetmall.weetmall.net.HttpUrlManager;
import com.weetmall.weetmall.net.RequestListener;
import com.weetmall.weetmall.net.RequestParams;
import com.weetmall.weetmall.task.ForgetPwTask;
import com.weetmall.weetmall.utils.AndroidUtils;
import com.weetmall.weetmall.utils.LogUtil;
import com.weetmall.weetmall.widget.CustomToast;

/**
 * 忘记密码界面
 * 
 */
public class ForgetPwActivity extends BaseActivity implements OnClickListener {

	public final static String TAG = "com.weetmall.weetmall";
	private final static int RESET_REQUESTCODE = 0;
	private final static int REQUEST_SUCCESS = 0x01;// 请求成功
	private final static int REQUEST_FAILED = 0x02;// 请求失败

	private TextView titleTv;// 标题
	private LinearLayout backLl;// 返回
	private EditText accountNameEdit;// 账号输入框
	private Button findPwBtn;// 找回密码按钮

	private RequestParams requestParams;// 请求参数封装的键值对
	private int loginType = 0;

	@Override
	protected void initData() {
		requestParams = new RequestParams();
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

	// 判断输入的账号是否为手机号或者邮箱地址
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

		if (!AndroidUtils.isMobileNO(uname) && !AndroidUtils.isEmail(uname)) {
			CustomToast.getToast(ForgetPwActivity.this,
					getString(R.string.account_error_string),
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			if (AndroidUtils.isMobileNO(uname)) {
				loginType = 0;
			}
			if (AndroidUtils.isEmail(uname)) {
				loginType = 1;
			}
		}
		
		findpw(uname, loginType);
	}

	/**
	 * 提交邮箱或者手机号
	 */
	private void findpw(String account, int type) {
		showProgress(null, null);
		String url = "";
		ForgetPwTask task = new ForgetPwTask();
		requestParams.clear();
		switch (type) {
		case 0:// 手机号
			url = HttpUrlManager.findPwByMobileUrl();
			requestParams.add("mobile", account);
			requestParams.add("smstype", "findpwd");
			break;
		case 1:// 邮箱
			url = HttpUrlManager.findPwByEmailUrl();
			requestParams.add("email", account);
			break;
		default:
			break;
		}
		task.request(ForgetPwActivity.this, url, HttpManager.HTTPMETHOD_JSON,
				requestParams, requestListener);
	}

	/**
	 * 登录接口监听类
	 */
	private RequestListener<ForgetPwBean> requestListener = new RequestListener<ForgetPwBean>() {

		@Override
		public void OnStart() {
			LogUtil.d(TAG, "开始请求OnStart-----------");
		}

		@Override
		public void onError(Exception e) {
			dismissProgress();
			LogUtil.d(TAG, "onError-----------" + e.getMessage());
			mHandler.obtainMessage(REQUEST_FAILED, e.getMessage())
					.sendToTarget();
		}

		@Override
		public void OnPaserComplete(ForgetPwBean bean) {
			dismissProgress();
			if (bean != null) {
				LogUtil.d(TAG, "OnPaserComplete:" + bean.getMsg());
				if ("1".equals(bean.getStatus())) {
					mHandler.obtainMessage(REQUEST_SUCCESS, bean.getMsg())
							.sendToTarget();
				} else {
					mHandler.obtainMessage(REQUEST_FAILED, bean.getMsg())
							.sendToTarget();
				}
			} else {
				mHandler.obtainMessage(REQUEST_FAILED,
						getString(R.string.request_failed_string))
						.sendToTarget();
			}
		}
	};

	/**
	 * handler用于处理网络请求的返回数据
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Object object = msg.obj;
			switch (msg.what) {
			case REQUEST_FAILED:// 登录失败
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
				intent.putExtra("loginType", loginType);
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
