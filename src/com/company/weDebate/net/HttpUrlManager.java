package com.company.weDebate.net;

/**
 * 描述：接口管理
 */

public class HttpUrlManager {
	public final static String ROOT_HTTP_URL = "http://120.25.144.68:10001";
	// 根域名
	public final static String HTTP_URL = "http://120.25.144.68:10001/weixiao";
	
	//系统初始化
	public final static String GET_TOKEN_STRING = "/oauth/getToken.do";
	
	//用户注册
	public final static String SAVE_USER_STRING = "/member/saveUser_v1.do";
	
	//列表所有的省份
	public final static String LIST_PROVINCE_STRING = "/position/listProvince_v1.do";
	
	//列表所有的城市
	public final static String LIST_CITY_STRING = "/position/listCity_v1.do";
	
	public static String getTokenUrl() {
		return HTTP_URL + GET_TOKEN_STRING;
	}
	
	public static String userRegisterUrl() {
		return HTTP_URL + SAVE_USER_STRING;
	}
	
	public static String getProvinceListUrl() {
		return HTTP_URL + LIST_PROVINCE_STRING;
	}
	
	public static String getCityListUrl() {
		return HTTP_URL + LIST_CITY_STRING;
	}
}