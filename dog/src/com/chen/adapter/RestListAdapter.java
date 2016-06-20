package com.chen.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.dog.R;
import com.chen.model.Restaurant;

public class RestListAdapter extends BaseAdapter {

	private Context context;
	private List<Restaurant> rests;
	private FinalBitmap fb;

	static class ListItemView { // 自定义控件集合
		public TextView restName;
		public TextView restAddress;
		public ImageView restImage;
	}

	public RestListAdapter(Context context, List<Restaurant> rests) {
		this.context = context;
		this.rests = rests;
		fb = FinalBitmap.create(context);
	}

	@Override
	public int getCount() {
		return rests.size();
	}

	@Override
	public Object getItem(int position) {
		return rests.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItem = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_rests, null);
			listItem = new ListItemView();
			listItem.restName = (TextView) convertView.findViewById(R.id.rest_name);
			listItem.restAddress = (TextView) convertView.findViewById(R.id.rest_address);
			listItem.restImage = (ImageView) convertView.findViewById(R.id.rest_image);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItemView) convertView.getTag();
		}

		Restaurant rest = rests.get(position);
		listItem.restName.setText(rest.getRestName());
		fb.display(listItem.restImage, rest.getImage());
		listItem.restAddress.setText(rest.getAddress());

		return convertView;
	}

}
