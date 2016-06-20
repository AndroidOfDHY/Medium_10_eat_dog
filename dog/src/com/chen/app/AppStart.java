package com.chen.app;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.chen.dog.LoginActivity;
import com.chen.dog.MainActivity;
import com.chen.dog.R;

public class AppStart extends Activity {

	private SharedPreferences sp;
	private SharedPreferences spf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.app_start, null);
		setContentView(view);

		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.4f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				initGlobal();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});

	}

	/**
	 * 从服务器获取版本信息
	 */
	private void initGlobal() {
		try {
			AppConfig.localVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode; // 设置本地版本号
			FinalHttp fh = new FinalHttp();
			fh.configTimeout(2000);
			fh.get(AppConfig.URLPATH + "servlet/SettingAction?action_flag=version",
					new AjaxCallBack<String>() {

						@Override
						public void onSuccess(String json) {
							try {
								AppConfig.serverVersion = Integer.parseInt(json.trim().toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
							redirectTo();
						}

						@Override
						public void onFailure(Throwable t, String strMsg) {
							AppConfig.serverVersion = AppConfig.localVersion;
							// AppConfig.serverVersion = 2;
							redirectTo();
						}
					});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 跳转到...
	 */
	private void redirectTo() {
		sp = getSharedPreferences("user", MODE_PRIVATE);
		String userId = sp.getString("name", "");
		if (userId.equals("")) {
			Intent intent = new Intent(AppStart.this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			AppConfig.USERID = userId;
			spf = getSharedPreferences("setting", MODE_PRIVATE);
			String[] a = spf.getString("setting", "5km").split("km");
			AppConfig.DISTANCE = Integer.parseInt(a[0]) * 1000;
			Intent intent = new Intent(AppStart.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
