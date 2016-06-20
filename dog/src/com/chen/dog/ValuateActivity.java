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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.chen.app.AppConfig;

public class ValuateActivity extends Activity {
	private EditText editText;
	private RatingBar ratingBar;
	private ImageView valImage;
	private int restId;
	private String mImagePath;
	private boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_valuate);
		restId = getIntent().getIntExtra("restId", 1);

		editText = (EditText) findViewById(R.id.content);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		valImage = (ImageView) findViewById(R.id.valImage);

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
			valImage.setImageBitmap(bp);
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
		mImagePath = AppConfig.CAMERA + "val_photo.jpg";
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
	 * 提交评价
	 * 
	 * @param v
	 */
	public void post(View v) {
		AjaxParams params = new AjaxParams();
		String reqPath = AppConfig.URLPATH + "servlet/ValuateAction?action_flag=valuate";
		params.put("userId", AppConfig.USERID);
		params.put("restId", "" + restId);
		params.put("content", editText.getText().toString());
		params.put("recommend", "" + ratingBar.getProgress());
		try {
			if (flag) {
				params.put("image", new File(mImagePath));
				reqPath += "&flag=true";
			} else {
				reqPath += "&flag=false";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} // 上传文件

		FinalHttp fh = new FinalHttp();

		fh.post(reqPath, params, new AjaxCallBack<String>() {

			@Override
			public void onSuccess(String t) {
				Toast.makeText(getApplicationContext(), "评价成功！", Toast.LENGTH_SHORT).show();
				finish();
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
				Toast.makeText(getApplicationContext(), "评价失败 请检查网络！", Toast.LENGTH_SHORT).show();
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
