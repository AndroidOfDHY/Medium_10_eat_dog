package com.chen.dog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

public class RestDetailActivity extends Activity {

	private Restaurant rest;
	private List<Valuate> vals;
	private FinalBitmap fb;
	private ListView valList;
	private TextView restName, restTel, restAddress, restIntro;
	private ImageView restImage;
	private RatingBar restRecom;
	private ValListAdapter adapter;
	private ProgressDialog pDialog;
	private int from = 0;
	private String mImagePath;
	private Button mark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rest_detail);
		rest = (Restaurant) getIntent().getSerializableExtra("rest");
		fb = FinalBitmap.create(this);
		AbstractWeibo.initSDK(this);
		findViews();
		vals = new ArrayList<Valuate>();
		adapter = new ValListAdapter(this, vals);
		valList.setAdapter(adapter);

		restIntro.setText("简介：" + rest.getIntro());
		restName.setText(rest.getRestName());
		fb.display(restImage, rest.getImage());
		restTel.setText("电话：" + rest.getTel());
		restAddress.setText("地址：" + rest.getAddress());
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
		restIntro = (TextView) head.findViewById(R.id.restIntro);
		mark = (Button) head.findViewById(R.id.mark);
	}

	private void getValuates(final boolean isRefresh) {
		FinalHttp fh = new FinalHttp();
		fh.get(AppConfig.URLPATH + "servlet/ValuateAction?action_flag=valuates&restId="
				+ rest.getRestId() + "&from=" + from, new AjaxCallBack<String>() {
			@Override
			public void onStart() {
				pDialog = ProgressDialog.show(RestDetailActivity.this, "请等待", "正在为你加载数据...", true);
			}

			@Override
			public void onSuccess(String json) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				if (isRefresh) {
					vals.clear();
				}
				if (JsonTools.getValuates("valuates", json).size() == 0) {
					Toast.makeText(getApplicationContext(), "没有更多了", Toast.LENGTH_SHORT).show();
				} else {
					vals.addAll(JsonTools.getValuates("valuates", json));
					restRecom.setProgress(getRecom());
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				Toast.makeText(RestDetailActivity.this, "加载失败 请检查网络！", Toast.LENGTH_SHORT).show();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor actualimagecursor = managedQuery(data.getData(), proj, null, null, null);
				int actual_image_column_index = actualimagecursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				actualimagecursor.moveToFirst();
				mImagePath = actualimagecursor.getString(actual_image_column_index);
			}
			showShareGrid();
		}

	}

	private File createPhoto() {
		mImagePath = AppConfig.CAMERA + "share_photo.jpg";
		File file = new File(mImagePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 分享
	 * 
	 * @param v
	 */
	public void share(View v) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("分享方式");
		builder.setItems(new String[] { "拍照分享", "本地图片分享", "纯文本分享" },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = null;

						switch (which) {
						case 0:
							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createPhoto()));
							startActivityForResult(intent, 0);
							break;

						case 1:
							intent = new Intent(Intent.ACTION_PICK, null);
							createPhoto();
							intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									"image/*");
							startActivityForResult(intent, 1);
							break;
						case 2:
							showShareGrid();
							break;
						}
					}

				});
		builder.create().show();

	}

	private void showShareGrid() {
		Intent intent = new Intent(this, ShareAllGird.class);
		intent.putExtra("notif_icon", R.drawable.ic_launcher);
		// 分享时Notification的标题
		intent.putExtra("notif_title", this.getString(R.string.app_name));
		intent.putExtra("latitude", AppConfig.LATITUDE);
		intent.putExtra("longitude", AppConfig.LONGITUDE);
		if (mImagePath != null) {
			intent.putExtra("imagePath", mImagePath);
		}
		startActivity(intent);
	}

	/**
	 * 评价
	 * 
	 * @param v
	 */
	public void valuate(View v) {
		Intent intent = new Intent(RestDetailActivity.this, ValuateActivity.class);
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
		getValuates(false);
	}

	public void mark(View v) {
		FinalHttp fh = new FinalHttp();
		fh.get(AppConfig.URLPATH + "servlet/RestAction?action_flag=addFavorite&userId="
				+ AppConfig.USERID + "&restId=" + rest.getRestId(), new AjaxCallBack<String>() {

			@Override
			public void onSuccess(String json) {
				if (json.trim().toString().equals("true")) {
					Toast.makeText(getApplicationContext(), "收藏成功", Toast.LENGTH_SHORT).show();
					mark.setBackgroundResource(R.drawable.tool_favorite_1_btn);
				} else {
					Toast.makeText(getApplicationContext(), "已收藏", Toast.LENGTH_SHORT).show();

				}
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				Toast.makeText(getApplicationContext(), "收藏失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getValuates(true);
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
