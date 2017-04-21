package com.renny.verticaldrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by renjianan on 2016/12/21.
 */

public class DrawerScrollLayout extends ViewGroup {

    private int showHeight;
    private View childViewContent;
    private int cl, ct, cr, cb;

    public DrawerScrollLayout(Context context) {
        super(context);
    }

    public DrawerScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setShowHeight(int showHeight) {
        this.showHeight = showHeight;
        if (childViewContent == null)
            return;
        childViewContent.offsetTopAndBottom(ct + getMeasuredHeight() - showHeight - childViewContent.getTop());

        //childViewContent.layout(cl, ct + getMeasuredHeight() - showHeight, cr, cb + getMeasuredHeight() - showHeight);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        MarginLayoutParams cParams;
        if (getChildCount() < 2)
            return;
        childViewContent = getChildAt(0);
        cParams = (MarginLayoutParams) childViewContent.getLayoutParams();
        cl = cParams.leftMargin;
        ct = cParams.topMargin;
        cr = cl + childViewContent.getMeasuredWidth();
        cb = childViewContent.getMeasuredHeight() + ct;
        childViewContent.layout(cl, ct + getMeasuredHeight() - showHeight, cr, cb + getMeasuredHeight() - showHeight);

        View childViewScroll = getChildAt(1);
        cParams = (MarginLayoutParams) childViewScroll.getLayoutParams();
        childViewScroll.layout(cParams.leftMargin,
                getMeasuredHeight() - childViewScroll.getMeasuredHeight(),
                cParams.leftMargin + childViewScroll.getMeasuredWidth(),
                getMeasuredHeight());
    }
}
