package com.chen.dog;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.onekeyshare.ShareAllGird;

import com.chen.adapter.ValListAdapter;
import com.chen.app.AppConfig;
import com.chen.model.Restaurant;
import com.chen.model.Valuate;
import com.chen.utils.JsonTools;

public class DetailActivity extends Activity {

	private Restaurant rest;
	private List<Valuate> vals;
	private FinalBitmap fb;
	private ListView valList;
	private TextView restName, restTel, restAddress;
	private ImageView restImage;
	private RatingBar restRecom;
	private ValListAdapter adapter;
	private ProgressDialog pDialog;
	private int from = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		rest = (Restaurant) getIntent().getSerializableExtra("rest");
		fb = FinalBitmap.create(this);
		AbstractWeibo.initSDK(this);
		vals = new ArrayList<Valuate>();
		getValuates();
		findViews();
		adapter = new ValListAdapter(this, vals);
		valList.setAdapter(adapter);

		restName.setText(rest.getRestName());
		fb.display(restImage, rest.getImage());
		restTel.setText(rest.getTel());
		restAddress.setText(rest.getAddress());
	}

	private void findViews() {
		valList = (ListView) findViewById(R.id.valList);
		View head = LayoutInflater.from(this).inflate(R.layout.detail_head, null);
		View foot = LayoutInflater.from(this).inflate(R.layout.detail_foot, null);
		valList.addHeaderView(head);
		valList.addFooterView(foot);
		restName = (TextView) head.findViewById(R.id.restName);
		restTel = (TextView) head.findViewById(R.id.restTel);
		restAddress = (TextView) head.findViewById(R.id.restAddress);
		restImage = (ImageView) head.findViewById(R.id.restImage);
		restRecom = (RatingBar) head.findViewById(R.id.restRecom);
	}

	private void getValuates() {
		FinalHttp fh = new FinalHttp();
		fh.get(AppConfig.URLPATH + "servlet/ValuateAction?action_flag=valuates&restId="
				+ rest.getRestId() + "&from=" + from, new AjaxCallBack<String>() {
			@Override
			public void onStart() {
				pDialog = ProgressDialog.show(DetailActivity.this, "请等待", "正在为你加载数据...", true);
			}

			@Override
			public void onSuccess(String json) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				vals.addAll(JsonTools.getValuates("valuates", json));
				restRecom.setProgress(getRecom());
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				Toast.makeText(DetailActivity.this, "加载失败 请检查网络！", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private int getRecom() {
		int recom = 0;
		int i = vals.size();
		if (i == 0) {
			return 0;
		}
		for (int j = 0; j < i; j++) {
			recom += vals.get(j).getRecommend();
		}
		return recom / i;
	}

	/**
	 * 分享
	 * 
	 * @param v
	 */
	public void share(View v) {
		Intent intent = new Intent(this, ShareAllGird.class);
		intent.putExtra("notif_icon", R.drawable.ic_launcher);
		// 分享时Notification的标题
		intent.putExtra("notif_title", this.getString(R.string.app_name));
		intent.putExtra("latitude", getIntent().getFloatExtra("latitude", 0));
		intent.putExtra("longitude", getIntent().getFloatExtra("longitude", 0));
		intent.putExtra("imagePath", Environment.getExternalStorageDirectory() + "/pic.jpg");
		startActivity(intent);
	}

	/**
	 * 评价
	 * 
	 * @param v
	 */
	public void valuate(View v) {
		Intent intent = new Intent(DetailActivity.this, ValuateActivity.class);
		intent.putExtra("restId", rest.getRestId());
		startActivity(intent);
	}

	/**
	 * 加载更多
	 * 
	 * @param v
	 */
	public void loadMore(View v) {
		from += 1;
		getValuates();
	}

	@Override
	protected void onDestroy() {
		AbstractWeibo.stopSDK(this);
		super.onDestroy();
	}
}
