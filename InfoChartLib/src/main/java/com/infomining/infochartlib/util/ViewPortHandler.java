package com.infomining.infochartlib.util;

import android.graphics.RectF;

public class ViewPortHandler {

    protected float mChartHeight;

    protected float mChartWidth;

    protected RectF mContentRect = new RectF();

    public void setChartDimens(float height, float width) {
        this.mChartHeight = height;
        this.mChartWidth = width;

        mContentRect.set(0, 0, width, height);
    }

    public void setOffset(float left, float top, float right, float bottom) {
        mContentRect.set(left, top, mChartWidth + right, mChartHeight + bottom);
    }

    public float getOffsetLeft() {
        return mContentRect.left;
    }

    public float getOffsetTop() {
        return mContentRect.top;
    }

    public float getOffsetRight() {
        return mContentRect.right - mChartWidth;
    }

    public float getOffsetBottom() {
        return mContentRect.bottom - mChartHeight;
    }

    public float getContentWidth() {
        return mContentRect.width();
    }

    public float getChartHeight() {
        return mContentRect.height();
    }

}
