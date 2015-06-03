package com.company.weDebate.fragment;

import com.company.weDebate.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 主菜单界面
 */
public class MainFragment extends Fragment  {
	public static final String FRAGMENT_TAG = MainFragment.class
			.getSimpleName();
	
	private TextView titleTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.test, null);
		initView(view);
		setAttribute();
		return view;
	}

	// 初始化数据
	private void initData() {
	}

	// 初始化控件
	private void initView(View view) {
		titleTv = (TextView) view.findViewById(R.id.titleTv);
	}

	// 设置控件属性
	private void setAttribute() {
		titleTv.setText(R.string.main_menu_string);
	}
}
