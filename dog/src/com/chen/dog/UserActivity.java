package com.chen.dog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.chen.app.AppConfig;
import com.chen.model.User;

public class UserActivity extends Activity implements OnCheckedChangeListener {
	private User user;
	private EditText nickname, age;
	private ImageView userImage;
	private String mImagePath;
	private boolean flag = false;
	private RadioGroup rGroup;
	private RadioButton radioFemale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		user = (User) getIntent().getSerializableExtra("user");
		nickname = (EditText) findViewById(R.id.nickname);
		age = (EditText) findViewById(R.id.age);
		userImage = (ImageView) findViewById(R.id.userImage);
		rGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioFemale = (RadioButton) findViewById(R.id.radiFemale);
		rGroup.setOnCheckedChangeListener(this);

		nickname.setText(user.getNickname());
		age.setText(user.getAge() + "");
		if (user.isSex()) {
			radioFemale.setChecked(true);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case 0:
				File file = new File(mImagePath);
				startPhotoZoom(Uri.fromFile(file));
				break;
			case 1:
				startPhotoZoom(data.getData());
				break;
			case 2:
				if (data != null) {
					setPicToView(data);
				}
				break;

			}
		}

	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param data
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			try {
				File file = new File(mImagePath);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩到流中
				flag = true;
				bos.flush();// 输出
				bos.close();// 关闭
			} catch (Exception e) {
				e.printStackTrace();
			}

			userImage.setImageBitmap(photo);
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param data
	 */
	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
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
		mImagePath = AppConfig.CAMERA + "head_photo.jpg";
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

	public void post(View v) {
		user.setNickname(nickname.getText().toString().trim());
		user.setAge(Integer.parseInt(age.getText().toString().trim()));
		AjaxParams params = new AjaxParams();
		params.put("userId", user.getUserId());
		params.put("nickname", user.getNickname());
		params.put("age", user.getAge() + "");
		params.put("sex", "" + user.isSex());
		String reqPath = AppConfig.URLPATH + "servlet/UserAction";
		try {
			if (flag) {
				params.put("image", new File(mImagePath));
				reqPath += "?action_flag=update";
			} else {
				reqPath += "?action_flag=updateNoImage";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} // 上传文件

		FinalHttp fh = new FinalHttp();
		fh.post(reqPath, params, new AjaxCallBack<String>() {

			@Override
			public void onSuccess(String json) {
				user.setUserImage(json.toString().trim());
				if (!user.getUserImage().equals("fail")) {
					SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
					sp.edit().putString("name", user.getUserId())
							.putString("pass", user.getUserPass())
							.putString("nickname", user.getNickname())
							.putBoolean("sex", user.isSex()).putInt("age", user.getAge())
							.putString("userImage", user.getUserImage()).commit();
					finish();
					Toast.makeText(UserActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
				Toast.makeText(UserActivity.this, "修改失败 请检查网络！", Toast.LENGTH_SHORT).show();
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

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (checkedId == R.id.radiFemale) {
			user.setSex(true);
		} else if (checkedId == R.id.radioMale) {
			user.setSex(false);
		}
	}
}
