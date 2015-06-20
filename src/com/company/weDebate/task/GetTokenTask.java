package com.company.weDebate.task;

import java.io.InputStream;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.company.weDebate.bean.GetTokenBean;
import com.company.weDebate.net.AsyncRunner;
import com.company.weDebate.utils.LogUtil;

/**
 * 描述：获取Token的json数据解析
 */

public class GetTokenTask extends AsyncRunner<GetTokenBean> {
	@Override
	protected GetTokenBean paserJSON(String json) {
		LogUtil.i("pg", "获取Token的json == " + json);
		JSONObject object = JSON.parseObject(json);
		if (object != null) {
			return JSON.toJavaObject(object, GetTokenBean.class);
		}
		return null;
	}

	@Override
	protected GetTokenBean paserXML(InputStream inputStream) {
		return null;
	}
}
