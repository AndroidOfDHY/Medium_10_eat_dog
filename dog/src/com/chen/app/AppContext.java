package com.chen.app;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

public class AppContext extends Application {

	private static AppContext mInstance;
	public boolean m_bKeyRight = true;
	private BMapManager mBMapManager;

	public static final String strKey = "E0419AC3C23A37152DA50BE7D8938D9331563C25";

	@Override
	public void onCreate() {
		mInstance = this;
		initEngineManager(this);
		createDirs();
		super.onCreate();
	}

	private void createDirs() {
		File camera = new File(AppConfig.CAMERA);
		File apk = new File(AppConfig.APK);
		if (!camera.exists()) {
			camera.mkdirs();
		}
		if (!apk.exists()) {
			apk.mkdirs();
		}

	}

	public void initEngineManager(Context context) {
		if (getmBMapManager() == null) {
			setmBMapManager(new BMapManager(context));
		}

		if (!getmBMapManager().init(strKey, new MyGeneralListener())) {
			Toast.makeText(AppContext.getInstance().getApplicationContext(), "BMapManager  初始化错误!",
					Toast.LENGTH_LONG).show();
		}
	}

	public static AppContext getInstance() {
		return mInstance;
	}

	public BMapManager getmBMapManager() {
		return mBMapManager;
	}

	public void setmBMapManager(BMapManager mBMapManager) {
		this.mBMapManager = mBMapManager;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(AppContext.getInstance().getApplicationContext(), "您的网络出错啦！",
						Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(AppContext.getInstance().getApplicationContext(), "输入正确的检索条件！",
						Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(AppContext.getInstance().getApplicationContext(), "输入正确的授权Key！",
						Toast.LENGTH_LONG).show();
				AppContext.getInstance().m_bKeyRight = false;
			}
		}
	}
}
