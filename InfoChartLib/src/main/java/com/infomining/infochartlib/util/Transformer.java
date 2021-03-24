package com.infomining.infochartlib.util;

import android.graphics.Matrix;

public class Transformer {

    protected Matrix mOffsetMatrix = new Matrix();

    protected Matrix mValueMatrix = new Matrix();

    protected ViewPortHandler mViewPortHandler;

    public Transformer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }

    public void valuesToPixel(float[] pts) {
        mValueMatrix.mapPoints(pts);
        mOffsetMatrix.mapPoints(pts);
    }

    public void initValueMatrix(float xMin, float yMin, float deltaX, float deltaY) {
        float scaleX = mViewPortHandler.mChartWidth / deltaX;
        float scaleY = mViewPortHandler.mChartHeight / deltaY;

        mValueMatrix.postTranslate(-xMin, -yMin);
        mValueMatrix.postScale(scaleX, -scaleY);
    }

    public void initOffsetMatrix() {
        mOffsetMatrix.postTranslate(mViewPortHandler.getOffsetLeft(), mViewPortHandler.getChartHeight() - mViewPortHandler.getOffsetBottom());
    }

}
