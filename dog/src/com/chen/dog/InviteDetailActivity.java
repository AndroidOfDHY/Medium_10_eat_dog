package com.chen.dog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chen.model.Invite;

public class InviteDetailActivity extends Activity {

	private Invite invite;
	private TextView inviteTitle, inviteTime, inviteContent, inviteUserInfo, inviteContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_detail);
		inviteTitle = (TextView) findViewById(R.id.inviteTitle);
		inviteTime = (TextView) findViewById(R.id.inviteTime);
		inviteContent = (TextView) findViewById(R.id.inviteContent);
		inviteUserInfo = (TextView) findViewById(R.id.inviteUserInfo);
		inviteContact = (TextView) findViewById(R.id.inviteContact);

		invite = (Invite) getIntent().getSerializableExtra("invites");
		if (invite.isSex()) {
			inviteUserInfo
					.setText("邀请人: " + invite.getNickname() + "\t女\t" + invite.getAge() + "岁");
		} else {
			inviteUserInfo
					.setText("邀请人: " + invite.getNickname() + "\t男\t" + invite.getAge() + "岁");
		}
		inviteTitle.setText(invite.getTitle());
		inviteTime.setText("约期: " + invite.getInviteTime());
		inviteContent.setText(invite.getContent());
		inviteContact.setText("联系电话: " + invite.getContact());

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
