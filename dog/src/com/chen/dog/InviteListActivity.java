package com.chen.dog;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.chen.adapter.InviteListAdapter;
import com.chen.app.AppConfig;
import com.chen.model.Invite;
import com.chen.utils.JsonTools;

public class InviteListActivity extends Activity implements OnItemClickListener {

	private ProgressDialog pDialog;
	private int from = 0;
	private List<Invite> invites;
	private InviteListAdapter adapter;
	private ListView inviteList;
	private View footView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_list);
		inviteList = (ListView) findViewById(R.id.inviteList);
		footView = LayoutInflater.from(this).inflate(R.layout.invite_foot, null);
		inviteList.addFooterView(footView);
		invites = new ArrayList<Invite>();
		adapter = new InviteListAdapter(this, invites);
		inviteList.setAdapter(adapter);
		inviteList.setOnItemClickListener(this);
		getInvites();
	}

	private void getInvites() {
		FinalHttp fh = new FinalHttp();
		fh.get(AppConfig.URLPATH + "servlet/InviteAction?action_flag=invites&restId="
				+ getIntent().getIntExtra("restId", 1) + "&from=" + from,
				new AjaxCallBack<String>() {

					@Override
					public void onStart() {
						pDialog = ProgressDialog.show(InviteListActivity.this, "请等待",
								"正在为你加载数据...", true);
					}

					@Override
					public void onSuccess(String json) {
						if (pDialog.isShowing()) {
							pDialog.dismiss();
						}
						if (JsonTools.getInvites("invites", json).size() == 0) {
							Toast.makeText(InviteListActivity.this, "没有更多了", Toast.LENGTH_SHORT)
									.show();
						} else {
							invites.addAll(JsonTools.getInvites("invites", json));
							adapter.notifyDataSetChanged();
						}
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						if (pDialog.isShowing()) {
							pDialog.dismiss();
						}
						Toast.makeText(InviteListActivity.this, "加载失败 请检查网络！", Toast.LENGTH_SHORT)
								.show();
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, InviteDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("invites", invites.get(position));
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void loadMore(View view) {
		from += 1;
		getInvites();
	}

	/**
	 * 发出邀约
	 * 
	 * @param view
	 */
	public void sendInvite(View view) {
		Intent intent = new Intent(this, InviteActivity.class);
		intent.putExtra("restId", getIntent().getIntExtra("restId", 1));
		startActivity(intent);
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
