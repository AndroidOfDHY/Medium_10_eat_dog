package com.chen.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.chen.model.Invite;
import com.chen.model.Restaurant;
import com.chen.model.User;
import com.chen.model.Valuate;

/**
 * @author 陈双喜, 2013-5-14
 * 
 */
public class JsonTools {

	public static User getUser(String key, String jsonString) {
		User user = new User();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject json = jsonObject.getJSONObject("user");
			user.setUserId(json.getString("userId"));
			user.setUserPass(json.getString("userPass"));
			user.setNickname(json.getString("nickname"));
			user.setSex(json.getBoolean("sex"));
			user.setAge(json.getInt("age"));
			user.setUserImage(json.getString("userImage"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;

	}

	public static List<Restaurant> getRests(String key, String jsonString) {
		List<Restaurant> list = new ArrayList<Restaurant>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			// 返回json的数组
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject2 = jsonArray.getJSONObject(i);
				Restaurant rest = new Restaurant();
				rest.setRestId(jsonObject2.getInt("restId"));
				rest.setLatitude(jsonObject2.getDouble("latitude"));
				rest.setLongitude(jsonObject2.getDouble("longitude"));
				rest.setIntro(jsonObject2.getString("intro"));
				rest.setRestName(jsonObject2.getString("restName"));
				rest.setTel(jsonObject2.getString("tel"));
				rest.setImage(jsonObject2.getString("image"));
				rest.setAddress(jsonObject2.getString("address"));
				list.add(rest);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Valuate> getValuates(String key, String jsonString) {
		List<Valuate> list = new ArrayList<Valuate>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			// 返回json的数组
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json2 = jsonArray.getJSONObject(i);
				Valuate valuate = new Valuate();
				valuate.setValId(json2.getInt("valId"));
				valuate.setRestId(json2.getInt("restId"));
				valuate.setNickName(json2.getString("nickName"));
				valuate.setUserImage(json2.getString("userImage"));
				valuate.setContent(json2.getString("content"));
				valuate.setImage(json2.getString("image"));
				valuate.setRecommend(json2.getInt("recommend"));
				JSONObject j = json2.getJSONObject("time");
				valuate.setTime(formatTime(j));
				list.add(valuate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatTime(JSONObject json) throws JSONException {
		Date time = new Date(json.getInt("year"), json.getInt("month"), json.getInt("date"),
				json.getInt("hours"), json.getInt("minutes"), json.getInt("seconds"));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(time);
	}

	public static List<Invite> getInvites(String key, String jsonString) {
		List<Invite> list = new ArrayList<Invite>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			// 返回json的数组
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json2 = jsonArray.getJSONObject(i);
				Invite invite = new Invite();
				invite.setInviteId(json2.getInt("inviteId"));
				invite.setNickname(json2.getString("nickname"));
				invite.setSex(json2.getBoolean("sex"));
				invite.setAge(json2.getInt("age"));
				invite.setPubTime(formatTime(json2.getJSONObject("pubTime")));
				invite.setInviteTime(formatTime(json2.getJSONObject("inviteTime")));
				invite.setTitle(json2.getString("title"));
				invite.setContent(json2.getString("content"));
				invite.setContact(json2.getString("contact"));
				invite.setUserImage(json2.getString("userImage"));
				list.add(invite);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
