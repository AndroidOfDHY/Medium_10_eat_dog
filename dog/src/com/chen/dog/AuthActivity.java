package com.chen.dog;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.Toast;
import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.framework.WeiboActionListener;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;

public class AuthActivity extends Activity implements OnClickListener, WeiboActionListener,
		Callback {

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		handler = new Handler(this);
		AbstractWeibo.initSDK(this);

		findViewById(R.id.ctvSw).setOnClickListener(this);
		findViewById(R.id.ctvTc).setOnClickListener(this);
		findViewById(R.id.ctvRr).setOnClickListener(this);
		findViewById(R.id.ctvQz).setOnClickListener(this);

		// 获取平台列表
		AbstractWeibo[] weibos = AbstractWeibo.getWeiboList(this);
		for (AbstractWeibo weibo : weibos) {
			if (!weibo.isValid()) {
				continue;
			}

			CheckedTextView ctv = getView(weibo);
			if (ctv != null) {
				ctv.setChecked(true);
				String userName = weibo.getDb().get("nickname"); // getAuthedUserName();
				if (userName == null || userName.length() <= 0 || "null".equals(userName)) {
					// 如果平台已经授权却没有拿到帐号名称，则自动获取用户资料，以获取名称
					userName = weibo.getName();
					weibo.setWeiboActionListener(this);
					weibo.showUser(null);
				}
				ctv.setText(userName);
			}
		}
	}

	@Override
	public void onClick(View v) {
		final AbstractWeibo weibo = getWeibo(v.getId());
		final CheckedTextView ctv = (CheckedTextView) v;
		if (weibo == null) {
			ctv.setChecked(false);
			ctv.setText(R.string.not_yet_authorized);
			return;
		}

		if (weibo.isValid()) {
			new AlertDialog.Builder(this).setTitle("友情提示").setMessage("你确定要取消授权?")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							weibo.removeAccount();
							ctv.setChecked(false);
							ctv.setText(R.string.not_yet_authorized);
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create().show();
			return;
		}

		weibo.setWeiboActionListener(this);
		weibo.showUser(null);
	}

	private AbstractWeibo getWeibo(int vid) {
		String name = null;
		switch (vid) {
		case R.id.ctvSw:
			name = SinaWeibo.NAME;
			break;
		case R.id.ctvTc:
			name = TencentWeibo.NAME;
			break;
		case R.id.ctvRr:
			name = Renren.NAME;
			break;
		case R.id.ctvQz:
			name = QZone.NAME;
			break;
		}

		if (name != null) {
			return AbstractWeibo.getWeibo(this, name);
		}
		return null;
	}

	private CheckedTextView getView(AbstractWeibo weibo) {
		if (weibo == null) {
			return null;
		}

		String name = weibo.getName();
		if (name == null) {
			return null;
		}

		View v = null;
		if (SinaWeibo.NAME.equals(name)) {
			v = findViewById(R.id.ctvSw);
		} else if (TencentWeibo.NAME.equals(name)) {
			v = findViewById(R.id.ctvTc);
		} else if (Renren.NAME.equals(name)) {
			v = findViewById(R.id.ctvRr);
		} else if (QZone.NAME.equals(name)) {
			v = findViewById(R.id.ctvQz);
		}
		if (v == null) {
			return null;
		}

		if (!(v instanceof CheckedTextView)) {
			return null;
		}

		return (CheckedTextView) v;
	}

	@Override
	public void onComplete(AbstractWeibo weibo, int action, HashMap<String, Object> res) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	@Override
	public void onError(AbstractWeibo weibo, int action, Throwable t) {
		t.printStackTrace();

		Message msg = new Message();
		msg.arg1 = 2;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	@Override
	public void onCancel(AbstractWeibo weibo, int action) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	/**
	 * 处理操作结果
	 * <p>
	 * 如果获取到用户的名称，则显示名称；否则如果已经授权，则显示 平台名称
	 */
	@Override
	public boolean handleMessage(Message msg) {
		AbstractWeibo weibo = (AbstractWeibo) msg.obj;
		switch (msg.arg1) {
		case 1: { // 成功
			Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
		}
			break;
		case 2: { // 失败
			Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
			return false;
		}
		case 3: { // 取消
			Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
			return false;
		}
		}

		CheckedTextView ctv = getView(weibo);
		if (ctv != null) {
			ctv.setChecked(true);
			String userName = weibo.getDb().get("nickname"); // getAuthedUserName();
			if (userName == null || userName.length() <= 0 || "null".equals(userName)) {
				userName = weibo.getName();
			}
			ctv.setText(userName);
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		AbstractWeibo.stopSDK(this);
		super.onDestroy();
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
