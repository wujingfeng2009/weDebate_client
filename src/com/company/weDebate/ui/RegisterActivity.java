package com.company.weDebate.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weetmall.weetmall.R;
import com.weetmall.weetmall.base.BaseActivity;
import com.weetmall.weetmall.bean.BaseBean;
import com.weetmall.weetmall.bean.LoginBean;
import com.weetmall.weetmall.net.HttpManager;
import com.weetmall.weetmall.net.HttpUrlManager;
import com.weetmall.weetmall.net.RequestListener;
import com.weetmall.weetmall.net.RequestParams;
import com.weetmall.weetmall.preferences.LoginPreference;
import com.weetmall.weetmall.task.GetSmsCodeTask;
import com.weetmall.weetmall.task.LoginTask;
import com.weetmall.weetmall.task.RegisterTask;
import com.weetmall.weetmall.utils.AndroidUtils;
import com.weetmall.weetmall.utils.LogUtil;
import com.weetmall.weetmall.widget.CustomToast;

/**
 * 描述：注册界面
 */

public class RegisterActivity extends BaseActivity implements OnClickListener {
	public final static String LOG_TAG = "com.weetmall.weetmall";
	public final static int GET_SMS_SUCESS = 0x01;// 获取短信验证码成功
	public final static int GET_SMS_FAILED = 0x02;// 获取短信验证码失败
	public final static int REGISTER_SUCESS = 0x03;// 注册成功
	public final static int REGISTER_FAILED = 0x04;// 注册失败
	public final static int LOGIN_SUCESS = 0x05;// 登录成功
	public final static int LOGIN_FAILED = 0x06;// 登录成功
	private RequestParams requestParams;
	public final static int PHONE_REGISTER = 1;// 手机注册
	public final static int EMAIL_REGISTER = 2;// 邮箱注册
	private int registerType = 1;
	private TextView titleTextView;// 标题
	private LinearLayout backLl;// 返回
	private RelativeLayout registerTypeRl;// 选中注册方式
	private TextView registerTypeText;// 注册方式（手机/邮箱）
	private TextView phoneTextString;// 手机号/邮箱地址
	private EditText phoneNumEdit;// 手机号输入框
	private EditText emailEdit;// 邮箱地址输入框
	private LinearLayout phoneCodeLl;// 手机验证码选项
	private EditText phoneCodeEdit;// 手机验证码输入框
	private Button getPhoneCodeBtn;// 获取手机验证码按钮
	private TextView nickTextString;// 昵称/用户名
	private EditText nickNameEdit;// 昵称/用户名输入框
	private EditText passwordEdit;// 密码
	private EditText confirmPasswordEdit;// 确认密码
	private EditText codeEdit;// 验证码
	private ImageView codeimage;// 验证码图片
	private TextView phoneCodeLine;
	private Button registerBtn;// 注册按钮
	private LoginPreference loginPreference;
	private boolean isGetSmsNow = false;// 当前是否正在获取短信验证码
	private boolean isRegisterNow = false;// 当前是否正在注册
	private String userName = "";
	private int flag = 0;// 当前为注册界面，还是完善资料界面
	private String openId = "";// 第三方授权返回的openid

	@Override
	protected void initData() {
		loginPreference = new LoginPreference(this);
		requestParams = new RequestParams();
		Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			flag = intent.getIntExtra("flag", 0);
			openId = intent.getStringExtra("openid");
		}
	}

	@Override
	protected void initView() {
		setContentView(R.layout.register);
		titleTextView = (TextView) this.findViewById(R.id.titleText);
		backLl = (LinearLayout) this.findViewById(R.id.backLl);
		registerTypeRl = (RelativeLayout) this
				.findViewById(R.id.registerTypeRl);
		registerTypeText = (TextView) this.findViewById(R.id.registerTypeText);
		phoneTextString = (TextView) this.findViewById(R.id.phoneTextString);
		phoneNumEdit = (EditText) this.findViewById(R.id.phoneNumEdit);
		phoneCodeLl = (LinearLayout) this.findViewById(R.id.phoneCodeLl);
		phoneCodeEdit = (EditText) this.findViewById(R.id.phoneCodeEdit);
		getPhoneCodeBtn = (Button) this.findViewById(R.id.getPhoneCodeBtn);
		nickTextString = (TextView) this.findViewById(R.id.nickTextString);
		nickNameEdit = (EditText) this.findViewById(R.id.nickNameEdit);
		passwordEdit = (EditText) this.findViewById(R.id.passwordEdit);
		confirmPasswordEdit = (EditText) this
				.findViewById(R.id.confirmPasswordEdit);
		codeEdit = (EditText) this.findViewById(R.id.codeEdit);
		codeimage = (ImageView) this.findViewById(R.id.codeimage);
		emailEdit = (EditText) this.findViewById(R.id.emailEdit);
		phoneCodeLine = (TextView) this.findViewById(R.id.phoneCodeLine);
		registerBtn = (Button) this.findViewById(R.id.registerBtn);
	}

	@Override
	protected void setAttribute() {
		if (flag == 0) {
			titleTextView.setText(R.string.register_title);
		} else {
			titleTextView.setText(R.string.complete_info_string);
			registerBtn.setText(R.string.commit_string);
		}
		backLl.setVisibility(View.VISIBLE);
		backLl.setOnClickListener(this);
		registerTypeRl.setOnClickListener(this);
		getPhoneCodeBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backLl:// 返回
			this.finish();
			break;
		case R.id.registerTypeRl:// 切换注册方式
			switch (registerType) {
			case PHONE_REGISTER:
				selectRegisterType(EMAIL_REGISTER);
				break;
			case EMAIL_REGISTER:
				selectRegisterType(PHONE_REGISTER);
				break;
			default:
				break;
			}
			break;
		case R.id.getPhoneCodeBtn:// 获取手机验证码
			getSmsCode(phoneNumEdit.getText().toString().trim());
			break;
		case R.id.registerBtn:// 注册
			registerInfoComplete();
			break;
		default:
			break;
		}
	}

	// 选择注册方式
	private void selectRegisterType(int type) {
		switch (type) {
		case PHONE_REGISTER:// 手机注册
			registerTypeText.setText(R.string.register_phone_string);
			phoneTextString.setText(R.string.phonenum_string);
			emailEdit.setVisibility(View.GONE);
			phoneNumEdit.setVisibility(View.VISIBLE);
			phoneCodeLl.setVisibility(View.VISIBLE);
			nickTextString.setText(R.string.nickname_string);
			nickNameEdit.setHint(R.string.nickname_hint_string);
			nickNameEdit.setText("");
			phoneCodeLine.setVisibility(View.VISIBLE);
			registerType = PHONE_REGISTER;
			break;
		case EMAIL_REGISTER:// 邮箱注册
			registerTypeText.setText(R.string.register_email_string);
			phoneTextString.setText(R.string.email_string);
			emailEdit.setVisibility(View.VISIBLE);
			phoneNumEdit.setVisibility(View.GONE);
			phoneCodeLl.setVisibility(View.GONE);
			nickTextString.setText(R.string.username_string);
			nickNameEdit.setHint(R.string.username_hint_string);
			nickNameEdit.setText("");
			phoneCodeLine.setVisibility(View.GONE);
			registerType = EMAIL_REGISTER;
			break;
		default:
			break;
		}
	}

	// 判断手机号码的合法性
	private boolean isMobileRight(String mobileNum) {
		// 判断手机号是否为空
		if (TextUtils.isEmpty(mobileNum)) {
			CustomToast.getToast(RegisterActivity.this,
					getString(R.string.phonenum_null_string),
					Toast.LENGTH_SHORT).show();
			return false;
		}

		// 判断手机号是否合法
		if (!AndroidUtils.isMobileNO(mobileNum)) {
			CustomToast.getToast(RegisterActivity.this,
					getString(R.string.phonenum_error_string),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 获取短信验证码
	private void getSmsCode(String mobileNum) {
		if (!isMobileRight(mobileNum)) {
			return;
		}
		showProgress(null, null);
		if (isGetSmsNow) {
			return;
		}
		requestParams.clear();
		GetSmsCodeTask getSmsCodeTask = new GetSmsCodeTask();
		requestParams.add("mobile", mobileNum);
		requestParams.add("smstype", "mobilereg");
		getSmsCodeTask.request(RegisterActivity.this,
				HttpUrlManager.getSendSmsUrl(), HttpManager.HTTPMETHOD_JSON,
				requestParams, getSmsListener);
	}

	/**
	 * 获取验证码接口监听类
	 */
	private RequestListener<BaseBean> getSmsListener = new RequestListener<BaseBean>() {

		@Override
		public void OnStart() {
			isGetSmsNow = true;
			LogUtil.d(LOG_TAG, "开始请求OnStart-----------");
		}

		@Override
		public void onError(Exception e) {
			isGetSmsNow = false;
			dismissProgress();
			LogUtil.d(LOG_TAG, "onError-----------" + e.getMessage());
			mHandler.obtainMessage(GET_SMS_FAILED, e.getMessage())
					.sendToTarget();
		}

		@Override
		public void OnPaserComplete(BaseBean bean) {
			isGetSmsNow = false;
			dismissProgress();
			if (bean != null) {
				LogUtil.d(LOG_TAG, "OnPaserComplete:" + bean.getMsg());
				if ("1".equals(bean.getStatus())) {
					mHandler.obtainMessage(GET_SMS_SUCESS, bean.getMsg())
							.sendToTarget();
				} else {
					mHandler.obtainMessage(GET_SMS_FAILED, bean.getMsg())
							.sendToTarget();
				}
			} else {
				mHandler.obtainMessage(GET_SMS_FAILED,
						getString(R.string.get_code_failed)).sendToTarget();
			}
		}
	};

	/**
	 * 注册接口监听类
	 */
	private RequestListener<BaseBean> registerListener = new RequestListener<BaseBean>() {

		@Override
		public void OnStart() {
			isRegisterNow = true;
			LogUtil.d(LOG_TAG, "开始请求OnStart-----------");
		}

		@Override
		public void onError(Exception e) {
			isRegisterNow = false;
			dismissProgress();
			LogUtil.d(LOG_TAG, "onError-----------" + e.getMessage());
			mHandler.obtainMessage(REGISTER_FAILED, e.getMessage())
					.sendToTarget();
		}

		@Override
		public void OnPaserComplete(BaseBean bean) {
			isRegisterNow = false;
			dismissProgress();
			if (bean != null) {
				LogUtil.d(LOG_TAG, "OnPaserComplete:" + bean.getMsg());
				if ("1".equals(bean.getStatus())) {
					mHandler.obtainMessage(REGISTER_SUCESS, bean.getMsg())
							.sendToTarget();
				} else {
					mHandler.obtainMessage(REGISTER_FAILED, bean.getMsg())
							.sendToTarget();
				}
			} else {
				mHandler.obtainMessage(REGISTER_FAILED,
						getString(R.string.register_failed)).sendToTarget();
			}
		}
	};

	// 开始注册
	private void register(String mobile, String email, String smsCode,
			String nickName, String password) {
		showProgress(null, null);
		if (isRegisterNow) {
			return;
		}
		RegisterTask registerTask = new RegisterTask();
		requestParams.clear();
		switch (registerType) {
		case PHONE_REGISTER:
			requestParams.add("mobile", mobile);
			requestParams.add("smscode", smsCode);
			userName = mobile;
			break;
		case EMAIL_REGISTER:
			requestParams.add("email", email);
			userName = email;
			break;
		default:
			break;
		}
		requestParams.add("nickname", nickName);
		requestParams.add("password", password);
		requestParams.add("regtype", 2);// 2表示android手机
		registerTask.request(RegisterActivity.this,
				HttpUrlManager.getRegisterUrl(registerType),
				HttpManager.HTTPMETHOD_JSON, requestParams, registerListener);
	}

	// 提交绑定
	private void bindSubmit(String mobile, String email, String smsCode,
			String nickName, String password) {
		showProgress(null, null);
		if (isRegisterNow) {
			return;
		}
		int urlType = 0;
		LoginTask task = new LoginTask();
		requestParams.clear();
		switch (registerType) {
		case PHONE_REGISTER:
			urlType = 3;
			requestParams.add("mobile", mobile);
			requestParams.add("smscode", smsCode);
			userName = mobile;
			break;
		case EMAIL_REGISTER:
			urlType = 4;
			requestParams.add("email", email);
			userName = email;
			break;
		default:
			break;
		}
		requestParams.add("authid", flag + "");
		requestParams.add("connectid", openId);
		requestParams.add("nickname", nickName);
		requestParams.add("password", password);
		requestParams.add("regtype", 2);// 2表示android手机
		task.request(RegisterActivity.this,
				HttpUrlManager.getRegisterUrl(urlType),
				HttpManager.HTTPMETHOD_JSON, requestParams, bindListener);
	}

	/**
	 * 绑定提交接口监听类
	 */
	private RequestListener<LoginBean> bindListener = new RequestListener<LoginBean>() {

		@Override
		public void OnStart() {
			LogUtil.d(LOG_TAG, "开始请求OnStart-----------");
		}

		@Override
		public void onError(Exception e) {
			dismissProgress();
			LogUtil.d(LOG_TAG, "onError-----------" + e.getMessage());
			mHandler.obtainMessage(LOGIN_FAILED, e.getMessage()).sendToTarget();
		}

		@Override
		public void OnPaserComplete(LoginBean bean) {
			dismissProgress();
			if (bean != null) {
				LogUtil.d(LOG_TAG, "OnPaserComplete:" + bean.getMsg());
				if ("1".equals(bean.getStatus())) {
					mHandler.obtainMessage(LOGIN_SUCESS, bean).sendToTarget();
				} else {
					mHandler.obtainMessage(LOGIN_FAILED, bean.getMsg())
							.sendToTarget();
				}
			} else {
				mHandler.obtainMessage(LOGIN_FAILED,
						getString(R.string.request_failed_string))
						.sendToTarget();
			}
		}
	};

	// 判断注册信息填写的完整性
	private void registerInfoComplete() {
		// 判断网络是否可用
		if (!AndroidUtils.isNetworkAvailable(RegisterActivity.this)) {
			CustomToast.getToast(RegisterActivity.this,
					getString(R.string.networkoff), Toast.LENGTH_SHORT).show();
			return;
		}

		// 用户名/昵称
		String nickName = nickNameEdit.getText().toString().trim();
		// 手机验证码
		String smsCode = phoneCodeEdit.getText().toString().trim();

		// 判断邮箱地址/手机号码的有效性
		String mobileString = phoneNumEdit.getText().toString().trim();
		String emailString = emailEdit.getText().toString().trim();
		switch (registerType) {
		case PHONE_REGISTER:
			if (!isMobileRight(mobileString)) {
				return;
			}
			// 验证码不能为空
			if (TextUtils.isEmpty(smsCode)) {
				CustomToast
						.getToast(RegisterActivity.this,
								getString(R.string.sms_null_string),
								Toast.LENGTH_SHORT).show();
				return;
			}
			// 昵称不能为空
			if (TextUtils.isEmpty(nickName)) {
				CustomToast.getToast(RegisterActivity.this,
						getString(R.string.nickname_null_string),
						Toast.LENGTH_SHORT).show();
				return;
			}

			break;
		case EMAIL_REGISTER:
			if (TextUtils.isEmpty(emailString)) {
				CustomToast.getToast(RegisterActivity.this,
						getString(R.string.email_null_string),
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (!AndroidUtils.isEmail(emailString)) {
				CustomToast.getToast(RegisterActivity.this,
						getString(R.string.email_error_string),
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (TextUtils.isEmpty(nickName)) {
				CustomToast.getToast(RegisterActivity.this,
						getString(R.string.username_hint_string),
						Toast.LENGTH_SHORT).show();
				return;
			}
			break;
		default:
			break;
		}

		// 密码
		String password = passwordEdit.getText().toString().trim();
		// 确认密码
		String confirmPw = confirmPasswordEdit.getText().toString().trim();
		// 密码为空
		if (TextUtils.isEmpty(password)) {
			CustomToast.getToast(RegisterActivity.this,
					getString(R.string.password_null_string),
					Toast.LENGTH_SHORT).show();
			return;
		}

		// 密码不足6位
		if (password.length() < 6) {
			CustomToast.getToast(RegisterActivity.this,
					getString(R.string.password_error_string),
					Toast.LENGTH_SHORT).show();
			return;
		}

		// 确认密码为空
		if (TextUtils.isEmpty(confirmPw)) {
			CustomToast.getToast(RegisterActivity.this,
					getString(R.string.confirmPw_null_string),
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		//两次输入的密码不同
		if (!confirmPw.equals(password)) {
			CustomToast.getToast(RegisterActivity.this,
					getString(R.string.confirmPw_error_string),
					Toast.LENGTH_SHORT).show();
			return;
		}

		// 开始注册
		if (flag == 0) {
			register(mobileString, emailString, smsCode, nickName, password);
		} else {
			bindSubmit(mobileString, emailString, smsCode, nickName, password);
		}
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
			case GET_SMS_SUCESS:// 获取短信验证码成功
				CustomToast.getToast(RegisterActivity.this,
						String.valueOf(object), Toast.LENGTH_SHORT).show();
				break;
			case LOGIN_FAILED:
			case REGISTER_FAILED:// 注册失败
			case GET_SMS_FAILED:// 获取短信验证码失败
				CustomToast.getToast(RegisterActivity.this,
						String.valueOf(object), Toast.LENGTH_SHORT).show();
				break;
			case REGISTER_SUCESS:
				CustomToast.getToast(RegisterActivity.this,
						getString(R.string.register_success),
						Toast.LENGTH_SHORT).show();
				saveInfo();
				setResult(RESULT_OK);
				RegisterActivity.this.finish();
				break;
			case LOGIN_SUCESS:// 绑定账号成功
				if (saveBindInfo(object)) {
					setResult(RESULT_OK);
					RegisterActivity.this.finish();
				}
				break;
			default:
				break;
			}
		}

	};

	// 注册成功后保存用户名
	private void saveInfo() {
		if (!TextUtils.isEmpty(userName)) {
			loginPreference.setUserName(this, userName);
		}
	}

	// 绑定成功后保存信息
	private boolean saveBindInfo(Object object) {
		if (object == null) {
			return false;
		}
		LoginBean loginBean = (LoginBean) object;
		if (loginBean == null || loginBean.getData() == null) {
			return false;
		}

		loginPreference.saveLoginInfo(this, userName, loginBean.getData()
				.getUserid(), loginBean.getData().getNickname(), loginBean
				.getData().getTruename(), loginBean.getData().getSex(),
				loginBean.getData().getQq(), loginBean.getData().getAreastr(),
				loginBean.getData().getBirthday(), loginBean.getData()
						.getAmount(), loginBean.getData().getNopay(), loginBean
						.getData().getYespay(), loginBean.getData()
						.getShipping(), loginBean.getData().getDiscount(),
				loginBean.getData().getYongjin(), loginBean.getData()
						.getUserrank(), loginBean.getData().getAvatar());
		loginPreference.setLoginState(this, true);
		return true;
	}
}
