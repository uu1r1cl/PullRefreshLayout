package com.dingji.pullrefreshlayout;

import android.content.Context;
import android.view.View;
import android.widget.Scroller;

public class ScrollRunnable implements Runnable {

	private Scroller mScroller;

	private int mLastScrollX;
	private int mLastScrollY;
	private int mCurrentScrollX;
	private int mCurrentScrollY;

	private View mView;

	private ScrollListener mOnScrollLsn;

	public void setScrollListener(ScrollListener listener) {
		mOnScrollLsn = listener;
	}

	public ScrollRunnable(Context context, View view) {
		mScroller = new Scroller(context);
		mView = view;
	}

	@Override
	public void run() {

		boolean isNotFinish = mScroller.computeScrollOffset();// 返回true，表示滑动未结束

		mCurrentScrollX = mScroller.getCurrX();
		mCurrentScrollY = mScroller.getCurrY();

		int moveX = mCurrentScrollX - mLastScrollX;
		int moveY = mCurrentScrollY - mLastScrollY;

		if (isNotFinish) {
			if (mOnScrollLsn != null) {
				mOnScrollLsn.onScroll(moveX, moveY);
			}
			mLastScrollX = mCurrentScrollX;
			mLastScrollY = mCurrentScrollY;
			mView.post(this); // 滑动没有结束，继续运行此Runnable
		} else {
			if (mOnScrollLsn != null) {
				mOnScrollLsn.onScrollEnd();
			}
			initMembers();
			mView.removeCallbacks(this);
		}
	}

	/**
	 * 
	 * @param distanceX
	 *            要滑动的X位移
	 * @param distanceY
	 *            要滑动的Y位移
	 * @param duration
	 *            void 滑动时间（毫秒）
	 */
	public synchronized void startScrollDistance(int distanceX, int distanceY,
			int duration) {
		if (distanceX == 0 && distanceY == 0) {
			return;
		}
		initMembers();
		mScroller.startScroll(0, 0, distanceX, distanceY, duration);
		mView.post(this);
	}

	private void initMembers() {
		mCurrentScrollX = 0;
		mLastScrollX = 0;
		mCurrentScrollY = 0;
		mLastScrollY = 0;
	}
}
