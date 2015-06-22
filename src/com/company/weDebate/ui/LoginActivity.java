package com.company.weDebate.ui;

import org.json.JSONException;
import org.json.JSONObject;
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
import android.widget.Toast;

import com.company.weDebate.R;
import com.company.weDebate.base.BaseActivity;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	public final static String TAG = "com.company.ilunch";
	public final static int COMPLETEINFO_REQUESTCODE = 1;// 完善资料
	public final static int LOGIN_SUCESS = 0x01;// 登录成功
	public final static int LOGIN_FAILED = 0x02;// 登录失败
	public final static int BIND_LOGIN_SUCESS = 0x03;// 已绑定
	public final static int NOT_BIND_LOGIN = 0x04;// 未绑定
	public final static int QQ_LOGIN_BIND = 4;
	public final static int SINA_LOGIN_BIND = 3;
	private RequestParams requestParams;// 请求参数封装的键值对
	private LoginPreference loginPreference;

	private LinearLayout backLl;// 返回
	private EditText accountNameEdit;// 账号输入框
	private EditText accountPwEdit;// 密码输入框
	private Button loginBtn;// 立即登录按钮
	private ImageView wechatIv;// 微信登录
	private ImageView qqIv;// qq登录
	private String uname;
	private String openid = "";// 第三方登录返回的openid
	private int authid = 0;

	// QQ登录
	private QQLoginUtil qqLoginUtil;
	// 微信登录
	private WechatLoginUtil wechatLoginUtil;
	// 新浪微博登录
	private WeiBoLoginUtil weiBoLoginUtil;

	@Override
	protected void initData() {
		qqLoginUtil = new QQLoginUtil(LoginActivity.this);
		wechatLoginUtil = new WechatLoginUtil(this);
		weiBoLoginUtil = new WeiBoLoginUtil(LoginActivity.this, callback);
		requestParams = new RequestParams();
		loginPreference = new LoginPreference(this);
	}

	@Override
	protected void initView() {
		setContentView(R.layout.login);
		backLl = (LinearLayout) this.findViewById(R.id.backLl);
		accountNameEdit = (EditText) this.findViewById(R.id.accountNameEdit);
		accountPwEdit = (EditText) this.findViewById(R.id.accountPwEdit);
		loginBtn = (Button) this.findViewById(R.id.loginBtn);
		wechatIv = (ImageView) this.findViewById(R.id.wechatIv);
		qqIv = (ImageView) this.findViewById(R.id.qqIv);
	}

	@Override
	protected void setAttribute() {
		backLl.setVisibility(View.VISIBLE);
		backLl.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		wechatIv.setOnClickListener(this);
		qqIv.setOnClickListener(this);

		if (!TextUtils.isEmpty(loginPreference.getUserName())) {
			accountNameEdit.setText(loginPreference.getUserName());
			accountNameEdit
			.setSelection(loginPreference.getUserName().length());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backLl:// 返回
			this.finish();
			break;
		case R.id.loginBtn:// 立即登录
			loginInfoComplete();
			break;
		case R.id.qqIv:// QQ登录
			qqLoginUtil.login(new BaseUiListener());
			break;
			// case R.id.weiboLoginBtn:// 微博登录
			// weiBoLoginUtil.login();
			// break;
		case R.id.wechatIv://微信登录
			wechatLoginUtil.login();
			break;
		default:
			break;
		}
	}

	/**
	 * QQ登录监听类
	 */
	public class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			CustomToast
			.getToast(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT)
			.show();
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
			// 保存登录成功返回的信息
			LogUtil.d(TAG, "QQ登录返回：" + values.toString());
			if (values.has("openid")) {
				try {
					authid = QQ_LOGIN_BIND;
					openid = values.getString("openid");
					isBind(authid, openid);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onError(UiError e) {
			CustomToast
			.getToast(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT)
			.show();
		}

		@Override
		public void onCancel() {
			CustomToast.getToast(LoginActivity.this, "已取消登录",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 判断QQ账号或新浪微博账号是否已经绑定 <br/>
	 * 
	 * @param [authid]-[3-新浪；4-QQ] <br/>
	 */
	private void isBind(int authid, String openId) {
		showProgress(null, null);
		ThirdLoginBindTask task = new ThirdLoginBindTask();
		requestParams.clear();
		requestParams.add("authid", authid + "");
		requestParams.add("connectid", openId);
		task.request(LoginActivity.this, HttpUrlManager.getBindLoginUrl(),
				HttpManager.HTTPMETHOD_JSON, requestParams, bindListener);
	}

	// 微博授权成功回调
	private Handler.Callback callback = new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			String token = (String) msg.obj;
			if (!TextUtils.isEmpty(token)) {
				authid = SINA_LOGIN_BIND;
				isBind(authid, token);
			}
			return false;
		}
	};

	// 判断账户密码填写的完整性
	private void loginInfoComplete() {
		// 判断网络是否可用
		if (!AndroidUtils.isNetworkAvailable(LoginActivity.this)) {
			CustomToast.getToast(LoginActivity.this,
					getString(R.string.networkoff), Toast.LENGTH_SHORT).show();
			return;
		}
		// 账号
		uname = accountNameEdit.getText().toString().trim();
		// 密码
		String password = accountPwEdit.getText().toString().trim();
		// 账号不能为空
		if (TextUtils.isEmpty(uname)) {
			CustomToast
			.getToast(LoginActivity.this,
					getString(R.string.account_null_string),
					Toast.LENGTH_SHORT).show();
			return;
		}

		/*
		 * int loginType = 0; if (!AndroidUtils.isMobileNO(uname) &&
		 * !AndroidUtils.isEmail(uname)) {
		 * CustomToast.getToast(LoginActivity.this,
		 * getString(R.string.account_error_string), Toast.LENGTH_SHORT).show();
		 * return; } else { if (AndroidUtils.isMobileNO(uname)) { loginType = 0;
		 * } if (AndroidUtils.isEmail(uname)) { loginType = 1; } }
		 */

		// 密码不能为空
		if (TextUtils.isEmpty(password)) {
			CustomToast.getToast(LoginActivity.this,
					getString(R.string.password_null_string),
					Toast.LENGTH_SHORT).show();
			return;
		}

		// 密码不足6位
		/*
		 * if (password.length() < 6) { CustomToast.getToast(LoginActivity.this,
		 * getString(R.string.password_error_string),
		 * Toast.LENGTH_SHORT).show(); return; }
		 */

		// 开始登陆
		toLogin(uname, password/* , loginType */);
	}

	/**
	 * 向服务器请求登录 <br/>
	 * 
	 * @param [uname]-[登录用户名] <br/>
	 * @param [password]-[密码] <br/>
	 */
	private void toLogin(String uname, String password/* , int loginType */) {
		showProgress(null, getString(R.string.toast_login_now));
		LoginTask loginTask = new LoginTask();
		requestParams.clear();
		// switch (loginType) {
		// case 0:// 手机登陆
		// requestParams.add("mobile", uname);
		// break;
		// case 1:// 邮箱登陆
		// requestParams.add("email", uname);
		// break;
		// default:
		// break;
		// }
		// requestParams.add("password", password);

		JSONObject requestParams = new JSONObject();

		try {
			requestParams.put("UserName", uname);
			requestParams.put("Pwd", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		loginTask.request(LoginActivity.this, HttpUrlManager.LOGIN_STRING,
				requestParams, listener);
	}

	/**
	 * 登录接口监听类
	 */
	private RequestListener<LoginBean> listener = new RequestListener<LoginBean>() {

		@Override
		public void OnStart() {
			LogUtil.d(TAG, "开始请求OnStart-----------");
		}

		@Override
		public void onError(Exception e) {
			dismissProgress();
			LogUtil.d(TAG, "onError-----------" + e.getMessage());
			mHandler.obtainMessage(LOGIN_FAILED, e.getMessage()).sendToTarget();
		}

		@Override
		public void OnPaserComplete(LoginBean bean) {
			dismissProgress();
			if (bean != null) {
				LogUtil.d(TAG, "OnPaserComplete:" + bean.getHead());
				if ("00".equals(bean.getHead().getResultCode())) {
					mHandler.obtainMessage(LOGIN_SUCESS, bean).sendToTarget();
				} else {
					mHandler.obtainMessage(LOGIN_FAILED,
							bean.getHead().getResultInfo()).sendToTarget();
				}
			} else {
				mHandler.obtainMessage(LOGIN_FAILED,
						getString(R.string.login_failed_string)).sendToTarget();
			}
		}
	};

	/**
	 * 登录接口监听类
	 */
	private RequestListener<LoginBean> bindListener = new RequestListener<LoginBean>() {

		@Override
		public void OnStart() {
			LogUtil.d(TAG, "开始请求OnStart-----------");
		}

		@Override
		public void onError(Exception e) {
			dismissProgress();
			LogUtil.d(TAG, "onError-----------" + e.getMessage());
			mHandler.obtainMessage(LOGIN_FAILED, e.getMessage()).sendToTarget();
		}

		@Override
		public void OnPaserComplete(LoginBean bean) {
			dismissProgress();
			if (bean != null) {
				// LogUtil.d(TAG, "OnPaserComplete:" + bean.getMsg());
				// if ("1".equals(bean.getStatus())) {// 已绑定
				// mHandler.obtainMessage(BIND_LOGIN_SUCESS, bean)
				// .sendToTarget();
				// } else if ("0".equals(bean.getStatus())) {// 未绑定
				// mHandler.obtainMessage(NOT_BIND_LOGIN, bean.getMsg())
				// .sendToTarget();
				// } else {
				// mHandler.obtainMessage(LOGIN_FAILED, bean.getMsg())
				// .sendToTarget();
				// }
			} else {
				mHandler.obtainMessage(LOGIN_FAILED,
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
			case LOGIN_SUCESS:// 登录成功
			case BIND_LOGIN_SUCESS:// 已绑定
				if (saveInfo(object)) {
					CustomToast.getToast(LoginActivity.this,
							getString(R.string.login_success_string),
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();

					setResult(RESULT_OK, intent);
					LoginActivity.this.finish();
				} else {
					CustomToast.getToast(LoginActivity.this,
							getString(R.string.login_failed_string),
							Toast.LENGTH_SHORT).show();
				}
				break;
			case LOGIN_FAILED:// 登录失败
				if (!TextUtils.isEmpty(String.valueOf(object))) {
					CustomToast.getToast(LoginActivity.this,
							String.valueOf(object), Toast.LENGTH_SHORT).show();
				}
				break;
			case NOT_BIND_LOGIN:// 未绑定
//				CustomToast.getToast(LoginActivity.this,
//						getString(R.string.complete_info_warn),
//						Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent(LoginActivity.this,
//						RegisterActivity.class);
//				intent.putExtra("openid", openid);
//				intent.putExtra("flag", authid);
//				startActivityForResult(intent, COMPLETEINFO_REQUESTCODE);
				break;
			default:
				break;
			}
		}
	};

	// 登陆成功后保存信息
	private boolean saveInfo(Object object) {
		if (object == null) {
			return false;
		}

		LoginBean loginBean = (LoginBean) object;
		if (loginBean == null || loginBean.getBody() == null) {
			return false;
		}

		loginPreference.saveLoginInfo(this, loginBean.getBody()
				.getPhoneActivate(), loginBean.getBody().getUsermoney(),
				loginBean.getBody().getDataID(), loginBean.getBody().getName(),
				loginBean.getBody().getTrueName(),
				loginBean.getBody().getSex(), loginBean.getBody().getTell(),
				loginBean.getBody().getPhone(), loginBean.getBody().getQq(),
				loginBean.getBody().getMsn(), loginBean.getBody().getRegTime(),
				loginBean.getBody().getPoint(), loginBean.getBody()
				.getPicture(), loginBean.getBody().getState(),
				loginBean.getBody().getEmail(), loginBean.getBody()
				.getPassword(), loginBean.getBody().getGroupID(),
				loginBean.getBody().getWebSite(), loginBean.getBody()
				.getOpenid(), loginBean.getBody().getWtype(), loginBean
				.getBody().getPayPassword());
		loginPreference.setLoginState(this, true);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		weiBoLoginUtil.setOnActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case COMPLETEINFO_REQUESTCODE:// 完善资料
				CustomToast.getToast(LoginActivity.this,
						getString(R.string.login_success_string),
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				LoginActivity.this.finish();
				break;
			default:
				break;
			}
		}
	}
}