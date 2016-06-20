package com.chen.model;

import java.io.Serializable;

public class Valuate implements Serializable {
	private static final long serialVersionUID = 1L;
	private int valId;
	private String nickName;
	private int restId;
	private String content;
	private String time;
	private int recommend;
	private String image;
	private String userImage;

	public int getValId() {
		return valId;
	}

	public void setValId(int valId) {
		this.valId = valId;
	}

	public int getRestId() {
		return restId;
	}

	public void setRestId(int restId) {
		this.restId = restId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

}
