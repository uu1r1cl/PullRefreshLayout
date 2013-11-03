package com.dingji.pullrefreshlayout;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PullRefreshHeaderView extends RelativeLayout {

	private Animation mArrowUpAnimation;//箭头向上转的动画

	private Animation mArrowDownAnimation;//箭头向下转的动画

	private ProgressBar mRefreshProgressBar;//加载中的progressbar

	private ImageView mArrowImageView;//箭头image view

	private TextView mRefreshTimeTextView;//下拉可以刷新，释放可以刷新，加载这些语言的text view

	private TextView mInstructionTextView;//更新于：07-08 15：20,加载更新时间的text view

	private String mPullToRefreshStr;// 下拉可以刷新

	private String mReleaseToRefreshStr;// 释放可以刷新

	private String mRefreshingStr;// 加载中...

	private String mRefreshAtStr;// 更新于：

	public PullRefreshHeaderView(Context context) {
		super(context);
		initMembers(context);
	}

	public PullRefreshHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initMembers(context);
	}

	private void initMembers(Context context) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		layoutInflater.inflate(R.layout.pull_refresh_header, this);

		mArrowImageView = (ImageView) findViewById(R.id.imageview_arrow);
		mRefreshProgressBar = (ProgressBar) findViewById(R.id.progressbar_refresh);
		mRefreshTimeTextView = (TextView) findViewById(R.id.textview_refresh_time);
		mInstructionTextView = (TextView) findViewById(R.id.textview_instruction);
		mArrowDownAnimation = AnimationUtils.loadAnimation(context,
				R.anim.rotate_down);
		mArrowUpAnimation = AnimationUtils.loadAnimation(context,
				R.anim.rotate_up);
		mArrowUpAnimation.setFillAfter(true);
		mArrowDownAnimation.setFillAfter(true);

		mPullToRefreshStr = context.getResources().getString(
				R.string.pull_to_refresh);
		mReleaseToRefreshStr = context.getResources().getString(
				R.string.release_to_refresh);
		mRefreshingStr = context.getResources().getString(R.string.refreshing);
		mRefreshAtStr = context.getResources().getString(R.string.refresh_at);
	}

	public void onRefresh(String refreshTime) {
		mArrowImageView.clearAnimation();
		mArrowImageView.setVisibility(View.INVISIBLE);
		mRefreshProgressBar.setVisibility(View.VISIBLE);
		mInstructionTextView.setText(mRefreshingStr);
		if (!TextUtils.isEmpty(refreshTime)) {
			String text = mRefreshAtStr + refreshTime;
			mRefreshTimeTextView.setText(text);
		}
	}

	public void onPullToRefresh(String refreshTime) {
		mRefreshProgressBar.setVisibility(View.INVISIBLE);
		mArrowImageView.setVisibility(View.VISIBLE);
		mInstructionTextView.setText(mPullToRefreshStr);
		if (!TextUtils.isEmpty(refreshTime)) {
			String text = mRefreshAtStr + refreshTime;
			mRefreshTimeTextView.setText(text);
		}
	}

	public void onReleaseToRefresh(String refreshTime) {
		mRefreshProgressBar.setVisibility(View.INVISIBLE);
		mArrowImageView.setVisibility(View.VISIBLE);
		mInstructionTextView.setText(mReleaseToRefreshStr);
		if (!TextUtils.isEmpty(refreshTime)) {
			String text = mRefreshAtStr + refreshTime;
			mRefreshTimeTextView.setText(text);
		}
	}

	public void onRefreshComplete() {
		mArrowImageView.clearAnimation();
		mArrowImageView.setVisibility(View.INVISIBLE);
		mRefreshProgressBar.setVisibility(View.INVISIBLE);
		mRefreshTimeTextView.setVisibility(View.INVISIBLE);
		mInstructionTextView.setVisibility(View.INVISIBLE);
	}

	public void startUpAnimation() {
		mArrowImageView.clearAnimation();
		mArrowImageView.startAnimation(mArrowUpAnimation);
	}

	public void startDownAnimation() {
		mArrowImageView.clearAnimation();
		mArrowImageView.startAnimation(mArrowDownAnimation);
	}
}
