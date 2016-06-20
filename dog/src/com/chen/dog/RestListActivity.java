package com.chen.dog;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chen.adapter.RestListAdapter;
import com.chen.model.Restaurant;

public class RestListActivity extends Activity implements OnItemClickListener {

	private ListView listRest;
	private RestListAdapter adapter;
	private List<Restaurant> rests;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rest_list);
		listRest = (ListView) findViewById(R.id.listRest);
		listRest.setOnItemClickListener(this);

		rests = (List<Restaurant>) getIntent().getSerializableExtra("rests");
		adapter = new RestListAdapter(this, rests);
		listRest.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(RestListActivity.this, RestDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("rest", rests.get(position));
		intent.putExtras(bundle);
		startActivity(intent);
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
