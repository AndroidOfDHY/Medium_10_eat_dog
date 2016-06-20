package com.chen.dog;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Toast;

import com.chen.adapter.InviteListAdapter;
import com.chen.app.AppConfig;
import com.chen.listener.ShakeListener;
import com.chen.listener.ShakeListener.OnShakeListener;
import com.chen.model.Invite;
import com.chen.utils.JsonTools;

public class ShakeActivity extends Activity implements OnDrawerOpenListener, OnDrawerCloseListener,
		OnShakeListener, OnItemClickListener {

	ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;
	private RelativeLayout mTitle;

	private SlidingDrawer mDrawer;
	private Button mDrawerBtn;
	private ListView shakeList;
	private InviteListAdapter adapter;
	private List<Invite> invites;
	private ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake);
		// drawerSet ();//设置 drawer监听 切换 按钮的方向
		mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);
		mTitle = (RelativeLayout) findViewById(R.id.shake_title_bar);
		mDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
		mDrawerBtn = (Button) findViewById(R.id.handle);
		shakeList = (ListView) findViewById(R.id.shake_list);
		invites = new ArrayList<Invite>();
		adapter = new InviteListAdapter(this, invites);
		shakeList.setAdapter(adapter);
		shakeList.setOnItemClickListener(this);

		mDrawer.setOnDrawerOpenListener(this);
		/* 设定SlidingDrawer被关闭的事件处理 */
		mDrawer.setOnDrawerCloseListener(this);
		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, InviteDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("invites", invites.get(position));
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void getInvites() {
		FinalHttp fh = new FinalHttp();
		fh.get(AppConfig.URLPATH + "servlet/InviteAction?action_flag=shakeInvites",
				new AjaxCallBack<String>() {

					@Override
					public void onStart() {
						pDialog = ProgressDialog.show(ShakeActivity.this, "请等待", "正在为你加载数据...",
								true);
					}

					@Override
					public void onSuccess(String json) {
						if (pDialog.isShowing()) {
							pDialog.dismiss();
						}
						invites.clear();
						invites.addAll(JsonTools.getInvites("invites", json));
						adapter.notifyDataSetChanged();
						mDrawer.animateOpen();
						mVibrator.cancel();
						mShakeListener.start();
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						if (pDialog.isShowing()) {
							pDialog.dismiss();
						}
						Toast.makeText(ShakeActivity.this, "加载失败 请检查网络！", Toast.LENGTH_SHORT)
								.show();
					}
				});
	}

	public void startAnim() { // 定义摇一摇动画动画
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, -0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, +0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);

		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, +0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, -0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);
	}

	public void startVibrato() { // 定义震动
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1); // 第一个｛｝里面是节奏数组，
																	// 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}

	@Override
	public void onDrawerOpened() {
		mDrawerBtn.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.shake_report_dragger_down));
		TranslateAnimation titleup = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, -1.0f);
		titleup.setDuration(200);
		titleup.setFillAfter(true);
		mTitle.startAnimation(titleup);
	}

	@Override
	public void onDrawerClosed() {
		mDrawerBtn.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.shake_report_dragger_up));
		TranslateAnimation titledn = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0f);
		titledn.setDuration(200);
		titledn.setFillAfter(false);
		mTitle.startAnimation(titledn);
	}

	@Override
	public void onShake() {
		// Toast.makeText(getApplicationContext(),
		// "抱歉，暂时没有找到在同一时刻摇一摇的人。\n再试一次吧！", Toast.LENGTH_SHORT).show();
		startAnim(); // 开始 摇一摇手掌动画
		mShakeListener.stop();
		startVibrato(); // 开始 震动
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getInvites();
			}
		}, 2000);

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