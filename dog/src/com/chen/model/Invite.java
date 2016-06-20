package com.chen.model;

import java.io.Serializable;

public class Invite implements Serializable {

	private static final long serialVersionUID = 1L;
	private int inviteId;
	private String nickname;
	private boolean sex;
	private int age;
	private String pubTime;
	private String inviteTime;
	private String title;
	private String content;
	private String contact;

	public int getInviteId() {
		return inviteId;
	}

	public void setInviteId(int inviteId) {
		this.inviteId = inviteId;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public String getPubTime() {
		return pubTime;
	}

	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}

	public String getInviteTime() {
		return inviteTime;
	}

	public void setInviteTime(String inviteTime) {
		this.inviteTime = inviteTime;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	private String userImage;
}
