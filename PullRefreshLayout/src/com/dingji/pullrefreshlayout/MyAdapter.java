package com.dingji.pullrefreshlayout;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private String[] mTextList = { "测试测试测试A", "测试测试测试B", "测试测试测试C", "测试测试测试D",
			"测试测试测试E", "测试测试测试F", "测试测试测试G", "测试测试测试H", "测试测试测试I", "测试测试测试G",
			"测试测试测试K", "测试测试测试L", "测试测试测试M", "测试测试测试N", "测试测试测试O", "测试测试测试P",
			"测试测试测试Q", "测试测试测试R", "测试测试测试S", "测试测试测试T" };
	private Context mContext;

	public class ViewHolder {
		private TextView mTestTv;
	}

	public MyAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return mTextList.length;
	}

	@Override
	public Object getItem(int position) {
		return mTextList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_my, null);
			viewHolder = new ViewHolder();
			viewHolder.mTestTv = (TextView) convertView
					.findViewById(R.id.tv_list_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.mTestTv.setText(mTextList[position]);
		return convertView;
	}

}
