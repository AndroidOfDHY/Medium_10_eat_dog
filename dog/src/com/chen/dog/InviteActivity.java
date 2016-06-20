package com.chen.dog;

import java.util.Calendar;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chen.app.AppConfig;

public class InviteActivity extends Activity {
	private int restId;
	private EditText titleEdit, contentEdit, contactEdit, timeEdit;
	private String date;
	private String time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite);

		restId = getIntent().getIntExtra("restId", 1);

		titleEdit = (EditText) findViewById(R.id.titleEdit);
		contentEdit = (EditText) findViewById(R.id.contentEdit);
		contactEdit = (EditText) findViewById(R.id.contactEdit);
		timeEdit = (EditText) findViewById(R.id.timeEdit);
	}

	public void selectTime(View v) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.date_pick);
		dialog.setTitle("选择时间");
		DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
		TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
		date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-"
				+ datePicker.getDayOfMonth();
		time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + ":00";
		Calendar calendar = Calendar.getInstance();
		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
					}
				});
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				time = hourOfDay + ":" + minute + ":00";
			}
		});

		Button button = (Button) dialog.findViewById(R.id.btn_sure);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				timeEdit.setText(date + " " + time);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void invite(View v) {
		AjaxParams params = new AjaxParams();

		params.put("userId", AppConfig.USERID);
		params.put("restId", "" + restId);
		params.put("title", titleEdit.getText().toString().trim());
		params.put("content", contentEdit.getText().toString().trim());
		params.put("contact", contactEdit.getText().toString().trim());
		params.put("inviteTime", timeEdit.getText().toString().trim());

		FinalHttp fh = new FinalHttp();
		fh.post(AppConfig.URLPATH + "servlet/InviteAction?action_flag=invite", params,
				new AjaxCallBack<String>() {

					@Override
					public void onSuccess(String json) {
						Toast.makeText(getApplicationContext(), "邀约信息已发送", Toast.LENGTH_SHORT)
								.show();
						finish();
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						super.onFailure(t, strMsg);
						Toast.makeText(getApplicationContext(), "邀约信息发送失败，请检测网络！",
								Toast.LENGTH_SHORT).show();
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
