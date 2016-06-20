package com.chen.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chen.dog.R;
import com.chen.model.Valuate;

public class ValListAdapter extends BaseAdapter {

	private Context context;
	private List<Valuate> vals;
	private FinalBitmap fb;

	static class ListItemView { // 自定义控件集合
		public TextView nickName;
		public TextView time;
		public TextView content;
		public ImageView image;
		public ImageView userImage;
		public RatingBar recom;
	}

	public ValListAdapter(Context context, List<Valuate> vals) {
		this.context = context;
		this.vals = vals;
		fb = FinalBitmap.create(context);
	}

	@Override
	public int getCount() {
		return vals.size();
	}

	@Override
	public Object getItem(int position) {
		return vals.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItem = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_vals, null);
			listItem = new ListItemView();
			listItem.nickName = (TextView) convertView.findViewById(R.id.nickName);
			listItem.time = (TextView) convertView.findViewById(R.id.time);
			listItem.content = (TextView) convertView.findViewById(R.id.content);
			listItem.image = (ImageView) convertView.findViewById(R.id.image);
			listItem.userImage = (ImageView) convertView.findViewById(R.id.userImage);
			listItem.recom = (RatingBar) convertView.findViewById(R.id.recom);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItemView) convertView.getTag();
		}
		Valuate v = vals.get(position);
		listItem.nickName.setText(v.getNickName());
		listItem.time.setText(v.getTime());
		listItem.content.setText(v.getContent());
		listItem.recom.setProgress(v.getRecommend());
		if (v.getImage().equals("")) {
			listItem.image.setVisibility(View.GONE);
		} else {
			fb.display(listItem.image, v.getImage());
		}
		if (!v.getUserImage().equals("")) {
			fb.display(listItem.userImage, v.getUserImage());
		}

		return convertView;
	}
}
