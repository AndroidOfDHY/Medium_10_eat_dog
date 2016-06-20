package com.chen.model;

import java.io.Serializable;

public class Restaurant implements Serializable {
	private static final long serialVersionUID = 1L;
	private int restId;
	private double latitude;
	private double longitude;
	private String restName;
	private String intro;
	private String tel;
	private String image;
	private String address;

	public int getRestId() {
		return restId;
	}

	public void setRestId(int restId) {
		this.restId = restId;
	}

	public String getRestName() {
		return restName;
	}

	@Override
	public String toString() {
		return "Restaurant [restId=" + restId + ", latitude=" + latitude + ", longitude="
				+ longitude + ", restName=" + restName + ", intro=" + intro + ", tel=" + tel
				+ ", image=" + image + "]";
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setRestName(String restName) {
		this.restName = restName;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
