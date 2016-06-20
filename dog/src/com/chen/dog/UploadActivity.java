package com.chen.dog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chen.app.AppConfig;

public class UploadActivity extends Activity {

	private double latitude, longitude;
	private EditText restName, intro, tel, address, type;
	private String mImagePath;
	private ImageView restImage;
	private boolean flag = false;
	private ProgressDialog p_dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		latitude = getIntent().getIntExtra("latitude", 0) / 1e6;
		longitude = getIntent().getIntExtra("longitude", 0) / 1e6;
		restName = (EditText) findViewById(R.id.restName);
		address = (EditText) findViewById(R.id.address);
		intro = (EditText) findViewById(R.id.intro);
		tel = (EditText) findViewById(R.id.tel);
		type = (EditText) findViewById(R.id.type);
		restImage = (ImageView) findViewById(R.id.restImage);
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
			Bitmap bp = BitmapFactory.decodeFile(mImagePath, null);
			restImage.setImageBitmap(bp);
			flag = true;
		}

	}

	/**
	 * 添加照片
	 */
	public void addPhoto(View view) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("添加照片");
		builder.setItems(new String[] { "拍照上传", "本地上传" }, new DialogInterface.OnClickListener() {

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
					intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					startActivityForResult(intent, 1);
					break;
				}
			}

		});
		builder.create().show();
	}

	private File createPhoto() {
		mImagePath = AppConfig.CAMERA + "upload_photo.jpg";
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

	public void upload(View v) {
		AjaxParams params = new AjaxParams();
		params.put("latitude", "" + latitude);
		params.put("longitude", "" + longitude);
		params.put("restName", restName.getText().toString().trim());
		params.put("intro", intro.getText().toString().trim());
		params.put("tel", tel.getText().toString().trim());
		params.put("address", address.getText().toString().trim());
		if (!type.getText().toString().equals("")) {
			params.put("type", type.getText().toString());
		} else {
			Toast.makeText(getApplicationContext(), "请选择一个类型", Toast.LENGTH_SHORT).show();
			return;
		}
		String reqPath = AppConfig.URLPATH + "servlet/RestAction?action_flag=add";
		try {
			if (flag) {
				params.put("image", new File(mImagePath));
				reqPath += "&flag=true";
			} else {
				reqPath += "&flag=false";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 上传文件
		FinalHttp fh = new FinalHttp();
		fh.post(reqPath, params, new AjaxCallBack<String>() {
			@Override
			public void onLoading(long count, long current) {
				p_dialog = ProgressDialog.show(UploadActivity.this, "请等待", "正在为您上传...", true);
			}

			@Override
			public void onSuccess(String t) {
				if (p_dialog.isShowing()) {
					p_dialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "上传成功！后台审核中...", Toast.LENGTH_SHORT).show();
				finish();
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
				if (p_dialog.isShowing()) {
					p_dialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "上传失败！请检查网络", Toast.LENGTH_SHORT).show();
			}

		});
	}

	public void selectType(View view) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("类型选择");
		final String[] restTypes = new String[] { "火锅", "食堂", "西餐", "KFC", "小吃", "家常菜", "小面", "其他" };
		builder.setItems(restTypes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				type.setText(restTypes[which]);
			}

		});
		builder.create().show();
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		backToMain();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			backToMain();
		}
		return true;
	}

	private void backToMain() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("over", true);
		setResult(MainActivity.UPLOAD_IS_OVER, resultIntent);
		MyMapView.isUpload = false;
		finish();
	}

}
