package com.company.weDebate.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.graphics.Color;
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
import com.weetmall.weetmall.utils.LogUtil;
import com.weetmall.weetmall.widget.CustomToast;

/**
 * 重置密码界面
 * 
 */
public class ResetPwActivity extends BaseActivity implements OnClickListener {

	public final static String LOG_TAG = "com.weetmall.weetmall";
	private final static int REQUEST_SUCCESS = 0x01;// 请求成功
	private final static int REQUEST_FAILED = 0x02;// 请求失败
	public final static int RESET_PW_SUCESS = 0x03;// 重置密码成功
	public final static int RESET_PW_FAILED = 0x04;// 重置密码失败

	private TextView titleTv;// 标题
	private LinearLayout backLl;// 返回
	private EditText codeEt;// 验证码输入框
	private EditText newPwEt;// 新密码输入框
	private EditText newPwAgainEt;// 确认密码输入框
	private Button getCodeBtn;// 获取验证码按钮
	private Button completeBtn;// 完成按钮

	private RequestParams requestParams;// 请求参数封装的键值对

	private ScheduledExecutorService executorService;// 计时器任务
	private int totalTime = 60;// 获取验证码倒计时总时间
	private final static int TIME_REMAIN = 0;// 倒计时正在进行
	private final static int TIME_UP = 1;// 倒计时结束

	private String accountStr = "";// 账号
	private int loginType = 0;// 0-手机号；1-邮箱

	@Override
	protected void initData() {
		requestParams = new RequestParams();
		Intent intent = getIntent();
		if (intent != null) {
			accountStr = intent.getStringExtra("account");
			loginType = intent.getIntExtra("loginType", 0);
		}
	}

	@Override
	protected void initView() {
		setContentView(R.layout.reset_pw);
		titleTv = (TextView) this.findViewById(R.id.titleText);
		backLl = (LinearLayout) this.findViewById(R.id.backLl);
		codeEt = (EditText) this.findViewById(R.id.codeEt);
		newPwEt = (EditText) this.findViewById(R.id.newPwEt);
		newPwAgainEt = (EditText) this.findViewById(R.id.newPwAgainEt);
		getCodeBtn = (Button) this.findViewById(R.id.getCodeBtn);
		completeBtn = (Button) this.findViewById(R.id.completeBtn);
	}

	@Override
	protected void setAttribute() {
		titleTv.setText(R.string.find_pw_title);
		backLl.setVisibility(View.VISIBLE);
		backLl.setOnClickListener(this);
		getCodeBtn.setOnClickListener(this);
		completeBtn.setOnClickListener(this);

		getCodeSuccess();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.backLl:// 返回
			this.finish();
			break;
		case R.id.getCodeBtn:// 重新获取验证码按钮
			reGetCode(accountStr, loginType);
			break;
		case R.id.completeBtn:// 完成按钮
			resetPassword();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取验证码成功，开启倒计时 <br/>
	 * 
	 * @param [参数1]-[参数1说明] <br/>
	 * @param [参数2]-[参数2说明] <br/>
	 */
	private void getCodeSuccess() {
		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleWithFixedDelay(new CodeTimeTask(), 1, 1,
				TimeUnit.SECONDS);
		getCodeBtn.setTextColor(Color.parseColor("#999999"));
		getCodeBtn.setBackgroundResource(R.drawable.forget_pw_et_bg);
		getCodeBtn.setTextSize(13);
		getCodeBtn.setEnabled(false);
	}

	// 获取验证码倒计时Task
	private class CodeTimeTask implements Runnable {

		@Override
		public void run() {
			totalTime--;
			// 更新重发时间
			if (totalTime > 0) {
				timeHandler.obtainMessage(TIME_REMAIN).sendToTarget();
			} else {
				timeHandler.obtainMessage(TIME_UP).sendToTarget();
			}
		}

	}

	// 发送验证码任务回调
	private Handler timeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case TIME_REMAIN:
				getCodeBtn.setText(String.format(
						getString(R.string.count_down_re_verify), totalTime));
				break;
			case TIME_UP:
				// 停止记时
				resetTime();
				break;
			default:
				break;
			}
		}

	};

	// 停止计时
	private void resetTime() {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
		}
		getCodeBtn.setEnabled(true);
		getCodeBtn.setText("重新获取验证码");
		getCodeBtn.setBackgroundResource(R.drawable.login_button_bg);
		getCodeBtn.setTextSize(15);
		getCodeBtn.setTextColor(getResources().getColor(R.color.white));
		totalTime = 60;
	}

	/**
	 * 重新获取验证码
	 */
	private void reGetCode(String account, int type) {
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
		task.request(ResetPwActivity.this, url, HttpManager.HTTPMETHOD_JSON,
				requestParams, requestListener);
	}

	/**
	 * 重新获验证码接口监听类
	 */
	private RequestListener<ForgetPwBean> requestListener = new RequestListener<ForgetPwBean>() {

		@Override
		public void OnStart() {
			LogUtil.d(LOG_TAG, "开始请求OnStart-----------");
		}

		@Override
		public void onError(Exception e) {
			dismissProgress();
			LogUtil.d(LOG_TAG, "onError-----------" + e.getMessage());
			mHandler.obtainMessage(REQUEST_FAILED, e.getMessage())
					.sendToTarget();
		}

		@Override
		public void OnPaserComplete(ForgetPwBean bean) {
			dismissProgress();
			if (bean != null) {
				LogUtil.d(LOG_TAG, "OnPaserComplete:" + bean.getMsg());
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
			case REQUEST_SUCCESS:// 获取验证码成功
				CustomToast.getToast(ResetPwActivity.this,
						String.valueOf(object), Toast.LENGTH_SHORT).show();
				getCodeSuccess();
				break;
			case RESET_PW_FAILED:// 重置密码失败
			case REQUEST_FAILED:// 获取验证码失败
				CustomToast.getToast(ResetPwActivity.this,
						String.valueOf(object), Toast.LENGTH_SHORT).show();
				break;
			case RESET_PW_SUCESS:// 重置密码成功
				setResult(RESULT_OK);
				ResetPwActivity.this.finish();
				break;
			default:
				break;
			}
		}

	};

	// 重置密码
	private void resetPassword() {
		// 判断短信验证码是否合法
		String codeStr = codeEt.getText().toString().trim();
		// 验证码不能为空
		if (TextUtils.isEmpty(codeStr)) {
			CustomToast.getToast(ResetPwActivity.this,
					getString(R.string.sms_null_string), Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// 密码
		String password = newPwEt.getText().toString().trim();
		// 确认密码
		String confirmPw = newPwAgainEt.getText().toString().trim();
		// 密码为空
		if (TextUtils.isEmpty(password)) {
			CustomToast.getToast(ResetPwActivity.this,
					getString(R.string.password_null_string),
					Toast.LENGTH_SHORT).show();
			return;
		}

		// 密码不足6位
		if (password.length() < 6) {
			CustomToast.getToast(ResetPwActivity.this,
					getString(R.string.password_error_string),
					Toast.LENGTH_SHORT).show();
			return;
		}

		// 确认密码为空
		if (TextUtils.isEmpty(confirmPw)) {
			CustomToast.getToast(ResetPwActivity.this,
					getString(R.string.confirmPw_null_string),
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 两次输入的密码不同
		if (!confirmPw.equals(password)) {
			CustomToast.getToast(ResetPwActivity.this,
					getString(R.string.confirmPw_error_string),
					Toast.LENGTH_SHORT).show();
			return;
		}

		requestResetPw(codeStr, password);
	}

	// 联网请求重置密码
	private void requestResetPw(String code, String pw) {
		showProgress(null, null);
		String url = "";

		ForgetPwTask task = new ForgetPwTask();
		requestParams.clear();
		switch (loginType) {
		case 0:// 手机号
			url = HttpUrlManager.resetPwByMobileUrl();
			requestParams.add("mobile", accountStr);
			break;
		case 1:// 邮箱
			url = HttpUrlManager.resetPwByEmailUrl();
			requestParams.add("email", accountStr);
			break;
		default:
			break;
		}
		requestParams.add("code", code);
		requestParams.add("password", pw);
		requestParams.add("repwd", pw);
		task.request(ResetPwActivity.this, url, HttpManager.HTTPMETHOD_JSON,
				requestParams, resetListener);
	}

	/**
	 * 重新获验证码接口监听类
	 */
	private RequestListener<ForgetPwBean> resetListener = new RequestListener<ForgetPwBean>() {

		@Override
		public void OnStart() {
			LogUtil.d(LOG_TAG, "开始请求OnStart-----------");
		}

		@Override
		public void onError(Exception e) {
			dismissProgress();
			LogUtil.d(LOG_TAG, "onError-----------" + e.getMessage());
			mHandler.obtainMessage(RESET_PW_FAILED, e.getMessage())
					.sendToTarget();
		}

		@Override
		public void OnPaserComplete(ForgetPwBean bean) {
			dismissProgress();
			if (bean != null) {
				LogUtil.d(LOG_TAG, "OnPaserComplete:" + bean.getMsg());
				if ("1".equals(bean.getStatus())) {
					mHandler.obtainMessage(RESET_PW_SUCESS, bean.getMsg())
							.sendToTarget();
				} else {
					mHandler.obtainMessage(RESET_PW_FAILED, bean.getMsg())
							.sendToTarget();
				}
			} else {
				mHandler.obtainMessage(RESET_PW_FAILED,
						getString(R.string.request_failed_string))
						.sendToTarget();
			}
		}
	};
}
