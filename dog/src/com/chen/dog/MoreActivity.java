package com.chen.dog;

import java.io.Serializable;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.app.AppConfig;
import com.chen.model.User;
import com.chen.utils.JsonTools;

public class MoreActivity extends Activity implements Callback {

	private ProgressDialog pDialog;
	private ImageView userImage;
	private TextView nickname, age, distance, sex;
	private SharedPreferences spf;
	private User user;
	private FinalBitmap fb;
	private SharedPreferences sp;
	private Handler handler;
	private String[] ds = new String[] { "1km", "5km", "10km", "20km" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		fb = FinalBitmap.create(this);
		handler = new Handler(this);
		findViews();
	}

	@Override
	protected void onResume() {
		sp = getSharedPreferences("user", MODE_PRIVATE);
		spf = getSharedPreferences("setting", MODE_PRIVATE);
		distance.setText(spf.getString("distance", "5km"));
		user = new User(sp.getString("name", ""), sp.getString("pass", ""), sp.getString(
				"nickname", ""), sp.getBoolean("sex", true), sp.getInt("age", 0), sp.getString(
				"userImage", ""));
		if (!user.getUserImage().equals("")) {
			fb.configLoadfailImage(R.drawable.user_pic_default_1);
			fb.display(userImage, user.getUserImage());
		}
		if (user.isSex()) {
			sex.setText("女");
		} else {
			sex.setText("男");
		}
		if (user.getAge() != 0) {
			age.setText(user.getAge() + "岁");
		}
		nickname.setText(user.getNickname());
		super.onResume();
	}

	private void findViews() {
		userImage = (ImageView) findViewById(R.id.userImage);
		sex = (TextView) findViewById(R.id.sex);
		age = (TextView) findViewById(R.id.age);
		nickname = (TextView) findViewById(R.id.nickname);
		distance = (TextView) findViewById(R.id.text_distance);
	}

	public void userInfo(View view) {
		Intent intent = new Intent(this, UserActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("user", user);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void updateVersion(View view) {
		AppConfig.checkVersion(this, true);
	}

	public void favorite(View view) {
		FinalHttp fh = new FinalHttp();
		fh.get(AppConfig.URLPATH + "servlet/RestAction?action_flag=listFavorite&userId="
				+ AppConfig.USERID, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				pDialog = ProgressDialog.show(MoreActivity.this, "请等待", "正在为你加载数据...", true);
			}

			@Override
			public void onSuccess(String json) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				Intent intent = new Intent(MoreActivity.this, RestListActivity.class);
				intent.putExtra("rests", (Serializable) JsonTools.getRests("rests", json));
				startActivity(intent);
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				Toast.makeText(MoreActivity.this, "加载失败 请检查网络！", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void auth(View view) {
		Intent intent = new Intent(this, AuthActivity.class);
		startActivity(intent);
	}

	public void exitUser(View view) {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		AppConfig.USERID = "";
		finish();
		MainActivity.mainActivity.finish();
	}

	/**
	 * 选择范围
	 */
	public void range(View view) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("范围选择");
		builder.setItems(ds, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				spf.edit().putString("distance", ds[which]).commit();
				handler.sendEmptyMessage(which);
			}

		});
		builder.create().show();
	}

	@Override
	public boolean handleMessage(Message msg) {
		distance.setText(ds[msg.what]);
		String[] a = ds[msg.what].split("km");
		AppConfig.DISTANCE = Integer.parseInt(a[0]) * 1000;
		return false;
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
