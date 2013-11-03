package com.dingji.pullrefreshlayout;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener,
		PullRefreshListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	private PullRefreshLayout mPullRefreshLayout;

	private ListView mStudentListView;

	private Context mContext;

	private MyAdapter mMyAdapter;
	
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initMembers();
	}

	private void initMembers() {
		mContext = getApplicationContext();
		mHandler = new Handler();
		mMyAdapter = new MyAdapter(mContext);
		initViews();
	}

	private void initViews() {
		mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.layout_pull_refresh);
		mStudentListView = (ListView) findViewById(R.id.listview_test);
		mStudentListView.setAdapter(mMyAdapter);
		mStudentListView.setOnItemClickListener(this);
		mPullRefreshLayout.setPullRreshListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Toast.makeText(mContext, "this is item is clicked.", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public boolean isRefreshReady() {
		boolean result = false;
		if (mStudentListView != null
				&& mStudentListView.getFirstVisiblePosition() == 0) {
			View childView = mStudentListView.getChildAt(0);
			if (childView != null) {
				result = childView.getTop() == 0;
			}
		}
		return result;
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(getRefreshCompletedRunnable(), 5000L);
	}

	private Runnable getRefreshCompletedRunnable() {
		return new Runnable() {
			
			@Override
			public void run() {
				mPullRefreshLayout.onRefershComplete();
			}
		};
	}

}
