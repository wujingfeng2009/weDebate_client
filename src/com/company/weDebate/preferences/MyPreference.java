package com.company.weDebate.preferences;

import android.content.ContentValues;
import android.content.Context;

/**
 * 系统信息 保存
 */
public class MyPreference extends BasePreferences {

	// 文件名
	private final static String PERFERENCE_NAME = "weDebate_perferences";
	// 是否是第一次安装
	private static final String isFirst = "isFirst";

	public MyPreference(Context context) {
		super(context, PERFERENCE_NAME);
	}

	public void saveParamIsFirst(Context context, boolean isFirst) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		values.put(MyPreference.isFirst, isFirst);
		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean getPerferenceIsFirst() {
		return getBoolean(MyPreference.isFirst, true);
	}
}