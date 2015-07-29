package com.company.weDebate.ui;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.company.weDebate.R;
import com.company.weDebate.base.BaseActivity;
import com.company.weDebate.preferences.LoginPreference;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 描述：注册界面
 */

public class RegisterActivity extends BaseActivity implements OnClickListener {
	public final static String LOG_TAG = "weDebate";

	private static final int MSG_VERIFY_SUCCESS = 0x01;
	private static final int MSG_VERIFY_FAIL = 0x02;

	private TextView titleTextView;// 标题
	private LinearLayout backLl;// 返回
	private TextView phoneTextString;// 手机号
	private EditText phoneNumEdit;// 手机号输入框
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
	private String userName = "";
	private int flag = 0;// 当前为注册界面，还是完善资料界面
	private String openId = "";// 第三方授权返回的openid

	@Override
	protected void initData() {
		loginPreference = new LoginPreference(this);
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
		getPhoneCodeBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backLl:// 返回
			this.finish();
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

	private void verifySmsCode(String code) {
		showProgress("", "");

		AVMobilePhoneVerifyCallback callback = new AVMobilePhoneVerifyCallback() {

			@Override
			public void done(AVException e) {
				if (e==null) {
					mHandler.sendEmptyMessage(MSG_VERIFY_SUCCESS);
				} else {
					dismissProgress();
					mHandler.sendEmptyMessage(MSG_VERIFY_FAIL);
				}
			}
		};

		AVService.virifySms(code, callback);
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

		showProgress("", "");

		RequestMobileCodeCallback callback = new RequestMobileCodeCallback() {

			@Override
			public void done(AVException e) {
				dismissProgress();

				if (e!=null) {
					CustomToast.getToast(RegisterActivity.this,
							getString(R.string.register_send_sms_error),
							Toast.LENGTH_SHORT).show();
				}
			}
		};

		AVService.requestMobilePhoneVerify(mobileNum, callback);
	}

	// 开始注册
	private void register(String mobile,
			String nickName, String password) {

		SignUpCallback signUpCallback = new SignUpCallback() {
			public void done(AVException e) {
				dismissProgress();

				if (e == null) {
					CustomToast.getToast(RegisterActivity.this,
							getString(R.string.register_success),
							Toast.LENGTH_SHORT).show();

					Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
					startActivity(mainIntent);

					RegisterActivity.this.finish();
				} else {
					switch (e.getCode()) {
					case 202:
						CustomToast.getToast(RegisterActivity.this,
								getString(R.string.error_register_user_name_repeat),
								Toast.LENGTH_SHORT).show();
						break;
					default:
						CustomToast.getToast(RegisterActivity.this,
								getString(R.string.register_failed),
								Toast.LENGTH_SHORT).show();
						break;
					}
				}
			}
		};

		AVService.signUp(nickName, password, mobile, signUpCallback);
	}

	// 提交绑定
	private void bindSubmit(String mobile, String smsCode,
			String nickName, String password) {
		showProgress(null, null);

	}

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

		//手机号码的有效性
		String mobileString = phoneNumEdit.getText().toString().trim();

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

		verifySmsCode(smsCode);
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
			case MSG_VERIFY_SUCCESS:
				// 开始注册
				if (flag == 0) {
					register(phoneNumEdit.getText().toString().trim(), 
							nickNameEdit.getText().toString().trim(),
							passwordEdit.getText().toString().trim());
				} else {
					//					bindSubmit(mobileString, smsCode, nickName, password);
				}
				break;
			case MSG_VERIFY_FAIL:
				CustomToast.getToast(RegisterActivity.this,
						getString(R.string.verify_sms_code_error),
						Toast.LENGTH_SHORT).show();
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
	//	private boolean saveBindInfo(Object object) {
	//		if (object == null) {
	//			return false;
	//		}
	//		LoginBean loginBean = (LoginBean) object;
	//		if (loginBean == null || loginBean.getData() == null) {
	//			return false;
	//		}
	//
	//		loginPreference.saveLoginInfo(this, userName, loginBean.getData()
	//				.getUserid(), loginBean.getData().getNickname(), loginBean
	//				.getData().getTruename(), loginBean.getData().getSex(),
	//				loginBean.getData().getQq(), loginBean.getData().getAreastr(),
	//				loginBean.getData().getBirthday(), loginBean.getData()
	//						.getAmount(), loginBean.getData().getNopay(), loginBean
	//						.getData().getYespay(), loginBean.getData()
	//						.getShipping(), loginBean.getData().getDiscount(),
	//				loginBean.getData().getYongjin(), loginBean.getData()
	//						.getUserrank(), loginBean.getData().getAvatar());
	//		loginPreference.setLoginState(this, true);
	//		return true;
	//	}
}
