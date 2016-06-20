package com.chen.utils;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class MyAnimationUtils {

	public static TranslateAnimation animationUp() {
		TranslateAnimation up = new TranslateAnimation(Animation.RELATIVE_TO_SELF,

		0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0);
		up.setDuration(300);
		up.setFillAfter(true);
		return up;
	}

	public static Animation animationUpBack() {
		TranslateAnimation up = new TranslateAnimation(Animation.RELATIVE_TO_SELF,

		0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1.0f);
		up.setDuration(300);
		up.setFillAfter(true);
		return up;
	}

	public static TranslateAnimation animationDown() {
		TranslateAnimation down = new TranslateAnimation(Animation.RELATIVE_TO_SELF,

		0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0);
		down.setDuration(300);
		down.setFillAfter(true);
		return down;
	}

	public static TranslateAnimation animationDownBack() {
		TranslateAnimation down = new TranslateAnimation(Animation.RELATIVE_TO_SELF,

		0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, -1.0f);
		down.setDuration(300);
		down.setFillAfter(true);
		return down;
	}

}
