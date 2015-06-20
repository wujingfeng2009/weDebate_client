package com.company.weDebate.helper;

import com.company.weDebate.bean.GetTokenBean;
import com.company.weDebate.net.AsyncRunner;
import com.company.weDebate.net.HttpUrlManager;
import com.company.weDebate.net.RequestListener;
import com.company.weDebate.net.RequestParams;
import com.company.weDebate.task.GetTokenTask;

import android.content.Context;

/**
 * 描述：获取Token帮助类
 */

public class GetTokenHelper {
	private static GetTokenHelper mInstance = null;
	
	private GetTokenHelper() {
		
	}
	
	public static GetTokenHelper getInstance() {
		if (mInstance == null) {
			synchronized (GetTokenHelper.class) {
				if (mInstance == null) {
					mInstance = new GetTokenHelper();
				}
			}
		}
		
		return mInstance;
	}
	
	public void doGetToken(Context context) {
		GetTokenTask task = new GetTokenTask();

		RequestParams params = new RequestParams();
		params.add("II.III.II.I", "ash");
		params.add("I.II.II.III", "love@false#1#0^");
		
		task.request(context, HttpUrlManager.getTokenUrl(),
				AsyncRunner.POST, params, reqServiceTimeListener);
	}

	private RequestListener<GetTokenBean> reqServiceTimeListener = new RequestListener<GetTokenBean>() {

		@Override
		public void onError(Exception e) {

		}

		@Override
		public void OnStart() {

		}

		@Override
		public void OnPaserComplete(GetTokenBean bean) {
			
		}
	};
}
