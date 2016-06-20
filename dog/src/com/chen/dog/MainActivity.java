package com.chen.dog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.chen.app.AppConfig;
import com.chen.app.AppContext;
import com.chen.model.Restaurant;
import com.chen.utils.BMapUtil;
import com.chen.utils.DensityUtil;
import com.chen.utils.JsonTools;
import com.chen.utils.MyAnimationUtils;

public class MainActivity extends Activity implements OnClickListener {

	public static final int UPLOAD_IS_OVER = 0x11;
	public static Activity mainActivity;
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();

	// 定位图层
	private MyLocationOverlay myLocationOverlay = null;
	private MyMapView mapView;
	private MapController mMapController;
	private MyOverlay mOverlay;
	private PopupOverlay pop = null;
	private ArrayList<OverlayItem> mItems = null;
	private TextView popupText = null;
	private View viewCache = null;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;
	private Button requestLocButton = null;
	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位
	boolean isLocationClientStop = false;
	private List<Restaurant> rests;
	private int mCurrent;
	private long exitTime = 0;
	private int from = 0;
	private ProgressDialog pDialog;
	private PopupWindow mPopup;
	private View popView;
	private LinearLayout menu;
	private RelativeLayout topBar;
	private String curType;
	private TextView curText;
	private Drawable[] markers;
	private int popIds[];
	private String[] popStrs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mainActivity = this;
		AppContext app = (AppContext) this.getApplication();
		if (app.getmBMapManager() == null) {
			app.setmBMapManager(new BMapManager(this));
		}
		AppConfig.checkVersion(this, false);
		setContentView(R.layout.activity_main);
		menu = (LinearLayout) findViewById(R.id.main_menu);
		topBar = (RelativeLayout) findViewById(R.id.relative);
		curText = (TextView) findViewById(R.id.curText);
		requestLocButton = (Button) findViewById(R.id.locBtn);
		requestLocButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 手动定位请求
				requestLocClick();
			}
		});

		mapView = (MyMapView) findViewById(R.id.bmapView);
		mMapController = mapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(15);
		// mapView.setBuiltInZoomControls(true);

		// 定位初始化
		mLocClient = new LocationClient(getApplicationContext());
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		option.disableCache(true);
		mLocClient.setLocOption(option);
		mLocClient.start();

		initPop();
		initOverlayMarker();
		// 定位图层初始化
		myLocationOverlay = new MyLocationOverlay(mapView);
		// 设置定位数据
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mapView.refresh();

	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || isLocationClientStop)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			AppConfig.LATITUDE = (float) location.getLatitude();
			AppConfig.LONGITUDE = (float) location.getLongitude();

			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = location.getRadius();
			locData.direction = location.getDerect();
			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mapView.refresh();
			// 是手动触发请求或首次定位时，移动到定位点
			if (isRequest || isFirstLoc) {
				// 移动地图到定位点
				mMapController.animateTo(new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
				isRequest = false;
				if (menu.getVisibility() == View.GONE) {
					menu.setVisibility(View.VISIBLE);
					menu.startAnimation(MyAnimationUtils.animationUp());
				}
			}
			// 首次定位完成
			isFirstLoc = false;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLocClick() {
		isRequest = true;
		mLocClient.requestLocation();
		Toast.makeText(MainActivity.this, "正在定位…", Toast.LENGTH_SHORT).show();
	}

	private void initOverlay() {
		// 容园食堂106.329854,29.599825
		// 禾园食堂106.333357,29.598427
		if (mOverlay != null) {
			mapView.getOverlays().remove(mOverlay);
		}
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka), mapView);
		for (int i = 0; i < rests.size(); i++) {
			Restaurant r = rests.get(i);
			GeoPoint p = new GeoPoint((int) (r.getLatitude() * 1E6), (int) (r.getLongitude() * 1E6));
			OverlayItem item = new OverlayItem(p, r.getRestName(), "");
			item.setMarker(markers[i]);
			mOverlay.addItem(item);
		}
		mItems = new ArrayList<OverlayItem>();
		mItems.addAll(mOverlay.getAllItem());
		mapView.getOverlays().add(mOverlay);
		mapView.refresh();

		/**
		 * 向地图添加自定义View.
		 */

		viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
		popupInfo = (View) viewCache.findViewById(R.id.popinfo);
		popupLeft = (View) viewCache.findViewById(R.id.popleft);
		popupRight = (View) viewCache.findViewById(R.id.popright);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);

		/**
		 * 创建一个popupoverlay
		 */
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				if (index == 0) {
					// 邀约
					Intent intent = new Intent(MainActivity.this, InviteListActivity.class);
					intent.putExtra("restId", rests.get(mCurrent).getRestId());
					startActivity(intent);
				} else if (index == 2) {
					// 详情
					Intent intent = new Intent(MainActivity.this, RestDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("rest", rests.get(mCurrent));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		};
		pop = new PopupOverlay(mapView, popListener);

	}

	public void getRests(final String type) {
		FinalHttp fh = new FinalHttp();
		fh.get(AppConfig.URLPATH + "servlet/RestAction?action_flag=rests&type=" + type + "&from="
				+ from + "&lat1=" + locData.latitude + "&lng1=" + locData.longitude + "&d="
				+ AppConfig.DISTANCE, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				pDialog = ProgressDialog.show(MainActivity.this, "请等待", "正在为你加载数据...", true);
			}

			@Override
			public void onSuccess(String json) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				if (JsonTools.getRests("rests", json).size() == 0 & from > 0) {
					Toast.makeText(getApplicationContext(), "没有更多了", Toast.LENGTH_SHORT).show();
				} else {
					rests = JsonTools.getRests("rests", json);
					curType = type;
					curText.setText(curType);
					if (topBar.getVisibility() == View.GONE) {
						topBar.setVisibility(View.VISIBLE);
						topBar.startAnimation(MyAnimationUtils.animationDown());
					}
				}

				initOverlay();
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				Toast.makeText(MainActivity.this, "加载失败 请检查网络！", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onPause() {
		isLocationClientStop = true;
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		isLocationClientStop = false;
		mapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();
		isLocationClientStop = true;
		mapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mapView.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		mapView.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}

	class MyOverlay extends ItemizedOverlay<OverlayItem> {
		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		protected boolean onTap(int index) {
			OverlayItem item = getItem(index);
			mCurrent = index;
			popupText.setText(getItem(index).getTitle());
			Bitmap[] bitMaps = { BMapUtil.getBitmapFromView(popupLeft),
					BMapUtil.getBitmapFromView(popupInfo), BMapUtil.getBitmapFromView(popupRight) };
			pop.showPopup(bitMaps, item.getPoint(), 32);
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			if (pop != null) {
				pop.hidePop();
			}
			return false;
		}

	}

	public void shake(View v) {
		Intent intent = new Intent(this, ShakeActivity.class);
		startActivity(intent);
	}

	public void more(View v) {
		Intent intent = new Intent(this, MoreActivity.class);
		startActivity(intent);
	}

	public void upload(View v) {
		MyMapView.isUpload = true;
		if (mOverlay != null) {
			mapView.getOverlays().remove(mOverlay);
		}
		if (topBar.getVisibility() == View.VISIBLE) {
			topBar.startAnimation(MyAnimationUtils.animationDownBack());
			topBar.setVisibility(View.GONE);
		}
		if (menu.getVisibility() == View.VISIBLE) {
			menu.startAnimation(MyAnimationUtils.animationUpBack());
			menu.setVisibility(View.GONE);
		}
		requestLocButton.setVisibility(View.GONE);

		// mapView.getOverlays().remove(myLocationOverlay);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == UPLOAD_IS_OVER) {
			requestLocButton.setVisibility(View.VISIBLE);
			isRequest = true;
			mLocClient.requestLocation();
			from = 0;
		}

	}

	public void search(View v) {
		if (!mPopup.isShowing()) {
			mPopup.showAtLocation(popView, Gravity.BOTTOM, 0, DensityUtil.dip2px(this, 50));
		} else {
			mPopup.dismiss();
		}

	}

	public void loadMore(View v) {
		from += 1;
		getRests(curType);
	}

	public void listRest(View v) {
		Intent intent = new Intent(this, RestListActivity.class);
		intent.putExtra("rests", (Serializable) rests);
		startActivity(intent);
	}

	public void initPop() {
		popView = getLayoutInflater().inflate(R.layout.type_select, null);
		mPopup = new PopupWindow(popView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mPopup.setFocusable(true);
		mPopup.setOutsideTouchable(true);
		mPopup.update();
		mPopup.setBackgroundDrawable(new BitmapDrawable());
		popIds = new int[] { R.id.pop0, R.id.pop1, R.id.pop2, R.id.pop3, R.id.pop4, R.id.pop5,
				R.id.pop6, R.id.pop7, };
		popStrs = new String[] { "火锅", "食堂", "西餐", "KFC", "小吃", "家常菜", "小面", "其他", };
		for (int i = 0; i < popIds.length; i++) {
			popView.findViewById(popIds[i]).setOnClickListener(this);
		}
	}

	/*
	 * 按两次返回退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			long now = System.currentTimeMillis();
			if ((now - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出好吃狗", Toast.LENGTH_SHORT).show();
				exitTime = now;
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < popIds.length; i++) {
			if (v.getId() == popIds[i]) {
				from = 0;
				getRests(popStrs[i]);
				break;
			}
		}
		mPopup.dismiss();

	}

	private void initOverlayMarker() {
		markers = new Drawable[] { getResources().getDrawable(R.drawable.icon_marka),
				getResources().getDrawable(R.drawable.icon_markb),
				getResources().getDrawable(R.drawable.icon_markc),
				getResources().getDrawable(R.drawable.icon_markd),
				getResources().getDrawable(R.drawable.icon_marke),
				getResources().getDrawable(R.drawable.icon_markf),
				getResources().getDrawable(R.drawable.icon_markg),
				getResources().getDrawable(R.drawable.icon_markh),
				getResources().getDrawable(R.drawable.icon_marki),
				getResources().getDrawable(R.drawable.icon_markj) };
	}
}

class MyMapView extends MapView {

	static boolean isUpload = false;
	float x1, x2, y1, y2;

	public MyMapView(Context context) {
		super(context);
	}

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x1 = event.getX();
			y1 = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			x2 = event.getX();
			y2 = event.getY();
			if (isUpload & x1 == x2 & y1 == y2) {
				GeoPoint point = this.getProjection().fromPixels((int) x2, (int) y2);
				Intent intent = new Intent(MainActivity.mainActivity, UploadActivity.class);
				intent.putExtra("latitude", point.getLatitudeE6());
				intent.putExtra("longitude", point.getLongitudeE6());
				MainActivity.mainActivity.startActivityForResult(intent,
						MainActivity.UPLOAD_IS_OVER);
			}
			break;
		}
		return super.onTouchEvent(event);
	}
}
