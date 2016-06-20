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

public class RegisterActivity extends Activity {

	private EditText name, pass;
	private ProgressDialog p_dialog;
	private String n, p;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		name = (EditText) findViewById(R.id.userName);
		pass = (EditText) findViewById(R.id.userPass);

		sp = getSharedPreferences("user", MODE_PRIVATE);
	}

	public void register(View v) {
		n = name.getText().toString().trim();
		p = pass.getText().toString().trim();
		if (n.equals("")) {
			name.requestFocus();
			name.setError("用户名不能为空！");
		} else if (p.equals("")) {
			pass.requestFocus();
			pass.setError("密码不能为空！");
		} else if (p.length() < 6 || p.length() > 12) {
			pass.requestFocus();
			pass.setError("密码长度不正确！");
		} else {
			registerByUser();
		}
	}

	private void registerByUser() {
		FinalHttp fh = new FinalHttp();
		fh.get(AppConfig.URLPATH + "servlet/UserAction?action_flag=register&userId=" + n
				+ "&userPass=" + p, new AjaxCallBack<String>() {
			@Override
			public void onStart() {
				p_dialog = ProgressDialog.show(RegisterActivity.this, "请等待", "正在为您注册...", true);
			}

			@Override
			public void onSuccess(String json) {
				if (p_dialog.isShowing()) {
					p_dialog.dismiss();
				}
				if (json.toString().trim().equals("true")) {
					sp.edit().putString("name", n).putString("pass", p).commit();
					User user = new User();
					user.setUserId(n);
					user.setUserPass(p);
					Intent intent = new Intent(RegisterActivity.this, UserActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", user);
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				} else {
					name.requestFocus();
					name.setError("用户名已存在!");
				}
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				if (p_dialog.isShowing()) {
					p_dialog.dismiss();
				}
				Toast.makeText(RegisterActivity.this, "注册超时，请检查网络！", Toast.LENGTH_SHORT).show();
			}

		});

	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

}
