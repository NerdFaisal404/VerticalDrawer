package com.renny.verticaldrawer;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;




/**
 * 垂直DrawerLayout
 * 实现步骤：
 * 1.使用静态方法来构ViewDragHelper,需要传入一个ViewDragHelper.Callback对象.
 * 2.重写onInterceptTouchEvent和onTouchEvent回调ViewDragHelper中对应方法.
 * 3.在ViewDragHelper.Callback中对视图做操作.
 * 4.使用ViewDragHelper.smoothSlideViewTo()方法平滑滚动.
 * 5.自定义一些交互逻辑的自由实现.
 */
public class VerticalDrawerLayout extends ViewGroup {
    private ViewDragHelper mTopViewDragHelper;

    private View mContentView;
    private ViewGroup mDrawerView;
    private boolean canScroll = true;
    private boolean mIsOpen = true;
    private int sourceHeight = 360;
    private openChangeListener mOpenChangeListener;

    public VerticalDrawerLayout(Context context) {
        this(context, null);
    }

    public VerticalDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOpenChangeListener(openChangeListener openChangeListener) {
        mOpenChangeListener = openChangeListener;
    }

    public void setSourceHeight(int sourceHeight) {
        if (this.sourceHeight == sourceHeight)
            return;
        this.sourceHeight = sourceHeight;
        invalidate();
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    private void init() {
        mTopViewDragHelper = ViewDragHelper.create(this, 1.5f, new ViewDragHelperCallBack());
        mTopViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
    }

    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {

            if (child == mDrawerView && !canScroll)
                return false;
            return child == mDrawerView;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {

            mTopViewDragHelper.captureChildView(mDrawerView, pointerId);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {

            return Math.max(Math.min(top, 0), -mDrawerView.getHeight() + sourceHeight);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            float movePercentage = (releasedChild.getHeight() + releasedChild.getTop()) / (float) releasedChild.getHeight();
            int finalTop = (xvel >= 0 && movePercentage > 0.5f) ? 0 : -releasedChild.getHeight() + sourceHeight;
            mTopViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), finalTop);
            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (mOpenChangeListener != null) {
                mOpenChangeListener.onShowHeightChanging(mDrawerView.getMeasuredHeight() + top);
            }
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            if (mDrawerView == null) return 0;
            return (mDrawerView == child) ? mDrawerView.getHeight() : 0;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mDrawerView == null)
                return;
            if (state == ViewDragHelper.STATE_IDLE) {
                mIsOpen = (mDrawerView.getTop() == 0);
                if (mOpenChangeListener != null) {
                    mOpenChangeListener.isOpen(mIsOpen);
                }

            }
        }
    }

    @Override
    public void computeScroll() {
        if (mTopViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public void closeDrawer() {
        if (mDrawerView == null)
            return;
        if (mIsOpen) {
            mTopViewDragHelper.smoothSlideViewTo(mDrawerView, mDrawerView.getLeft(), -mDrawerView.getHeight() + sourceHeight);
            invalidate();
        }
    }

    public void openDrawer() {
        if (mDrawerView == null)
            return;
        if (!mIsOpen) {
            mTopViewDragHelper.smoothSlideViewTo(mDrawerView, mDrawerView.getLeft(), 0);
            invalidate();
        }
    }

    public boolean isDrawerOpened() {
        return mIsOpen;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mTopViewDragHelper.shouldInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTopViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);

        MarginLayoutParams params = (MarginLayoutParams) mContentView.getLayoutParams();
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                measureWidth - (params.leftMargin + params.rightMargin), MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                measureHeight - (params.topMargin + params.bottomMargin), MeasureSpec.EXACTLY);
        mContentView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        mDrawerView.measure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = getChildAt(0);
        mDrawerView = (ViewGroup) getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        MarginLayoutParams params = (MarginLayoutParams) mContentView.getLayoutParams();
        mContentView.layout(params.leftMargin, params.topMargin,
                mContentView.getMeasuredWidth() + params.leftMargin,
                mContentView.getMeasuredHeight() + params.topMargin);
        if (changed) {
            params = (MarginLayoutParams) mDrawerView.getLayoutParams();
            mDrawerView.layout(params.leftMargin, params.topMargin - mDrawerView.getMeasuredHeight() + sourceHeight,
                    mDrawerView.getMeasuredWidth() + params.leftMargin,
                    params.topMargin + sourceHeight);
        }
    }

    public interface openChangeListener {

        void isOpen(boolean isOpen);

        void onShowHeightChanging(int showHeight);
    }
}
