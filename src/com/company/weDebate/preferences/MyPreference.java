package com.company.weDebate.preferences;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

/**
 * 系统信息 保存
 */
public class MyPreference extends BasePreferences {

	// 文件名
	private final static String PERFERENCE_NAME = "ilunch_perferences";
	// 午餐提醒时间
	private final static String LUNCH_TIME_ALERT = "lunchTimeAlert";

	// 菜品集合
	private final static String SHOP_DATA = "shopData";

	// 当前城市
	private final static String LOCATION_CITY = "locationCity";
	// 当前城市编码
	private final static String LOCATION_CITY_CID = "locationCityCid";

	// 我的位置-城市
	private final static String MY_LOCATION_CITY = "my_location_city";
	// 我的位置-区域
	private final static String MY_LOCATION_QY = "my_location_qy";
	// 我的位置-大厦
	private final static String MY_LOCATION_DS = "my_location_ds";
	// 我的位置-城市ID
	private final static String MY_LOCATION_CITY_ID = "my_location_city_id";
	// 我的位置-区域ID
	private final static String MY_LOCATION_QY_ID = "my_location_qy_id";
	// 我的位置-大厦ID
	private final static String MY_LOCATION_DS_ID = "my_location_ds_id";
	// 最新获取到的经度lat
	public static final String LAST_LAT = "last_lat";
	// 最新获取到的纬度lng
	public static final String LAST_LNG = "last_lng";

	public MyPreference(Context context) {
		super(context, PERFERENCE_NAME);
	}

	public void setLunchTimeAlert(Context context, String alertTime) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		values.put(LUNCH_TIME_ALERT, alertTime);
		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getLunchTimeAlert() {
		return getString(LUNCH_TIME_ALERT);
	}

	public void saveShopData(Context context, String shopData) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		if (!TextUtils.isEmpty(shopData)) {
			values.put(SHOP_DATA, shopData);
		}

		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取菜品列表
	public String getShopData() {
		return getString(SHOP_DATA);
	}

	public void clearShopData(Context context) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		preferenses.edit().remove(SHOP_DATA).commit();
	}

	public void setLocationCity(Context context, String locationCity,
			String locationCid) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		if (!TextUtils.isEmpty(locationCity)) {
			values.put(LOCATION_CITY, locationCity);
		}
		if (!TextUtils.isEmpty(locationCid)) {
			values.put(LOCATION_CITY_CID, locationCid);
		}

		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取当前定位的城市
	public String getLocationCity() {
		return getString(LOCATION_CITY);
	}

	// 获取当前定位的城市编码
	public String getLocationCityCid() {
		return getString(LOCATION_CITY_CID);
	}

	public void setMyLocationCity(Context context, String city) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		values.put(MY_LOCATION_CITY, city);
		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMyLocationCity() {
		return getString(MY_LOCATION_CITY);
	}

	public void setMyLocationQy(Context context, String qy) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		values.put(MY_LOCATION_QY, qy);
		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMyLocationQy() {
		return getString(MY_LOCATION_QY);
	}

	public void setMyLocationDs(Context context, String ds) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		values.put(MY_LOCATION_DS, ds);
		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMyLocationDs() {
		return getString(MY_LOCATION_DS);
	}

	public void setMyLocationCityID(Context context, String id) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		values.put(MY_LOCATION_CITY_ID, id);
		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMyLocationCityID() {
		return getString(MY_LOCATION_CITY_ID);
	}

	public void setMyLocationQyID(Context context, String id) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		values.put(MY_LOCATION_QY_ID, id);
		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMyLocationQyID() {
		return getString(MY_LOCATION_QY_ID);
	}

	public void setMyLocationDsID(Context context, String id) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		values.put(MY_LOCATION_DS_ID, id);
		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMyLocationDsid() {
		return getString(MY_LOCATION_DS_ID);
	}

	public void saveLatlng(Context context, String lat, String lng) {
		if (null == preferenses) {
			throw new NullPointerException("SharedPreferences is null!");
		}

		if (null == context) {
			throw new NullPointerException("context is null!");
		}

		ContentValues values = new ContentValues();
		if (!TextUtils.isEmpty(lat)) {
			values.put(LAST_LAT, lat);
		}
		if (!TextUtils.isEmpty(lng)) {
			values.put(LAST_LNG, lng);
		}
		try {
			write(values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPerferenceLat() {
		String lat = getString(LAST_LAT);
		if (TextUtils.isEmpty(lat)) {
			lat = "0";
		}

		return lat;
	}

	public String getPerferenceLng() {
		String lng = getString(LAST_LNG);
		if (TextUtils.isEmpty(lng)) {
			lng = "0";
		}

		return lng;
	}
}