package com.chen.model;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userId;
	private String userPass;
	private String nickname;
	private boolean sex;
	private int age;
	private String userImage;

	public User() {

	}

	public User(String userId, String userPass, String nickname, boolean sex, int age,
			String userImage) {
		this.userId = userId;
		this.userPass = userPass;
		this.nickname = nickname;
		this.sex = sex;
		this.age = age;
		this.userImage = userImage;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public boolean isSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

}
