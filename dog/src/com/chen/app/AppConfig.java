package com.chen.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

import com.chen.dog.R;
import com.chen.service.UpdateService;

public class AppConfig {
	public static int localVersion = 0;
	public static int serverVersion = 0;
	public static int DISTANCE = 5000;
	public static String USERID = "";
	public static float LATITUDE = 0;
	public static float LONGITUDE = 0;
	public final static String URLPATH = "http://58.221.43.228:8090/dog/";
	public final static String APKURL = URLPATH + "/apk/dog.apk";
	public final static String CAMERA = Environment.getExternalStorageDirectory().getPath()
			+ "/dog/Camera/";
	public final static String APK = Environment.getExternalStorageDirectory().getPath()
			+ "/dog/Apk/";

	private static int viewWidthInPix = -1;
	private static int viewHeightInPix = -1;

	/**
	 * 检查更新版本
	 */
	public static void checkVersion(final Context context, boolean isLast) {

		if (localVersion < serverVersion) {
			// 发现新版本，提示用户更新
			new AlertDialog.Builder(context).setTitle("软件升级").setMessage("发现新版本,建议立即更新使用.")
					.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// 开启更新服务UpdateService
							// 这里为了把update更好模块化，可以传一些updateService依赖的值
							// 如布局ID，资源ID，动态获取的标题,这里以app_name为例
							Intent updateIntent = new Intent(context, UpdateService.class);
							updateIntent.putExtra("titleId", R.string.app_name);
							context.startService(updateIntent);
						}
					}).setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create().show();
		} else if (isLast) {
			Toast.makeText(context, "你当前已经是最新版本了", Toast.LENGTH_SHORT).show();
		}
	}

	public static int getViewWidthInPix(Context context) {
		if (viewWidthInPix == -1) {
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			viewWidthInPix = manager.getDefaultDisplay().getWidth();
		}
		return viewWidthInPix;
	}

	public static int getViewHeightInPix(Context context) {
		if (viewHeightInPix == -1) {
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			viewHeightInPix = manager.getDefaultDisplay().getHeight();
		}
		return viewHeightInPix;
	}
}
