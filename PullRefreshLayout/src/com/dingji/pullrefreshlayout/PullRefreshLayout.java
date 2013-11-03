package com.dingji.pullrefreshlayout;

import java.text.SimpleDateFormat;
import java.util.Date;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class PullRefreshLayout extends FrameLayout implements ScrollListener {

	private static final String TAG = PullRefreshLayout.class.getSimpleName();

	private static final String TIME_PATTERN = "MM-dd HH:mm:ss";

	private static final int STATE_PULL_TO_REFRESH = 0x0;// 下拉可以刷新

	private static final int STATE_RELEASE_TO_REFRESH = 0x1;// 释放可以刷新

	private static final int STATE_REFRESHING = 0x3;// 正在刷新

	private static final int STATE_DEFAULT = 0x4;// header view被隐藏

	private static final int SCROLL_DURATION_DEFAULT = 400;

	private PullRefreshHeaderView mHeaderView;

	private ScrollRunnable mScrollRunable;

	private View mContentView;

	private int mHeaderViewHeight;

	private int mHeaderViewTop;

	private double mTouchEventDownY;// Down事件的Y坐标

	private double mTouchEventDownX;// Down事件的X坐标

	private int mTouchSlop;

	private String mRefreshTime;

	private PullRefreshListener mPullRefreshListener;// 回调，根据layout的状态可以做一些相应的处理

	private int mState = STATE_DEFAULT;// 记录 header view的状态

	public PullRefreshLayout(Context context) {
		super(context);
		initMembers(context);
	}

	public PullRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initMembers(context);
	}

	private void initMembers(Context context) {

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mRefreshTime = getCurrentTime();

		mHeaderView = new PullRefreshHeaderView(context);
		mScrollRunable = new ScrollRunnable(context, this);

		mScrollRunable.setScrollListener(this);
		setClipChildren(true);
		setDrawingCacheEnabled(false);

		mHeaderViewHeight = context.getResources().getDimensionPixelSize(
				R.dimen.pull_refresh_header_height);
		mHeaderViewTop = -mHeaderViewHeight;

	}

	public void setPullRreshListener(PullRefreshListener listener) {
		mPullRefreshListener = listener;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		requestDisallowInterceptTouchEvent(false);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.i(TAG, "onInterceptTouchEvent action is: " + ev.getAction());

		mTouchEventY = ev.getY();
		switch (ev.getAction()) {

		case MotionEvent.ACTION_DOWN:
			mTouchEventDownY = ev.getY();
			mTouchEventDownX = ev.getX();

			if (mHeaderViewTop > -mHeaderViewHeight) {
				Log.e(TAG,
						"onInterceptTouchEvent headerViewTop > -headerViewHeight return true");
				return true;
			} else {
				return false;
			}

		case MotionEvent.ACTION_MOVE:
			if (mHeaderViewTop > -mHeaderViewHeight) {
				return true;
			} else {
				if ((isRefreshReady())
						&& ((ev.getY() - mTouchEventDownY) >= mTouchSlop * 0.5)
						&& (Math.abs((ev.getY() - mTouchEventDownY)) > 2 * Math
								.abs((ev.getX() - mTouchEventDownX)))) {
					Log.e(TAG, "onInterceptTouchEvent return is :" + true);
					return true;
				} else {
					return false;
				}
			}

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mHeaderViewTop > -mHeaderViewHeight) {
				return true;
			} else {
				return false;
			}
		default:
			break;
		}

		return super.onInterceptTouchEvent(ev);

	}

	private double mTouchEventY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "onTouchEvent action is :" + event.getAction());

		switch (event.getAction()) {

		case MotionEvent.ACTION_MOVE:

			// if (Math.abs((event.getY() - mTouchEventDownY)) > Math.abs((event
			// .getX() - mTouchEventDownX))) {

			int moveDistance = (int) (event.getY() - mTouchEventY);
			Log.e(TAG, "moveDistance is :" + moveDistance);

			if (moveDistance < 0) {
				moveDistance = moveDistance < -(mHeaderViewTop + mHeaderViewHeight) ? -(mHeaderViewTop + mHeaderViewHeight)
						: moveDistance;
			}

			mHeaderView.offsetTopAndBottom(moveDistance);
			mContentView.offsetTopAndBottom(moveDistance);
			invalidate();

			int headerViewTop = mHeaderView.getTop();
			if (headerViewTop >= 0 && mHeaderViewTop < 0
					&& mState != STATE_REFRESHING) {
				mHeaderView.startUpAnimation();
				mHeaderView.onReleaseToRefresh(mRefreshTime);
				mState = STATE_RELEASE_TO_REFRESH;
			}

			if (headerViewTop < 0 && mHeaderViewTop >= 0
					&& mState != STATE_REFRESHING) {
				mHeaderView.startDownAnimation();
				mHeaderView.onPullToRefresh(mRefreshTime);
				mState = STATE_PULL_TO_REFRESH;
			}

			mHeaderViewTop = headerViewTop;

			mTouchEventY = event.getY();
			// }
			break;
		case MotionEvent.ACTION_UP:

			if (mHeaderView.getTop() >= 0) {
				// 刷新
				if (mState != STATE_REFRESHING) {
					if (null != mPullRefreshListener) {
						mPullRefreshListener.onRefresh();
					}
					mState = STATE_REFRESHING;
					mHeaderView.onRefresh(mRefreshTime);
				}

				scrollToRefresh();

			} else {
				// 不刷新
				if (mState != STATE_REFRESHING) {
					if (mHeaderView.getTop() > -mHeaderViewHeight) {
						mState = STATE_DEFAULT;
						scrollToClose();
					}
				}
			}

			break;
		default:
			break;
		}

		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		mHeaderView.layout(0, mHeaderViewTop, width, mHeaderViewTop
				+ mHeaderViewHeight);

		mContentView.layout(0, mHeaderViewTop + mHeaderViewHeight, width,
				mHeaderViewTop + mHeaderViewHeight + height);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mContentView = getChildAt(0);
		mContentView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		addView(mHeaderView);
	}

	@SuppressLint("SimpleDateFormat")
	private String getCurrentTime() {
		String time = "";

		SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
		Date date = new Date();
		time = sdf.format(date);

		return time;
	}

	@Override
	public void onScroll(int moveX, int moveY) {
		moveY = moveY > mHeaderView.getBottom() ? mHeaderView.getBottom()
				: moveY;

		mHeaderView.offsetTopAndBottom(-moveY);
		mContentView.offsetTopAndBottom(-moveY);
		invalidate();
		mHeaderViewTop = mHeaderView.getTop();
	}

	@Override
	public void onScrollEnd() {
		if (mState == STATE_DEFAULT) {
			mHeaderView.onPullToRefresh(mRefreshTime);
		}
	}

	private void scrollToClose() {
		mScrollRunable.startScrollDistance(0, mHeaderView.getBottom(),
				SCROLL_DURATION_DEFAULT);
	}

	private void scrollToRefresh() {
		mScrollRunable.startScrollDistance(0, mHeaderView.getTop(),
				SCROLL_DURATION_DEFAULT);
	}

	private boolean isRefreshReady() {
		boolean isRefreshReady = false;
		if (mPullRefreshListener != null) {
			isRefreshReady = mPullRefreshListener.isRefreshReady();
		}
		Log.e(TAG, "isRefreshReady is : " + isRefreshReady);
		return isRefreshReady;
	}

	public void onRefershComplete() {
		mState = STATE_DEFAULT;
		mRefreshTime = getCurrentTime();
		scrollToClose();
	}

}
