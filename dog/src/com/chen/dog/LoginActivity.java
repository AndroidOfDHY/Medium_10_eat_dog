package com.chen.dog;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chen.app.AppConfig;
import com.chen.model.User;
import com.chen.utils.JsonTools;

public class LoginActivity extends Activity {

	private EditText userName;
	private EditText userPass;
	private ProgressDialog p_dialog;
	private SharedPreferences sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		userName = (EditText) findViewById(R.id.userName);
		userPass = (EditText) findViewById(R.id.userPass);
	}

	@Override
	protected void onResume() {
		super.onResume();
		sp = getSharedPreferences("user", MODE_PRIVATE);
		userName.setText(sp.getString("name", ""));
		userPass.setText(sp.getString("pass", ""));
	}

	/**
	 * 登录
	 * 
	 * @param v
	 */
	public void login(View v) {
		final String usernameStr = userName.getText().toString().trim();
		final String userpassStr = userPass.getText().toString().trim();
		if (usernameStr == null || usernameStr.equals("")) {
			userName.requestFocus();
			userName.setError("用户名不能为空");
		} else if (userpassStr == null || userpassStr.equals("")) {
			userPass.requestFocus();
			userPass.setError("密码不能为空");
		} else {
			loginByUser(usernameStr, userpassStr);
		}
	}

	/**
	 * 注册
	 * 
	 * @param v
	 */
	public void register(View v) {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
	}

	private void loginByUser(final String name, final String pass) {
		FinalHttp fh = new FinalHttp();
		fh.get(AppConfig.URLPATH + "servlet/UserAction?action_flag=login&userId=" + name
				+ "&userPass=" + pass, new AjaxCallBack<String>() {
			@Override
			public void onStart() {
				p_dialog = ProgressDialog.show(LoginActivity.this, "请等待", "正在为您登录...", true);
			}

			@Override
			public void onSuccess(String json) {
				if (p_dialog.isShowing()) {
					p_dialog.dismiss();
				}
				if (json.toString().trim().equals("false")) {
					userName.requestFocus();
					userName.setError("用户名或密码错误!");
				} else {
					User user = JsonTools.getUser("user", json);
					sp = getSharedPreferences("user", MODE_PRIVATE);
					sp.edit().putString("name", name).putString("pass", pass)
							.putString("nickname", user.getNickname())
							.putBoolean("sex", user.isSex()).putInt("age", user.getAge())
							.putString("userImage", user.getUserImage()).commit();
					AppConfig.USERID = name;
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				if (p_dialog.isShowing()) {
					p_dialog.dismiss();
				}
				Toast.makeText(LoginActivity.this, "登录超时，请检查网络！", Toast.LENGTH_SHORT).show();
			}

		});
	}

}
