package com.chen.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chen.dog.R;
import com.chen.model.Invite;

public class InviteListAdapter extends BaseAdapter {

	private Context context;
	private List<Invite> invites;

	static class ListItemView { // 自定义控件集合
		public TextView nickName;
		public TextView time;
		public TextView title;
	}

	public InviteListAdapter(Context context, List<Invite> invites) {
		this.context = context;
		this.invites = invites;
	}

	@Override
	public int getCount() {
		return invites.size();
	}

	@Override
	public Object getItem(int position) {
		return invites.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItem = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_invites, null);
			listItem = new ListItemView();
			listItem.nickName = (TextView) convertView.findViewById(R.id.invite_nickname);
			listItem.time = (TextView) convertView.findViewById(R.id.invite_time);
			listItem.title = (TextView) convertView.findViewById(R.id.invite_title);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItemView) convertView.getTag();
		}
		Invite invite = invites.get(position);
		listItem.title.setText(invite.getTitle());
		listItem.time.setText(invite.getInviteTime());
		listItem.nickName.setText(invite.getNickname());

		return convertView;
	}

}
